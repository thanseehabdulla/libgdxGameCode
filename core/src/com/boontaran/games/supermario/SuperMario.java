package com.boontaran.games.supermario;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.boontaran.games.GdxGame;
import com.boontaran.games.StageGame;
import com.boontaran.games.supermario.data.Data;
import com.boontaran.games.supermario.media.Media;
import com.boontaran.games.supermario.screens.Intro;
import com.boontaran.games.supermario.screens.LevelMap;
import com.boontaran.games.supermario.screens.LevelMap2;
import com.boontaran.games.supermario.screens.LevelMap3;
import com.boontaran.games.supermario.screens.LevelMap4;
import com.boontaran.games.supermario.screens.LevelMap5;
import com.boontaran.games.supermario.screens.LevelMap6;
import com.boontaran.games.supermario.screens.LevelMapP;
import com.boontaran.games.supermario.screens.LevelMapP2;
import com.boontaran.games.supermario.screens.LevelMapP3;
import com.boontaran.games.supermario.screens.LevelMapP4;
import com.boontaran.games.supermario.screens.LevelMapP5;
import com.boontaran.games.supermario.screens.LevelMaps;

public class SuperMario extends GdxGame {
	private static final String TAG = "SuperMario";

//	private final SuperMario supermario;

	//asset loader & the state
	private AssetManager manager;
	private boolean isLoadingAssets;

	//shared texture for the game
	public static TextureAtlas atlas;
	//font
	public static BitmapFont font1,font2;
	public static PlayServices playServices;


	//screens
	private Intro intro;
	private LevelMap map;

	//persistent data handling
	public static Data data;
	public static Media media;
	
	//game events
	
	public static final int LEVEL_FAILED = 101;
	public static final int LEVEL_COMPLETED = 102;
	public static final int LEVEL_PAUSED = 103;
	public static final int LEVEL_RESUMED = 104;
	
	
	
	private FPSLogger fpsLogger;
	private LevelMap2 map2;

//	public SuperMario() {
//		//World.debug = true;
//	}




	public SuperMario(PlayServices playServices) {
		this.playServices = playServices;
	}

	public SuperMario() {

	}




//	public SuperMario(SuperMario superMario) {
//		this.supermario=superMario;
//	}

	@Override
	public void create() {
		super.create();

//		if(playServices.isWifiConnected()) {playServices.showBannerAd();}
		
		Gdx.input.setCatchBackKey(true);
		data = new Data();
		
		if(Settings.PROFILE_GL) {
			GLProfiler.enable();
		}
		if(Settings.LOG_FPS) {
			fpsLogger = new FPSLogger();
		}
		
		Gdx.app.log(TAG, "libgdx version :"+com.badlogic.gdx.Version.VERSION);
		
		StageGame.setAppSize(960, 540);
		//World.debug = true;
		
		//load & generate bitmap font
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/SuperMario256.ttf"));
		
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.characters = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ,:.'%()-";
		parameter.size = 36;
		parameter.minFilter = TextureFilter.MipMapLinearNearest;
		parameter.magFilter = TextureFilter.Linear;
		parameter.genMipMaps = true;
		font1 = generator.generateFont(parameter);
		
		FreeTypeFontParameter parameter2 = new FreeTypeFontParameter();
		parameter2.characters = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ,:.'%()-";
		parameter2.size = 48;
		parameter2.minFilter = TextureFilter.MipMapLinearNearest;
		parameter2.magFilter = TextureFilter.Linear;
		parameter2.genMipMaps = true;
		font2 = generator.generateFont(parameter2);
		
		generator.dispose();
		
		
		//load assets
		manager = new AssetManager();
		manager.load("images/pack.atlas", TextureAtlas.class);

		//load sounds
		manager.load("sounds/click.mp3" , Sound.class);
		manager.load("sounds/jump.mp3" , Sound.class);
		manager.load("sounds/coin.ogg" , Sound.class);
		manager.load("sounds/coin2.ogg" , Sound.class);
		manager.load("sounds/hit.ogg" , Sound.class);
		manager.load("sounds/hit2.ogg" , Sound.class);
		manager.load("sounds/bullet_pack.mp3" , Sound.class);
		manager.load("sounds/bullet.mp3" , Sound.class);
		manager.load("sounds/bullet2.mp3" , Sound.class);
		manager.load("sounds/bullet3.mp3" , Sound.class);
		manager.load("sounds/flag.mp3" , Sound.class);
		manager.load("sounds/level_completed.mp3" , Sound.class);
		manager.load("sounds/dog1.ogg" , Sound.class);
		manager.load("sounds/dog2.mp3" , Sound.class);
		manager.load("sounds/run.mp3" , Sound.class);
		manager.load("sounds/elephant.mp3" , Sound.class);
		manager.load("sounds/eagle.mp3" , Sound.class);
		manager.load("sounds/hurt.mp3" , Sound.class);
		manager.load("sounds/alarm.mp3" , Sound.class);
		//load music
		manager.load("sounds/music/intro.ogg" , Music.class);
		manager.load("sounds/music/level.ogg" , Music.class);
		manager.load("sounds/music/track.ogg", Music.class);
		manager.load("sounds/music/track2.ogg", Music.class);
		manager.load("sounds/music/track3.ogg", Music.class);
		manager.load("sounds/music/track4.ogg", Music.class);
		manager.load("sounds/music/track5.ogg", Music.class);
		manager.load("sounds/music/track6.ogg", Music.class);
			
		isLoadingAssets = true;
	}
	private void onAssetsCompleted() {
		//show intro
		showIntro();
		
	}

