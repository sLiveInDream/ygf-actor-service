package com.man4fun.template.gateway.actor;

import com.google.protobuf.ByteString;
import com.man4fun.template.actor.AbstractLogicActor;
import com.man4fun.template.business.dubbo.ActorEndRequest;
import com.man4fun.template.business.dubbo.ActorMigrationRequest;
import com.man4fun.template.business.dubbo.ActorMsg;
import com.man4fun.template.business.dubbo.ActorMsgTypeEnum;
import com.man4fun.template.business.dubbo.PlayerService;
import com.man4fun.template.data.IDataSource;
import com.man4fun.template.gateway.cache.ConnectContext;
import com.man4fun.template.gateway.cache.ConnectContextCache;
import com.man4fun.template.gateway.remote.RemoteService;
import com.man4fun.template.router.ServerRouteManager;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConnectActor extends AbstractLogicActor {
	private String openId;
	private long userId;

	public ConnectActor(String actorId, IDataSource dataSource) {
		super(actorId, dataSource);
	}

	@Override
	protected void afterLoadEntity(int loadFromMsg) {
	}

	@Override
	protected void handleActorMsg(ActorMsg msg) {
		// 登录消息。
		if (msg.getMsgType() == ActorMsgTypeEnum.LOGIN.getNumber()) {
			if (userId != 0) {
				log.warn(
						"connect actor user repeat login! userId: {}, channelKey: {}",
						userId, actorId());
				return;
			}
			// TODO 调用登录服务校验 token。
			String openId = "abc";
			long userId = 1L;
			this.openId = openId;
			this.userId = userId;

			// 填充连接上下文。
			ConnectContext connectContext = ConnectContextCache.get(actorId());
			if (connectContext == null) {
				log.error("ConnectContext is null, channelKey: {}", actorId());
				return;
			}
			connectContext.setOpenId(openId);
			connectContext.setUserId(userId);
			ConnectContextCache.put(actorId(), connectContext);

			// 触发玩家服务创建 PlayerActor。
			RemoteService.getInstance().sendMsgToPlayerActor(
					ActorMsgTypeEnum.START_VALUE, userId, ByteString.EMPTY);
			return;
		}

		if (userId == 0L) {
			log.error(
					"connect actor send msg fail! userId is null, channelKey: {}, msg:{}",
					actorId(), msg);
			return;
		}

		// 迁移消息（gm触发）
		if (msg.getMsgType() == ActorMsgTypeEnum.MIGRATION_SRC.getNumber()) {
			// 先给旧路由发送开始迁移消息
			RemoteService.getInstance().sendMsgToPlayerActor(msg.getMsgType(),
					userId, msg.getBody());
			// 清掉路由缓存
			ServerRouteManager.removeRoute(PlayerService.class.getName(),
					String.valueOf(userId));
			// 发送start消息以触发重新路由
			RemoteService.getInstance().sendMsgToPlayerActor(
					ActorMsgTypeEnum.MIGRATION_DST.getNumber(), userId,
					ActorMigrationRequest.newBuilder().build().toByteString());
			return;
		}

		RemoteService.getInstance().sendMsgToPlayerActor(msg.getMsgType(),
				userId, msg.getBody());
	}

	@Override
	public void postStop() throws Exception {
		super.postStop();
		if (userId == 0L) {
			log.error(
					"connect actor postStop fail! userId is null, channelKey: {}",
					actorId());
			return;
		}
		RemoteService.getInstance().sendMsgToPlayerActor(
				ActorMsgTypeEnum.END.getNumber(), userId,
				ActorEndRequest.newBuilder().build().toByteString());
		// 连接断开直接清掉路由缓存,目前只有player服务，未来有别的服务也要清掉
		ServerRouteManager.removeRoute(PlayerService.class.getName(),
				String.valueOf(userId));
	}
}
