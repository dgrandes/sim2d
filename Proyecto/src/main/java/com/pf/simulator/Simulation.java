package com.pf.simulator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.pf.manager.AgentManager;
import com.pf.model.Agent;
import com.pf.model.Exit;
import com.pf.model.SpawnPoint;
import com.pf.simulator.threads.AgentThread;
import com.pf.simulator.threads.ExitThread;
import com.pf.simulator.threads.GenerateAgentsSpawnThread;
import com.pf.simulator.threads.RemovedAgentsSpawnThread;

public class Simulation {


	private int currentStep;

	private  Long frameskip = 1000L;
	private Long recframeskip = 1000L;
	private boolean enabled = true;
	private boolean shouldSplitAgents = true;

	
	
	//Callables are the ones who are going to do the work
	private List<Callable<List<Agent>>> agentCallables = new ArrayList<Callable<List<Agent>>>();
	private List<Callable<List<Agent>>> spawnCallables = new ArrayList<Callable<List<Agent>>>();
	private List<Callable<List<Agent>>>  exitCallables = new ArrayList<Callable<List<Agent>>>();
	
	//Futures will be the ones to receive the HashMaps from Callables
	private Set<Future<List<Agent>>> agentFutureSet = new HashSet<Future<List<Agent>>>();
	private Set<Future<List<Agent>>> spawnFutureSet = new HashSet<Future<List<Agent>>>();
	private Set<Future<List<Agent>>> exitFutureSet = new HashSet<Future<List<Agent>>>();
	
	private RemovedAgentsSpawnThread spawnRemovedAgentsThread = new RemovedAgentsSpawnThread();
	private GenerateAgentsSpawnThread generateAgentsSpawnThread = new GenerateAgentsSpawnThread();
	private ExecutorService pool = Executors.newCachedThreadPool();
	
	
	SimEnvironment simEnvironment;
	
	//Number of agents, exits or spawn points per thread, 0 means all
	float agentloadFactor = 1;
	float exitsloadfactor = 0;
	float spawnloadfactor = 0;
	
	private List<Agent> newAgents = new ArrayList<Agent>();
	private List<Agent> removedAgents = new ArrayList<Agent>();
	
	AgentManager manager;
	
	public String envFilePath = "";
	public String agentsFilePath  = "";
	
	public void reset()
	{
		//enabled = false;
		shouldSplitAgents = true;
		currentStep = 0;
		cleanUp();

	}
	
	public Long getRecframeskip() {
		return recframeskip;
	}

	public void setRecframeskip(Long recframeskip) {
		this.recframeskip = recframeskip;
	}

	public  Long getFrameskip() {
		return frameskip;
	}

	public void setFrameskip(Long frameskip2) {
		this.frameskip = frameskip2;
	}

	public int getCurrentStep() {
		return currentStep;
	}

	public void setCurrentStep(int currentStep) {
		this.currentStep = currentStep;
	}

	public Simulation() {        
		currentStep = 0;
		
	}
	
	//Create the threads responsible for exit and spawning, called from
	//GuiFrame.environmentLoaded.
	public void createEnvThreads()
	{
		simEnvironment =  Simulator.getSimEnvironment();

		try {
			 manager =  simEnvironment.getAgentManager();
			createExitThreads((List<Exit>) simEnvironment.getEnvironment().getExits());
			createSpawnThreads((List<SpawnPoint>) simEnvironment.getEnvironment().getSpawnPoints());
			createAgentThreads(simEnvironment.getAgentManager().getAgents());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		
		}
	}
	
	public void createAgentThreads(List<Agent> agents)
	{
		agentCallables.clear();
		int qty = (int)(agents.size()/agentloadFactor) +1;
		for (int i = 0; i < qty; i++)
		{
			agentCallables.add(new AgentThread());
		}
	}
	
	public void createExitThreads(List<Exit> exits)
	{
		int qty;
		exitCallables.clear();
		if (exitsloadfactor == 0)
			qty = 1;
		else
			qty = (int)(exits.size() / exitsloadfactor)+1;
		

		float exitsPerThread = exits.size() / qty;
		for (int i  = 0; i< qty; i++)
		{
				
			int max = (int)Math.min(exits.size(), (i+1)*exitsPerThread);
			Callable<List<Agent>> c = new ExitThread();
			((ExitThread) c).assignExits( exits.subList((int)(i*exitsPerThread), max));
			exitCallables.add(c);
		
		}
		
	}
	
	public void createSpawnThreads(List<SpawnPoint> spawnpoints)
	{
/*		int qty;
		startime = System.currentTimeMillis();
		spawnCallables.clear();
		
		if (spawnloadfactor == 0)
			qty = 1;
		else
			qty = (int)(spawnpoints.size() / spawnloadfactor)+1;
		
		
		float spawnPerThread = spawnpoints.size() / qty;
		for (int i  = 0; i< qty; i++)
		{
			Callable<List<Agent>> c = new GenerateAgentsSpawnThread();
			int max = (int)Math.min(spawnpoints.size(), (i+1)*spawnPerThread);
			((GenerateAgentsSpawnThread)c).assignSpawnPoints( spawnpoints.subList((int)(i*spawnPerThread), max));
						
			spawnCallables.add(c);
		}
		
		//Create the thread responsible for recreating the deleted agents
		//from the spawnpoints thata generateAgentsOnDeletion
		
		*/
		generateAgentsSpawnThread.assignSpawnPoints(spawnpoints);
		spawnRemovedAgentsThread.setSpawnPoints(spawnpoints);
		
		
	}
	