	private void showIntro() {
		//create
		intro = new Intro(this);
		setScreen(intro);

		//listener
		intro.setCallback(new StageGame.Callback() {
			@Override
			public void call(int code) {
				//play btn clicked
				if(code == Intro.PLAY) {
					showLevelMap();
				}else if(code == Intro.SURVIVAL){
					showLevelMaps();
				}else if (code == Intro.PLAY2){
					showLevelMapp();
				}
			}

			@Override
			public void call(int code, int value) {

			}


		});

		//start the music
		SuperMario.media.playMusic("intro");

	}


	//showing level map with the icons
	private void showLevelMap() {
		//create
		map = new LevelMap();
		setScreen(map);

		//the callback
		map.setCallback(new StageGame.Callback() {
			@Override
			public void call(int code) {
				//icon selected, start level with the particular id
				if(code == LevelMap.ON_ICON_SELECTED) {
					startLevel(map.selectedLevelId);
				}else if(code == LevelMap.Next){
					showLevelMap2();
				}
				//back to intro screen
				else if(code == LevelMap.ON_BACK) {
					showIntro();
				}
			}

			@Override
			public void call(int code, int value) {

			}
		});

		SuperMario.media.stopMusic("level");
		SuperMario.media.playMusic("intro");

	}





	//showing level map with the icons
	private void showLevelMap2() {
		//create
		map2 = new LevelMap2();
		setScreen(map2);

		//the callback
		map2.setCallback(new StageGame.Callback() {
			@Override
			public void call(int code) {
				//icon selected, start level with the particular id
				if(code == LevelMap2.ON_ICON_SELECTED) {
					startLevel(map2.selectedLevelId);
				}else if(code == LevelMap2.Next){
					showLevelMap3();
				}
				//back to intro screen
				else if(code == LevelMap.ON_BACK) {
					showIntro();
				}
			}

			@Override
			public void call(int code, int value) {

			}
		});

		SuperMario.media.stopMusic("level");
		SuperMario.media.playMusic("intro");

	}



	//showing level map with the icons
	private void showLevelMap3() {
		//create
		final LevelMap3 map3 = new LevelMap3();
		setScreen(map3);

		//the callback
		map3.setCallback(new StageGame.Callback() {
			@Override
			public void call(int code) {
				//icon selected, start level with the particular id
				if(code == LevelMap3.ON_ICON_SELECTED) {
					startLevel(map3.selectedLevelId);
				}else if(code == LevelMap3.Next){
					showLevelMap4();
				}
				//back to intro screen
				else if(code == LevelMap3.ON_BACK) {
					showIntro();
				}
			}

			@Override
			public void call(int code, int value) {

			}
		});

		SuperMario.media.stopMusic("level");
		SuperMario.media.playMusic("intro");

	}

