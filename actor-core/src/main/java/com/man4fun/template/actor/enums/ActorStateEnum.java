package com.man4fun.template.actor.enums;

public enum ActorStateEnum {
	STARTING(0), RUNNING(1), CLOSING(2);

	private int state;

	ActorStateEnum(int v) {
		this.state = v;
	}

	public int getState() {
		return state;
	}
}
