package com.pf.logging;

import com.pf.model.Agent;

public interface ISimulatorLogger extends IBaseLogger {

	public void putNewAgent(Agent a, float time);
	
	public void agentDied(Agent a, float time);
}