	//showing level map with the icons
	private void showLevelMap4() {
		//create
		final LevelMap4 map4 = new LevelMap4();
		setScreen(map4);

		//the callback
		map4.setCallback(new StageGame.Callback() {
			@Override
			public void call(int code) {
				//icon selected, start level with the particular id
				if(code == LevelMap4.ON_ICON_SELECTED) {
					startLevel(map4.selectedLevelId);
				}else if(code == LevelMap4.Next){
					showLevelMap5();
				}
				//back to intro screen
				else if(code == LevelMap4.ON_BACK) {
					showIntro();
				}
			}

			@Override
			public void call(int code, int value) {

			}
		});

		SuperMario.media.stopMusic("level");
		SuperMario.media.playMusic("intro");

	}

	//showing level map with the icons
	private void showLevelMap5() {
		//create
		final LevelMap5 map5 = new LevelMap5();
		setScreen(map5);

		//the callback
		map5.setCallback(new StageGame.Callback() {
			@Override
			public void call(int code) {
				//icon selected, start level with the particular id
				if(code == LevelMap5.ON_ICON_SELECTED) {
					startLevel(map5.selectedLevelId);
				}else if(code == LevelMap5.Next){
					showLevelMap6();
				}
				//back to intro screen
				else if(code == LevelMap5.ON_BACK) {
					showIntro();
				}
			}

			@Override
			public void call(int code, int value) {

			}
		});

		SuperMario.media.stopMusic("level");
		SuperMario.media.playMusic("intro");

	}


	//showing level map with the icons
	private void showLevelMap6() {
		//create
		final LevelMap6 map6 = new LevelMap6();
		setScreen(map6);

		//the callback
		map6.setCallback(new StageGame.Callback() {
			@Override
			public void call(int code) {
				//icon selected, start level with the particular id
				if(code == LevelMap6.ON_ICON_SELECTED) {
					startLevel(map6.selectedLevelId);
				}else if(code == LevelMap6.Next){
					showIntro();
				}
				//back to intro screen
				else if(code == LevelMap6.ON_BACK) {
					showIntro();
				}
			}

			@Override
			public void call(int code, int value) {

			}
		});

		SuperMario.media.stopMusic("level");
		SuperMario.media.playMusic("intro");

	}

	//showing level map with the icons
	private void showLevelMaps() {
		//create
		final LevelMaps maps1 = new LevelMaps();
		setScreen(maps1);

		//the callback
		maps1.setCallback(new StageGame.Callback() {
			@Override
			public void call(int code) {
				//icon selected, start level with the particular id
				if(code == LevelMaps.ON_ICON_SELECTED) {
					startLevel(maps1.selectedLevelId);
				}else if(code == LevelMaps.Next){
					showIntro();
				}
				//back to intro screen
				else if(code == LevelMap6.ON_BACK) {
					showIntro();
				}
			}

			@Override
			public void call(int code, int value) {

			}
		});

		SuperMario.media.stopMusic("level");
		SuperMario.media.playMusic("intro");

	}

	//showing level map with the icons
	private void showLevelMapp() {
		//create
		final LevelMapP maps1 = new LevelMapP();
		setScreen(maps1);

		//the callback
		maps1.setCallback(new StageGame.Callback() {
			@Override
			public void call(int code) {
				//icon selected, start level with the particular id
				if(code == LevelMapP.ON_ICON_SELECTED) {
					startLevel(maps1.selectedLevelId);
				}else if(code == LevelMapP.Next){
					showLevelMapp2();
				}
				//back to intro screen
				else if(code == LevelMapP.ON_BACK) {
					showIntro();
				}
			}

			@Override
			public void call(int code, int value) {

			}
		});





		SuperMario.media.stopMusic("level");
		SuperMario.media.playMusic("intro");

	}


