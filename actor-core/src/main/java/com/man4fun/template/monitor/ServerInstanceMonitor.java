package com.man4fun.template.monitor;

import org.springframework.beans.factory.InitializingBean;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServerInstanceMonitor implements InitializingBean {
	private static ServerInstanceMonitor instance;

	@Override
	public void afterPropertiesSet() throws Exception {
		instance = this;
	}
}
