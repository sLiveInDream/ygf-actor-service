package com.man4fun.template.gateway.connect;

import java.util.concurrent.TimeUnit;

import com.man4fun.template.business.dubbo.ServiceMsg;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyServer {
	private static final EventLoopGroup bossGroup = new NioEventLoopGroup(
			Runtime.getRuntime().availableProcessors() * 2);
	private static final EventLoopGroup workerGroup = new NioEventLoopGroup(
			NettyConfig.INSTANCE.WORKER_SIZE);

	public ChannelFuture service() throws Exception {
		log.info("starting up server...");

		ServerBootstrap bootstrap = new ServerBootstrap();
		bootstrap.group(bossGroup, workerGroup);
		bootstrap.channel(NioServerSocketChannel.class);
		bootstrap.childHandler(new ChannelInitializer<Channel>() {

			@Override
			protected void initChannel(Channel ch) throws Exception {

				ChannelPipeline pipeline = ch.pipeline();
				pipeline.addLast(new LengthFieldBasedFrameDecoder(
						NettyConfig.INSTANCE.MAX_FRAME_LENGTH, 0, 4, 0, 4));

				pipeline.addLast(
						new ProtobufDecoder(ServiceMsg.getDefaultInstance()));
				pipeline.addLast(new ProtobufEncoder());
				pipeline.addLast(
						new IdleStateHandler(10, 40, 0, TimeUnit.SECONDS));
				pipeline.addLast(new ServerHandler());
			}

		});
		bootstrap.option(ChannelOption.SO_BACKLOG, 128);
		bootstrap.childOption(ChannelOption.SO_KEEPALIVE, false);
		bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
		bootstrap.childOption(ChannelOption.SO_RCVBUF, 128 * 1024);
		bootstrap.childOption(ChannelOption.SO_SNDBUF, 128 * 1024);
		bootstrap.childOption(ChannelOption.WRITE_BUFFER_WATER_MARK,
				new WriteBufferWaterMark(32 * 1024, 64 * 1024));
		ChannelFuture f = bootstrap
				.bind(NettyConfig.INSTANCE.IP, NettyConfig.INSTANCE.PORT)
				.sync();
		log.info("server started at: {}", NettyConfig.INSTANCE.PORT);
		return f;
	}

	public void shutdown() {
		log.info("shutting down server...");
		boolean workerShutdown = workerGroup.shutdownGracefully()
				.awaitUninterruptibly(
						NettyConfig.INSTANCE.SHUTDOWN_MAX_WAIT_SECONDS,
						TimeUnit.SECONDS);
		boolean bossShutdown = bossGroup.shutdownGracefully()
				.awaitUninterruptibly(
						NettyConfig.INSTANCE.SHUTDOWN_MAX_WAIT_SECONDS,
						TimeUnit.SECONDS);

		log.info("shutdown completed. workerShutdown: {}, bossShutdown: {}",
				workerShutdown, bossShutdown);
	}
}
