package com.pf.gui;

import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.pf.simulator.Simulator;

@SuppressWarnings("serial")
public class ReplaySlider extends JInternalFrame {

	private ReplaySliderPanel panel; 
	
	public ReplaySlider()
	{
		super("Replay Progress");
        panel = new ReplaySliderPanel();
        setValue(0);
        panel.setOpaque(true);
        setContentPane(panel);

	}
	
	public void setValue(int value)
	{
		
		panel.updateMyValue(value);
		
	}

class ReplaySliderPanel extends JPanel implements ChangeListener,MouseListener {
	public JSlider slider;
	public int frameToJumpTo;
	public boolean enableJump = false;
	
	public void updateMyValue(int value)
	{
		slider.setValue(value);
	}
	public ReplaySliderPanel(){
		 super(new BorderLayout());
	        slider = new JSlider(0, 100);
	        
	        slider.addChangeListener(this);
	        slider.addMouseListener(this);
	        JPanel panel = new JPanel();
	        panel.add(slider);
	        add(panel);
	        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
	        
	}
	
    /** Listen to the slider. */
    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider)e.getSource();
        
        if(!source.getValueIsAdjusting()){
   /*     	if(enableJump)
        	{
        		
        		int progress = (int)source.getValue();
        		System.out.println("Jumpn to frame"+progress);
              //  
                enableJump = false;
        //	}*/
            
        }
    }
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		frameToJumpTo = slider.getValue();
		
		System.out.println("Solto el slider, cambio de frame a "+ frameToJumpTo);
		enableJump = true;
		Simulator.replay.setReplayStepWithPercentange(frameToJumpTo);

		
		
	}
	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}
}
