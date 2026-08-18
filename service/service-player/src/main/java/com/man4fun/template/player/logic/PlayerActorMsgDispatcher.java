package com.man4fun.template.player.logic;

import com.man4fun.template.actor.entity.ActorEntity;
import com.man4fun.template.business.dubbo.ActorMsg;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PlayerActorMsgDispatcher {
	private ActorEntity entity;

	public PlayerActorMsgDispatcher(ActorEntity entity) {
		this.entity = entity;
	}

	public void handleActorMsg(ActorMsg actorMsg) {
		// TODO 根据消息类型分发到不同 handler，可改成注解扫描注册。
	}
}
