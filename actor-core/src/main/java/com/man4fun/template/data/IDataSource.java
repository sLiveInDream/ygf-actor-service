package com.man4fun.template.data;

import java.util.concurrent.CompletableFuture;

import com.man4fun.template.actor.entity.ActorEntity;

public interface IDataSource {
	CompletableFuture<ActorEntity> loadAsync(String actorId);

	void saveAsync(String actorId, ActorEntity entity);
}
