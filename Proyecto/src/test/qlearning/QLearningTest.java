package qlearning;


import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.pf.movement.qlearning.QLearningMovement;

public class QLearningTest {

	private QLearningMovement mvt;
	
	@Before
	public void setUp() throws Exception {
		mvt = new QLearningMovement();
	}

	@Test
	public void testMvtNotNull() throws Exception {
		assertNotNull(mvt);
	}
	
	@After
	public void tearDown() throws Exception {
		mvt = null;
	}

}
