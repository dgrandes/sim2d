package com.pf.movement.qlearning;

import java.awt.Polygon;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.pf.manager.AgentManager;
import com.pf.manager.EnvironmentManager;
import com.pf.math.Vector2;
import com.pf.model.Agent;
import com.pf.model.Obstacle;
import com.pf.model.PolygonObstacle;
import com.pf.movement.IAgentMovement;
import com.pf.simulator.Simulator;

public abstract class QLearningBaseMovement implements IAgentMovement {

	public static int ITERATION_LENGTH = 200;
	public static float ITERATION_QTY = 5000;
	public static boolean QLearningEnabled = false;
	public static Vector2 referenceVector = new Vector2(0, 1);
	public static float maxViewDistance = 10;
	public static final float maxViewAngle = 180;
	public static final int slices = 6;
	public static float learningRate = 0.1f;
	public static final float collisionReinforcement = -1;
	public static final float regularReinforcement = 0;
	public static final float rewardReinforcement = 10;
	public static float discountFactor = 0.9f;
	public static float decay = -99999f;
	public static final float QCorrectionMod = 1.4f;
	public static Rectangle2D.Float bounds;
	public static float boundsPadding = 10;
	public Vector2 agentOrigin;
	public float distanceMoved = 0;
	public List<Float> agentInstantSpeed = new ArrayList<Float>();
	public boolean mainActor = false;
	protected Random randomNumber = new Random();
	public float explorationRate = 100;
	public float counter = 0;

	public float qStatus[];

	protected boolean didCollideWithAgent = false;
	protected QLearningState lastKnownState;
	protected QAction lastActionTaken;
	protected Vector2 lastReinforcement = new Vector2(0, 0);
	public Map<QlearningTuple, Float> qMatrix = new HashMap<QlearningTuple, Float>();
	public String filename;

	public int crashes = 0;
	public int victory = 0;
	public int steps = 0;

	public QLearningBaseMovement() {
		super();
		QLearningEnabled = true;

	}

	public void updateUtilityOnIteration() {

	}

	public void updateUtilityOnCollision() {

	}

	public void increaseVictoryCount() {
		victory++;
	}

	public void updateMovement(Agent agent, AgentManager agentManager,
			EnvironmentManager environment, float deltaTime) {

		if (decay == -99999f)
			decay = 150.0f / ITERATION_QTY;
		Vector2 desiredVelocity = ((agent.destination.sub(agent.position))
				.normalize()).scale(agent.getvDesired());
		Vector2 desireForce = (desiredVelocity).scale(1 / agentManager.tao);
		Vector2 contactForce = calculateContactPeopleIForce(agent,
				agentManager, deltaTime).add(
				calculateContactObstacleIForce(agent, agentManager,
						environment, deltaTime));
		Vector2 qCorrection = new Vector2(0, 0);

		counter += deltaTime;

		if (didCollideWithAgent) {
			crashes++;
			updateUtilityOnCollision();
			agent.position = new Vector2(agentOrigin);
			didCollideWithAgent = false;
		}

		repositionAgent(agent);

		qCorrection = calculateQCorrection(agent, agentManager, environment,
				desiredVelocity, counter);

		Vector2 oldAgentPosition = agent.position;

		if (counter > ITERATION_LENGTH) {

			if (steps++ == ITERATION_QTY && mainActor)
				Simulator.stopSimulation();

			updateUtilityOnIteration();

			lastKnownState = new QLearningState(qStatus);
			lastActionTaken = new QAction(qCorrection, desireForce);
			counter = 0;

			Vector2 currentVelocity = desireForce.sub(contactForce)
					.add(qCorrection).scale(deltaTime).scale(1 / agent.mass);
			agentInstantSpeed.add(agent.position.add(currentVelocity).distance(
					oldAgentPosition)
					/ deltaTime);
		}

		Vector2 currentVelocity = desireForce.sub(contactForce)
				.add(qCorrection).scale(deltaTime).scale(1 / agent.mass);
		agent.velocity = currentVelocity;

		agent.position = agent.position.add(agent.velocity.scale(deltaTime));

		agent.SetForces(qCorrection, contactForce, desireForce);

		distanceMoved += agent.position.distance(oldAgentPosition);

	}

