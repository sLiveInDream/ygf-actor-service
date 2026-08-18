package com.man4fun.template.player.remote;

import javax.annotation.PostConstruct;

import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;

import com.man4fun.template.business.dubbo.GatewayService;
import com.man4fun.template.business.dubbo.ServiceNotify;

@Component
public class RemoteService {
	private static RemoteService instance;
	@DubboReference
	private GatewayService gatewayService;

	@PostConstruct
	public void init() {
		instance = this;
	}

	public static RemoteService getInstance() {
		return instance;
	}

	public void notify(ServiceNotify msg) {
		gatewayService.notify(msg);
	}
}
