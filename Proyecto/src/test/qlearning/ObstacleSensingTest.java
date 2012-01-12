package qlearning;


import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.pf.math.Vector2;
import com.pf.model.Agent;
import com.pf.model.PolygonObstacle;
import com.pf.movement.qlearning.QLearningMovement;

public class ObstacleSensingTest {

	private QLearningMovement mvt;
	private Agent a;
	private List<PolygonObstacle> obstacles;
	private Vector2 pointa;
	private Vector2 pointb;
	
	@Before
	public void setUp() throws Exception {
		mvt = new QLearningMovement();
		a = new Agent();
		a.radius = 10;
		a.position = new Vector2(0,0);
		a.setvDesired(1);
		obstacles = new ArrayList<PolygonObstacle>();
		pointa =  new Vector2(10,0);
		pointb = new Vector2(0,10);
	}

	@Test
	public void testMvtNotNull() throws Exception {
		assertNotNull(mvt);
	}
	
	@Test
	public void testRiskOfNoObstacles() throws Exception{
		float risk = mvt.checkRiskForObstacles(a,obstacles,pointa, pointb);
		
		assertThat(risk, is(0.0f));
	}
	
	@Test
	public void testRiskIsNotZeroWithAnObstacle() throws Exception{
		int[] xpoints = {-20,20,20,-20};
		int[] ypoints = {20,20,-20,-20};
		PolygonObstacle o = new PolygonObstacle(xpoints,ypoints,4);
		obstacles.add(o);
		float risk = mvt.checkRiskForObstacles(a, obstacles, pointa, pointb);
		assertThat(risk, not(is(0.0f)));
	}
	
	@Test
	public void testRiskIsNotZeroAtTheEdge() throws Exception{
		int[] xpoints = {-20,20,20,-20};
		int[] ypoints = {20,20,-20,-20};
		a.position = new Vector2(0,-30);
		pointa = new Vector2(-1,-20);
		pointb = new Vector2(1,-20);
		PolygonObstacle o = new PolygonObstacle(xpoints,ypoints,4);
		obstacles.add(o);
		float risk = mvt.checkRiskForObstacles(a, obstacles, pointa, pointb);
		assertThat(risk, not(is(0.0f)));
	}
	
	@Test
	public void testRiskIsZeroOneUnitFromTheEdge() throws Exception{
		int[] xpoints = {-20,20,20,-20};
		int[] ypoints = {20,20,-20,-20};
		a.position = new Vector2(0,-41);
		pointa = new Vector2(-1,-20);
		pointb = new Vector2(1,-20);
		PolygonObstacle o = new PolygonObstacle(xpoints,ypoints,4);
		obstacles.add(o);
		float risk = mvt.checkRiskForObstacles(a, obstacles, pointa, pointb);
		assertThat(risk, is(0.0f));
	}
	
	
	@Test
	public void testSensingIsZeroForEmptyObstaclesAndAgents() throws Exception{
		
	}
	
	@After
	public void tearDown() throws Exception {
		mvt = null;
	}
}