	protected void reward() {
		QlearningTuple lastTuple = new QlearningTuple(lastKnownState,
				lastActionTaken);

		if (lastKnownState.slicesSum() < 1) {
			System.out.println("Actualizando 0,0,...");
		}

		Float utility = qMatrix.get(lastTuple);
		if (utility == null) {
			utility = new Float(0);
		}

		utility = new Float(utility + learningRate
				* (rewardReinforcement - utility));
		qMatrix.put(lastTuple, utility);
	}

	protected void repositionAgent(Agent agent) {
		// Arma el bounds que tiene como centro el punto medio entre el destino
		// y el agente. Tiene como longitud por lado la distancia entre el
		// destino
		// y el agente + el padding
		Rectangle2D bounds = Simulator.getSimEnvironment().getBounds(25);

		// Si llega a destino es victory
		if (agent.position.sub(agent.destination).mod() < 0.01f) {
			increaseVictoryCount();
			// agent.position = new Vector2(agentOrigin);
		} else if (agent.position.getY() < bounds.getMinY()
				|| agent.position.getX() < bounds.getMinX()
				|| agent.position.getX() > bounds.getMaxX()
				|| agent.position.getY() > bounds.getMaxY()) {
			// agent.position = new Vector2(agentOrigin);
			didCollideWithAgent = true;
		}
	}

	protected QAction getBestActionForState(QLearningState qLearningState) {
		qMatrix.get(new QlearningTuple(lastKnownState, lastActionTaken));
		Float bestUtility = null;
		QAction actionToTake = null;

		if (qLearningState.slice1 == 0 && qLearningState.slice7 == 0
				&& qLearningState.slice6 == 0 && qLearningState.slice5 == 0
				&& qLearningState.slice4 == 0 && qLearningState.slice3 == 0
				&& qLearningState.slice2 == 0 && qLearningState.slice8 == 0)
			return new QAction(ActionType.ACTION_NONE);

		ActionType actions[] = ActionType.values();
		for (int i = 0; i < actions.length; i++) {
			ActionType action = actions[i];
			Float someUtility = qMatrix.get(new QlearningTuple(qLearningState,
					new QAction(action)));

			if (someUtility == null) {
				someUtility = new Float(0);
			}

			if (bestUtility == null) {
				bestUtility = someUtility;
				actionToTake = new QAction(action);
			} else if (someUtility != null) {
				if (bestUtility < someUtility) {
					bestUtility = someUtility;
					actionToTake = new QAction(action);
				}
			}

		}

		return actionToTake;
	}

	protected Float getMaxUtilityForState(QLearningState lastKnownState) {
		qMatrix.get(new QlearningTuple(lastKnownState, lastActionTaken));
		Float bestUtility = null;

		ActionType actions[] = ActionType.values();
		for (int i = 0; i < actions.length; i++) {
			ActionType action = actions[i];
			Float someUtility = qMatrix.get(new QlearningTuple(lastKnownState,
					new QAction(action)));

			if (bestUtility == null) {
				bestUtility = someUtility;
			} else if (someUtility != null) {
				if (bestUtility < someUtility) {
					bestUtility = someUtility;
				}
			}

		}
		return bestUtility;
	}

	protected Boolean testForLessThreat(QLearningState newState,
			QLearningState oldState) {

		if (newState == null || oldState == null) {
			return false;
		}

		float newThreat = newState.slicesSum();

		float oldThreat = oldState.slicesSum();

		if ((int) oldThreat > (int) newThreat) {
			return true;
		} else {
			return false;
		}
	}

	protected Vector2 calculateQCorrection(Agent agent,
			AgentManager agentManager, EnvironmentManager environment,
			Vector2 desiredVelocity, float counter2) {

		if (counter > ITERATION_LENGTH) {
			qStatus = senseObjects(agent, agentManager, environment,
					desiredVelocity);
			explorationRate -= decay;

			if (testForLessThreat(new QLearningState(qStatus), lastKnownState)) {
				reward();
			}

			if (randomNumber.nextInt(100) > explorationRate) {
				lastReinforcement = doBestAction(desiredVelocity);
			} else {
				lastReinforcement = doRandomAction(desiredVelocity);
			}
		}

		return lastReinforcement;
	}

