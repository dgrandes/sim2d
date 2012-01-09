package com.pf.gui;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Observable;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import com.pf.simulator.Simulator;

public class ButtonPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	public HashMap<String, GuiButton> mybuttons = new HashMap<String, GuiButton>();

	SwingWorker worker;

	public ButtonPanel(Observable state) {

		GuiButton Play, Stop, Rew, Rec, Fwd, End, Start;
		Play = new PlayButton("play", 4);
		Play.addIcon("play", new ImageIcon(ClassLoader.getSystemResource("play.png")));
		Play.setIcon("play");
		Play.addIcon("pause", new ImageIcon(ClassLoader.getSystemResource("pause.png")));
		Stop = new StopButton("stop",2);

		
		Stop.addIcon("stop", new ImageIcon(ClassLoader.getSystemResource("stop.png")));
		Stop.setIcon("stop");

		/*
		 * Start = new startButton("start",new
		 * ImageIcon("src/images/start.png")); Rew = new rewButton("rew",new
		 * ImageIcon("src/images/rew.png"));
		 * 
		 * Rec = new RecButton("rec",new ImageIcon("src/images/rec.png")); Fwd =
		 * new FwdButton("fwd",new ImageIcon("src/images/fwd.png")); End = new
		 * EndButton("end", new ImageIcon("src/images/end.png"));
		 * 
		 * Play.addListener("play", playListener); Play.addListener("pause",
		 * pauseListener); Stop.addListener("stop", stopListener);
		 * Rec.addListener("rec", recListener);
		 */
		Rec = new RecButton("rec",3);
		Rec.addIcon("rec", new ImageIcon(ClassLoader.getSystemResource("rec.png")));
		Rec.setIcon("rec");
		Rec.addListener("rec", recListener);
		Play.addListener("play", playListener);
		Play.addListener("replay", replayListener);
		Play.addListener("pause", pauseListener);
		Stop.addListener("stop", stopListener);
		Stop.addListener("replay", stopReplay);
		Play.addListener("pauseReplay", pauseReplayListener);
		
		
		Start = new StartButton("start",0);
		Start.addIcon("start", new ImageIcon(ClassLoader.getSystemResource("start.png")));
		Start.setIcon("start");
		Start.addListener("start", restartReplayListener);
		End = new EndButton("end",6);
		End.addIcon("end", new ImageIcon(ClassLoader.getSystemResource("end.png")));
		End.setIcon("end");
		End.addListener("end", endListener);
		Fwd = new FwdButton("fwd",5);
		Fwd.addIcon("fwd", new ImageIcon(ClassLoader.getSystemResource("fwd.png")));
		Fwd.setIcon("fwd");
		Fwd.addListener("fwd", fwdListener);
		Rew = new RewButton("rew",1);	
		Rew.addIcon("rew", new ImageIcon(ClassLoader.getSystemResource("rew.png")));
		Rew.setIcon("rew");
		Rew.addListener("rew", rewListener);
		mybuttons.put("end", End);
		mybuttons.put("fwd",Fwd);
		mybuttons.put("play", Play);
		mybuttons.put("rec", Rec);
		mybuttons.put("stop", Stop);
		mybuttons.put("rew", Rew);
		mybuttons.put("start",Start);

		for(int i = 0; i <= 6; i++)
		{
			for (GuiButton b : mybuttons.values())
			{
				if(b.index == i)
				{
					state.addObserver(b);
					add(b.button,b.index);
					break;
				}
			}	
		}
		
			

	}

	ActionListener playListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {

			Simulator.playSimulation();

		}

	};

	ActionListener stopListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {

			GuiStates state = Simulator.getGuiFrame().state.getCurrentState();
			if (state == GuiStates.recording)
				Simulator.stopRecording();
			else
				Simulator.stopSimulation();

		}

	};

	ActionListener pauseListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			Simulator.pauseSimulation();
		}

	};

	ActionListener recListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {

			Simulator.recSimulation();
		}

	};


	ActionListener rewListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			Simulator.rewReplay();

		}
	};

	ActionListener replayListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			Simulator.playReplay();
		}
	};

	ActionListener restartReplayListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			Simulator.restartReplay();
		}
	};

	ActionListener pauseReplayListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			Simulator.pauseReplay();

		}
	};

	ActionListener fwdListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			Simulator.fwdReplay();

		}
	};

	ActionListener endListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			Simulator.endReplay();

		}
	};

	ActionListener stopReplay = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			Simulator.stopReplay();

		}
	};

	public void addButton(Container c, String title, ActionListener listener) {
		JButton button = new JButton(title);
		c.add(button);
		button.addActionListener(listener);
	}

	Object doReplayWork() throws Exception {
		System.out.println("Replay Started");
		/*
		 * for(int i = 0; i < Simulator.getReplay().howManySteps(); i++) {
		 * System
		 * .out.println(i+" step de "+Simulator.getReplay().howManySteps()); if
		 * (!pause && !Simulator.getReplay().replayStep()) { float sleep = (1 /
		 * (float) Simulator.getReplay().getFps())*1000;
		 * System.out.println("going to sleep for "+sleep);
		 * Thread.sleep((long)sleep); } else { if(!pause) return
		 * "Replay Terminated"; try { Thread.sleep(1000); } catch
		 * (InterruptedException e) { if (reset) { reset = false; return
		 * "Replay Reseted"; } } } } return "Replay Terminated";
		 */
		return null;

	}

};
