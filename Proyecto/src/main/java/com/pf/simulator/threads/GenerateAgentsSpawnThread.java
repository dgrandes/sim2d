package com.pf.simulator.threads;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

import com.pf.math.Vector2;
import com.pf.model.Agent;
import com.pf.model.SpawnPoint;
import com.pf.simulator.Simulator;
import com.pf.util.Util;

public class GenerateAgentsSpawnThread implements Callable<List<Agent>> {

	
	private List<SpawnPoint> spawnpoints;
	//SimEnvironment simEnvironment;
	private List<Agent> newAgents;
	private int current_step = -1;	
	
	public void assignSpawnPoints(List<SpawnPoint> spawnPoints)
	{
		spawnpoints = new ArrayList<SpawnPoint>();
		for(SpawnPoint s : spawnPoints)
			if(!s.generatesOnAgentDeletion())
				this.spawnpoints.add(s);
		
	}
	
	@Override
	public List<Agent> call() throws Exception {
		
		if(current_step == -1 )
			throw new Exception("Unset Parameters");
		
		newAgents = new ArrayList<Agent>();
		
		for (SpawnPoint s : spawnpoints) {
		
				float chance = s.getChanceToGenerate();
				if(new Random().nextFloat() <= chance) {
					
						float mass = Util.generateUniformDist(s.getMassMin(), s.getMassMax());
						float radius = Util.generateUniformDist(s.getRadiusMin(), s.getRadiusMax());
						
						Agent agent = new Agent();

						agent.radius = radius;
		/*				boolean clearSpanPoint = false;
						int tries = 50;
						Vector2 position = null;
						
						while(tries > 0 && !clearSpanPoint)
						{
							clearSpanPoint = true;
							 position = s.randomPointWithin(agent);
							 for (Agent oAgent : Simulator.simEnvironment.getAgentManager().getBackedUpAgents()) {
									if(!agent.equals(oAgent)) {
										float radiusSum = agent.radius + oAgent.radius;
										float distance = agent.position.distance(oAgent.position);
										
										if(radiusSum >= distance) {
											clearSpanPoint = false;
										}
									}
							 }
							 tries--;
										
							 
						}*/
						agent.setBirthSpawnId(s.getId());
						agent.id = -1;
						agent.movementType = s.getMovementType();
						agent.exitIdentifier = s.getDesiredExitId();
						agent.position =  s.randomPointWithin(agent);
						agent.mass = mass;
						agent.velocity = new Vector2(0,0);
						newAgents.add(agent);
				
				}
		}
		return newAgents;
	}


	public Agent generateRandomAgent(SpawnPoint spawnPoint) {
		Agent a = new Agent();
		a.position = new Vector2(0,0);
		a.radius = spawnPoint.getRandomRadius();
		
		try {
			Simulator.configureAgent(a,spawnPoint.getMovementType());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return a;
	}

	public void setCurrentStep(int i) {
		this.current_step = i;
		
	}


}
