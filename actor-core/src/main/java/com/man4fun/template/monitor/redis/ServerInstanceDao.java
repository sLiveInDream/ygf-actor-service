package com.man4fun.template.monitor.redis;

import java.util.List;
import java.util.Optional;

import com.man4fun.template.monitor.redis.model.InstanceRecord;

/**
 * Redis 中的服务实例状态 DAO。
 *
 * instance 记录表达的是：某类服务的某个地址当前是否可承载新 Actor。 Router 会结合 Dubbo invoker
 * 列表和这里的实例状态选择目标实例。
 */
public interface ServerInstanceDao {
	/**
	 * 保存实例心跳和负载状态，并刷新 TTL。
	 */
	void saveInstance(InstanceRecord record, long ttlMillis);

	/**
	 * 查询单个实例状态。
	 */
	Optional<InstanceRecord> getInstance(String serviceType, String address);

	/**
	 * 查询某类服务的全部有效实例。
	 */
	List<InstanceRecord> listInstances(String serviceType);

	/**
	 * 删除实例状态和实例索引。
	 */
	void deleteInstance(String serviceType, String address);
}
