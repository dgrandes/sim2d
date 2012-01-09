package com.pf.util;

import java.util.Random;

public class Util {

	static Random random = new Random();
	
	public static float generateUniformDist(float min, float max) {
		
		return random.nextFloat() * (max - min) + min;
		
	}

}
