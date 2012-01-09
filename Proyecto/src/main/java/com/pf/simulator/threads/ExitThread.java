package com.pf.simulator.threads;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import com.pf.manager.AgentManager;
import com.pf.model.Agent;
import com.pf.model.Exit;
import com.pf.movement.qlearning.QLearningBaseMovement;
import com.pf.simulator.SimEnvironment;
import com.pf.simulator.Simulator;

public class ExitThread implements Callable<List<Agent>> {


	List<Exit> myExits;
	SimEnvironment simEnvironment;
	AgentManager agentManager;
	List<Agent> removedAgents = new ArrayList<Agent>();
	
	public ExitThread()
	{
		simEnvironment = Simulator.getSimEnvironment();
		try {
			agentManager = simEnvironment.getAgentManager();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Exits crasheo en getAgentManager");
		}
	}

	public void assignExits(List<Exit> exits)
	{
		myExits = exits;
	}
	
	@Override
	public List<Agent> call() throws Exception {
		removedAgents.clear();
		for(Agent a: agentManager.getBackedUpAgents())
		{
			for (Exit e: myExits) {
				String agent_exit_id = a.getExitIdentifier();
				if(!agent_exit_id.equals(e.getId()))
					continue;
				if(e.distanceTo(a) <= 0 || e.contains(a)) {
					if(a.getMovement() instanceof QLearningBaseMovement)
						((QLearningBaseMovement)a.getMovement()).increaseVictoryCount();
					removedAgents.add(a);
					try {
						//StateLog.get().agentDied(a, Simulator.getSimulation().getCurrentStep() * simEnvironment.getEnvironment().deltaTime);
						Simulator.registerDeletedAgent(a, Simulator.getSimulation().getCurrentStep() * simEnvironment.getEnvironment().deltaTime);
				
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						System.err.println("Crasheo exits en getEnivornment");
					}
				}
			}
		}
		return removedAgents;

	}

}
