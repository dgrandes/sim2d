package com.pf.movement.qlearning;

public class QlearningTuple {

	public QLearningState state;
	public QAction action;
	
	public QlearningTuple(QLearningState lastKnownState, QAction lastActionTaken) {
		state = lastKnownState;
		action = lastActionTaken;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((action == null) ? 0 : action.hashCode());
		result = prime * result + ((state == null) ? 0 : state.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof QlearningTuple))
			return false;
		QlearningTuple other = (QlearningTuple) obj;
		if (action == null) {
			if (other.action != null)
				return false;
		} else if (!action.equals(other.action))
			return false;
		if (state == null) {
			if (other.state != null)
				return false;
		} else if (!state.equals(other.state))
			return false;
		return true;
	}
	
	
}
