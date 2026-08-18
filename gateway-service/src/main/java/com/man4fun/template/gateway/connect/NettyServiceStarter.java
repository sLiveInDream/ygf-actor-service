package com.man4fun.template.gateway.connect;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class NettyServiceStarter implements ApplicationRunner {
	@Override
	public void run(ApplicationArguments args) throws Exception {
		NettyServer nettyServer = new NettyServer();
		nettyServer.service();
		Runtime.getRuntime().addShutdownHook(new Thread(nettyServer::shutdown));
	}
}
