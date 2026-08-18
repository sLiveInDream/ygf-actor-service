package com.man4fun.template.player.actor;

import com.man4fun.template.actor.AbstractLogicActor;
import com.man4fun.template.business.dubbo.ActorMsg;
import com.man4fun.template.business.dubbo.ActorMsgTypeEnum;
import com.man4fun.template.business.dubbo.ServiceNotify;
import com.man4fun.template.data.IDataSource;
import com.man4fun.template.player.logic.PlayerActorMsgDispatcher;
import com.man4fun.template.player.remote.RemoteService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PlayerActor extends AbstractLogicActor {
	private PlayerActorMsgDispatcher dispatcher;

	public PlayerActor(String actorId, IDataSource dataSource) {
		super(actorId, dataSource);
	}

	@Override
	public void preStart() throws Exception {
		super.preStart();
		setPersistTick(true);
	}

	@Override
	protected void afterLoadEntity(int loadFromMsg) {
		dispatcher = new PlayerActorMsgDispatcher(entity);
		if (loadFromMsg == ActorMsgTypeEnum.START.getNumber()) {
			// 给客户端发送登录完成消息
			RemoteService.getInstance().notify(ServiceNotify.newBuilder()
					.setUserId(Long.parseLong(actorId())).build());
		}

		if (loadFromMsg == ActorMsgTypeEnum.MIGRATION_DST.getNumber()) {
			// 给客户端发送迁移完成消息
		}
	}

	@Override
	protected void handleActorMsg(ActorMsg msg) {
		dispatcher.handleActorMsg(msg);
	}
}
