package com.pf.movement.qlearning;

import java.util.List;

import com.pf.math.Vector2;
import com.pf.model.Agent;


public class QLearningMovement extends QLearningBaseMovement {

	public QLearningMovement(Void nothing) {

	}

	public QLearningMovement() {
		super();
		
	}

	public void updateUtilityOnCollision() {
		QlearningTuple lastTuple = new QlearningTuple(lastKnownState,
				lastActionTaken);

		Float utility = qMatrix.get(lastTuple);
		if (utility == null) {
			utility = new Float(0);
		}

		utility = new Float(utility + learningRate
				* (collisionReinforcement - utility));
		qMatrix.put(lastTuple, utility);
		
		lastKnownState = null;
		lastActionTaken = null;
	}

	public void updateUtilityOnIteration() {
		QlearningTuple learningTuple = new QlearningTuple(lastKnownState,
				lastActionTaken);
		Float newUtility = getMaxUtilityForState(new QLearningState(qStatus));
		Float utility = qMatrix.get(learningTuple);

		if (newUtility != null) { // if null then there's nothing to update
									// for the previous action
			if (utility == null) {
				utility = new Float(0);
			}

			utility = new Float(
					utility
							+ learningRate
							* (regularReinforcement + discountFactor
									* newUtility - utility));
			qMatrix.put(learningTuple, utility);
		}
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "q-learning";
	}




}
