package com.man4fun.template.player.business;

import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import com.man4fun.template.actor.ActorSystemManager;
import com.man4fun.template.actor.enums.ActorTypeEnum;
import com.man4fun.template.business.dubbo.ActorMsg;
import com.man4fun.template.business.dubbo.ActorResponse;
import com.man4fun.template.business.dubbo.DubboPlayerServiceTriple;

@Service
@DubboService
public class PlayerService
		extends DubboPlayerServiceTriple.PlayerServiceImplBase {

	@Override
	public ActorResponse handleActorMsg(ActorMsg actorMsg) {
		if (actorMsg.getActorId().isEmpty()) {
			return ActorResponse.newBuilder().setCode(1).build();
		}
		ActorSystemManager.getInstance().sendMsg(ActorTypeEnum.PLAYER.getName(),
				actorMsg.getActorId(), actorMsg);

		// immediately return accepted
		return ActorResponse.newBuilder().setCode(0).build();
	}
}
