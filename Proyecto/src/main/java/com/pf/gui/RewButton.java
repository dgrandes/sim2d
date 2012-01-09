package com.pf.gui;


public class RewButton extends GuiButton {

	public RewButton(String key, int index) {
		super(key, index);
	}

	@Override
	public void onLoad()
	{
		button.setEnabled(false);
	}

	@Override
	public void onReplayLoaded()
	{
		setListener("rew");
		button.setEnabled(false);
	}
	
	@Override
	public void onReplayPlaying()
	{
		button.setEnabled(true);
	}
	@Override
	public void onReplayFwd()
	{
		button.setEnabled(true);
	}
	
	@Override
	public void onReplayEnd()
	{
		button.setEnabled(true);
	}
	
	@Override
	public void onReplayStart()
	{
		button.setEnabled(false);
	}
	
	@Override
	public void onReplayStop()
	{
		button.setEnabled(false);
	}
}
