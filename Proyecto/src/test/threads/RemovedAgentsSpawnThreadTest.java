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
import com.pf.model.SpawnPoint;
import com.pf.simulator.threads.RemovedAgentsSpawnThread;


public class RemovedAgentsSpawnThreadTest {

	RemovedAgentsSpawnThread spawnThread;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		spawnThread = new RemovedAgentsSpawnThread();
	
		
	}

	@After
	public void tearDown() throws Exception {
		spawnThread = null;
		
	}
	
	@Test
	public void testSetParameters() throws Exception{
		List<SpawnPoint>  spawnpoints = new ArrayList<SpawnPoint>();
		SpawnPoint sp1 = new SpawnPoint();
		sp1.init("spawn1",true,true,"social", "exit1");
		spawnpoints.add(sp1);
		spawnThread.setSpawnPoints(spawnpoints);
		
		List<Agent> removedAgents = new ArrayList<Agent>();
		Agent a = new Agent();
		a.setBirthSpawnId("spawn1");
		removedAgents.add(a);
		
		spawnThread.setRemovedAgents(removedAgents);
		
		List<Agent> newAgents = spawnThread.call();
		assertNotNull(newAgents);
		assertTrue("agents should have been deleted",newAgents.size() > 0);
		
	}
	
	@Test
	public void testNewAgentsStartWithinSpawnpoint() throws Exception{
		List<SpawnPoint>  spawnpoints = new ArrayList<SpawnPoint>();
		SpawnPoint sp1 = new SpawnPoint();
		sp1.init("spawn1",false,true,"social", "exit1");
		spawnpoints.add(sp1);
		spawnThread.setSpawnPoints(spawnpoints);
		
		List<Agent> removedAgents = new ArrayList<Agent>();
		Agent a = new Agent();
		a.setBirthSpawnId("spawn1");
		Vector2 first_spawn_point = sp1.randomPointWithin(a);
		a.setOriginalSpawnPoint(first_spawn_point);
		removedAgents.add(a);
		spawnThread.setRemovedAgents(removedAgents);
		List<Agent> newagents= spawnThread.call();
		assertNotNull(newagents);
		for(Agent newa : newagents)
		{
			SpawnPoint a_spoint = null;
			for(SpawnPoint sp : spawnpoints)
				if (sp.getId().equals(newa.getOriginalSpawnId()))
					a_spoint = sp;
			assertNotNull(a_spoint);
			assertTrue("new agent is inside spawnpoint", a_spoint.contains(newa));
			assertTrue("positions are different",newa.position.distance(newa.originalSpawn)>=1 );
		}
	}
	
	@Test 
	public void testNewAgentStartsAtTheSamePointWhenPreservesSpawns() throws Exception{
		List<SpawnPoint>  spawnpoints = new ArrayList<SpawnPoint>();
		SpawnPoint sp1 = new SpawnPoint();
		sp1.init("spawn1",true,true,"social", "exit1");
		
		spawnpoints.add(sp1);
		spawnThread.setSpawnPoints(spawnpoints);
		
		List<Agent> removedAgents = new ArrayList<Agent>();
		Agent a = new Agent();
		a.setBirthSpawnId("spawn1");
		
		
		Vector2 first_spawn_point = sp1.randomPointWithin(a);
		a.setOriginalSpawnPoint(first_spawn_point);
		
		removedAgents.add(a);
		spawnThread.setRemovedAgents(removedAgents);
		List<Agent> newagents= spawnThread.call();
		assertNotNull(newagents);
		for(Agent newa : newagents)
		{
			SpawnPoint a_spoint = null;
			for(SpawnPoint sp : spawnpoints)
				if (sp.getId().equals(newa.getOriginalSpawnId()))
					a_spoint = sp;
			assertNotNull(a_spoint);
			assertTrue("new agent is inside spawnpoint", a_spoint.contains(newa));
			assertTrue("pos is the same "+a.position+", "+first_spawn_point,a.position.distance(first_spawn_point) <= 1);
			
		}
	}
	
	@Test 
	public void testAgentsMovTypesMatchesAgents() throws Exception{
		List<SpawnPoint>  spawnpoints = new ArrayList<SpawnPoint>();
		SpawnPoint sp1 = new SpawnPoint();
		sp1.init("spawn1",true,true,"social", "exit1");
		
		spawnpoints.add(sp1);
		spawnThread.setSpawnPoints(spawnpoints);
		
		List<Agent> removedAgents = new ArrayList<Agent>();
		Agent a = new Agent();
		a.setBirthSpawnId("spawn1");
		

		a.movementType = "q-learning";
		removedAgents.add(a);
		spawnThread.setRemovedAgents(removedAgents);
		List<Agent> newagents= spawnThread.call();
		assertNotNull(newagents);
		for(Agent newa : newagents)
		{
			
			
			SpawnPoint a_spoint = spawnThread.getSpawnPoint(newa.getOriginalSpawnId());
			
			assertNotNull(a_spoint);
			assertEquals("q-learning",newa.movementType);
			
			
		}
	}
	
	@Test
	public void testWithNoValidSpawnPoints() throws Exception{
		List<SpawnPoint>  spawnpoints = new ArrayList<SpawnPoint>();
		SpawnPoint sp1 = new SpawnPoint();
		sp1.init("spawn1",true,false,"social", "exit1");
		spawnpoints.add(sp1);
		spawnThread.setSpawnPoints(spawnpoints);
		
		List<Agent> removedAgents = new ArrayList<Agent>();
		Agent a = new Agent();
		a.setBirthSpawnId("spawn1");
		Vector2 first_spawn_point = sp1.randomPointWithin(a);
		a.setOriginalSpawnPoint(first_spawn_point);
		removedAgents.add(a);
		spawnThread.setRemovedAgents(removedAgents);
		List<Agent> newagents= spawnThread.call();
		assertNotNull(newagents);

	}
	

}
