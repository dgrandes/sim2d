package com.pf.simulator.replay;

import java.awt.Color;
import java.awt.geom.Ellipse2D;

public class ReplayShape {

	public Ellipse2D shape;
	public Color color;
	
	public ReplayShape (Ellipse2D s, Color c)
	{
		this.shape = s;
		this.color = c;
	}
}
