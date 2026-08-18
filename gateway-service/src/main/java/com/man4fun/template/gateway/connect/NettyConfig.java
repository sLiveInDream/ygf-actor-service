package com.man4fun.template.gateway.connect;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class NettyConfig {
	public static NettyConfig INSTANCE;

	@PostConstruct
	public void init() {
		INSTANCE = this;
	}

	@Value("${netty.server.ip: 0.0.0.0}")
	public String IP;
	@Value("${netty.server.port: 6656}")
	public int PORT;
	@Value("${netty.server.worker-size: 100}")
	public int WORKER_SIZE;
	@Value("${netty.server.shutdown-waitSeconds: 30}")
	public int SHUTDOWN_MAX_WAIT_SECONDS;
	@Value("${netty.server.maxFrameLength: 65536}")
	public int MAX_FRAME_LENGTH;
	@Value("${netty.serverId: 1}")
	public int SERVER_ID;
}
