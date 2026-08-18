package com.man4fun.template.gateway.actor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.man4fun.template.actor.ActorSystemConfig;
import com.man4fun.template.actor.ActorSystemManager;
import com.man4fun.template.actor.CommonSupervisorActor;
import com.man4fun.template.actor.enums.ActorTypeEnum;
import com.man4fun.template.business.dubbo.GatewayService;
import com.man4fun.template.router.ServerRouteManager;
import com.man4fun.template.router.redis.impl.ServerRouteDaoImpl;

@Configuration
public class ActorConfig {
	@Bean
	public ActorSystemManager actorSystemManager() {
		ActorSystemConfig actorSystemConfig = new ActorSystemConfig();
		actorSystemConfig.setActorSystemName("actorSystem");
		actorSystemConfig.setServiceName(GatewayService.class.getName());

		ActorSystemConfig.ActorBranchConfig actorBranchConfig = new ActorSystemConfig.ActorBranchConfig();
		actorBranchConfig.setBranchName(ActorTypeEnum.CONNECT.getName());
		actorBranchConfig.setSupervisorNum(3);
		actorBranchConfig.setSupervisorClass(CommonSupervisorActor.class);
		actorBranchConfig.setChildClass(ConnectActor.class);
		actorSystemConfig.addActorBranchConfig(actorBranchConfig);
		return new ActorSystemManager(actorSystemConfig);
	}

	@Bean
	public ServerRouteManager getServerRouteManager() {
		return new ServerRouteManager(new ServerRouteDaoImpl(null));
	}
}
