package com.man4fun.template.actor;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.man4fun.template.actor.entity.ActorEntity;
import com.man4fun.template.actor.enums.ActorConstantMsgEnum;
import com.man4fun.template.actor.enums.ActorStateEnum;
import com.man4fun.template.actor.receive.LoadEntityMsg;
import com.man4fun.template.business.dubbo.ActorMsg;
import com.man4fun.template.data.IDataSource;

import akka.actor.AbstractActor;
import akka.actor.Cancellable;
import lombok.extern.slf4j.Slf4j;
import scala.concurrent.duration.Duration;

@Slf4j
public abstract class AbstractLogicActor extends AbstractActor {
	private final String actorId;
	protected ActorEntity entity;
	private int state;
	private final Map<String, Cancellable> cancellableMap = new HashMap<>();
	private final IDataSource dataSource;
	private boolean persistTick = false;

	public AbstractLogicActor(String actorId, IDataSource dataSource) {
		this.actorId = actorId;
		this.dataSource = dataSource;
	}

	protected String actorId() {
		return actorId;
	}

	protected int state() {
		return state;
	}

	protected void setPersistTick(boolean persistTick) {
		this.persistTick = persistTick;
	}

	@Override
	public void preStart() throws Exception {
		super.preStart();
		this.state = ActorStateEnum.STARTING.getState();
		log.info("actor start. {}", self().path().name());
	}

	@Override
	public void postStop() throws Exception {
		super.postStop();
		this.state = ActorStateEnum.CLOSING.getState();
		// 关闭定时器。
		try {
			for (Map.Entry<String, Cancellable> entry : cancellableMap
					.entrySet()) {
				Cancellable tickCancellable = entry.getValue();
				if (tickCancellable != null && !tickCancellable.isCancelled()) {
					tickCancellable.cancel();
				}
			}
		} catch (Exception e) {
			log.error("actor fail to cancel tickCancellable. actorName:{}",
					self().path().name(), e);
		}
		// 停止前最后保存一次数据。
		try {
			dataSource.saveAsync(actorId, entity);
		} catch (Exception e) {
			log.error("actor entity failed to persist. actorName:{}",
					self().path().name(), e);
		}
	}

	@Override
	public Receive createReceive() {
		return receiveBuilder().match(LoadEntityMsg.class, this::onLoadEntity)
				.matchEquals(ActorConstantMsgEnum.PERSIST_TICK.getValue(),
						this::onPersistTick)
				.match(ActorMsg.class, this::onActorMsg).build();
	}

	private void onLoadEntity(LoadEntityMsg loadEntityMsg) {
		if (state != ActorStateEnum.STARTING.getState()) {
			log.warn("actor load entity state error! actorName:{}, state:{}",
					self().path().name(), state);
			return;
		}
		this.entity = loadEntityMsg.getActorEntity();
		this.state = ActorStateEnum.RUNNING.getState();
		if (this.persistTick) {
			startTickWithFixedDelay(
					ActorConstantMsgEnum.PERSIST_TICK.getValue(), 5000);
		}
		log.info("actor load entity over. actorName:{}", self().path().name());
		afterLoadEntity(loadEntityMsg.getLoadFromMsg());
	}

	protected abstract void afterLoadEntity(int loadFromMsg);

	private void onPersistTick(String msg) {
		if (state != ActorStateEnum.RUNNING.getState()) {
			return;
		}
		dataSource.saveAsync(actorId, entity);
	}

	private void onActorMsg(ActorMsg msg) {
		if (state != ActorStateEnum.RUNNING.getState()) {
			log.warn(
					"actor not ready to receive messages. actorName: {}, msg:{}, state:{}",
					self().path().name(), msg, state);
			return;
		}
		handleActorMsg(msg);
	}

	protected abstract void handleActorMsg(ActorMsg msg);

	private void startTickWithFixedDelay(String type, long millisSeconds) {
		cancellableMap.put(type,
				getContext().system().scheduler().scheduleWithFixedDelay(
						Duration.create(millisSeconds, TimeUnit.MILLISECONDS),
						Duration.create(millisSeconds, TimeUnit.MILLISECONDS),
						getSelf(), type, getContext().system().dispatcher(),
						getSelf()));
		log.info("actor start tick with fixed delay. type:{}, delay:{}", type,
				millisSeconds);
	}

	private void startTickAtFixedRate(String type, long millisSeconds) {
		cancellableMap.put(type,
				getContext().system().scheduler().scheduleAtFixedRate(
						Duration.create(millisSeconds, TimeUnit.MILLISECONDS),
						Duration.create(millisSeconds, TimeUnit.MILLISECONDS),
						getSelf(), type, getContext().system().dispatcher(),
						getSelf()));
		log.info("actor start tick with fixed rate. type:{}, delay:{}", type,
				millisSeconds);
	}
}
