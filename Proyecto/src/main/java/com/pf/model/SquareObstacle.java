package com.pf.model;

import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import com.pf.math.Vector2;

@Deprecated
public class SquareObstacle extends Obstacle{

	private Line2D.Float line0;
	private Line2D.Float line1;
	private Line2D.Float line2;
	private Line2D.Float line3;
	private Rectangle2D.Float body;
	
	public float distanceTo(Agent agent) {
		Point2D agentPosition = new Point2D.Float(agent.position.getX(), agent.position.getY());

		float distanceToSelf = (float)Math.min(
							Math.min(line0.ptSegDist(agentPosition), line1.ptSegDist(agentPosition)),
							Math.min(line2.ptSegDist(agentPosition), line3.ptSegDist(agentPosition)));

		
		return distanceToSelf - agent.radius;
	}

	public Vector2 getP0() {
		return new Vector2(line0.x1, line0.y1);
	}
	
	public Vector2 getP1() {
		return new Vector2(line1.x1, line1.y1);		
	}
	
	public Vector2 getP2() {
		return new Vector2(line2.x1, line2.y1);	
	}
	
	public Vector2 getP3() {
		return new Vector2(line3.x1, line3.y1);
	}
	
	public SquareObstacle() {
		
	}
	
	public SquareObstacle(Vector2 p0, Vector2 p1, Vector2 p2, Vector2 p3) {
		line0 = new Line2D.Float(p0.getX(), p0.getY(), p1.getX(), p1.getY());
		line1 = new Line2D.Float(p1.getX(), p1.getY(), p2.getX(), p2.getY());
		line2 = new Line2D.Float(p2.getX(), p2.getY(), p3.getX(), p3.getY());
		line3 = new Line2D.Float(p3.getX(), p3.getY(), p0.getX(), p0.getY());
	
	}


	@Override
	public Vector2 normalTo(Agent agent) {
		Vector2 ret = tangentTo(agent);
		
		float normX = ret.getY();
		float normY = -1 * ret.getX();
		
		return new Vector2(normX, normY);
	}

	@Override
	public Vector2 tangentTo(Agent agent) {
		Line2D.Float nearestLine = line0;
		Point2D.Float agentPosition = new Point2D.Float(agent.position.getX(), agent.position.getY());
		
		if(line1.ptSegDist(agentPosition) < nearestLine.ptSegDist(agentPosition)) {
			nearestLine = line1;
		}
		
		if(line2.ptSegDist(agentPosition) < nearestLine.ptSegDist(agentPosition)) {
			nearestLine = line2;
		}
		
		if(line3.ptSegDist(agentPosition) < nearestLine.ptSegDist(agentPosition)) {
			nearestLine = line3;
		}
	
		return new Vector2(nearestLine.x2 - nearestLine.x1, nearestLine.y2 - nearestLine.y1);

	
		
	}

	@Override
	public Shape getShape() {
		if(body == null )
		{
			float width = Math.max(Math.max(line0.x2-line0.x1, line1.x2 - line1.x1),
							Math.max(line2.x2-line2.x1, line3.x2 - line3.x1));
			float height = Math.max(Math.max(line0.y2-line0.y1, line1.y2 - line1.y1),
					Math.max(line2.y2-line2.y1, line3.y2 - line3.y1));
			float x0 = Math.min(Math.min(line0.x1, line2.x1),
					Math.min(line3.x1, line1.x1));
			float y0 = Math.min(Math.min(line0.y1, line2.y1),
					Math.min(line3.y1, line1.y1));
			body = new Rectangle2D.Float(x0,y0,width,height);
		}
		return body;
	}
	
	@Override
	public Vector2 randomPointWithin(Agent a) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean contains(Agent agent) {
		// TODO Auto-generated method stub
		return false;
	}
	
	
}