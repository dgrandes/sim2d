package com.pf.model;

import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Random;

import com.pf.math.Vector2;



public class Exit implements EnvironmentObject{

	Polygon body;
	public String id;
	private boolean preservesTargets;
	transient Random rand = new Random(System.currentTimeMillis());
	
	public float distanceTo(Agent agent) {
		
		Point2D agentPosition = new Point2D.Float(agent.position.getX(), agent.position.getY());
		Line2D nearestLine = NearestLineToPoint(agentPosition);
		float distanceToAgent = (float) (nearestLine.ptSegDist(agentPosition) - agent.radius);

		return distanceToAgent;
	}

	private Line2D NearestLineToPoint(Point2D point)
	{
		
		float[] coords = new float[6];
		Point2D initialPoint = new Point2D.Float();
		Point2D startPoint = new Point2D.Float();
		Point2D nextPoint = new Point2D.Float();
		Line2D nearestLine = new Line2D.Float(new Point2D.Float(Float.MAX_VALUE - 10, Float.MAX_VALUE),
				new Point2D.Float(Float.MAX_VALUE-10,Float.MAX_VALUE));
		
		for(PathIterator i = body.getPathIterator(null);!i.isDone();i.next())
		{
			int type = i.currentSegment(coords);
			switch(type)
			{
			
			case PathIterator.SEG_LINETO: 
			{
				nextPoint = new Point2D.Float(coords[0],coords[1]);
				
				Line2D thisLine = new Line2D.Float(startPoint,nextPoint);
				if(thisLine.ptSegDist(point) < nearestLine.ptSegDist(point))
					nearestLine = thisLine;
				startPoint = nextPoint;
				
			}break;
			//When its SEG_CLOSE, coords contains the last before the initial one.
			case PathIterator.SEG_CLOSE:{
				nextPoint = new Point2D.Float(coords[0],coords[1]);
				//Make a line from the last to the first point
				Line2D thisLine = new Line2D.Float(nextPoint,initialPoint);
				if(thisLine.ptSegDist(point) < nearestLine.ptSegDist(point))
					nearestLine = thisLine;
				
				
			}break;
			case PathIterator.SEG_MOVETO:{
				startPoint = new Point2D.Float(coords[0],coords[1]);
				initialPoint = new Point2D.Float(coords[0],coords[1]);
			}break;
			}
		}

		return nearestLine;
	}
	
	public Shape getShape() {
		return body;
	}


	@Override
	public Vector2 randomPointWithin(Agent a) {
		Rectangle2D r = body.getBounds2D();
		int rx;
		int ry;

		while(true)
		{
			rx = rand.nextInt((int)r.getWidth());
			ry = rand.nextInt((int)r.getHeight());
			rx += r.getMinX();
			ry += r.getMinY();
			Point2D pt = new Point2D.Float(rx,ry);
			if(body.contains(pt))
				return new Vector2(rx,ry);
		}
		
	}

	public void setPreservesTargets(boolean preservesTargets) {
		this.preservesTargets = preservesTargets;
	}

	public boolean isPreservesTargets() {
		return preservesTargets;
	}

	@Override
	public boolean contains(Agent agent) {
		Rectangle2D r = body.getBounds2D();
		return r.contains(new Point2D.Float(agent.position.getX(),agent.position.getY()));
	}

	public String getId() {
		// TODO Auto-generated method stub
		return id;
	}
}
