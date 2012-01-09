package com.pf.movement.qlearning.interpreter;

import java.io.File;
import java.io.FileReader;
import java.util.Scanner;
import java.util.regex.Pattern;

import com.pf.math.Vector2;
import com.pf.movement.qlearning.ActionType;
import com.pf.movement.qlearning.QAction;
import com.pf.movement.qlearning.QLearningBaseMovement;
import com.pf.movement.qlearning.QLearningState;
import com.pf.movement.qlearning.QlearningTuple;
import com.pf.simulator.Simulator;

public class QInterpreterMovement extends QLearningBaseMovement implements
		Cloneable {

	public static String QMatrixFilename;

	protected Vector2 doBestAction(Vector2 desiredVelocity, float[] qStatus) {
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

	protected QAction getBestActionForState(QLearningState qLearningState) {

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
				System.out.println("utility for " + action + " is null");
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
		
		System.out.println("chose " + actionToTake);
		return actionToTake;
	}

	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

	public QInterpreterMovement() throws Exception {
		super();
		if (qMatrix.isEmpty()) {
			String filename = "";
			if (QMatrixFilename.equals("")) {
				Simulator.getGuiFrame().showConfirmation(
						"Select QMatrix File To Use:", "QMatrix File");
				filename = Simulator.getGuiFrame().promptUserForFileName();
			} else
				filename = QMatrixFilename;

			if (filename == null)
				throw new Exception("Aborting QInterpreter QMatrix Load");
			File fFile = new File(filename);
			Scanner s = new Scanner(new FileReader(fFile));
			try {
				while (s.hasNextLine())
					parseQMatrixElement(s.nextLine());
			} finally {
				s.close();
			}
			QMatrixFilename = filename;
			explorationRate = 0;
		}
	}

	public static void reset() {
		QMatrixFilename = "";
	}

	public QInterpreterMovement(String qmatrixfilename) throws Exception {
		super();
		if (qMatrix.isEmpty()) {
			File fFile = new File(qmatrixfilename);
			Scanner s = new Scanner(new FileReader(fFile));
			try {
				while (s.hasNextLine())
					parseQMatrixElement(s.nextLine());
			} finally {
				s.close();
			}
			QMatrixFilename = qmatrixfilename;
			explorationRate = 0;
		}
	}

	private void parseQMatrixElement(String input) {
		if (!input.startsWith("[")) {
			return;
		}

		Scanner scanner = new Scanner(input);

		QlearningTuple t;
		QLearningState s;
		QAction a = null;
		scanner.useDelimiter(Pattern.compile("[\\[\\],>(==)(\\s)+]"));
		int slicectr = 0;
		int vectorsize = 8;
		float result = 0;
		float[] slicevector = new float[8];
		String action;
		Pattern p = Pattern.compile("\\S+");
		while (scanner.hasNext()) {
			if (scanner.hasNext(p)) {
				if (scanner.hasNextFloat()) {
					if (slicectr >= vectorsize)
						result = scanner.nextFloat();
					else
						slicevector[slicectr++] = scanner.nextFloat();
				} else {
					action = scanner.next();
					if (action.equals("ACTION_BACK"))
						a = new QAction(ActionType.ACTION_BACK);

					else if (action.equals("ACTION_RIGHT"))
						a = new QAction(ActionType.ACTION_RIGHT);

					else if (action.equals("ACTION_LEFT"))
						a = new QAction(ActionType.ACTION_LEFT);

					else if (action.equals("ACTION_NONE"))
						a = new QAction(ActionType.ACTION_NONE);
					else
						System.out.println("->" + scanner.next());
				}
			} else
				scanner.next();

		}

		s = new QLearningState(slicevector);
		t = new QlearningTuple(s, a);
		qMatrix.put(t, result);
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "q-interpreter";
	}

}
