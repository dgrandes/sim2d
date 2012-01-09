package com.pf.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.pf.movement.qlearning.QLearningMovement;
import com.pf.simulator.Simulator;

public class QLearnOptions extends JFrame   {

	private static final long serialVersionUID = 1L;
	GridLayout layout = new GridLayout(6,2);
	public QLearningMovement mvt;

	public JTextField tfIterLength = new JTextField() ;
	public JTextField tfIterQty = new JTextField(); 
	public JTextField tfMaxView = new JTextField(); 
	public JTextField tfLearnRate = new JTextField();
	public JTextField tfDiscFactor = new JTextField() ;
	public QLearnOptions() throws Exception
	{
	
		try{
			Simulator.getFirstQLearningAgentId();
		}catch(Exception e)
		{
			throw new Exception("No QLearning agents have been loaded!");

		}
		if(Simulator.getGuiFrame().state.getCurrentState() == GuiStates.recording)
			throw new Exception("Can't change values during recording");
		layout.setHgap(10);
		layout.setVgap(10);


		JPanel mainpanel = new JPanel();
		addFieldsToPanel(mainpanel);
		add(mainpanel, BorderLayout.CENTER);
		JPanel buttonPanel = new JPanel();
		addButtonsToPanel(buttonPanel);
		add(buttonPanel, BorderLayout.SOUTH);
		updateTextFields();
		pack();
		setVisible(true);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}
	
	public void addButtonsToPanel(JPanel panel)
	{
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		
		panel.add(cancel);
		JButton confirm = new JButton("Confirm");
		confirm.addActionListener(new Confirm(this));
		panel.add(confirm);
	}
	public void addFieldsToPanel(JPanel panel)
	{
		panel.setLayout(layout);
		panel.add(new JLabel("Iteration Length"));
		panel.add(tfIterLength);
		panel.add(new JLabel("Iteration Qty"));
		panel.add(tfIterQty);
		panel.add(new JLabel("Learning Rate"));
		panel.add(tfLearnRate);
		panel.add(new JLabel("Discount Factor"));
		panel.add(tfDiscFactor);
		panel.add(new JLabel("Max View Dist"));
		panel.add(tfMaxView);
		
		
	}
	
	public void updateTextFields()
	{
		tfIterLength.setText(new Integer(QLearningMovement.ITERATION_LENGTH).toString());
		tfIterQty.setText(new Float(QLearningMovement.ITERATION_QTY).toString());
		tfLearnRate.setText(new Float(QLearningMovement.learningRate).toString());
		tfDiscFactor.setText(new Float(QLearningMovement.discountFactor).toString());
		tfMaxView.setText(new Float(QLearningMovement.maxViewDistance).toString());
	}
	



	public class Confirm implements ActionListener
	{

		QLearnOptions frame;
		public Confirm(QLearnOptions frame)
		{	
			this.frame = frame;
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			
			try {
				QLearningMovement.setITERATION_LENGTH(Integer.parseInt(tfIterLength.getText()));
				QLearningMovement.setITERATION_QTY( Float.parseFloat(tfIterQty.getText()));
				QLearningMovement.setDiscountFactor(Float.parseFloat(tfDiscFactor.getText()));
				QLearningMovement.setLearningRate(Float.parseFloat(tfLearnRate.getText()));
				QLearningMovement.setMaxViewDistance(Float.parseFloat(tfMaxView.getText()));
			} catch (NumberFormatException e1) {
				Simulator.getGuiFrame().showError("All values must be numbers!", "Error");
				return;
			} catch (Exception e1) {
				Simulator.getGuiFrame().showError(e1.getMessage(),"Error");
				return;
			}

			dispose();
			Simulator.getGuiFrame().showConfirmation("Values changed sucessfully!", "Success");
			
		}
		
	}
}
