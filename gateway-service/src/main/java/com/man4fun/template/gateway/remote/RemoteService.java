package com.man4fun.template.gateway.remote;

import javax.annotation.PostConstruct;

import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;

import com.google.protobuf.ByteString;
import com.man4fun.template.business.dubbo.ActorMsg;
import com.man4fun.template.business.dubbo.ActorResponse;
import com.man4fun.template.business.dubbo.PlayerService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RemoteService {
	private static RemoteService instance;
	@DubboReference
	private PlayerService playerService;

	@PostConstruct
	public void init() {
		instance = this;
	}

	public static RemoteService getInstance() {
		return instance;
	}

	public void sendMsgToPlayerActor(int msgType, long userId,
			ByteString body) {
		if (body == null) {
			body = ByteString.EMPTY;
		}
		// 构造 ActorMsg
		ActorMsg actorMsg = ActorMsg.newBuilder()
				.setActorId(String.valueOf(userId)).setMsgType(msgType)
				.setBody(body).build();
		// 调用远程服务
		ActorResponse response = playerService.handleActorMsg(actorMsg);
		if (response.getCode() != 0) {
			log.error(
					"send msg to actorService fail! userId: {}, msgType: {}, code: {}",
					userId, msgType, response.getCode());
		}
	}
}
