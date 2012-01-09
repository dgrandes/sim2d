package com.pf.simulator.threads;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

import com.pf.math.Vector2;
import com.pf.model.Agent;
import com.pf.model.SpawnPoint;

public class RemovedAgentsSpawnThread implements Callable<List<Agent>> {

	
	private List<SpawnPoint> spawnpoints;
	//SimEnvironment simEnvironment;
	private List<Agent> newAgents;
	private List<Agent> removedAgents;
	private transient Random rand = new Random(System.currentTimeMillis());


	@Override
	public List<Agent> call() throws Exception {

		newAgents = new ArrayList<Agent>();
		
		if(spawnpoints.size() == 0)
			return newAgents;
		
		
		//newAgents.addAll(removedAgents);
		
		for(Agent a : removedAgents)
		{
			SpawnPoint sp = getSpawnPoint(a.getOriginalSpawnId());
			if(sp == null)
			{
				System.out.println("couldnt find "+a.getOriginalSpawnId());
				continue;
			}
			if(sp.isPreservesSpawns())
				a.position = a.originalSpawn;
			else
			{
				a.position = generateRandomPoint(sp, a);
				a.originalSpawn = a.position;
			}
			if(sp.getvMin() != 0 || sp.getvMax() != 0)
				a.setvDesired(getRandomVDesired(sp));
			newAgents.add(a);
		}
		return newAgents;
	}

	private float getRandomVDesired(SpawnPoint s)
	{
		float myrand = rand.nextFloat()*(s.getvMax() - s.getvMin());
		myrand += s.getvMin();		
		return myrand;
	}

	public void setSpawnPoints(List<SpawnPoint> spawnpoints) {
		this.spawnpoints = new ArrayList<SpawnPoint>();
		for(SpawnPoint p : spawnpoints)
			if(p.generatesOnAgentDeletion())
				this.spawnpoints.add(p);
		
		
	}


	public void setRemovedAgents(List<Agent> removedAgents) {
		// TODO Auto-generated method stub
		this.removedAgents = removedAgents;
		
	}


	public SpawnPoint getSpawnPoint(String id)
	{
		for(SpawnPoint s : spawnpoints)
		{
			if(s.getId().equals(id))
				return s;
		}
		return null;
	}
	
	public Vector2 generateRandomPoint(SpawnPoint s, Agent a)
	{
		return s.randomPointWithin(a);
	}



}
