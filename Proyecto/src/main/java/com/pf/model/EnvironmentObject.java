package com.pf.model;

import com.pf.math.Vector2;
import java.awt.Shape;

public interface EnvironmentObject {

	public float distanceTo(Agent agent);
	
	public abstract Shape getShape();

	public Vector2 randomPointWithin(Agent a);
	public abstract boolean contains(Agent agent);
}