	protected Vector2 doBestAction(Vector2 desiredVelocity) {
		QAction actionToTake = getBestActionForState(new QLearningState(qStatus));

		if (actionToTake != null) {
			float angle = 0;
			float multiplier = 1;

			float angleOffset = (float) Math.acos(desiredVelocity.normalize()
					.dot(referenceVector));

			switch (actionToTake.action) {
			case ACTION_LEFT:
				angle = 270;
				break;
			case ACTION_BACK:
				angle = 180;
				multiplier = 0.5f;
				break;
			case ACTION_RIGHT:
				angle = 90;
				break;
			default:
				return new Vector2(0, 0);
			}
			angle -= 270;
			Vector2 pushDirection = new Vector2(
					(float) (desiredVelocity.mod() * Math.cos(angle * Math.PI
							/ 180 + angleOffset)),
					(float) (desiredVelocity.mod() * Math.sin(angle * Math.PI
							/ 180 + angleOffset)));
			pushDirection = pushDirection.normalize();
			return pushDirection.scale(QCorrectionMod).scale(multiplier);
		}

		return new Vector2(0, 0);
	}

	protected Vector2 doRandomAction(Vector2 desiredVelocity) {
		Vector2 pushDirection;
		float angle;
		float multiplier = 1;
		float angleOffset = (float) Math.acos(desiredVelocity.normalize().dot(
				referenceVector));

		int randomInt = randomNumber.nextInt(4);
		switch (randomInt) {
		case 0:
			angle = 90; // Left
			break;
		case 1:
			angle = 180; // Back
			multiplier = 0.5f;
			break;
		case 2:
			angle = 270; // Right
			break;
		default:
			return new Vector2(0, 0); // None
		}
		pushDirection = new Vector2(
				(float) (desiredVelocity.mod() * Math.cos(angle * Math.PI / 180
						+ angleOffset)),
				(float) (desiredVelocity.mod() * Math.sin(angle * Math.PI / 180
						+ angleOffset)));
		pushDirection = pushDirection.normalize();
		return pushDirection.scale(QCorrectionMod).scale(multiplier);

	}

	public float[] senseObjects(Agent agent, AgentManager agentManager,
			EnvironmentManager environment, Vector2 desiredVelocity) {

		float qStatus[] = new float[slices + 2];
		float sliceAngle = (float) (maxViewAngle / slices * Math.PI / 180);
		float angleOffset = (float) Math.acos(desiredVelocity.normalize().dot(
				referenceVector));

		for (int i = 0, j = 0; i < slices; i++, j++) {

			if (i != 2 && i != 3) {
				Vector2 firstPoint = new Vector2(
						(float) (agent.position.getX() + 10 * Math.cos(sliceAngle
								* i - angleOffset)),
						(float) (agent.position.getY() + 10 * Math
								.sin(sliceAngle * i - angleOffset)));
				Vector2 secondPoint = new Vector2(
						(float) (agent.position.getX() + 10 * Math.cos(sliceAngle
								* (i + 1) - angleOffset)),
						(float) (agent.position.getY() + 10 * Math
								.sin(sliceAngle * (i + 1) - angleOffset)));
				qStatus[j] = checkTriangle(agent, firstPoint, secondPoint,
						agentManager, environment);
			} else {
				Vector2 firstPoint = new Vector2(
						(float) (agent.position.getX() + 10 * Math.cos(sliceAngle
								* i - angleOffset)),
						(float) (agent.position.getY() + 10 * Math
								.sin(sliceAngle * i - angleOffset)));
				Vector2 secondPoint = new Vector2(
						(float) (agent.position.getX() + 10 * Math.cos(sliceAngle
								* (i + 0.5) - angleOffset)),
						(float) (agent.position.getY() + 10 * Math
								.sin(sliceAngle * (i + 0.5) - angleOffset)));
				Vector2 thirdPoint = new Vector2(
						(float) (agent.position.getX() + 10 * Math.cos(sliceAngle
								* (i + 1) - angleOffset)),
						(float) (agent.position.getY() + 10 * Math
								.sin(sliceAngle * (i + 1) - angleOffset)));

				qStatus[j++] = checkTriangle(agent, firstPoint, secondPoint,
						agentManager, environment);
				qStatus[j] = checkTriangle(agent, secondPoint, thirdPoint,
						agentManager, environment);
			}
		}

		return qStatus;
	}

