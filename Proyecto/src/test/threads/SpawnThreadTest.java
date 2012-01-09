package threads;


import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.pf.math.Vector2;
import com.pf.model.Agent;
import com.pf.model.Lapsus;
import com.pf.model.SpawnPoint;
import com.pf.movement.IAgentMovement;
import com.pf.simulator.threads.GenerateAgentsSpawnThread;

public class SpawnThreadTest {

	GenerateAgentsSpawnThread spawnThread;
	SpawnPoint spawnPoint;
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		spawnThread = new GenerateAgentsSpawnThread();
		spawnPoint = new SpawnPoint();
		
	}

	@After
	public void tearDown() throws Exception {
		spawnThread = null;
		spawnPoint= null;
	}
	
	@Test	
	public void test_sample() throws Exception {
		
		assertNotNull(spawnThread);
	}
	
	@Test
	public void test_call_works() throws Exception{
		spawnThread.assignSpawnPoints(new ArrayList<SpawnPoint>());
		spawnThread.setCurrentStep(0);
		List<Agent> call_agents = spawnThread.call();
		assertNotNull(call_agents);
		assertEquals(call_agents.size(), 0);
	}
	
	@Test
	public void test_agent_generation() throws Exception
	{
		spawnPoint.setGeneratesOnAgentDeletion(false);
		spawnPoint.setMassMax(20);
		spawnPoint.setMassMin(10);
		spawnPoint.setMovementType("social");
		spawnPoint.setPreservesSpawns(false);
		spawnPoint.setRadiusMax(10);
		spawnPoint.setRadiusMin(5);
		Agent a = spawnThread.generateRandomAgent(spawnPoint);
		
		assertNotNull(a);
		float width = a.getRadius();
		float minRadius = spawnPoint.getRadiusMin();
		assertTrue("Width "+width+" wasnt greater than "+minRadius,width > minRadius );
		IAgentMovement mov =  a.getMovement();
		assertEquals(mov.getDescription(),"social");
				
	}
	

	@Test 
	public void test_chance_to_generate() throws Exception{
		Lapsus one = new Lapsus();
		one.setF(0.5f);
		one.setTime(10);
		Lapsus two = new Lapsus();
		two.setF(0.5f);
		two.setTime(10);
		spawnPoint.setLapsusOne(one);
		spawnPoint.setLapsusTwo(two);
		
		Lapsus l = spawnPoint.getCurrentLapsus();
		
		assertNotNull(l);
		assertTrue("lapsus chance is greater than 0",l.getF() > 0);
		
	}
	
	@Test
	public void test_new_agents() throws Exception
	{
		Lapsus one = new Lapsus();
		one.setF(1f);
		one.setTime(10);
		Lapsus two = new Lapsus();
		two.setF(1f);
		two.setTime(10);
		spawnPoint = generateSpawnpoint(new Vector2(5,10), 
				new Vector2(5,10),"social", false, false, "exit1",one,two);
		ArrayList<SpawnPoint> spawns = new ArrayList<SpawnPoint>();
		spawns.add(spawnPoint);
		spawnThread.assignSpawnPoints(spawns);
		spawnThread.setCurrentStep(0);
		List<Agent> newagents = spawnThread.call();
		assertNotNull(newagents);
		int wantedagents = 1;
		assertTrue("new agents should be at least "+wantedagents+" instead was "+newagents.size(),newagents.size() >= wantedagents);
		for(Agent a : newagents)
		{
			assertNotNull(a.getShape());
			assertNotNull(a.getRadius());
			assertNotNull(a.getMovementType());
		}
		
	}
	
	private SpawnPoint generateSpawnpoint(Vector2 massrange, Vector2 radiusRange,
			String movType, boolean preservesSpawns, boolean generatesonDeletion,
			String desiredExit, Lapsus l1, Lapsus l2)
	{
		SpawnPoint resp = new SpawnPoint();
		resp.setDesiredExitId(desiredExit);
		resp.setMassMin(massrange.getX());
		resp.setMassMax(massrange.getY());
		resp.setRadiusMin(radiusRange.getX());
		resp.setRadiusMax(radiusRange.getY());
		resp.setPreservesSpawns(preservesSpawns);
		resp.setMovementType(movType);
		resp.setGeneratesOnAgentDeletion(generatesonDeletion);
		resp.setLapsusOne(l1);
		resp.setLapsusTwo(l2);
		return resp;
	}
}