	//showing level map with the icons
	private void showLevelMapp2() {
		//create
		final LevelMapP2 maps1 = new LevelMapP2();
		setScreen(maps1);

		//the callback
		maps1.setCallback(new StageGame.Callback() {
			@Override
			public void call(int code) {
				//icon selected, start level with the particular id
				if(code == LevelMapP2.ON_ICON_SELECTED) {
					startLevel(maps1.selectedLevelId);
				}else if(code == LevelMapP2.Next){
					showLevelMapp3();
				}
				//back to intro screen
				else if(code == LevelMapP2.ON_BACK) {
					showIntro();
				}
			}

			@Override
			public void call(int code, int value) {

			}
		});

		SuperMario.media.stopMusic("level");
		SuperMario.media.playMusic("intro");

	}

	//showing level map with the icons
	private void showLevelMapp3() {
		//create
		final LevelMapP3 maps1 = new LevelMapP3();
		setScreen(maps1);

		//the callback
		maps1.setCallback(new StageGame.Callback() {
			@Override
			public void call(int code) {
				//icon selected, start level with the particular id
				if(code == LevelMapP3.ON_ICON_SELECTED) {
					startLevel(maps1.selectedLevelId);
				}else if(code == LevelMapP3.Next){
					showLevelMapp4();
				}
				//back to intro screen
				else if(code == LevelMapP3.ON_BACK) {
					showIntro();
				}
			}

			@Override
			public void call(int code, int value) {

			}
		});

		SuperMario.media.stopMusic("level");
		SuperMario.media.playMusic("intro");

	}
	//showing level map with the icons
	private void showLevelMapp4() {
		//create
		final LevelMapP4 maps1 = new LevelMapP4();
		setScreen(maps1);

		//the callback
		maps1.setCallback(new StageGame.Callback() {
			@Override
			public void call(int code) {
				//icon selected, start level with the particular id
				if(code == LevelMapP4.ON_ICON_SELECTED) {
					startLevel(maps1.selectedLevelId);
				}else if(code == LevelMapP4.Next){
					showLevelMapp5();
				}
				//back to intro screen
				else if(code == LevelMapP4.ON_BACK) {
					showIntro();
				}
			}

			@Override
			public void call(int code, int value) {

			}
		});

		SuperMario.media.stopMusic("level");
		SuperMario.media.playMusic("intro");

	}

	//showing level map with the icons
	private void showLevelMapp5() {
		//create
		final LevelMapP5 maps1 = new LevelMapP5();
		setScreen(maps1);

		//the callback
		maps1.setCallback(new StageGame.Callback() {
			@Override
			public void call(int code) {
				//icon selected, start level with the particular id
				if(code == LevelMapP5.ON_ICON_SELECTED) {
					startLevel(maps1.selectedLevelId);
				}else if(code == LevelMapP5.Next){
					showIntro();
				}
				//back to intro screen
				else if(code == LevelMapP5.ON_BACK) {
					showIntro();
				}
			}

			@Override
			public void call(int code, int value) {

			}
		});

		SuperMario.media.stopMusic("level");
		SuperMario.media.playMusic("intro");

	}
	private void startLevel(int levelId) {
		Level level=null;
		
		//create "level" object based on the id
		level = new Level(levelId);
		setScreen(level);

		//set bg
		level.setBackgroundRegion("level_bg");
		
		//the level callback
		level.setCallback(new StageGame.Callback() {
			@Override
			public void call(int code) {
				//failed, back to map
				if(code == Level.FAILED) {
					showLevelMap();
				}
				
				//completed back to map also
				else if(code == Level.COMPLETED) {
					showLevelMap();	
				}
				
			}

			@Override
			public void call(int code, int value) {
				
			}
		});
		
		
		SuperMario.media.stopMusic("intro");
		SuperMario.media.playMusic("level");
		
	
	}
	
	
	@Override
	public void render() {
		
		//loading assets
		if(isLoadingAssets) {
			if(manager.update()) { //if assets loaded
				isLoadingAssets = false;
				atlas = manager.get("images/pack.atlas" ,TextureAtlas.class );
				media = new Media(manager);
				onAssetsCompleted();
			}
		}


		
		if(Settings.PROFILE_GL) {
			System.out.println("---------");
			System.out.println("draw calls : "+GLProfiler.drawCalls);
			System.out.println("texture bindings:"+GLProfiler.textureBindings);
			GLProfiler.reset();
		}
		if(Settings.LOG_FPS) {
			fpsLogger.log();
		}
		super.render();
	}


}

