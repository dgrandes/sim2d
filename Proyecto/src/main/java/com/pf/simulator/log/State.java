package com.pf.simulator.log;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.pf.math.Vector2;
import com.pf.model.Agent;


@SuppressWarnings("serial")
public class State implements Serializable {

	public Float time;
	public Map<Integer, AgentPositioningData> agentsState;
	
	public State() {
		agentsState = new HashMap<Integer, AgentPositioningData>();
	}
	
	public State(List<Agent> newAgents, float time) {
		this.time = time;
		
		agentsState = new HashMap<Integer, AgentPositioningData>();
		
		for (Agent a : newAgents) {
			agentsState.put(a.id, new AgentPositioningData(a.id, a.position, a.velocity));
		}
	}
	
	public String StateToString() {
		StringBuffer buffer = new StringBuffer();
		
		for (int agentKey : agentsState.keySet()) {
			AgentPositioningData agent = agentsState.get(agentKey);
			String state = String.format("%d|%d|%f|%f|%f|%f\n", time.intValue(), agentKey, 
					agent.position.getX(), agent.position.getY(), agent.velocity.getX(), agent.velocity.getY()); 
			buffer.append(state);
		}
		return buffer.toString();
	}
	
	//Returns an Array of tuples (d,d,f,f,f,f)
	public ArrayList<ArrayList<Object>> getStateParameters()
	{
		ArrayList<ArrayList<Object>> array = new ArrayList<ArrayList<Object>>();
		for (int agentKey : agentsState.keySet()) {
			ArrayList<Object> elem = new ArrayList<Object>(); 
			AgentPositioningData agent = agentsState.get(agentKey);
			elem.add(time.intValue());
			elem.add(agentKey);
			elem.add(agent.position.getX());
			elem.add(agent.position.getY());
			elem.add( agent.velocity.getX());
			elem.add(agent.velocity.getY());
	    	array.add(elem);
		}
		return array;
	}
	
	static public State StringToState(String stateString) {
		State state = new State();

		state.agentsState = new HashMap<Integer, AgentPositioningData>();
		
		Pattern pattern = Pattern.compile("([[+-]?\\d\\.]+)\\|([[+-]?\\d\\.]+)\\|([[+-]?\\d\\.]+)\\|([[+-]?\\d\\.]+)\\|([[+-]?\\d\\.]+)\\|([[+-]?\\d\\.]+)");
		Matcher m = pattern.matcher(stateString);
					
		while(m.find()) {
			state.time = Float.parseFloat(m.group(1));
			AgentPositioningData agent = new AgentPositioningData();
			agent.id = Integer.parseInt(m.group(2));
			agent.position = new Vector2(Float.parseFloat(m.group(3)), Float.parseFloat(m.group(4)));
			agent.velocity = new Vector2(Float.parseFloat(m.group(5)), Float.parseFloat(m.group(6)));
			state.agentsState.put(agent.id, agent);
		}
		
		
		return state;
	}
}