	protected float checkTriangle(Agent self, Vector2 firstPoint,
			Vector2 secondPoint, AgentManager agentManager,
			EnvironmentManager environment) {

		// TODO this is an inefficient way to check if a point is inside a
		// triangle
		float risk = 0;

		for (Agent agent : agentManager.getAgents()) {
			if (agent.id == self.id) {
				continue;
			}

			if (pointInTriangle(agent.position, self.position, firstPoint,
					secondPoint)) {

				Vector2 relativeSpeed = self.velocity.sub(agent.velocity);
				float riskValue = relativeSpeed.dot(self.position
						.sub(agent.position));

				if (riskValue <= -0.05) {
					risk = 2;
					break;
				} else if (riskValue < 0) {
					risk = 1;
				}

			}

		}
		// Ahora el riesgo de un obstaculo es considerable
		if (risk == 0) {
			risk = checkRiskForObstacles(self,
					(List<PolygonObstacle>) environment.getPolygonObstacles(),
					firstPoint, secondPoint);

		}

		return risk;
	}

	public float checkRiskForAgent(Agent a, List<Agent> drones, Vector2 pointa,
			Vector2 pointb) {
		// TODO Auto-generated method stub
		float risk = 0;
		if (drones == null)
			return 0;
		for (Agent agent : drones) {
			if (agent.id == a.id) {
				continue;
			}
			if (pointInTriangle(agent.position, a.position, pointa, pointb)) {
				Vector2 relativeSpeed = a.velocity.sub(agent.velocity);
				float riskValue = relativeSpeed.dot(a.position
						.sub(agent.position));
				risk = riskValue;
				
				if (riskValue <= -0.05) {
					risk = 2;
					break;
				} else if (riskValue < 0) {
					risk = 1;
				}
			}
		}
		return risk;
	}

	public float checkRiskForObstacles(Agent a,
			List<PolygonObstacle> obstacles, Vector2 pointa, Vector2 pointb) {
		float triangle_radius = 10;
		float risk = 0;
		boolean riskPresent = false;
		if (obstacles == null)
			return 0;
		for (PolygonObstacle obstacle : obstacles) {

			// Esta muy lejos para ser sensado
			if (obstacle.distanceTo(a) > triangle_radius)
				continue;
			if (obstacle.containsPoint(pointa)
					|| obstacle.containsPoint(pointb))
				riskPresent = true;
			else {
				// Posibilidad que el obstaculo sea mas chico que el triangulo
				int npoints = 3;
				int[] xpoints = new int[npoints];
				int[] ypoints = new int[npoints];
				xpoints[0] = (int) a.position.getX();
				xpoints[1] = (int) pointa.getX();
				xpoints[2] = (int) pointb.getX();
				ypoints[0] = (int) a.position.getY();
				ypoints[1] = (int) pointa.getY();
				ypoints[2] = (int) pointb.getY();

				Polygon triangle_shape = new Polygon(xpoints, ypoints, 3);
				if (triangle_shape
						.intersects(obstacle.getShape().getBounds2D()))
					riskPresent = true;
			}

			if (riskPresent) {
				Vector2 relativeSpeed = a.velocity.scale(-1);

				Vector2 closest_point = obstacle.closestPointToAgent(a);
				float riskValue = relativeSpeed.dot(a.position
						.sub(closest_point));

				if (riskValue <= -0.05) {
					risk = 2;
					break;
				} else if (riskValue < 0) {
					risk = 1;
				}
			}

		}

		return risk;

	}

	protected boolean pointInTriangle(Vector2 p, Vector2 a, Vector2 b, Vector2 c) {
		return sameSide(p, a, b, c) && sameSide(p, b, a, c)
				&& sameSide(p, c, a, b);

	}

	protected boolean sameSide(Vector2 p1, Vector2 p2, Vector2 a, Vector2 b) {
		Vector2 cp1 = (b.sub(a)).cross((p1.sub(a)));
		Vector2 cp2 = (b.sub(a)).cross((p2.sub(a)));

		return cp1.dot(cp2) >= 0;
	}

