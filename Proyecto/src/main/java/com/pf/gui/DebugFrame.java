package com.pf.gui;

import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.pf.model.Agent;
import com.pf.simulator.SimEnvironment;

@SuppressWarnings("serial")
public class DebugFrame extends JInternalFrame {

	private Agent a;
	private static int count = 0;
	private static final int debugWidth = 380;
	private static final int debugHeight = 160;
	private static final int debugX = 0;
	private static final int debugY = 280;

	private JTextField posTx;
	private JTextField velTx;
	private JTextField granTx;
	private JTextField desTx;
	private JTextField contactTx;

	private JTextField posTy;
	private JTextField velTy;
	private JTextField granTy;
	private JTextField desTy;
	private JTextField contactTy;
	private SimEnvironment env;
	private DebugPanel dpanel;

	public DebugFrame(Agent a, SimEnvironment e) {
		super("#" + (count + 1) + ":Agent(" + a.id + ")", true, true, true,
				true);
		this.a = a;
		env = e;
		// setSize(debugWidth,debugHeight);
		setLocation(debugX, (count % 3) * debugY);
		dpanel = new DebugPanel();
		//		
		add(dpanel);
	}

	public void Update() {
		dpanel.Update();
	}

	public class DebugPanel extends JPanel {

		public DebugPanel() {
			super();
			setPreferredSize(new Dimension(debugWidth - 10, debugHeight));
			setLayout(new GridLayout(6, 3));

			add(new JLabel(""));
			add(new JLabel("X"));
			add(new JLabel("Y"));
			add(new JLabel("Pos:"));
			posTx = new JTextField(Float.toString(a.position.getX()));
			posTy = new JTextField(Float.toString(a.position.getY()));
			add(posTx);
			add(posTy);

			add(new JLabel("Vel:"));
			velTx = new JTextField(Float.toString(a.velocity.getX()));
			velTy = new JTextField(Float.toString(a.velocity.getY()));
			add(velTx);
			add(velTy);
			add(new JLabel("Granular:"));
			granTx = new JTextField(Float.toString(a.granularForce.getX()));
			granTy = new JTextField(Float.toString(a.granularForce.getY()));
			add(granTx);
			add(granTy);
			add(new JLabel("Contact:"));
			contactTx = new JTextField(Float.toString(a.contactForce.getX()));
			contactTy = new JTextField(Float.toString(a.contactForce.getY()));
			add(contactTx);
			add(contactTy);
			add(new JLabel("Desire:"));
			desTx = new JTextField(Float.toString(a.desireForce.getX()));
			desTy = new JTextField(Float.toString(a.desireForce.getY()));
			add(desTx);
			add(desTy);

		}

		public void Update() {
			Agent localA;
			try {
				localA = env.getAgentManager().getAgent(a.id);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				return;
			}
			posTx.setText(Float.toString(localA.position.getX()));
			posTy.setText(Float.toString(localA.position.getY()));
			velTx.setText(Float.toString(localA.velocity.getX()));
			velTy.setText(Float.toString(localA.velocity.getY()));
			granTx.setText(Float.toString(localA.granularForce.getX()));
			granTx.setText(Float.toString(localA.granularForce.getY()));
			contactTx.setText(Float.toString(localA.contactForce.getX()));
			contactTx.setText(Float.toString(localA.contactForce.getY()));
			desTx.setText(Float.toString(localA.desireForce.getX()));
			desTy.setText(Float.toString(localA.desireForce.getX()));
		}
	}

}
