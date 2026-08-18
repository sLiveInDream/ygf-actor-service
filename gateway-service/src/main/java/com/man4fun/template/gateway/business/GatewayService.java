package com.man4fun.template.gateway.business;

import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import com.man4fun.template.business.dubbo.ActorResponse;
import com.man4fun.template.business.dubbo.DubboGatewayServiceTriple;
import com.man4fun.template.business.dubbo.ServiceNotify;
import com.man4fun.template.gateway.cache.ConnectContextCache;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@DubboService
public class GatewayService
		extends DubboGatewayServiceTriple.GatewayServiceImplBase {

	public ActorResponse notify(ServiceNotify notify) {
		ChannelHandlerContext channelHandlerContext = ConnectContextCache
				.getChannelHandlerContext(notify.getUserId());
		if (channelHandlerContext == null) {
			log.error(
					"notify fail! channelHandlerContext is null! userId:{},msg:{}",
					notify.getUserId(), notify);
		} else {
			// TODO 为每个玩家维护通知消息序列号，按服务类型划分以保障消息顺序。
			channelHandlerContext.channel().writeAndFlush(notify);
		}

		return ActorResponse.newBuilder().setCode(0).build();
	}
}
