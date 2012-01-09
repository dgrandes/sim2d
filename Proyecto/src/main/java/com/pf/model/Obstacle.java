package com.pf.model;

import com.pf.math.Vector2;


public abstract class Obstacle implements EnvironmentObject{
	
	public abstract float distanceTo(Agent agent);

	public abstract Vector2 tangentTo(Agent agent);
	
	public abstract Vector2 normalTo(Agent agent);
	
	public abstract boolean contains(Agent agent);
	
}