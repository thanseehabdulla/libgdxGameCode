package com.boontaran.games.supermario;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.boontaran.games.platformerLib.Entity;

public class Timer extends Entity {
	//amount of bullet inside the stock
	private int amount;

	public Timer(int amount) {
		this.amount = amount;
		noGravity = true;
		
		//the image
		Image img = new Image(SuperMario.atlas.findRegion("timer"));
		setImage(img);
		setSize(53, 58);
	}
	public int getAmount() {
		return amount;
	}
	
	
	//set if float in air or not
	public void setFloating(boolean floating) {
		noGravity = floating;
	}

}
