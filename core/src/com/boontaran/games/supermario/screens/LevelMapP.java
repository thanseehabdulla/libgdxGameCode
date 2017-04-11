package com.boontaran.games.supermario.screens;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.boontaran.games.StageGame;
import com.boontaran.games.supermario.LevelButton;
import com.boontaran.games.supermario.SuperMario;

import static com.boontaran.games.supermario.SuperMario.atlas;

public class LevelMapP extends StageGame {
	public static final int ON_ICON_SELECTED = 1;
	public static final int ON_BACK = 2;
	public static final int Next = 4;
	public int selectedLevelId;

	public static int getRandomInteger(int max,int min){
		return ((int)(Math.random()*(max-min)))+min;
	}

	@Override
	protected void create() {
//int tmp=getRandomInteger(10,1);
		int tmp=1;
		if(tmp==1 || tmp ==6) {
			NinePatch patch = new NinePatch(atlas.findRegion("map_bg5"), 2, 2, 2, 2);
			Image bg = new Image(patch);
			fillScreen(bg, true, true);
			// fitSize(bg,100,100,false,false);
			addChild(bg);
		}else if(tmp ==2 || tmp ==7){
			NinePatch patch = new NinePatch(atlas.findRegion("map_bg2"), 2, 2, 2, 2);
			Image bg = new Image(patch);
			fillScreen(bg, true, true);
			// fitSize(bg,100,100,false,false);
			addChild(bg);
		}else if(tmp==3 || tmp==8){
			NinePatch patch = new NinePatch(atlas.findRegion("map_bg3"), 2, 2, 2, 2);
			Image bg = new Image(patch);
			fillScreen(bg, true, true);
			// fitSize(bg,100,100,false,false);
			addChild(bg);
		}else if(tmp==4 ||tmp==9){
			NinePatch patch = new NinePatch(atlas.findRegion("map_bg4"), 2, 2, 2, 2);
			Image bg = new Image(patch);
			fillScreen(bg, true, true);
			// fitSize(bg,100,100,false,false);
			addChild(bg);
		}else if(tmp==5 || tmp ==10){
			NinePatch patch = new NinePatch(atlas.findRegion("map_bg5"), 2, 2, 2, 2);
			Image bg = new Image(patch);
			fillScreen(bg, true, true);
			// fitSize(bg,100,100,false,false);
			addChild(bg);
		}
		//total score
		int totalScore=0;

		int curLevelProgress = 1+SuperMario.data.getLevelProgress();


//		//world 1
//		LevelButton level1 = new LevelButton(1, "       PLAY ");
//		addChild(level1);
//centerActorX(level1);
//		level1.setX(level1.getWidth()-120);
//		level1.setY(getHeight() - level1.getHeight() - 450);
//		level1.addListener(levelButtonListener);
//		totalScore += SuperMario.data.getScore(1);



//
////		if(level5.getId() > curLevelProgress) {
////			level5.lock();
////		}
//		totalScore += SuperMario.data.getScore(5);



//		//world 6
//		LevelButton level6 = new LevelButton(2, "    NEXT SCENARIO     ");
//		addOverlayChild(level6);
//		centerActorX(level6);
//		//centerActorX(level6);
//         level6.setX(level6.getWidth()+50);
//		level6.setY(getHeight() - level1.getHeight() - 450);
//		level6.addListener(levelButtonListener2);
//
//		//world 6
//		LevelButton level7 = new LevelButton(777, "    BACK     ");
//		addOverlayChild(level7);
//		centerActorX(level7);
//		//centerActorX(level6);
//		level7.setX(level6.getWidth()+380);
//		level7.setY(getHeight() - level1.getHeight() );
//		level7.addListener(levelButtonListener3);

	}
	//if icon clicked
	private ClickListener levelButtonListener = new ClickListener(){

		@Override
		public void clicked(InputEvent event, float x, float y) {
			LevelButton icon = (LevelButton)event.getTarget();
			
			if(icon.isLocked()) return;
			
			
			
			//note the selected id
			selectedLevelId = icon.getId();
			
			//notify main program
			call(ON_ICON_SELECTED);
			
			SuperMario.media.playSound("click");
			
			
		}
		
	};


	//if icon clicked
	private ClickListener levelButtonListener2 = new ClickListener(){

		@Override
		public void clicked(InputEvent event, float x, float y) {
			//LevelButton icon = (LevelButton)event.getTarget();
			//notify main program
			//note the selected id
			//selectedLevelId = icon.getId();
				call(Next);

			SuperMario.media.playSound("click");


		}

	};

	//if icon clicked
	private ClickListener levelButtonListener3 = new ClickListener(){

		@Override
		public void clicked(InputEvent event, float x, float y) {
			//LevelButton icon = (LevelButton)event.getTarget();
			//notify main program
			//note the selected id
			//selectedLevelId = icon.getId();
			call(ON_BACK);

			SuperMario.media.playSound("click");


		}

	};



	@Override
	public boolean keyDown(int keycode) {
		if(keycode == Keys.ESCAPE || keycode == Keys.BACK){  //if back key is pressed
			call(ON_BACK);
			SuperMario.media.playSound("click");
			
			return true;
		}
		return super.keyDown(keycode);
	}

	@Override
	public void dispose() {
		// Clear you Screen Explicitly


	}

}
