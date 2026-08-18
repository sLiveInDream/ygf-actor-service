package com.man4fun.template.actor.enums;

public enum ActorConstantMsgEnum {
	PERSIST_TICK("persist_tick"), LOGIC_TICK("logic_tick");

	private String value;

	ActorConstantMsgEnum(String v) {
		value = v;
	}

	public String getValue() {
		return value;
	}
}
