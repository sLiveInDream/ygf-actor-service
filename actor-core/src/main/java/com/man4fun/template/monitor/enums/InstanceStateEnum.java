package com.man4fun.template.monitor.enums;

import lombok.Getter;

/**
 * 服务实例状态。
 */
@Getter
public enum InstanceStateEnum {
	/**
	 * 健康，允许创建新 Actor，也允许路由已有 Actor。
	 */
	UP(0),
	/**
	 * 正在下线/滚更，不允许创建新 Actor，但已有 Actor 仍可路由。
	 */
	DRAINING(1),
	/**
	 * 不可用
	 */
	DOWN(2);

	private int value;

	InstanceStateEnum(int value) {
		this.value = value;
	}
}
