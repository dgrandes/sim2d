package com.pf.gui;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

@SuppressWarnings("serial")
public class ProgressBarFrame extends JInternalFrame {


	private ProgressBarPanel panel; 
	public ProgressBarFrame()
	{
		super("Training Progress");
        panel = new ProgressBarPanel();
        panel.setOpaque(true);
        setContentPane(panel);

	}
	
	public void updateProgress(float value)
	{
		panel.progressBar.setValue((int)(value*100));
	}
}


@SuppressWarnings("serial")
class ProgressBarPanel extends JPanel{

    public JProgressBar progressBar;
 
    public ProgressBarPanel() {
        super(new BorderLayout());
        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        //progressBar.setSize(width, height)
        JPanel panel = new JPanel();
       // panel.setPreferredSize(new Dimension(380,100));
        panel.add(progressBar);
        add(panel);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    }


}