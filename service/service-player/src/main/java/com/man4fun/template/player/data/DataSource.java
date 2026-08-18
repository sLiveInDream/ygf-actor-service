package com.man4fun.template.player.data;

import java.util.concurrent.CompletableFuture;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.man4fun.template.actor.entity.ActorEntity;
import com.man4fun.template.data.IDataSource;

import lombok.extern.slf4j.Slf4j;

/**
 * 具体持久化实现，可以对接任意存储。
 */
@Slf4j
@Component
public class DataSource implements IDataSource {
	private static DataSource instance;

	public static DataSource getInstance() {
		return instance;
	}

	@PostConstruct
	public void init() {
		instance = this;
	}

	@Override
	public CompletableFuture<ActorEntity> loadAsync(String actorId) {
		return CompletableFuture.completedFuture(this.load(actorId));
	}

	private ActorEntity load(String actorId) {
		ActorEntity actorEntity = new ActorEntity();
		actorEntity.setActorId(actorId);
		return actorEntity;
	}

	@Override
	public void saveAsync(String actorId, ActorEntity entity) {

	}
}
