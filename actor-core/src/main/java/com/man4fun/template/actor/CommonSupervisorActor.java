package com.man4fun.template.actor;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.man4fun.template.actor.entity.ActorEntity;
import com.man4fun.template.actor.enums.ActorStateEnum;
import com.man4fun.template.actor.receive.LoadEntityMsg;
import com.man4fun.template.business.dubbo.ActorMsg;
import com.man4fun.template.business.dubbo.ActorMsgTypeEnum;
import com.man4fun.template.business.dubbo.ActorStartRequest;
import com.man4fun.template.data.IDataSource;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.OneForOneStrategy;
import akka.actor.PoisonPill;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.actor.Terminated;
import akka.japi.pf.DeciderBuilder;
import lombok.extern.slf4j.Slf4j;
import scala.concurrent.duration.Duration;

@Slf4j
public class CommonSupervisorActor extends AbstractActor {
	private String serviceName;
	private final Class<? extends AbstractActor> childClass;
	private final IDataSource dataSource;
	private final Map<String, ActorRef> children = new HashMap<>();
	private final Map<String, Integer> childState = new HashMap<>();
	private final int MAX_RETRY_START_COUNT = 2;
	private final long RETRY_DELAY_MS = 500;

	public CommonSupervisorActor(String serviceName,
			Class<? extends AbstractActor> childClass, IDataSource dataSource) {
		this.serviceName = serviceName;
		this.childClass = childClass;
		this.dataSource = dataSource;
	}

	private final SupervisorStrategy strategy = new OneForOneStrategy(10,
			scala.concurrent.duration.Duration.create("1 minute"),
			DeciderBuilder.matchAny(e -> {
				// TODO 根据异常类型决定是否通知客户端。
				log.error("Child throw exception, applying resume.", e);
				return SupervisorStrategy.resume();
			}).build());

	@Override
	public SupervisorStrategy supervisorStrategy() {
		return strategy;
	}

	@Override
	public Receive createReceive() {
		return receiveBuilder().match(ActorMsg.class, this::onActorMsg)
				.match(LoadEntityMsg.class, this::onLoadEntity)
				.match(Terminated.class, this::onTerminated).build();
	}

	private void onActorMsg(ActorMsg msg) {
		String actorId = msg.getActorId();
		int msgType = msg.getMsgType();

		ActorRef child = children.get(actorId);
		if (msgType == ActorMsgTypeEnum.START.getNumber()
				|| msgType == ActorMsgTypeEnum.MIGRATION_DST.getNumber()) {
			createChild(msg);
			return;
		}

		// TODO 当前立即释放 actor，后续可保留几分钟以优化频繁登录/退出体验。
		if (msgType == ActorMsgTypeEnum.END.getNumber()
				|| msgType == ActorMsgTypeEnum.MIGRATION_SRC.getNumber()) {
			if (child != null) {
				destroyChild(child, actorId);
				log.info(
						"actor is closing. supervisor: {}, actor: {}, msgType: {}",
						self().path().name(), actorId, msgType);
			} else {
				log.warn(
						"supervisor try to stop actor but not exist. supervisor:{}, actor:{}, msgType: {}",
						self().path().name(), actorId, msgType);
			}
			return;
		}

		if (child != null) {
			if (childState.get(actorId) == ActorStateEnum.RUNNING.getState()) {
				child.tell(msg, sender());
			} else {
				log.error(
						"actor not ready to receive messages. supervisor:{}, actor: {}, msgType:{}, state:{}",
						self().path().name(), actorId, msgType,
						childState.get(actorId));
			}
		} else {
			log.error(
					"failed to route message to actor. supervisor:{}, actor: {}, msgType:{}",
					self().path().name(), actorId, msgType);
		}
	}

	private void onLoadEntity(LoadEntityMsg loadEntityMsg) {
		ActorEntity actorEntity = loadEntityMsg.getActorEntity();
		if (actorEntity.getActorId() == null
				|| actorEntity.getActorId().isEmpty()) {
			log.error("load actorEntity error! supervisor:{}",
					self().path().name());
			return;
		}
		ActorRef child = children.get(actorEntity.getActorId());
		if (child != null) {
			child.tell(loadEntityMsg, self());
			childState.put(actorEntity.getActorId(),
					ActorStateEnum.RUNNING.getState());
		} else {
			log.warn(
					"fail to load ActorEntity because actor not exist. supervisor:{} actor:{}",
					self().path().name(), actorEntity.getActorId());
		}
	}

