package com.pf.movement;


import com.pf.manager.AgentManager;
import com.pf.manager.EnvironmentManager;
import com.pf.math.Vector2;
import com.pf.model.Agent;
import com.pf.model.Obstacle;

public class SocialForceMovement implements IAgentMovement{

	public void updateMovement(Agent agent, AgentManager agentManager,
			EnvironmentManager environment, float deltaTime) {
	
		Vector2 desiredVelocity = ((agent.destination.sub(agent.position)).normalize()).scale(agent.getvDesired());
		Vector2 desireForce = ((desiredVelocity.sub(agent.velocity)).scale(1/agentManager.tao));
		
		Vector2 peopleInteractionForce = calculatePeopleIForce(agent, agentManager, deltaTime);

		Vector2 wallInteractionForce = calculateObstacleIForce(agent, agentManager, environment, deltaTime);

		Vector2 currentVelocity = (desireForce.add(peopleInteractionForce).add(wallInteractionForce)).scale(deltaTime).scale(1/agent.mass);
		agent.velocity = currentVelocity;
		

		agent.position = agent.position.add(agent.velocity.scale(deltaTime));
		
		Vector2 granularForce = calculateGranularPeopleIForce(agent, agentManager, deltaTime)
									.add(calculateGranularObstacleIForce(agent, agentManager, environment, deltaTime));
		Vector2 contactForce = calculateContactPeopleIForce(agent, agentManager, deltaTime)
									.add(calculateContactObstacleIForce(agent, agentManager, environment, deltaTime));

		agent.SetForces(granularForce, contactForce, desireForce);

		
	}

	private Vector2 calculateContactObstacleIForce(Agent agent,
			AgentManager agentManager, EnvironmentManager environment,
			float deltaTime) {
		
		Vector2 acummulatedForce = new Vector2();
		
		float maxForceIntensity = 0;
		
		for (Obstacle obstacle : environment.getObstacles()) {
			float radiusMinusDistance = -1 * obstacle.distanceTo(agent);
			float bodyForceIntensity;
			float tangentialForceIntensity;
			Vector2 thisForce;

			if(obstacle.distanceTo(agent) < agent.radius) {
	
				bodyForceIntensity = agentManager.Kn * (radiusMinusDistance); 
				tangentialForceIntensity = agentManager.Kt * radiusMinusDistance * agent.velocity.dot(obstacle.tangentTo(agent));
			} else {
				bodyForceIntensity = 0;
				tangentialForceIntensity = 0;
			}
			
			Vector2 tangentialDirection = obstacle.tangentTo(agent).normalize();
			Vector2 normalDirection = obstacle.normalTo(agent).normalize();
			
			thisForce = (normalDirection.scale(bodyForceIntensity))
						.sub(tangentialDirection.scale(tangentialForceIntensity));
			
			acummulatedForce = acummulatedForce.add(thisForce);
			
			if(thisForce.mod() > maxForceIntensity) {
				maxForceIntensity = thisForce.mod();
			}
		}
		return acummulatedForce.normalize().scale(maxForceIntensity);
	}

	private Vector2 calculateGranularObstacleIForce(Agent agent,
			AgentManager agentManager, EnvironmentManager environment,
			float deltaTime) {
		Vector2 acummulatedForce = new Vector2();
		
		float maxForceIntensity = 0;
		for (Obstacle obstacle : environment.getObstacles()) {
			float radiusMinusDistance = -1 * obstacle.distanceTo(agent);
			float normalForceIntensity = (float)(agentManager.A * Math.exp((radiusMinusDistance)/agentManager.B));
			Vector2 thisForce;

			
			Vector2 normalDirection = obstacle.normalTo(agent).normalize();
			
			thisForce = (normalDirection.scale(normalForceIntensity ));
			
			acummulatedForce = acummulatedForce.add(thisForce);
			
			if(thisForce.mod() > maxForceIntensity) {
				maxForceIntensity = thisForce.mod();
			}
		}
		return acummulatedForce.normalize().scale(maxForceIntensity);
	}

	private Vector2 calculateContactPeopleIForce(Agent agent,
			AgentManager agentManager, float deltaTime) {
		
		Vector2 force = new Vector2(0,0);
		
		float bodyForceIntensity;
		Vector2 frictionForce;
		
		float radiusSum;
		float distance;
		
		for (Agent oAgent : agentManager.getBackedUpAgents()) {
			if(agent.id != oAgent.id) {
				radiusSum = agent.radius + oAgent.radius;
				distance = agent.position.distance(oAgent.position);
				frictionForce = new Vector2();
				bodyForceIntensity = 0;
				
				if(radiusSum >= distance) {
					bodyForceIntensity = agentManager.Kn * (radiusSum - distance); 
					frictionForce = (((agent.velocity.sub(oAgent.velocity)).scale(oAgent.velocity.mod() - agent.velocity.mod())).scale(radiusSum - distance)).scale(agentManager.Kt);
				}
				
				force = (force.add(((agent.position.sub(oAgent.position)).normalize()).scale(bodyForceIntensity))).add(frictionForce);
			}
		}
		return force;
	}

