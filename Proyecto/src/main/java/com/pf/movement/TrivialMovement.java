package com.pf.movement;


import com.pf.manager.AgentManager;
import com.pf.manager.EnvironmentManager;
import com.pf.model.Agent;

public class TrivialMovement implements IAgentMovement {

	public void updateMovement(Agent agent, AgentManager agents,
			EnvironmentManager environment, float deltaTime) {
		agent.position = agent.position.add(agent.velocity.scale(deltaTime));
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "trivial";
	}

}
