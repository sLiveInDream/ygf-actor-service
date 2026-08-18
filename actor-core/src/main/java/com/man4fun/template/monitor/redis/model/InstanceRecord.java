package com.man4fun.template.monitor.redis.model;

import com.man4fun.template.monitor.enums.InstanceStateEnum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Redis 中的服务实例状态记录。
 */
@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InstanceRecord {
	// 服务名,与ActorSystem一一对应
	private String serviceName;
	// Dubbo provider 地址，与 invoker.getUrl().getAddress() 对齐。
	private String address;
	// 实例状态。
	private InstanceStateEnum state = InstanceStateEnum.UP;
	// 实例业务版本，用于滚动更新和灰度。
	private String version;
	// 启动时间
	private long startTime;
}
