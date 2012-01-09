package com.pf.simulator.log;

import java.io.Serializable;

import com.pf.math.Vector2;

@SuppressWarnings("serial")
public class AgentPositioningData implements Serializable{

	public int id;
	public Vector2 position;
	public Vector2 velocity;

	public AgentPositioningData(int id, Vector2 position, Vector2 velocity) {
		this.id = id;
		this.position = position;
		this.velocity = velocity;
	}
	
	public AgentPositioningData()
	{
		
	}
}
