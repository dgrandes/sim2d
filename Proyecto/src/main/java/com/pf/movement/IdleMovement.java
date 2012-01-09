package com.pf.movement;


import com.pf.manager.AgentManager;
import com.pf.manager.EnvironmentManager;
import com.pf.math.Vector2;
import com.pf.model.Agent;

public class IdleMovement implements IAgentMovement {

	public void updateMovement(Agent agent, AgentManager agents,
			EnvironmentManager environment, float deltaTime) {
		agent.SetForces(new Vector2(0,0), new Vector2(0,0), new Vector2(0,0));
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "idle";
	}

}
