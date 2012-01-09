package com.pf.simulator.threads;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import com.pf.manager.AgentManager;
import com.pf.model.Agent;
import com.pf.simulator.SimEnvironment;
import com.pf.simulator.Simulator;

public class AgentThread implements Callable<List<Agent>> {

	List<Agent> myAgents = new ArrayList<Agent>();;
	AgentManager agentManager;
	SimEnvironment simEnvironment;
	public AgentThread()
	{
		simEnvironment = Simulator.getSimEnvironment();
		try {
			agentManager = Simulator.getSimEnvironment().getAgentManager();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("Breaks everything!");
		}
	}
	
	public void assignAgents(List<Agent> agents)
	{
		myAgents.clear();
		myAgents = new ArrayList<Agent>(agents);
		
		
	}
	
	@Override
	public List<Agent>  call() throws Exception {

		for(Agent a: myAgents)
		{
			
			try {
					a.movement.updateMovement(a, simEnvironment.getAgentManager(), simEnvironment.getEnvironment(), simEnvironment.getEnvironment().deltaTime);
					
					if(a.logger != null)
						a.logger.writeState();
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("crasheo agentThread en simenvironment get*");
			}

		}
		return myAgents;
	}

}
