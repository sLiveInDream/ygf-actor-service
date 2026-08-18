package com.man4fun.template.actor.component;

public abstract class AbstractActorComponent {
	private boolean active = true;
	private boolean dirty = false;

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isDirty() {
		return dirty;
	}

	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}
}
