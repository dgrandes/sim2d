package com.pf.movement.qlearning;

import com.pf.math.Vector2;

public class QAction {

	public ActionType action;
	
	@Override
	public String toString() {
		return action.toString();
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((action == null) ? 0 : action.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof QAction))
			return false;
		QAction other = (QAction) obj;
		if (action != other.action)
			return false;
		return true;
	}
	public QAction(Vector2 qCorrection, Vector2 desireForce) {
		if(qCorrection.mod() == 0) {
			action = ActionType.ACTION_NONE;
		} else {
			float angle = (float) Math.acos(qCorrection.normalize().dot(desireForce.normalize()));
			if (angle < 1.6 && qCorrection.getX() < 0) {
				action = ActionType.ACTION_RIGHT;
			} else if (angle < 1.6 && qCorrection.getX() > 0) {
				action = ActionType.ACTION_LEFT;
			} else if (angle < 3.2) {
				action = ActionType.ACTION_BACK;
			} else {
				action = ActionType.ACTION_BACK;
			}
		}
	}
	public QAction(ActionType action) {
		this.action = action;
	}

}
