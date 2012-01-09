package com.pf.manager;

public abstract class Manager<ManagerType> {

	public abstract void initWith(String filename) throws Exception;

	public abstract void saveToFile(String filename);

}
