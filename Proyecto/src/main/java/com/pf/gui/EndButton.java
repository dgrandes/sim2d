package com.pf.gui;


public class EndButton extends GuiButton {

	public EndButton(String key, int index) {
		super(key, index);
		// TODO Auto-generated constructor stub
	}


	@Override
	public void onReplayLoaded()
	{
		button.setEnabled(true);
		setListener("end");
	}
	
	@Override
	public void onReplayEnd()
	{
		button.setEnabled(false);
	}
	
	@Override
	public void onLoad()
	{
		button.setEnabled(false);
	}


}
