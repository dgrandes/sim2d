package com.pf.model;

import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import com.pf.logging.IBaseLogger;
import com.pf.math.Vector2;
import com.pf.movement.IAgentMovement;


public class Agent implements Cloneable{

	public float mass;
	public float radius;
	public Vector2 position;
	public Vector2 originalSpawn;
	public Vector2 velocity;
	public Vector2 destination;
	public String exitIdentifier;
	public String movementType;
	public String originalSpawnPoint;
	public transient Vector2 granularForce;
	public transient Vector2 contactForce;
	public transient Vector2 desireForce;
	public transient Ellipse2D body;
	public transient IAgentMovement movement;
	public transient IBaseLogger logger = null;
	private transient float vDesired;
	public int id;
	private transient boolean  markForRemoval = false;
	
	@Override
	public String toString() {
		return "Agent [destination=" + destination + ", id=" + id + ", mass="
				+ mass + ", position=" + position + ", radius=" + radius
				+ ", velocity=" + velocity + "]";
	}


	
	public Agent() {
	}
	
	public Agent(int id,float mass, float radius, Vector2 position, Vector2 velocity)
	{
		this.mass = mass;
		this.radius = radius;
		this.position = position;
		originalSpawn = position;
		this.velocity = velocity;
		this.id = id;
		
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result
				+ ((position == null) ? 0 : position.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Agent other = (Agent) obj;
		if (id != other.id)
			return false;
		if (position == null) {
			if (other.position != null)
				return false;
		} else if (!position.equals(other.position))
			return false;
		return true;
	}
	
	public Object clone(){
        Object obj=null;
        try{
            obj=super.clone();
        }catch(CloneNotSupportedException ex){
            System.out.println(" no se puede duplicar");
        }
        return obj;
    }
	
	public Ellipse2D getShape()
	{
	
		body = new Ellipse2D.Float(position.getX() - radius, position.getY() - radius,radius*2, radius*2);
	 
		return body;
	}
	
	public boolean containsPoint(Point2D p)
	{
		body = new Ellipse2D.Float(position.getX(), position.getY(),radius*2, radius*2);
		return body.contains(p);
	}
	
	public void SetForces(Vector2 gf, Vector2 cf, Vector2 df)
	{
		granularForce = gf;
		contactForce = cf;
		desireForce = df;
	}



	public float getRadius() {
		// TODO Auto-generated method stub
		return this.radius;
	}



	public IAgentMovement getMovement() {
		// TODO Auto-generated method stub
		return movement;
	}

	public String getMovementType()
	{
		return movementType;
	}


	public void setBirthSpawnId(String string) {
		// TODO Auto-generated method stub
		this.originalSpawnPoint  = string;
	}
	
	public String getOriginalSpawnId()
	{
		return originalSpawnPoint;
	}



	public void setOriginalSpawnPoint(Vector2 vector2) {
		this.originalSpawn = vector2;
		
	}



	public String getExitIdentifier() {
		// TODO Auto-generated method stub
		if(exitIdentifier != null)
			return exitIdentifier;
		else
			System.out.println("FALTA EL EXIT ID");
		return null;
	}



	public void markForRemoval(boolean mark) {
		markForRemoval = true;
		
	}
	
	public boolean markedForRemoval()
	{
		return markForRemoval;
	}



	public void setvDesired(float vDesired) {
		this.vDesired = vDesired;
	}



	public float getvDesired() {
		return vDesired;
	}
}
