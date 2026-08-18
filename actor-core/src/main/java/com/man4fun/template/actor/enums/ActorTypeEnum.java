package com.man4fun.template.actor.enums;

public enum ActorTypeEnum {

	/**
	 * 连接Actor
	 */
	CONNECT(1, "connect"),
	/**
	 * 玩家Actor
	 */
	PLAYER(2, "player");

	private int type;
	private String name;

	ActorTypeEnum(int v, String n) {
		type = v;
		name = n;
	}

	public int getType() {
		return type;
	}

	public String getName() {
		return name;
	}
}
