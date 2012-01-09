package com.pf.simulator.log;

import java.util.HashMap;
import java.util.Map;

import com.pf.manager.EnvironmentManager;
import com.pf.model.Agent;
import com.pf.simulator.Simulator;

public class LogReference {

	public Map<Integer, AgentSoftReference> agentsReference = new HashMap<Integer, AgentSoftReference>() ;

	public EnvironmentManager envReference;

	public EnvironmentManager getEnvReference() {
		if(envReference != null && envReference.getObstacles() == null )
			envReference.init(envReference);
		return envReference;
	}
	
	
	public Map<Integer, AgentSoftReference> getAgentsReference() {
		return agentsReference;
	}

	public void setEnvReference(EnvironmentManager envReference) {
		this.envReference = envReference;
	}

	public int getAgentQty()
	{
		return agentsReference.size();
	}
	

	public void putNewAgent(Agent agent, float time) {
		if(agentsReference.get(agent.id) == null) {
			AgentSoftReference agentReference = new AgentSoftReference();
			agentReference.id = agent.id;
			agentReference.mass = agent.mass;
			agentReference.radius = agent.radius;
			if(agent.movementType.equals("social"))
				agentReference.color = Simulator.socialColorString;
			else if (agent.movementType.equals("q-learning"))
				agentReference.color = Simulator.qlearningColorString;
			else if (agent.movementType.equals("q-interpreter"))
			{
				if(agent.logger == null)
					agentReference.color = Simulator.qinterpreterColorString;
				else
					agentReference.color = Simulator.qinterpreterLoggedColorString;
			}
			else
				agentReference.color = Simulator.defaultColorString;
			agentReference.startTime = time;
			agentReference.endTime = -1; 
			
			agentsReference.put(agent.id, agentReference);
		}
	}
	 
	public void agentDied(Agent agent, float time) {
		if(agentsReference.get(agent.id) != null) {

			agentsReference.get(agent.id).endTime = time;
		}
	}
	
	public boolean envReferenceSet()
	{
		return envReference != null; 
	}
	
	
}
