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

public class AgentSensingTest {

	private QLearningMovement mvt;
	private Agent a;
	private List<Agent> drones;
	private Vector2 pointa;
	private Vector2 pointb;
	
	@Before
	public void setUp() throws Exception {
		mvt = new QLearningMovement();
		a = new Agent();
		a.radius = 10;
		a.position = new Vector2(0,0);
		a.setvDesired(10);
		drones = new ArrayList<Agent>();
		pointa =  new Vector2(-5,5);
		pointb = new Vector2(5,5);
	}

	@Test
	public void testMvtNotNull() throws Exception {
		assertNotNull(mvt);
	}
	
	@Test
	public void testRiskOfNoOtherAgentsAround() throws Exception{
		float risk = mvt.checkRiskForAgent(a,drones,pointa, pointb);
		assertThat(risk, is(0.0f));
	}
	
	@Test
	public void testRiskOfOtherAgentsAreNull() throws Exception{
		float risk = mvt.checkRiskForAgent(a,null,pointa, pointb);
		assertThat(risk, is(0.0f));
	}

	
	@Test
	public void testSensingNotZeroGivenAnAgentInFront() throws Exception{
		Agent b = new Agent();
		a.id = 0;
		b.id = 1;
		b.position = new Vector2(0,2);
		b.velocity = new Vector2(0,-10);
		a.velocity = new Vector2(0,10);
		drones.add(b);
		float sensing = mvt.checkRiskForAgent(a, drones, pointa, pointb);
		assertThat(sensing,not(is(0.0f)));
	}
	
	@Test
	public void testSensingIsTwoGivenAnAgentInFrontWithSpeed() throws Exception{
		Agent b = new Agent();
		a.id = 0;
		b.id = 1;
		b.position = new Vector2(0,2);
		b.velocity = new Vector2(0,-10);
		a.velocity = new Vector2(0,10);
		drones.add(b);
		float sensing = mvt.checkRiskForAgent(a, drones, pointa, pointb);
		assertThat(sensing,is(2.0f));
	}
	
	@Test
	public void testSensingIsOneWithAnAgentInRangeButStill() throws Exception{
		Agent b = new Agent();
		a.id = 0;
		b.id = 1;
		b.position = new Vector2(0,5f);
		b.velocity = new Vector2(0,0);
		a.velocity = new Vector2(0,1);
		a.setvDesired(0.1f);
		drones.add(b);
		float sensing = mvt.checkRiskForAgent(a, drones, pointa, pointb);
		assertThat(sensing,is(1.0f));		
	}
	
	@After
	public void tearDown() throws Exception {
		mvt = null;
	}
}
