package com.pf.model;

import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Random;

import com.pf.math.Vector2;

public class SpawnPoint implements EnvironmentObject{

	Polygon body;
	private boolean generatesOnAgentDeletion;
	private boolean preservesSpawns;
	private Lapsus lapsusOne;
	private Lapsus lapsusTwo;
	private transient Lapsus currentLapsus;
	private transient long lastRequestTimestamp;
	private transient Random rand = new Random(System.currentTimeMillis());
	
	private float massMin;
	private float massMax;
	private float radiusMin;
	private float radiusMax;
	private float desiredSpeed;
	private float vMin;
	private float vMax;
	private String desiredExitId;
	private String movementType;
	private String id;
	public float distanceTo(Agent agent) {
		return 0;
	}

	@Override
	public Shape getShape() {
		if(body == null)
			body = new Polygon(new int[]{-60,50,50,-60}, new int[]{100,100,0,0}, 4);
		return body;
	}

	public float getChanceToGenerate() {
		Lapsus current= getCurrentLapsus();
		if(current ==  null)
		{
			getCurrentLapsus();
		}
		
		return current.getF();
	}

	public void setGeneratesOnAgentDeletion(boolean generatesOnAgentDeletion) {
		this.generatesOnAgentDeletion = generatesOnAgentDeletion;
	}

	public boolean generatesOnAgentDeletion() {
		return generatesOnAgentDeletion;
	}

	@Override
	public Vector2 randomPointWithin(Agent a) {
		Rectangle2D r = getShape().getBounds2D();
		int rx;
		int ry;
		float padding = (float)Math.ceil(a.radius*1.0);
		while(true)
		{
			float x = (float) (r.getWidth() -2*padding);
			if(x <= 0)
				rx = 0;
			else
				rx = rand.nextInt((int)(r.getWidth()-2*padding));
			float y = (float) (r.getHeight() -2*padding);
			if( y <= 0)
				ry = 0;
			else
				ry = rand.nextInt((int)(r.getHeight()-2*padding));

			rx += r.getMinX()+padding;
			ry += r.getMinY()+padding;
			
			Point2D pt = new Point2D.Float(rx,ry);
			if(body.contains(pt))
			{
				Vector2 resp = new Vector2(rx,ry); 
				return resp;
			}
		}
		
		
	}
	
	public Lapsus getCurrentLapsus() {
		if(currentLapsus == null) {
			lastRequestTimestamp = System.currentTimeMillis();
			currentLapsus = lapsusOne;
		} else if (System.currentTimeMillis() - lastRequestTimestamp > currentLapsus.getTime()) {
			if(currentLapsus == lapsusOne) {
				currentLapsus = lapsusTwo;
			} else {
				currentLapsus = lapsusOne;
			}
		}
		
		return currentLapsus;
	}

	public void setMassMin(float massMin) {
		this.massMin = massMin;
	}

	public float getMassMin() {
		return massMin;
	}

	public void setRadiusMin(float radiusMin) {
		this.radiusMin = radiusMin;
	}

	public float getRadiusMin() {
		return radiusMin;
	}

	public void setMassMax(float massMax) {
		this.massMax = massMax;
	}

	public float getMassMax() {
		return massMax;
	}

	public void setRadiusMax(float radiusMax) {
		this.radiusMax = radiusMax;
	}

	public float getRadiusMax() {
		return radiusMax;
	}

	public void setDesiredSpeed(float desiredSpeed) {
		this.desiredSpeed = desiredSpeed;
	}

	public float getDesiredSpeed() {
		return desiredSpeed;
	}

	public void setDesiredExitId(String desiredExitId) {
		this.desiredExitId = desiredExitId;
	}

	public String getDesiredExitId() {
		return desiredExitId;
	}

	public void setPreservesSpawns(boolean preservesSpawns) {
		this.preservesSpawns = preservesSpawns;
	}

	public boolean isPreservesSpawns() {
		return preservesSpawns;
	}

	@Override
	public boolean contains(Agent agent) {
		Rectangle2D r = getShape().getBounds2D();
		return r.contains(new Point2D.Float(agent.position.getX(),agent.position.getY()));
	}

	public void setMovementType(String movementType) {
		this.movementType = movementType;
	}

	public String getMovementType() {
		return movementType;
	}

	public float getRandomRadius() {
		return rand.nextFloat()*(radiusMax - radiusMin)+radiusMin;
		
	}

	public void setLapsusOne(Lapsus one) {
		this.lapsusOne = one;
		
	}

	public void setLapsusTwo(Lapsus two) {
		this.lapsusTwo = two;
		
	}
	
	public void init(String id, boolean preservesSpawns, boolean generatesOnDeletion,
			String movType, String exit)
	{
		Lapsus one = new Lapsus();
		one.setF(0.5f);
		one.setTime(10);
		Lapsus two = new Lapsus();
		two.setF(0.5f);
		two.setTime(10);
		setId(id);
		setMassMin(1);
		setMassMax(10);
		setRadiusMin(1);
		setRadiusMax(10);
		setPreservesSpawns(preservesSpawns);
		setMovementType(movType);
		setGeneratesOnAgentDeletion(generatesOnDeletion);
		setLapsusOne(one);
		setLapsusTwo(two);
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		if(id ==null)
			id = "spawn";
		return id;
	}

	public void setvMin(float vMin) {
		this.vMin = vMin;
	}

	public float getvMin() {
		return vMin;
	}

	public void setvMax(float vMax) {
		this.vMax = vMax;
	}

	public float getvMax() {
		return vMax;
	}


}
