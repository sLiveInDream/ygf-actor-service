package com.man4fun.template.actor.entity;

import java.util.HashMap;
import java.util.Map;

import com.man4fun.template.actor.component.AbstractActorComponent;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ActorEntity {
	private String actorId;
	private Map<Class<?>, AbstractActorComponent> componentMap = new HashMap<>();

	public ActorEntity() {
	}

	public ActorEntity(String actorId) {
		this.actorId = actorId;
	}

	public <T> T addComponent(Class<T> componentClazz) {
		AbstractActorComponent component = componentMap.get(componentClazz);
		if (component != null) {
			log.warn("Entity:{}, addComponent , component already exist:{}",
					actorId, componentClazz.getName());
			return null;
		}

		try {
			component = (AbstractActorComponent) componentClazz.newInstance();
		} catch (Exception e) {
			log.error("Entity:{}, addComponent exception:{}, stackTrace:{}",
					actorId, e.getMessage(), e.getStackTrace());
			return null;
		}

		componentMap.put(componentClazz, component);
		return (T) component;
	}

	public <T> T getComponentData(Class<T> componentClazz) {
		AbstractActorComponent component = componentMap.get(componentClazz);
		if (!component.isActive()) {
			return null;
		}

		return (T) component;
	}

	public boolean removeComponent(Class<?> componentClazz) {
		AbstractActorComponent component = componentMap.get(componentClazz);
		if (component == null) {
			return false;
		}
		component.setActive(false);
		return true;
	}

	public void setActorId(String actorId) {
		this.actorId = actorId;
	}

	public String getActorId() {
		return actorId;
	}
}
