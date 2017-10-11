package com.leo.cse.backend.profile;

abstract class ProfileField {

	public Class<?> type;

	public abstract boolean acceptsValue(Object value);

	public abstract Object getValue();

	public abstract void setValue(Object value);

}