	private Vector2 calculateGranularPeopleIForce(Agent agent,
			AgentManager agentManager, float deltaTime) {
		Vector2 force = new Vector2();
		
		float socialIntensity;
		
		float radiusSum;
		float distance;
		for (Agent oAgent : agentManager.getBackedUpAgents()) {
			if(agent.id != oAgent.id) {
				radiusSum = agent.radius + oAgent.radius;
				distance = agent.position.distance(oAgent.position);
				
				socialIntensity = (float)(agentManager.A * Math.exp(( radiusSum - distance)/agentManager.B));
				
				force = (force.add(((agent.position.sub(oAgent.position)).normalize()).scale(socialIntensity)));
			}
		}
		return force;
	
	}

	private Vector2 calculateObstacleIForce(Agent agent, AgentManager agentManager, 
			EnvironmentManager environment, float deltaTime) {
		
		Vector2 acummulatedForce = new Vector2();
		
		float maxForceIntensity = 0;
		
		for (Obstacle obstacle : environment.getObstacles()) {

			//Si me meti en un obstaculo, lo mando directo hasta el final
			if(obstacle.contains(agent))
			{
				agent.position = agent.destination;
				return new Vector2(0,0);
			}
			float radiusMinusDistance = -1 * obstacle.distanceTo(agent);
			float normalForceIntensity = (float)(agentManager.A * Math.exp((radiusMinusDistance)/agentManager.B));
			float bodyForceIntensity;
			float tangentialForceIntensity;
			Vector2 thisForce;

			if(obstacle.distanceTo(agent) < 0) {
	
				bodyForceIntensity = agentManager.Kn * (radiusMinusDistance); 
				tangentialForceIntensity = agentManager.Kt * radiusMinusDistance * agent.velocity.dot(obstacle.tangentTo(agent));
			} else {
				bodyForceIntensity = 0;
				tangentialForceIntensity = 0;
			}
			
			Vector2 tangentialDirection = obstacle.tangentTo(agent).normalize();
			Vector2 normalDirection = obstacle.normalTo(agent).normalize();
			
			//System.out.println("tangent " + tangentialForceIntensity + " x " + tangentialDirection);
		//	System.out.println("normal " + (normalForceIntensity) + " x " + normalDirection);
		//	System.out.println("body " + (bodyForceIntensity) + " x " + normalDirection);
			
			thisForce = (normalDirection.scale(normalForceIntensity + bodyForceIntensity))
						.sub(tangentialDirection.scale(tangentialForceIntensity));
			
			acummulatedForce = acummulatedForce.add(thisForce);
			if(thisForce.mod() > maxForceIntensity) {
				maxForceIntensity = thisForce.mod();
			}
		}
		//System.out.println("acum" + acummulatedForce);
	//	System.out.println("acumNorm" + acummulatedForce.normalize());
	//	System.out.println("acumMod" + acummulatedForce.mod());
		
		return acummulatedForce.normalize().scale(maxForceIntensity);
	}

	private Vector2 calculatePeopleIForce(Agent agent, AgentManager agentManager,
			float deltaTime) {
		
		Vector2 force = new Vector2();
		
		float socialIntensity;
		float bodyForceIntensity;
		Vector2 frictionForce;
		
		float radiusSum;
		float distance;
		for (Agent oAgent : agentManager.getBackedUpAgents()) {
			if(!agent.equals(oAgent)) {
				radiusSum = agent.radius + oAgent.radius;
				distance = agent.position.distance(oAgent.position);
				
				socialIntensity = (float)(agentManager.A * Math.exp(( radiusSum - distance)/agentManager.B));
				
				if(radiusSum >= distance) {
					bodyForceIntensity = agentManager.Kn * (radiusSum - distance); 
					frictionForce = (((agent.velocity.sub(oAgent.velocity)).scale(oAgent.velocity.mod() - agent.velocity.mod())).scale(radiusSum - distance)).scale(agentManager.Kt);
				} else {
					bodyForceIntensity = 0;
					frictionForce = new Vector2();
				}
				
				force = (force.add(((agent.position.sub(oAgent.position)).normalize()).scale(socialIntensity + bodyForceIntensity))).add(frictionForce);
				if(force.mod() > 10000)
				{
					//necesario por el caso cuando uno aparece encima de otro
					force = force.scale(10/force.mod());
				}
			}
		}
		return force;
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "social";
	}

}
