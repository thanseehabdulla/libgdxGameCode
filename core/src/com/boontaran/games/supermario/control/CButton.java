package com.boontaran.games.supermario.control;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class CButton extends Group {
	//the states
	private Image up,down;
	private boolean isPressed;
	private int i=0;
	
	public CButton(Image up, Image down , float minHeight) {
		this.up = up;
		this.down = down;
		
		//resize if less than min size
		if(up.getHeight() < minHeight) {
			up.setSize(minHeight, minHeight);
			down.setSize(minHeight, minHeight);
		}
		
		//add the image
		addActor(down);
		addActor(up);
		
		setSize(down.getWidth(), down.getHeight());
		i++;
		if(i==1) {
			this.up.setVisible(true);
		}else{
			this.up.setVisible(false);
		}
		this.up.setVisible(true);
		this.down.setVisible(false);
		
		//monitor the touch down and up
		addListener(new ClickListener(){
			
			//switch the display 
			@Override
			public boolean touchDown(InputEvent event, float x, float y,int pointer, int button) {
				isPressed = true;
				CButton.this.up.setVisible(false);
				CButton.this.down.setVisible(true);
				return true;
			}
			
			//switch the display
			@Override
			public void touchUp(InputEvent event, float x, float y,	int pointer, int button) {
				CButton.this.up.setVisible(true);
				CButton.this.down.setVisible(false);
				isPressed = false;
			}
			
		});
	}



	public boolean isPressed() {
		return isPressed;
	}

}
