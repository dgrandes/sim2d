package com.pf.movement.qlearning;

public class QLearningState {

	public QLearningState(float slice1, float slice2, float slice3,
			float slice4, float slice5, float slice6, float slice7, float slice8) {
		super();
		this.slice1 = slice1;
		this.slice2 = slice2;
		this.slice3 = slice3;
		this.slice4 = slice4;
		this.slice5 = slice5;
		this.slice6 = slice6;
		this.slice7 = slice7;
		this.slice8 = slice8;
	}


	public float slicesSum() {
		return slice1 + slice2 + slice3 + slice4 * 2 + slice5 * 2 + slice6 + slice7 + slice8; 
	}
	
	@Override
	public String toString() {
		return "[" + slice1 + "," + slice2
				+ "," + slice3 + "," + slice4 + ","
				+ slice5 + "," + slice6 + "," + slice7 + "," + slice8 + "]";
	}
	public float slice1 = 0;
	public float slice2 = 0;
	public float slice3 = 0;
	public float slice4 = 0;
	public float slice5 = 0;
	public float slice6 = 0;
	public float slice7 = 0;
	public float slice8 = 0;
	
	public QLearningState(float qVector[]) {
		slice1 = qVector[0];
		slice2 = qVector[1];
		slice3 = qVector[2];
		slice4 = qVector[3];
		slice5 = qVector[4];
		slice6 = qVector[5];
		slice7 = qVector[6];
		slice8 = qVector[7];
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(slice1);
		result = prime * result + Float.floatToIntBits(slice2);
		result = prime * result + Float.floatToIntBits(slice3);
		result = prime * result + Float.floatToIntBits(slice4);
		result = prime * result + Float.floatToIntBits(slice5);
		result = prime * result + Float.floatToIntBits(slice6);
		result = prime * result + Float.floatToIntBits(slice7);
		result = prime * result + Float.floatToIntBits(slice8);
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof QLearningState))
			return false;
		QLearningState other = (QLearningState) obj;
		if (Float.floatToIntBits(slice1) != Float.floatToIntBits(other.slice1))
			return false;
		if (Float.floatToIntBits(slice2) != Float.floatToIntBits(other.slice2))
			return false;
		if (Float.floatToIntBits(slice3) != Float.floatToIntBits(other.slice3))
			return false;
		if (Float.floatToIntBits(slice4) != Float.floatToIntBits(other.slice4))
			return false;
		if (Float.floatToIntBits(slice5) != Float.floatToIntBits(other.slice5))
			return false;
		if (Float.floatToIntBits(slice6) != Float.floatToIntBits(other.slice6))
			return false;
		if (Float.floatToIntBits(slice7) != Float.floatToIntBits(other.slice7))
			return false;
		if (Float.floatToIntBits(slice8) != Float.floatToIntBits(other.slice8))
			return false;
		return true;
	}

}