	protected Vector2 calculateContactObstacleIForce(Agent agent,
			AgentManager agentManager, EnvironmentManager environment,
			float deltaTime) {

		Vector2 acummulatedForce = new Vector2();

		float maxForceIntensity = 0;

		for (Obstacle obstacle : environment.getObstacles()) {
			float radiusMinusDistance = -1 * obstacle.distanceTo(agent);
			float bodyForceIntensity;
			float tangentialForceIntensity;
			Vector2 thisForce;

			if (obstacle.distanceTo(agent) < 0 || obstacle.contains(agent)) {
				didCollideWithAgent = true;
				bodyForceIntensity = agentManager.Kn * (radiusMinusDistance);
				tangentialForceIntensity = agentManager.Kt
						* radiusMinusDistance
						* agent.velocity.dot(obstacle.tangentTo(agent));
			} else {
				bodyForceIntensity = 0;
				tangentialForceIntensity = 0;
			}

			Vector2 tangentialDirection = obstacle.tangentTo(agent).normalize();
			Vector2 normalDirection = obstacle.normalTo(agent).normalize();

			thisForce = (normalDirection.scale(bodyForceIntensity))
					.sub(tangentialDirection.scale(tangentialForceIntensity));

			acummulatedForce = acummulatedForce.add(thisForce);

			if (thisForce.mod() > maxForceIntensity) {
				maxForceIntensity = thisForce.mod();
			}
		}
		return acummulatedForce.normalize().scale(maxForceIntensity);
	}

	protected Vector2 calculateContactPeopleIForce(Agent agent,
			AgentManager agentManager, float deltaTime) {

		Vector2 force = new Vector2(0, 0);

		float bodyForceIntensity;
		Vector2 frictionForce;

		float radiusSum;
		float distance;

		for (Agent oAgent : agentManager.getAgents()) {
			if (agent.id != oAgent.id) {
				radiusSum = agent.radius + oAgent.radius;
				distance = agent.position.distance(oAgent.position);

				frictionForce = new Vector2();
				bodyForceIntensity = 0;

				if (radiusSum >= distance) {
					didCollideWithAgent = true;
					bodyForceIntensity = agentManager.Kn
							* (radiusSum - distance);
					frictionForce = (((agent.velocity.sub(oAgent.velocity))
							.scale(oAgent.velocity.mod() - agent.velocity.mod()))
							.scale(radiusSum - distance))
							.scale(agentManager.Kt);
				}

				force = (force.add(((agent.position.sub(oAgent.position))
						.normalize()).scale(bodyForceIntensity)))
						.add(frictionForce);
			}
		}
		return force;
	}

	public static int getITERATION_LENGTH() {
		return ITERATION_LENGTH;
	}

	public static void setITERATION_LENGTH(int iTERATION_LENGTH)
			throws Exception {
		if (iTERATION_LENGTH <= 0)
			throw new Exception("Iterations must have a length greater than 0");
		ITERATION_LENGTH = iTERATION_LENGTH;
	}

	public static float getITERATION_QTY() {

		return ITERATION_QTY;
	}

	public static void setITERATION_QTY(float iTERATION_QTY) throws Exception {
		if (iTERATION_QTY <= 0)
			throw new Exception(
					"The number of iterations must be greater than 0");
		ITERATION_QTY = iTERATION_QTY;

	}

	public static float getMaxViewDistance() {
		return maxViewDistance;
	}

	public static void setMaxViewDistance(float maxViewDistance)
			throws Exception {
		if (maxViewDistance <= 0)
			throw new Exception("The View Distance must be greater than 0");
		QLearningMovement.maxViewDistance = maxViewDistance;
	}

	public static float getLearningRate() {
		return learningRate;
	}

	public static void setLearningRate(float learningRate) throws Exception {
		if (learningRate <= 0)
			throw new Exception("The Learning Rate must be between 0 and 1");
		QLearningMovement.learningRate = learningRate;
	}

	public static float getDiscountFactor() {
		return discountFactor;
	}

	public static void setDiscountFactor(float discountFactor) throws Exception {
		if (discountFactor <= 0)
			throw new Exception("The Discount Factor must be less than 1");
		QLearningMovement.discountFactor = discountFactor;
	}

	public static float getDecay() {
		return decay;
	}

	public static void setDecay(float decay) throws Exception {
		QLearningMovement.decay = decay;
	}

}
