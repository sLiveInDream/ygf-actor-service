package com.man4fun.template.player.actor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.man4fun.template.actor.ActorSystemConfig;
import com.man4fun.template.actor.ActorSystemManager;
import com.man4fun.template.actor.CommonSupervisorActor;
import com.man4fun.template.actor.enums.ActorTypeEnum;
import com.man4fun.template.business.dubbo.PlayerService;
import com.man4fun.template.player.data.DataSource;
import com.man4fun.template.router.ServerRouteManager;
import com.man4fun.template.router.redis.impl.ServerRouteDaoImpl;

@Configuration
public class ActorConfig {
	@Bean
	public ActorSystemManager actorSystemManager() {
		ActorSystemConfig actorSystemConfig = new ActorSystemConfig();
		actorSystemConfig.setActorSystemName("actorSystem");
		actorSystemConfig.setDataSource(DataSource.getInstance());
		actorSystemConfig.setServiceName(PlayerService.class.getName());

		ActorSystemConfig.ActorBranchConfig actorBranchConfig = new ActorSystemConfig.ActorBranchConfig();
		actorBranchConfig.setBranchName(ActorTypeEnum.PLAYER.getName());
		actorBranchConfig.setSupervisorNum(3);
		actorBranchConfig.setSupervisorClass(CommonSupervisorActor.class);
		actorBranchConfig.setChildClass(PlayerActor.class);
		actorSystemConfig.addActorBranchConfig(actorBranchConfig);
		return new ActorSystemManager(actorSystemConfig);
	}

	@Bean
	public ServerRouteManager getServerRouteManager() {
		return new ServerRouteManager(new ServerRouteDaoImpl(null));
	}
}
