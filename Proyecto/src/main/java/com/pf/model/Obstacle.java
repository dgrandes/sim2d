package com.pf.model;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import com.pf.math.Vector2;


public abstract class Obstacle implements EnvironmentObject{
	
	public abstract float distanceTo(Agent agent);

	public abstract Vector2 tangentTo(Agent agent);
	
	public abstract Vector2 normalTo(Agent agent);
	
	public abstract boolean contains(Agent agent);
	
	public static Vector2 IntersectionBetweenTwoLines(Line2D a, Line2D b)
	{
		Point2D start1, end1, start2, end2;
		start1 = a.getP1();
		end1 = a.getP2();
		start2 = b.getP1();
		end2 = b.getP2();
		
		float denom = (float) (((end1.getX() - start1.getX()) * (end2.getY() - start2.getY())) - ((end1.getY() - start1.getY())*(end2.getX()- start2.getX())));
		        

        //  AB & CD are parallel 
        if (denom == 0)
        	return null;

		 float numer = (float) (((start1.getY() - start2.getY()) * (end2.getX() - start2.getX())) - ((start1.getX() - start2.getX()) * (end2.getY() - start2.getY())));

        float r = numer / denom;

        float numer2 = (float) (((start1.getY() - start2.getY()) * (end1.getX() - start1.getX())) - ((start1.getX() - start2.getX()) * (end1.getY() - start1.getY())));

        float s = numer2 / denom;

        if ((r < 0 || r > 1) || (s < 0 || s > 1))
        		return null;

        // Find intersection point
        Vector2 result = new Vector2(
        		(float)(start1.getX() + (r * (end1.getX() - start1.getX()))),
        		(float)(start1.getY() + (r * (end1.getY() - start1.getY()))));

        return result;
		 
	}
	
}