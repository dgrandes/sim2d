package com.pf.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.pf.simulator.Simulator;

public class DialogOption extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JFormattedTextField tf;
	String title;

	public enum Type {
		FRAMESKIP, RECFRAMESKIP, REPLAY_FPS
	}

	public DialogOption(Type t) {
		JPanel panel = new JPanel();
		JLabel label = new JLabel();
		JButton button = new JButton("OK");
		switch (t) {
		case FRAMESKIP: {
			setTitle("Frames to Skip");
			label = new JLabel("Frames");
			tf = new JFormattedTextField(NumberFormat.getIntegerInstance());
			button.addActionListener(new FrameskipConfirm());
			tf.setValue((Object) Simulator.getSimulation().getFrameskip());
		}
			break;
		case RECFRAMESKIP: {
			setTitle("Rec Frame Skip");
			label = new JLabel("Frames");
			tf = new JFormattedTextField(NumberFormat.getIntegerInstance());
			button.addActionListener(new RecFrameConfirm());
			tf.setValue((Object) Simulator.getSimulation().getRecframeskip());
		}
			break;

		case REPLAY_FPS:
		{
			setTitle("Replay Speed");
			label = new JLabel("Speed");
			tf = new JFormattedTextField(NumberFormat.getIntegerInstance());
			button.addActionListener(new ReplayFPSConfirm());
			try {
				tf.setValue((Object)Simulator.getReplay().getSpeed());
			} catch (Exception e) {
				Simulator.getGuiFrame().showError("No Replay loaded!", "Error setting Replay FPS");
				dispose();
				return;
			}		
		}break;
		}
		
		tf.setColumns(16);

		panel.add(label);
		panel.add(tf);
		panel.add(button);
		getContentPane().add(panel, BorderLayout.SOUTH);
		pack();

	}

	public class FrameskipConfirm implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			Long frameskip = (Long) tf.getValue();
			if (frameskip == null)
				frameskip = 1000L;
			Simulator.getSimulation().setFrameskip(frameskip);
			dispose();

		}

	}

	public class RecFrameConfirm implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			Long frameskip = (Long) tf.getValue();
			if (frameskip == null)
				frameskip = 1000L;
			Simulator.getSimulation().setRecframeskip(frameskip);
			dispose();

		}

	}
	
	public class ReplayFPSConfirm implements ActionListener{
		
		public void actionPerformed(ActionEvent e) {
			System.out.println("Text es "+tf.getText());
			Integer fps = Integer.valueOf((tf.getText()));
			if (fps == null)
				fps = 1;
			try {
				Simulator.getReplay().setSpeed(fps);
			} catch (Exception e1) {
				Simulator.getGuiFrame().showError("Replay Speed!", "Error setting Replay Speed");
			}
			dispose();

		}
		
	}

}
