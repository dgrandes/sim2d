package com.pf.model;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Random;

import com.pf.math.Vector2;

public class CircleObstacle extends Obstacle{

	private float radius;
	private Vector2 center;
	transient private Ellipse2D body; 
	transient Random rand = new Random(System.currentTimeMillis());
	
	public float distanceTo(Agent agent) {
		return center.distance(agent.position) - (radius + agent.radius);
	}

	public CircleObstacle() {
		
	}
	
	public CircleObstacle(Vector2 center, float radius) {
		this.center = center;
		this.radius = radius;
		
	}


	@Override
	public Vector2 normalTo(Agent agent) {
		return agent.position.sub(this.center);
	}

	@Override
	public Vector2 tangentTo(Agent agent) {
		Vector2 tangent = this.normalTo(agent);
		tangent.setX(tangent.getX() * -1);
		return tangent;
	}

	@Override
	public Shape getShape() {
		if(body == null)
		{
			
			body = new Ellipse2D.Float(center.getX() - radius , center.getY() -radius, radius*2, radius*2);
		}
		return body;
	}

	@Override
	public Vector2 randomPointWithin(Agent a) {
		Rectangle2D r = body.getBounds2D();
		int rx;
		int ry;

		while(true)
		{
			rx = rand.nextInt((int)(r.getWidth()-a.radius));
			ry = rand.nextInt((int)(r.getHeight()-a.radius));
			rx += r.getMinX()+a.radius;
			ry += r.getMinY()+a.radius;
			Point2D pt = new Point2D.Float(rx,ry);
			if(body.contains(pt))
				return new Vector2(rx,ry);
		}
		
		
	}

	public String toString()
	{
		return "radius:"+radius+"; center:"+center;
	}

	@Override
	public boolean contains(Agent agent) {
		// TODO Auto-generated method stub
		return false;
	}



	
	
}