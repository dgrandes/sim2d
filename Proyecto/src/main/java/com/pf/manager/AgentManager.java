package com.pf.manager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.google.gson.Gson;
import com.pf.model.Agent;

public class AgentManager extends Manager<AgentManager> {

	// private List<Agent> agents;
	private HashMap<Integer, Agent> agents;
	transient private HashMap<Integer, Boolean> debugAgents = new HashMap<Integer, Boolean>();

	private List<Agent> updatedAgents = new ArrayList<Agent>();
	private List<Agent> clonedAgents = new ArrayList<Agent>();
	private List<Agent> drawingAgents;

	private int backupStep = -1;

	public float A = 2000f;
	public float B = 0.08f;
	public float Kn = 100000f;
	public float Kt = 200000f;
	public float vDesired = 2.4f;
	public float tao = 0.5f;
	
	private int assignedId = 0;

	public List<Agent> getAgents() {
		return updatedAgents;
	}

	public List<Agent> getAgentsForDrawing() {
		return drawingAgents;
	}

	// Adds new agents to this list.
	public void addAgents(List<Agent> agents) {

		updatedAgents.addAll(agents);
	}

	public AgentManager() {

	}

	public AgentManager(String filename) throws Exception {
		try{
			initWith(filename);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public void addAgent(Agent a) {
		agents.put(a.id, a);
	}

	public static AgentManager loadFromFile(String filename) throws Exception{
		StringBuilder agentsJSON = new StringBuilder();
		String read;

		try {
			FileReader fstream;
			fstream = new FileReader(filename);
			BufferedReader in = new BufferedReader(fstream);
			while ((read = in.readLine()) != null) {
				agentsJSON.append(read);
			}
			AgentManager agentManager = new Gson().fromJson(
					agentsJSON.toString(), AgentManager.class);
			return agentManager;
		} catch (Exception e) {
			System.err.println("Agent [LoadFromFile] : " + e.getMessage());
			throw new Exception(e);
		}

		
	}

	@Override
	public void saveToFile(String filename) {
		String agentsJSON = new Gson().toJson(this);

		try {
			FileWriter fstream = new FileWriter(filename);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(agentsJSON);
			out.close();
		} catch (Exception e) {
			System.err.println("AgentManager [saveToFile] : " + e.getMessage());
		}
	}

	public void initWith(String filename) throws Exception {
		AgentManager agentManager = loadFromFile(filename);
		if(agentManager.vDesired != 0.0)
			this.vDesired = agentManager.vDesired;
		if(agentManager.A != 0.0)
			this.A = agentManager.A;
		if(agentManager.B != 0.0)
			this.B =agentManager.B;
		if(agentManager.Kn != 0.0)
			this.Kn =agentManager.Kn;
		if(agentManager.Kt != 0.0)
			this.Kt =agentManager.Kt;
		if(agentManager.tao != 0.0)
			this.tao =agentManager.tao;
		
		this.updatedAgents = new ArrayList<Agent>(agentManager.agents.values());
		backupAgents(-1);
		drawingAgents = new ArrayList<Agent>(updatedAgents);
		drawingAgents = Collections.synchronizedList(drawingAgents);
	}

	public void setAgentDebugMode(int id) {

		if (debugAgents.containsKey(id))
			debugAgents.remove(id);
		else
			debugAgents.put(id, true);

	}

	public boolean isAgentDebugged(int id) {
		if (debugAgents.containsKey(id))
			return true;
		else
			return false;
	}

	public Agent getAgent(int id) {
		return agents.get(id);
	}

	public int getMaxId() {
		int id = 0;

		for (Agent a : updatedAgents) {
			if (a.id > id) {
				id = a.id;
			}
		}

		return id;
	}

	public void InitReplay() {

	}

	public synchronized void backupAgents(int step) {

		if (backupStep < step) {
			clonedAgents.clear();

			for (Agent a : updatedAgents)
				clonedAgents.add((Agent) a.clone());

			backupStep = step;
		}

	}

	public synchronized void backupDrawAgents() {

		drawingAgents.clear();

		for (Agent a : updatedAgents)
			drawingAgents.add((Agent) a.clone());

	}

	// threads should NOT modify this.
	public List<Agent> getBackedUpAgents() {

		return clonedAgents;
	}

	public synchronized boolean removeAgent(Agent agent) {

		for (Agent a : updatedAgents) {
			if (agent.id == a.id) {
				updatedAgents.remove(a);
				//If you are debugging this agent, remove it as well.
				if (debugAgents.containsKey(a.id))
					debugAgents.remove(a.id);
				return true;
			}
		}
		return false;
	}

	public int getNextId() {
		// TODO Auto-generated method stub
		if(assignedId == 0)
			assignedId = getMaxId();
		return assignedId++;
	}
}
