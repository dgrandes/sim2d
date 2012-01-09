package com.pf.simulator;

import java.awt.geom.Rectangle2D;
import java.util.List;

import com.pf.logging.QLearningLogger;
import com.pf.manager.AgentManager;
import com.pf.manager.EnvironmentManager;
import com.pf.model.Agent;
import com.pf.model.Exit;
import com.pf.model.SpawnPoint;
import com.pf.movement.IAgentMovement;
import com.pf.movement.IdleMovement;
import com.pf.movement.SocialForceMovement;
import com.pf.movement.TrivialMovement;
import com.pf.movement.qlearning.QLearningMovement;
import com.pf.movement.qlearning.interpreter.QInterpreterMovement;

public class SimEnvironment {

	private AgentManager agentManager;
	private  EnvironmentManager environment;
	private IAgentMovement movement;
	private Rectangle2D.Float bounds;
	private int steps;

	public IAgentMovement getMovement() {
		return movement;
	}

	public void setMovement(IAgentMovement movement) {
		this.movement = movement;
	}

	public AgentManager getAgentManager() throws Exception {
		if(agentManager == null )
			throw new Exception("Agent Manager is null");
		else
			return agentManager;
	}

	public void setAgentManager(AgentManager agentManager) {
		this.agentManager = agentManager;
	}

	public EnvironmentManager getEnvironment() throws Exception{
		if(environment == null )
			throw new Exception("Environment is null");
		else
			return environment;
	}

	public Rectangle2D getBounds(int padding)
	{
		if(bounds  != null )
			return bounds;
		float minx = Float.MAX_VALUE;
		float miny = Float.MAX_VALUE;
		float maxx = Float.MIN_VALUE;
		float maxy = Float.MIN_VALUE;
		for( SpawnPoint p : environment.getSpawnPoints())
		{
			Rectangle2D b = p.getShape().getBounds2D();
			if(b.getMinX() < minx)
				minx = (float) b.getMinX();
			if(b.getMinY() < miny)
				miny = (float) b.getMinY();
			if(b.getMaxX() > maxx)
				maxx = (float)b.getMaxX();
			if(b.getMaxY() > maxy)
				maxy = (float)b.getMaxY();
		}
		for (Exit p : environment.getExits())
		{
			Rectangle2D b = p.getShape().getBounds2D();
			if(b.getMinX() < minx)
				minx = (float) b.getMinX();
			if(b.getMinY() < miny)
				miny = (float) b.getMinY();
			if(b.getMaxX() > maxx)
				maxx = (float)b.getMaxX();
			if(b.getMaxY() > maxy)
				maxy = (float)b.getMaxY();
		}
		float pad2 = padding/2.0f;
		bounds = new Rectangle2D.Float(minx - pad2, miny - pad2, (maxx - minx)+padding, (maxy- miny)+padding);
		return bounds;
	}
	public void setEnvironment(EnvironmentManager environment) {
		this.environment = environment;
	}

	public int getSteps() {
		return steps;
	}

	public void setSteps(int steps) {
		this.steps = steps;
	}

	public void saveAgentsToFile(String absolutePath) {
		agentManager.saveToFile(absolutePath);
	}

	public void saveEnvironmentToFile(String absolutePath) {
		environment.saveToFile(absolutePath);
	}
	
	public void configureAgentsFromFile(String filename) throws Exception{
		agentManager = new AgentManager(filename);
		System.out.println("Loading Agents...");
		configureAgents(agentManager.getAgents());
		
	}
	
	public void configureAgents(List<Agent> agents) throws Exception
	{
		for (Agent agent : agents) {
			
			agent.originalSpawn = agent.position;
			int currentStep = Simulator.getSimulation().getCurrentStep();
			if(agent.id < 0)
				agent.id = Simulator.getSimEnvironment().getAgentManager().getNextId();
			if(agent.getvDesired() == 0)
				agent.setvDesired(agentManager.vDesired);
			configureAgentsMovement(agent,agent.movementType);
			
			
			if(agent.exitIdentifier != null) {
				for (Exit anExit : environment.getExits()) {
					if(anExit.id != null && anExit.id.equals(agent.exitIdentifier)) {
							if(!anExit.isPreservesTargets())
								agent.destination = anExit.randomPointWithin(agent);
							break;
					}
				}
			}
			Simulator.registerNewAgent(agent, currentStep * getEnvironment().deltaTime);
		}

	}
	public void configureEnvironmentFromFile(String filename) throws Exception {
		QLearningMovement.QLearningEnabled = false;
		environment = new EnvironmentManager(filename);
		prepareAgents(agentManager);
		
	}
	
	public void resetEnvironment()
	{
		environment = null;
		agentManager = null;
		movement = null;
		bounds = null;
	}
	
	public SimEnvironment(String filename) throws Exception {
	/*	configureAgentsFromFile(filename + "-Agent");
		configureEnvironmentFromFile(filename + "-Environment");
		*/
		
		configure(filename);
	}

	public SimEnvironment()
	{
		steps = -1;
	//	movement = new SocialForceMovement();
	}
	
	public void configure(String filename) throws Exception
	{
		resetEnvironment();
		steps = -1;
		configureAgentsFromFile(filename + "-Agent");
		configureEnvironmentFromFile(filename + "-Environment");
	}
	
	public boolean isEnvironmentReady()
	{
		return environment != null && agentManager != null;
	}
	
	private void prepareAgents(AgentManager agentManager) {
		//System.out.println("Preparing Agents...");
		if(environment == null || agentManager == null)
			return;

		try {
			configureAgents(agentManager.getAgents());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public boolean hasEnd() {
		return false;
	}

	public void configureAgentsMovement(Agent a, String movementType) throws Exception {
		if (movementType.equals("idle")) {
			a.movement = new IdleMovement();
		} else if (movementType.equals("social")) {
			a.movement = new SocialForceMovement();
		} else if (movementType.equals("q-learning")) {
			if(movement == null){
				a.movement = (IAgentMovement)new QLearningMovement();
				a.logger = new QLearningLogger(a);
				((QLearningMovement)a.movement).agentOrigin = a.originalSpawn;
				((QLearningMovement)a.movement).mainActor = true;
				Simulator.addLog(a.logger);
				movement = a.movement;
			}
		} else if (movementType.equals("trivial")) {
			a.movement = new TrivialMovement();
		} else if (movementType.equals("q-interpreter")) {
			if(movement == null)
			{
				a.movement = new QInterpreterMovement();
				
				a.logger = new QLearningLogger(a);
				((QInterpreterMovement)a.movement).agentOrigin = a.originalSpawn;
				((QInterpreterMovement)a.movement).mainActor = true;
				Simulator.addLog(a.logger);	
				movement = a.movement;
			}else
			{	
				if(a.movement == null)
				{
					QInterpreterMovement mymove = (QInterpreterMovement)movement;
					a.logger = null;
					a.movement = (IAgentMovement) mymove.clone();
					((QInterpreterMovement)a.movement).agentOrigin = a.originalSpawn;
					((QInterpreterMovement)a.movement).mainActor = false;
					
				}
				
			}
			
		}
		
	}
	
	


}
