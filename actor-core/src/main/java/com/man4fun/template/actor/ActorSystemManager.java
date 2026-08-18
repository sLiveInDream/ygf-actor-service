package com.man4fun.template.actor;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.DisposableBean;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ActorSystemManager implements DisposableBean {
	private static ActorSystemManager instance;
	private String serviceName;
	private ActorSystem actorSystem;
	private Map<String, ActorRef> supervisorMaps = new HashMap<>();
	private Map<String, Integer> supervisorCountMap = new HashMap<>();
	private long monitorInitDelay;
	private long monitorInterval;

	public ActorSystemManager(ActorSystemConfig actorSystemConfig) {
		String actorSystemName = actorSystemConfig.getActorSystemName();
		log.info("actor system {} initializing...", actorSystemName);
		instance = this;
		monitorInitDelay = actorSystemConfig.getMonitorInitDelayMs();
		monitorInterval = actorSystemConfig.getMonitorIntervalMs();
		serviceName = actorSystemConfig.getServiceName();
		actorSystem = ActorSystem.create(actorSystemName);
		initSupervisor(actorSystemName, actorSystemConfig);
		log.info("actor system {} initialized", actorSystemName);
	}

	private void initSupervisor(String actorSystemName,
			ActorSystemConfig actorSystemConfig) {
		for (ActorSystemConfig.ActorBranchConfig actorBranchConfig : actorSystemConfig
				.getActorBranchConfigList()) {
			for (int i = 0; i < actorBranchConfig.getSupervisorNum(); i++) {
				String actorName = generateActorName(
						actorBranchConfig.getBranchName(), i);
				try {
					ActorRef tmpRef = actorSystem.actorOf(
							Props.create(actorBranchConfig.getSupervisorClass(),
									serviceName,
									actorBranchConfig.getChildClass(),
									actorSystemConfig.getDataSource()),
							actorName);
					supervisorMaps.put(actorName, tmpRef);
					supervisorCountMap
							.put(actorBranchConfig.getBranchName(),
									supervisorCountMap.getOrDefault(
											actorBranchConfig.getBranchName(),
											0) + 1);
					log.info("actor system supervisor {} initialized",
							actorName);
				} catch (Exception e) {
					log.error("actor system {} create supervisor {} exception!",
							actorSystemName, actorName, e);
				}
			}
		}
	}

	public static ActorSystemManager getInstance() {
		return instance;
	}

	@Override
	public void destroy() {
		actorSystem.terminate();
	}

	private int shardFor(String branchName, String id) {
		int supervisorNum = supervisorCountMap.getOrDefault(branchName, 0);
		if (supervisorNum == 0) {
			log.error(
					"actor system no supervisor found for branch {} when sharding for {}",
					branchName, id);
			return -1;
		}
		// & Integer.MAX_VALUE 清除符号位，避免 hashCode() 为负数或 Math.abs(MIN_VALUE) 溢出
		return (id.hashCode() & Integer.MAX_VALUE) % supervisorNum;
	}

	public void sendMsg(String branchName, String id, Object msg) {
		if (msg == null) {
			return;
		}
		int shard = shardFor(branchName, id);
		String actorName = generateActorName(branchName, shard);
		ActorRef supervisor = supervisorMaps.get(actorName);
		if (supervisor == null) {
			log.error(
					"actor system no supervisor found for shard {} when sending msg for branch {} when sharding for {}",
					shard, branchName, id);
			return;
		}
		supervisor.tell(msg, ActorRef.noSender());
	}

	private String generateActorName(String branchName, int index) {
		return branchName + ":" + index;
	}
}
