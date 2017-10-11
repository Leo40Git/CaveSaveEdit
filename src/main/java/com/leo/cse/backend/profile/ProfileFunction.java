package com.leo.cse.backend.profile;

abstract class ProfileFunction {
	public int argNum;
	public Class<?>[] argType;
	public Class<?> retType;

	public abstract Object call(Object[] args);
}
