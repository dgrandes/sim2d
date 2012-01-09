package com.pf.movement;


import com.pf.manager.AgentManager;
import com.pf.manager.EnvironmentManager;
import com.pf.model.Agent;

public interface IAgentMovement{

	public void updateMovement(Agent agent, AgentManager agents, EnvironmentManager environment, float deltaTime);

	public String getDescription();

	
}