	private void createChild(ActorMsg msg) {
		String actorId = msg.getActorId();
		int msgType = msg.getMsgType();
		ActorRef oldChild = children.get(actorId);
		if (oldChild == null) {
			String name = generateActorName(self().path().name(), actorId);
			ActorRef child = getContext().actorOf(
					Props.create(childClass, actorId, dataSource), name);
			if (dataSource != null) {
				dataSource.loadAsync(actorId).whenComplete((entity, e) -> {
					if (e != null) {
						log.error(
								"failed to load data for actor. supervisor:{}, actor:{}, msgType:{}",
								self().path().name(), actorId, msgType, e);
						self().tell(LoadEntityMsg.builder()
								.loadFromMsg(msg.getMsgType())
								.actorEntity(new ActorEntity(actorId)).build(),
								self());
					} else {
						self().tell(
								LoadEntityMsg.builder().actorEntity(entity)
										.loadFromMsg(msg.getMsgType()).build(),
								self());
					}
				});
			} else {
				log.warn(
						"supervisor try to create actor but dataSource is null! create default actor. supervisor:{}, actor:{},msgType:{}",
						self().path().name(), actorId, msgType);
				self().tell(
						LoadEntityMsg.builder().loadFromMsg(msg.getMsgType())
								.actorEntity(new ActorEntity(actorId)).build(),
						self());
			}
			getContext().watch(child);
			children.put(actorId, child);
			childState.put(actorId, ActorStateEnum.STARTING.getState());
			return;
		}

		// 如果发现处于closing状态，就间隔固定时间重试，重试n次，如果最终失败再打印日志
		if (childState.get(actorId) == ActorStateEnum.CLOSING.getState()) {
			ActorStartRequest actorStartRequest;
			try {
				actorStartRequest = ActorStartRequest.parseFrom(msg.getBody());
			} catch (Exception e) {
				actorStartRequest = ActorStartRequest.newBuilder().build();
			}
			if (actorStartRequest.getRetryCount() >= MAX_RETRY_START_COUNT) {
				log.error(
						"supervisor try to create actor but exist. supervisor:{}, actor:{}, retryCount:{},msgType:{}",
						self().path().name(), actorId,
						actorStartRequest.getRetryCount(), msgType);
			}
			ActorStartRequest finalActorStartRequest = actorStartRequest;
			getContext().system().scheduler().scheduleOnce(
					Duration.create(RETRY_DELAY_MS, TimeUnit.MILLISECONDS),
					() -> {
						self().tell(msg.toBuilder()
								.setBody(ActorStartRequest.newBuilder()
										.setRetryCount(finalActorStartRequest
												.getRetryCount() + 1)
										.build().toByteString())
								.build(), self());
					}, getContext().system().dispatcher());
			return;
		}

		log.warn(
				"supervisor try to create actor but exist. supervisor:{}, actor:{}, actorState:{}, msgType:{}",
				self().path().name(), actorId,
				ActorStateEnum.RUNNING.getState(), msgType);
	}

	private void destroyChild(ActorRef child, String actorId) {
		child.tell(PoisonPill.getInstance(), self());
		childState.put(actorId, ActorStateEnum.CLOSING.getState());
	}

	private void onTerminated(Terminated t) {
		ActorRef dead = t.getActor();
		String removeKey = null;
		for (Map.Entry<String, ActorRef> e : children.entrySet()) {
			if (e.getValue().equals(dead)) {
				removeKey = e.getKey();
				break;
			}
		}
		if (removeKey != null) {
			children.remove(removeKey);
			childState.remove(removeKey);
			log.info("actor terminated. {}", dead.path().name());
		} else {
			log.warn("unknown child terminated: {}", dead.path().name());
		}
	}

	private String generateActorName(String parentName, String actorId) {
		return parentName + ":" + actorId;
	}
}
