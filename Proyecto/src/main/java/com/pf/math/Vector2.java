package com.pf.math;


public class Vector2 implements Cloneable{
	float x;
	float y;
	
	public float getX() {
		return x;
	}
	public void setX(float x) {
		this.x = x;
	}
	public float getY() {
		return y;
	}
	public void setY(float y) {
		this.y = y;
	}
	
	public Vector2() {
		this.x = 0f;
		this.y = 0f;
	}
	
	public Vector2(float x, float y) {
		this.x = x;
		this.y = y;
	}
	public Vector2(Vector2 v)
	{
		this.x = v.getX();
		this.y = v.getY();
	}
	@Override
	public String toString() {
		return "Vector2 [x=" + x + ", y=" + y + "]";
	}
	
	public Vector2 sub(final Vector2 o) {
		return new Vector2(getX() - o.getX(), getY() - o.getY());
	}
	
	public Vector2 add(final Vector2 o) {
		return new Vector2(getX() + o.getX(), getY() + o.getY());
	}
	
	public float distance(Vector2 o) {
		float x = getX() - o.getX();
		float y = getY() - o.getY();
		return (float)Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
	}
	
	public Vector2 scale(final float i) {
		return new Vector2(getX() * i, getY() * i);
	}
	
	public float dot(final Vector2 i) {
		return this.getX() * i.getX() + this.getY() * i.getY();
	}
	
	public Vector2 cross(Vector2 v2 )
    {
        float x1 = getY() * v2.getX() - getX() * v2.getY();
        float y1 = getX() * v2.getY() - getY() * v2.getX();
        
        x = x1;
        y = y1;
        
        return this;
    }
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(x);
		result = prime * result + Float.floatToIntBits(y);
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
		Vector2 other = (Vector2) obj;
		if (Float.floatToIntBits(x) != Float.floatToIntBits(other.x))
			return false;
		if (Float.floatToIntBits(y) != Float.floatToIntBits(other.y))
			return false;
		return true;
	}
	
	public float mod() {
		return (float)Math.sqrt(Math.pow(getX(), 2) + Math.pow(getY(), 2));
	}
	
	public Vector2 normalize() {
		if(mod() <= 1.4E-30)
			return scale(0);
		
		return scale(1/mod());
	}
	
}
