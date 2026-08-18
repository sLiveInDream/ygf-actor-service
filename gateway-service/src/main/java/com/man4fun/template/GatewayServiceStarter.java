package com.man4fun.template;

import java.util.Scanner;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.man4fun.template.business.dubbo.ActorMsgTypeEnum;
import com.man4fun.template.business.dubbo.ActorStartRequest;
import com.man4fun.template.gateway.remote.RemoteService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
@EnableDubbo
public class GatewayServiceStarter {
	public static void main(String[] args) {
		SpringApplication.run(GatewayServiceStarter.class, args);
		log.info("GatewayServiceStarter start...");

		Scanner sc = new Scanner(System.in);
		while (sc.hasNextLine()) {
			String input = sc.nextLine();
			if ("login".equals(input)) {
				try {
					RemoteService.getInstance().sendMsgToPlayerActor(
							ActorMsgTypeEnum.START.getNumber(), 1L,
							ActorStartRequest.newBuilder().build()
									.toByteString());
					log.info("test login");
				} catch (Exception e) {
					log.error("send login msg failed.", e);
				}
			}
			if ("r".equals(input)) {
				log.info("channelRead");

			}
			if ("in".equals(input)) {
				log.info("channelInActive");
			}
		}

		sc.close();
	}
}
