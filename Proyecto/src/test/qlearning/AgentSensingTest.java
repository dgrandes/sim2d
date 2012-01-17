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
import com.pf.movement.qlearning.QLearningMovement;

public class AgentSensingTest {

	private QLearningMovement mvt;
	private Agent a;
	private Agent b;
	private List<Agent> drones;
	private Vector2 pointa;
	private Vector2 pointb;
	private float sensing;
	private String description;
	@Before
	public void setUp() throws Exception {
		mvt = new QLearningMovement();
		a = new Agent();
		b = new Agent();
		a.velocity = new Vector2(0,0);
		b.velocity = new Vector2(0,0);
		a.radius = 1;
		b.radius = 1;
		a.position = new Vector2(0, 0);
		b.position = new Vector2(0,0);
		a.setvDesired(10);
		drones = new ArrayList<Agent>();
		pointa = new Vector2(-5, 5);
		pointb = new Vector2(5, 5);
	}


	@Test
	public void testRiskOfNoOtherAgentsAround() throws Exception {
		description = "Risk of No Other Agents Around";
		 sensing = mvt.checkRiskForAgent(a, drones, pointa, pointb);
		assertThat(sensing, is(0.0f));
	}

	@Test
	public void testRiskOfOtherAgentsAreNull() throws Exception {
		description = "";
		float risk = mvt.checkRiskForAgent(a, null, pointa, pointb);
		assertThat(risk, is(0.0f));
	}

	@Test
	public void testSensingNotZeroGivenAnAgentInFront() throws Exception {
		description = "Risk of Agent Right in Front of us";
		a.id = 0;
		b.id = 1;
		b.position = new Vector2(0, 2);
		b.velocity = new Vector2(0, -10);
		a.velocity = new Vector2(0, 10);
		drones.add(b);
		sensing = mvt.checkRiskForAgent(a, drones, pointa, pointb);
		assertThat(sensing, not(is(0.0f)));
	}

	@Test
	public void testSensingIsTwoGivenAnAgentInFrontWithSpeed() throws Exception {
		description = "Risk of Agent in Front moving";
		a.id = 0;
		b.id = 1;
		b.position = new Vector2(0, 2);
		b.velocity = new Vector2(0, -10);
		a.velocity = new Vector2(0, 10);
		drones.add(b);
		sensing = mvt.checkRiskForAgent(a, drones, pointa, pointb);
		assertThat(sensing, is(2.0f));
	}

	@Test
	public void testToGetARiskOf1FromAStillAgent() throws Exception {
		description = "Risk of Agent in Front standing still";
		a.id = 0;
		b.id = 1;
		// Si la position es mayor a 0.04, ya es un risk de 2
		b.position = new Vector2(0, 0.04f);
		b.velocity = new Vector2(0, 0);
		a.velocity = new Vector2(0, 1);

		a.setvDesired(0.1f);
		drones.add(b);
		sensing = mvt.checkRiskForAgent(a, drones, pointa, pointb);
		assertThat(sensing, is(1.0f));
	}

	@Test
	public void testToGetARiskOf1FromAMovingAgentAtTheSameOppositeVelocity()
			throws Exception {
		description = "Risk = 1 of Agent in Front moving";
		a.id = 0;
		b.id = 1;
		b.position = new Vector2(0, 0.004f);
		b.velocity = new Vector2(0, -1);
		a.velocity = new Vector2(0, 1);
		pointa = new Vector2(-5, 5);
		pointb = new Vector2(5, 5);
		a.setvDesired(0.1f);
		drones.add(b);
		sensing = mvt.checkRiskForAgent(a, drones, pointa, pointb);
		assertThat(sensing, is(1.0f));
	}

	@Test
	public void testOfRiskTotheside() throws Exception {
		description = "Risk of an Agent 45 degrees standing still";
		a.id = 0;
		b.id = 1;
		b.position = new Vector2(3, 3.0f);
		a.velocity = new Vector2(0, 1);
		pointa = new Vector2(-5, 5);
		pointb = new Vector2(5, 5);
		b.velocity = new Vector2(0, 0);
		a.setvDesired(0.1f);
		drones.add(b);
		sensing = mvt.checkRiskForAgent(a, drones, pointa, pointb);
		assertThat(sensing, is(not(0.0f)));
	}

	@Test
	public void testOfRiskTothesideWithVelocity() throws Exception {
		description = "Risk of an Agent 45 degrees moving";
		a.id = 0;
		b.id = 1;
		b.position = new Vector2(3, 3.0f);
		a.velocity = new Vector2(0, 1);
		pointa = new Vector2(-5, 5);
		pointb = new Vector2(5, 5);
		b.velocity = new Vector2(0, -1);
		a.setvDesired(0.1f);
		drones.add(b);
		sensing = mvt.checkRiskForAgent(a, drones, pointa, pointb);
		assertThat(sensing, is(not(0.0f)));
	}

	@After
	public void tearDown() throws Exception {
		printScenario();
	}

	private void printScenario() {
		if(description.equals(""))
			return;
		System.out.println("Scenario "+description+"-------------------");
		System.out.println("Agent Main ->pos:"+a.position+",     vel:"+a.velocity);
		if(drones.size() != 0)
			System.out.println("Other Agent->pos:"+b.position+",     vel:"+b.velocity);
		System.out.println("Points  :     a: "+pointa+"      b: "+pointb);
		System.out.println("RISK:   "+sensing);
		System.out.println("Scenario End-------------------------------\n");
	}
}
