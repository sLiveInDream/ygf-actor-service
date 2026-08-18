package com.man4fun.template.gateway.connect;

import com.man4fun.template.actor.ActorSystemManager;
import com.man4fun.template.actor.enums.ActorTypeEnum;
import com.man4fun.template.business.dubbo.ActorEndRequest;
import com.man4fun.template.business.dubbo.ActorMsg;
import com.man4fun.template.business.dubbo.ActorMsgTypeEnum;
import com.man4fun.template.business.dubbo.ActorStartRequest;
import com.man4fun.template.business.dubbo.ServiceMsg;
import com.man4fun.template.gateway.cache.ConnectContext;
import com.man4fun.template.gateway.cache.ConnectContextCache;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServerHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		String channelKey = ChannelUtil.getChannelKey(ctx.channel());
		ConnectContextCache.put(channelKey,
				new ConnectContext(channelKey, ctx));
		ActorSystemManager.getInstance().sendMsg(
				ActorTypeEnum.CONNECT.getName(), channelKey,
				ActorMsg.newBuilder()
						.setMsgType(ActorMsgTypeEnum.START.getNumber())
						.setActorId(channelKey).setBody(ActorStartRequest
								.newBuilder().build().toByteString())
						.build());
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		try {
			String channelKey = ChannelUtil.getChannelKey(ctx.channel());
			ServiceMsg request = (ServiceMsg) msg;
			ActorSystemManager.getInstance().sendMsg(
					ActorTypeEnum.CONNECT.getName(), channelKey,
					ActorMsg.newBuilder().setMsgType(request.getMsgType())
							.setActorId(channelKey)
							.setBody(request.toByteString()).build());
		} catch (Exception e) {
			log.error("ServerHandler channelRead error", e);
		}
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		String channelKey = ChannelUtil.getChannelKey(ctx.channel());
		ConnectContextCache.remove(channelKey);
		ActorSystemManager.getInstance()
				.sendMsg(ActorTypeEnum.CONNECT.getName(), channelKey,
						ActorMsg.newBuilder()
								.setMsgType(ActorMsgTypeEnum.END.getNumber())
								.setActorId(channelKey).setBody(ActorEndRequest
										.newBuilder().build().toByteString())
								.build());
		super.channelInactive(ctx);
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx)
			throws Exception {
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
			throws Exception {
		if (evt instanceof IdleStateEvent) {
			IdleStateEvent event = (IdleStateEvent) evt;
			switch (event.state()) {
			case ALL_IDLE:
				log.info("{}ALL_IDLE:{}", ctx, ctx.channel());
				break;
			case READER_IDLE:
				log.info("{}READER_IDLE:{}", ctx, ctx.toString());
				ctx.close();
				break;
			case WRITER_IDLE:
				log.info("{}WRITER_IDLE:{}", ctx, ctx.toString());
				break;
			default:
				break;
			}
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		log.error("ServerHandler exceptionCaught", cause);
	}
}
