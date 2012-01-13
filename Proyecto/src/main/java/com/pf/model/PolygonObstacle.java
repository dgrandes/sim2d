package com.pf.model;

import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Random;

import com.pf.math.Vector2;

public class PolygonObstacle extends Obstacle {

	Polygon body;
	transient Random rand = new Random(System.currentTimeMillis());
	
	public PolygonObstacle()
	{
		
	}
	@Override
	public float distanceTo(Agent agent) {
		
		Point2D agentPosition = agent.getPositionAsPoint2D();
		Line2D nearestLine = NearestLineToPoint(agentPosition);
		
		float distanceToAgent = (float) (nearestLine.ptSegDist(agentPosition) - agent.radius);

		//System.out.println("distance to agent "+distanceToAgent);
		return distanceToAgent;
	}

	public Vector2 closestPointToAgent(Agent a)
	{
		if(contains(a))
			return null;
		Point2D agentPosition = a.getPositionAsPoint2D();
		Line2D nearestLine = NearestLineToPoint(agentPosition);
		Line2D AgentToMidObstacle = new Line2D.Float(agentPosition, getMiddlePoint2D());
		return IntersectionBetweenTwoLines(nearestLine, AgentToMidObstacle);
	}
	

	private Point2D getMiddlePoint2D() {
		Rectangle2D b = body.getBounds2D();
		return new Point2D.Float((float)b.getCenterX(),(float)b.getCenterY());
		
	}
	@Override
	public Shape getShape() {
		return body;
	}

	@Override
	public Vector2 normalTo(Agent agent) {
		Point2D.Float agentPosition = new Point2D.Float(agent.position.getX(), agent.position.getY());
		Line2D.Float nearestLine = (Line2D.Float) NearestLineToPoint(agentPosition);
		Vector2 ret = new Vector2(nearestLine.x2 - nearestLine.x1, nearestLine.y2 - nearestLine.y1);
		
		float normX = ret.getY();
		float normY = -1 * ret.getX();
		Vector2 nynormal1 = new Vector2(normX,normY);
		Point2D.Float normal1 = new Point2D.Float(normX,normY);
	
		Point2D.Float normal2 = new Point2D.Float(-normX, -normY);
		Vector2 mynormal2 = new Vector2(-normX, -normY);
		
		normal1  = new Point2D.Float(normal1.x+agentPosition.x,normal1.y+agentPosition.y);
		normal2  = new Point2D.Float(normal2.x+agentPosition.x,normal2.y+agentPosition.y);
		float dist1, dist2;
		dist1 = (float) nearestLine.ptSegDist(normal1);
		dist2 = (float) nearestLine.ptSegDist(normal2);
		if(dist1 > dist2)
			return nynormal1;
		else
			return mynormal2;
		
	}

	@Override
	public Vector2 tangentTo(Agent agent) {
		Point2D.Float agentPosition = new Point2D.Float(agent.position.getX(), agent.position.getY());
		Line2D.Float nearestLine = (Line2D.Float) NearestLineToPoint(agentPosition);
		return new Vector2(nearestLine.x2 - nearestLine.x1, nearestLine.y2 - nearestLine.y1);
	}

	
	public PolygonObstacle(int[] xpoints, int[] ypoints,int npoints)
	{
		body = new Polygon(xpoints, ypoints, npoints);
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
	@Override
	public boolean contains(Agent agent) {
		return containsPoint(agent.position);
	}
	
	public boolean containsPoint(Vector2 point)
	{
		Rectangle2D r = body.getBounds2D();
		return r.contains(new Point2D.Float(point.getX(),point.getY()));
	}
}