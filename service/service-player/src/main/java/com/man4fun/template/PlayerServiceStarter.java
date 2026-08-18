package com.man4fun.template;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableDubbo
@SpringBootApplication
public class PlayerServiceStarter {
	public static void main(String[] args) {
		SpringApplication.run(PlayerServiceStarter.class, args);
		log.info("PlayerServiceStarter Service start...");
	}
}
