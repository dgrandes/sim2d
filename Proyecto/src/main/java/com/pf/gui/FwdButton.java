package com.pf.gui;


public class FwdButton extends GuiButton {

	public FwdButton(String key, int index) {
		super(key, index);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onLoad()
	{
		button.setEnabled(false);
	}

	@Override
	public void onReplayLoaded()
	{
		setListener("fwd");
		button.setEnabled(true);
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
	
	public void onReplayRew()
	{
		button.setEnabled(true);
	}
	
	@Override
	public void onReplayEnd()
	{
		button.setEnabled(false);
	}
	
	@Override
	public void onReplayStart()
	{
		button.setEnabled(true);
	}

	@Override
	public void onReplayStop()
	{
		button.setEnabled(false);
	}
}
