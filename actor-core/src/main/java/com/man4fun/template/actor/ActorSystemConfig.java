package com.man4fun.template.actor;

import java.util.ArrayList;
import java.util.List;

import com.man4fun.template.data.IDataSource;

import akka.actor.AbstractActor;

public class ActorSystemConfig {
	private String actorSystemName;
	private String serviceName;
	private IDataSource dataSource;
	private long monitorInitDelayMs = 5000;
	private long monitorIntervalMs = 10000;
	private List<ActorBranchConfig> actorBranchConfigList = new ArrayList<>();

	public String getActorSystemName() {
		return actorSystemName;
	}

	public List<ActorBranchConfig> getActorBranchConfigList() {
		return actorBranchConfigList;
	}

	public void setActorSystemName(String actorSystemName) {
		this.actorSystemName = actorSystemName;
	}

	public void setActorBranchConfigList(
			List<ActorBranchConfig> actorBranchConfigList) {
		this.actorBranchConfigList = actorBranchConfigList;
	}

	public void addActorBranchConfig(ActorBranchConfig actorBranchConfig) {
		this.actorBranchConfigList.add(actorBranchConfig);
	}

	public IDataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(IDataSource dataSource) {
		this.dataSource = dataSource;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public long getMonitorInitDelayMs() {
		return monitorInitDelayMs;
	}

	public void setMonitorInitDelayMs(long monitorInitDelayMs) {
		this.monitorInitDelayMs = monitorInitDelayMs;
	}

	public long getMonitorIntervalMs() {
		return monitorIntervalMs;
	}

	public void setMonitorIntervalMs(long monitorIntervalMs) {
		this.monitorIntervalMs = monitorIntervalMs;
	}

	public static class ActorBranchConfig {
		private String branchName;
		private int supervisorNum;
		private Class<? extends CommonSupervisorActor> supervisorClass;
		private Class<? extends AbstractActor> childClass;

		public String getBranchName() {
			return branchName;
		}

		public int getSupervisorNum() {
			return supervisorNum;
		}

		public Class<? extends CommonSupervisorActor> getSupervisorClass() {
			return supervisorClass;
		}

		public void setBranchName(String branchName) {
			this.branchName = branchName;
		}

		public void setSupervisorNum(int supervisorNum) {
			this.supervisorNum = supervisorNum;
		}

		public void setSupervisorClass(
				Class<? extends CommonSupervisorActor> supervisorClass) {
			this.supervisorClass = supervisorClass;
		}

		public Class<? extends AbstractActor> getChildClass() {
			return childClass;
		}

		public void setChildClass(Class<? extends AbstractActor> childClass) {
			this.childClass = childClass;
		}
	}
}
