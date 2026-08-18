package com.man4fun.template.actor.receive;

import com.man4fun.template.actor.entity.ActorEntity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoadEntityMsg {
	private ActorEntity actorEntity;
	private int loadFromMsg;
}