	public void splitAgentsInThreads()
	{

		shouldSplitAgents = false;
		List<Agent> agents = null;
		try {
			agents = simEnvironment.getAgentManager().getAgents();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int agentsRemaining = agents.size();
		int currentAgent = 0; 
		for (int i = 0; i < agentCallables.size() ; i++)
		{
			//int max = (int)Math.min(agents.size(), (i+1)*agentloadFactor);
			int offset = (int) Math.ceil((double)agentsRemaining /(double)(agentCallables.size()));
			((AgentThread)agentCallables.get(i)).assignAgents( agents.subList(currentAgent,currentAgent+ offset));
			currentAgent += offset;
			agentsRemaining -= offset;
		}
	}

	public List<Agent> getRemovedAgents()
	{
		return Collections.unmodifiableList(new ArrayList<Agent>(removedAgents));
	}
	
	private void cleanUp()
	{
		agentCallables.clear();
		exitCallables.clear();
		spawnCallables.clear();
		agentFutureSet.clear();
		exitFutureSet.clear();
		spawnFutureSet.clear();
	//	pool.shutdown();
		simEnvironment.resetEnvironment();
	}
	public boolean simulateStep() throws Exception{
		
	
		if(!enabled)
		{
			cleanUp();
			return true;
		}
		
		if(simEnvironment.hasEnd() && currentStep >= simEnvironment.getSteps())
			return true;
		
		//AgentManager should backup the agents
		
		manager.backupAgents(currentStep);
		
		
		//Splitting all the agents among the threads, they will modify agents from AgentManager...
		if(shouldSplitAgents)
			splitAgentsInThreads();
		
		agentFutureSet.clear();
		exitFutureSet.clear();
		spawnFutureSet.clear();
		//Create the callables, add them to the pool and execute them and attach them to the Futures
		
		for(Callable<List<Agent>> c : agentCallables)
		{
			Future<List<Agent>> future = pool.submit(c);
			agentFutureSet.add(future);
		}
		
		for(Callable<List<Agent>>  c : exitCallables)
		{
			Future<List<Agent>> future = pool.submit(c);
			exitFutureSet.add(future);
		}
		
		for(Callable<List<Agent>> c : spawnCallables)
		{
			
			Future<List<Agent>> future = pool.submit(c);
			spawnFutureSet.add(future);
		}
		
		spawnRemovedAgentsThread.setRemovedAgents(removedAgents);
		generateAgentsSpawnThread.setCurrentStep(currentStep);
		Future<List<Agent>> spawnRemovedAgents = pool.submit(spawnRemovedAgentsThread);
		Future<List<Agent>> spawnGeneratedAgents = pool.submit(generateAgentsSpawnThread);
		//Clear the interrupt status	
		if(Thread.currentThread().isInterrupted())
			Thread.interrupted();	
		


	
		//These will hold the processing results of the callables
		ArrayList<Agent>  newRemovedAgents = new ArrayList<Agent>();

		
		for(Future<List<Agent>> f : agentFutureSet)
			f.get();
		
		for(Future<List<Agent>> f : exitFutureSet)
			newRemovedAgents.addAll(f.get());
		
		for(Future<List<Agent>>  f : spawnFutureSet)
			newAgents.addAll(f.get());
		
		newAgents.addAll(spawnRemovedAgents.get());
		newAgents.addAll(spawnGeneratedAgents.get());
		
		Simulator.configureAgents(newAgents);
//		//Save the removed agents for the next cycle, spawn uses them
		removedAgents.clear();
		removedAgents.addAll(newRemovedAgents);
		
		//Remove the agents.
		if(removedAgents.size() != 0 )
		{
			for (Agent a : removedAgents)
			{
				if(!manager.removeAgent(a))
					System.out.println("No se pudo remover "+a);
			}
		}
		
	
	
		//Add the new agents
		if(newAgents.size() > 0)
		{
			manager.addAgents(newAgents);
			shouldSplitAgents = true;
		}
		
		newAgents.clear();
		//Render if necessary
		if(currentStep % recframeskip == 0 )
		{
			
			Simulator.logState();
			//StateLog.get().recordState(manager.getAgents(), currentStep * simEnvironment.getEnvironment().deltaTime);
		}
		if(currentStep % frameskip == 0) {
			
			//If we are not finished drawing, skip this draw...
	//		System.out.println(currentStep);
			//We have to create another agent backup, because the drawing will occur at the same time as the modification
			//of agents, updated and cloned. Thats why we backup another list now, so that no concurrent modification occurs.
			if(!Simulator.getGuiFrame().isPanelDrawing())
			{
				manager.backupDrawAgents();
//				double diff = (double)((System.nanoTime()) - startime )/1000000000;
//				System.out.println("Step took  "+(diff));
//				startime = System.nanoTime();
				Simulator.getGuiFrame().UpdateSimPanel();
			}
		}
		
		currentStep++;

		return false;
	}

	public boolean simulate() {
		try {
			while(!simulateStep() && enabled);
				return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		
	}
	
	public boolean simulateNSteps(int n) {
		SimEnvironment simEnvironment = Simulator.getSimEnvironment();
		try {
			while(!simulateStep() && (n--) >= 0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		if(simEnvironment.hasEnd() && currentStep >= simEnvironment.getSteps())
			return true;
		return false;
	}
	
	
	public void close()
	{
		pool.shutdown();
	}
	
	
}
