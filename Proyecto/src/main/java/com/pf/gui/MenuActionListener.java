package com.pf.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

import com.pf.gui.DialogOption.Type;
import com.pf.simulator.Simulator;

public class MenuActionListener {

	public final static JFileChooser fc = new JFileChooser();

	
	public static boolean loadAgents() {
		Simulator.getGuiFrame().showConfirmation("Select Agents File",
				"Loading Simulation");
		
		int returnVal = fc.showOpenDialog(null);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();

			String path = file.getAbsolutePath();
			try {
				Simulator.loadAgents(path);
				return true;
			} catch (Exception e1) {
				e1.printStackTrace();
				Simulator.getGuiFrame().showError("Error! "+e1.getMessage(),
						"Error Loading Agents");
				return false;
			}
		}
		return false;
	}



	public static boolean loadEnv() {
		Simulator.getGuiFrame().showConfirmation("Select Environment File",
				"Loading Simulation");
		
		int returnVal = fc.showOpenDialog(null);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			String path = file.getAbsolutePath();
			try {
				Simulator.loadEnvironment(path);
				return true;
			} catch (Exception e1) {
				Simulator.getGuiFrame().showError("Invalid Environment File!",
						"Error Loading Environment");
				return false;
			}
		}
		return false;

	}
	static class LoadSimulationActionListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			Simulator.unloadSimulation();
			if (!loadEnv())
				return;
			loadAgents();

		}
	}

	static class LoadAgentsActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			loadAgents();
		}

	}

	static class LoadReplayActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			String refpath, steppath;
			refpath = "";
			steppath = "";
			Simulator.getGuiFrame().showConfirmation("Select Reference File",
					"Loading Replay");

			int returnVal = fc.showOpenDialog(null);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				refpath = file.getAbsolutePath();

			}
			else 
				return;

			try {
				Simulator.loadReplayReference(refpath);
			} catch (Exception ex) {
				System.err.println("Replay Loading Failed");
				return;
			}

			Simulator.getGuiFrame().showConfirmation("Select Steps File",
					"Loading Replay");

			returnVal = fc.showOpenDialog(null);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				steppath = file.getAbsolutePath();

			}

			try {
				Simulator.loadReplaySteps(steppath);
			} catch (Exception ex) {
				System.err.println("Replay Loading Failed");
				return;
			}
			Simulator.loadReplay();

		}

	}

	static class LoadEnvironmentActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			loadEnv();
		}
	}

	static class SaveAgentsActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {


			int returnVal = fc.showSaveDialog(null);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				Simulator.simEnvironment.saveAgentsToFile(file
						.getAbsolutePath());
			}
		}

	}

	static class SaveEnvironmentActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {


			int returnVal = fc.showSaveDialog(null);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				Simulator.simEnvironment.saveEnvironmentToFile(file
						.getAbsolutePath());
			}
		}

	}

	static class SetFrameSkipActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			DialogOption f = new DialogOption(Type.FRAMESKIP);
			f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			f.setVisible(true);

		}

	}
	static class ShowQLOptions implements ActionListener {
		
		public void actionPerformed(ActionEvent arg0)
		{
			
			QLearnOptions f;
			try {
				f = new QLearnOptions();
				f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				f.setVisible(true);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Simulator.getGuiFrame().showError(e.getMessage(), "Error");
				return;
			}
			
		}
	}

	static class SetRecFrameSkipActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			DialogOption f = new DialogOption(Type.RECFRAMESKIP);
			f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			f.setVisible(true);

		}

	}

/*	static class SaveSimulationActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			GuiStates rec = Simulator.getGuiFrame().state.getCurrentState();
			if (rec != GuiStates.recording) {
				JOptionPane.showMessageDialog(null, "You Are Not Recording!");
				return;
			}

			Simulator.pauseSimulation();

			final JFileChooser fc = new JFileChooser();

			int returnVal = fc.showSaveDialog(null);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				Simulator.saveSimulationLog(file.getAbsolutePath());

			}

			Simulator.stopSimulation();
		}

	}*/

	static class SetReplayFPSListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			DialogOption f = new DialogOption(Type.REPLAY_FPS);
			f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			f.setVisible(true);

		}

	}
}
