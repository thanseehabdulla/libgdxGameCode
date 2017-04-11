package com.boontaran.games.supermario.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.boontaran.games.StageGame;
import com.boontaran.games.supermario.LevelButton;
import com.boontaran.games.supermario.PauseDialog2;
import com.boontaran.games.supermario.SuperMario;

public class Intro extends StageGame {
	public static final int PLAY = 1;
	public static final int PLAY2 = 2;
	private PauseDialog2 pauseDialog;
	private int key=0;
	public static final int SURVIVAL=3;


	private final int SHOW_ADS = 1;
	private final int HIDE_ADS = 0;
	public static SuperMario game;

	public Intro(SuperMario game)
	{
		this.game = game;
	}

	@Override
	protected void create() {
		//intro bg
		final Image bg = new Image(SuperMario.atlas.findRegion("intro_bg"));
		//resize the bg to fill the screen, keep aspect ratio
		fillScreen(bg, true, false);
		addChild(bg);

		//game title
		Image title = new Image(SuperMario.atlas.findRegion("title"));
		centerActorX(title);

		title.setY(getHeight() - title.getHeight() - 100);
		addChild(title);
		
//		//play button
//		ImageButton playBtn = new ImageButton(
//				new TextureRegionDrawable(SuperMario.atlas.findRegion("play_btn")),
//				new TextureRegionDrawable(SuperMario.atlas.findRegion("play_btn_down")));
//		centerActorX(playBtn);
//		playBtn.setY(100);
//		addChild(playBtn);


		//world 6
		final LevelButton level6 = new LevelButton(9999, "    STORY MODE    ");
		addOverlayChild(level6);
		centerActorX(level6);
		//centerActorX(level6);
		level6.setX(getWidth()-level6.getWidth());
		level6.setY(getHeight() - level6.getHeight()-30 );


		//world 6
		LevelButton level7 = new LevelButton(9999, "   SURVIVAL MODE  ");
		addOverlayChild(level7);
		centerActorX(level7);
		//centerActorX(level6);
		level7.setX(getWidth()-level6.getWidth());
		level7.setY(getHeight() - level6.getHeight()-100 );

		//world 6
		LevelButton level8 = new LevelButton(9999, "   OPTION             ");
		addOverlayChild(level8);
		centerActorX(level8);
		//centerActorX(level6);
		level8.setX(getWidth()-level6.getWidth());
		level8.setY(getHeight() - level6.getHeight()-170 );




		//world 6
		LevelButton level11 = new LevelButton(9999, "   LEADERBOARD          ");
		addOverlayChild(level11);
		centerActorX(level11);
		//centerActorX(level6);
		level11.setX(getWidth()-level6.getWidth()-630);
		level11.setY(getHeight() - level6.getHeight()-100 );

		level11.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				SuperMario.media.playSound("click");
				game.playServices.showAchievement();
			}

		});



		//world 6
		LevelButton level9 = new LevelButton(9999, "   ACTORS          ");
		addOverlayChild(level9);
		centerActorX(level9);
		//centerActorX(level6);
		level9.setX(getWidth()-level6.getWidth()-630);
		level9.setY(getHeight() - level6.getHeight()-200 );

//world 6
		LevelButton level10;

			 level10= new LevelButton(9999, "   EXIT                  ");
		addOverlayChild(level10);
			centerActorX(level10);
			//centerActorX(level6);
			level10.setX(getWidth() - level6.getWidth());
			level10.setY(getHeight() - level6.getHeight() - 240);
			//btn listener

		level10.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				SuperMario.media.playSound("click");
				Gdx.app.exit();

			}

		});


		level6.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				SuperMario.media.playSound("click");
				call(PLAY);
			}
			
		});

		level7.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				SuperMario.media.playSound("click");
				call(SURVIVAL);
			}

		});

		level9.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				SuperMario.media.playSound("click");
				call(PLAY2);
				game.playServices.showInterstitialAd();
				}

		});
		level8.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				SuperMario.media.playSound("click");

				//show the paused dialog
				pauseDialog = new PauseDialog2();
				addOverlayChild(pauseDialog);
				centerActorXY(pauseDialog);
				key = 0;

			}

		});





key=1;



	}

	@Override
	public boolean keyDown(int keycode) {

		if (keycode == Keys.ESCAPE || keycode == Keys.BACK) { //if the back key pressed
			if (key == 0) {
				pauseDialog.remove();
			} else {

				dispose();
				Gdx.app.exit();
				System.exit(0);


			}
			return true;
		}


		return super.keyDown(keycode);
	}


	
	
	

}
