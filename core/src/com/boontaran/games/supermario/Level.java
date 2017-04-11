package com.boontaran.games.supermario;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Pool;
import com.boontaran.MessageListener;
import com.boontaran.games.platformerLib.Entity;
import com.boontaran.games.platformerLib.World;
import com.boontaran.games.supermario.control.CButton;
import com.boontaran.games.supermario.control.JoyStick;
import com.boontaran.games.supermario.enemies.Enemy;
import com.boontaran.games.supermario.enemies.Enemy1;
import com.boontaran.games.supermario.enemies.Enemy2;
import com.boontaran.games.supermario.enemies.Enemy3;
import com.boontaran.games.supermario.enemies.Enemy4;
import com.boontaran.games.supermario.enemies.Enemy5;
import com.boontaran.games.supermario.enemies.Enemy6;
import com.boontaran.games.supermario.panel.BombCounter;
import com.boontaran.games.supermario.panel.BulletCounter;
import com.boontaran.games.supermario.panel.BunCounter;
import com.boontaran.games.supermario.panel.CoinCounter;
import com.boontaran.games.supermario.panel.HealthBar;
import com.boontaran.games.supermario.panel.ScoreCounte;
import com.boontaran.games.supermario.panel.ScoreCounter;
import com.boontaran.games.tiled.TileLayer;

public class Level extends World {
	//tmx filename & location
	protected String tmxFile;
	
	//values based on the tmx file
	private int mapWidth,mapHeight,tilePixelWidth,tilePixelHeight;
	private int levelWidth , levelHeight;
		
	//level states
	protected static final int GAME_PLAY = 1;
	protected static final int GAME_COMPLETED = 2;
	protected static final int GAME_PAUSED= 3;
	protected int state;
	
	//key states
	private boolean keyLeft,keyRight,keyJump,keyFire,keyBomb,keyBun;
	
	//entities
	protected Mario mario;
	private Flag flag;
	
	//level bg
	protected Image levelBg;
		
	//callback message code
	public static final int FAILED = 1;
	public static final int COMPLETED = 2;
	
	public int id; //id of level, should be unique..
	
	//top panel items
	private HealthBar healthBar;
	private CoinCounter coinCounter;
	private BombCounter bombCounter;
	private BunCounter bunCounter;
	private BulletCounter bulletCounter;
	private ScoreCounte scoreCounter;
	private ScoreCounter hp;
	
	//reference coin, the function is to synchronize all coins animation
	public static Coin coin;
	
	//level data
	private int numCoins,numBullets,numBombs,numBuns,score;
	
	//pause dialog
	private PauseDialog pauseDialog;
	
	//on screen virtual button
	private JoyStick joyStick;
	private CButton jumpBtn,bulletBtn,bombBtn,bunBtn;
	
	//pools
	//brick debris
	private Pool<Debris> poolDebris = new Pool<Debris>(){
		@Override
		protected Debris newObject() {
			return new Debris();
		}
	};
	
	//coins
	private Pool<Coin> poolCoins = new Pool<Coin>(){
		@Override
		protected Coin newObject() {
			return new Coin();
		}
	};
	
	//bullet
	private Pool<Bullet> poolBullets = new Pool<Bullet>(){

		@Override
		protected Bullet newObject() {
			return new Bullet();
		}
		
	};


	//bomb
	private Pool<Bomb> poolBombs = new Pool<Bomb>(){

		@Override
		protected Bomb newObject() {
			return new Bomb();
		}

	};


	//bun
	private Pool<Bun> poolBuns = new Pool<Bun>(){

		@Override
		protected Bun newObject() {
			return new Bun();
		}

	};

	//bullet explosion
	private Pool<BulletExp> poolBulletExps = new Pool<BulletExp>(){
		@Override
		protected BulletExp newObject() {
			
			final BulletExp exp = new BulletExp();
			exp.addListener(new MessageListener(){
				@Override
				protected void receivedMessage(int message, Actor actor) {
					if(message == BulletExp.COMPELTED) {
						removeEntity(exp);
						poolBulletExps.free((BulletExp) actor);
					}
				}});
			return exp;
		}
	};

    public static int getRandomInteger(int max,int min){
	return ((int)(Math.random()*(max-min)))+min;
    }
	//bomb explosion
	private Pool<BombExp> poolBombExps = new Pool<BombExp>(){
		@Override
		protected BombExp newObject() {

			final BombExp exp = new BombExp();
			exp.addListener(new MessageListener(){
				@Override
				protected void receivedMessage(int message, Actor actor) {
					if(message == BombExp.COMPELTED) {
						removeEntity(exp);
						poolBombExps.free((BombExp) actor);
					}
				}});
			return exp;
		}
	};

	//bun explosion
	private Pool<BunExp> poolBunExps = new Pool<BunExp>(){
		@Override
		protected BunExp newObject() {

			final BunExp exp = new BunExp();
			exp.addListener(new MessageListener(){
				@Override
				protected void receivedMessage(int message, Actor actor) {
					if(message == BunExp.COMPELTED) {
						removeEntity(exp);
						poolBunExps.free((BunExp) actor);
					}
				}});
			return exp;
		}
	};

	//score label
	private Pool<ToastLabel> poolScoreLabels = new Pool<ToastLabel>() {
		@Override
		protected ToastLabel newObject() {
			return new ToastLabel();
		}
		
	};
	private int timecount;
	private int worldtimer;

	private int ms;
	private int j=0;
	private int e=0;
	private int box2=0;

	// end pools
	


	public Level(int id) {
		this.id = id;
		tmxFile = "tiled/level"+id+".tmx";
		//World.debug = true;
	}
	public void setBackgroundRegion(String regionName) {
		int tmp = getRandomInteger(10,1);

		if(tmp==1 || tmp ==6){
			levelBg = new Image(SuperMario.atlas.findRegion(regionName));
			addBackground(levelBg, true, false);
		}else if(tmp==2 || tmp==7){
			levelBg = new Image(SuperMario.atlas.findRegion(regionName+2));
			addBackground(levelBg, true, false);
		}else if(tmp==3 || tmp==8)
		{
			levelBg = new Image(SuperMario.atlas.findRegion(regionName+3));
			addBackground(levelBg, true, false);
		}else if(tmp==4 || tmp==9)
		{
			levelBg = new Image(SuperMario.atlas.findRegion(regionName+4));
			addBackground(levelBg, true, false);
		}else if(tmp==5 || tmp==10 ) {
			levelBg = new Image(SuperMario.atlas.findRegion(regionName +5));
			addBackground(levelBg, true, false);
		}

	}

	@Override
	protected void create() {
		gravity.y = Settings.GRAVITY;
		init();

		if (tmxFile == null) {
			throw new Error("TMX file not defined yet !!!");
		}

		//prepare the reference coin
		Level.coin = new Coin();
		Level.coin.setAsRefference();
		addOverlayChild(Level.coin);
		Level.coin.setColor(1, 1, 1, 0);

		buildLevel();
		if (mario == null) {
			throw new Error("hero not defined yet in tmx file !!!");
		}

		//pull hero on top of other objects
		stage.addActor(mario);


		//camera follow hero
		camController.followObject(mario);
		//camera clamp, make sure it only showing game area
		camController.setBoundary(new Rectangle(0, 0, levelWidth, levelHeight));


		//the top panel
		//health bar
		healthBar = new HealthBar();
		addOverlayChild(healthBar);
		healthBar.setX(10);
		healthBar.setY(getHeight() - healthBar.getHeight()-5);

		//coin counter
		coinCounter = new CoinCounter();
		addOverlayChild(coinCounter);
		coinCounter.setX(healthBar.getRight() + 50);
		coinCounter.setY(getHeight() - coinCounter.getHeight()-5);
		//bullet
		bulletCounter = new BulletCounter();
		addOverlayChild(bulletCounter);
		bulletCounter.setX(coinCounter.getRight() + 30);
		bulletCounter.setY(coinCounter.getY() - 1);

		//bomb
		bombCounter = new BombCounter();
		addOverlayChild(bombCounter);
		bombCounter.setX(coinCounter.getRight() + 100);
		bombCounter.setY(coinCounter.getY() -1);


		//bun
		bunCounter = new BunCounter();
		addOverlayChild(bunCounter);
		bunCounter.setX(coinCounter.getRight() + 170);
		bunCounter.setY(coinCounter.getY() - 1);

		//score
		scoreCounter = new ScoreCounte();
		addOverlayChild(scoreCounter);
		scoreCounter.setX(coinCounter.getRight() + 270);
		scoreCounter.setY(getHeight() - scoreCounter.getHeight() - 25);



		//Hp+ score
		hp = new ScoreCounter();
		addOverlayChild(hp);
		hp.setX(getWidth() - hp.getWidth()+130);
		hp.setY(getHeight() -hp.getHeight() - 25);


		//create the on screen control
		float minHeight = mmToPx(10);
		joyStick = new JoyStick(minHeight);
		addOverlayChild(joyStick);
		joyStick.setPosition(15, 15);
		//on screen button
		jumpBtn = new CButton(new Image(SuperMario.atlas.findRegion("jump")), new Image(SuperMario.atlas.findRegion("jump_down")), minHeight);
		bulletBtn = new CButton(new Image(SuperMario.atlas.findRegion("bullet_btn")), new Image(SuperMario.atlas.findRegion("bullet_btn_down")), minHeight);

		bombBtn = new CButton(new Image(SuperMario.atlas.findRegion("bomb_btn")), new Image(SuperMario.atlas.findRegion("bomb_btn_down")), minHeight);


		bunBtn = new CButton(new Image(SuperMario.atlas.findRegion("bun_btn")), new Image(SuperMario.atlas.findRegion("bun_btn_down")), minHeight);
		jumpBtn.setPosition(getWidth() - jumpBtn.getWidth() - 15, 15);
		addOverlayChild(jumpBtn);

		bulletBtn.setPosition(jumpBtn.getX() - bulletBtn.getWidth() - 0.4f * bulletBtn.getWidth(), 15);
		addOverlayChild(bulletBtn);


		bombBtn.setPosition(getWidth() - jumpBtn.getWidth() - 15, 135);
		addOverlayChild(bombBtn);


		bunBtn.setPosition(getWidth() - jumpBtn.getWidth() - 15, 250);
		addOverlayChild(bunBtn);

		//camController.setDefaultZoom(1.5f);
		updatePanel();
		state = GAME_PLAY;


		if (id == 1) {
			worldtimer = 55;
			numCoins = 50;
		} else if (id == 2) {
			worldtimer = 50;
			numCoins = 55;
		} else if (id == 3) {
			worldtimer = 50;
			numCoins = 52;
		} else if (id == 4) {
			worldtimer = 50;
			numCoins = 52;
		} else if (id == 5) {
			worldtimer = 55;
			numCoins = 42;
			mario.fullHealth = 20;
			mario.health = 20;
		} else if (id == 6) {
			worldtimer = 50;
			numCoins = 52;
		} else if (id == 7) {
			worldtimer = 50;
			numCoins = 40;
		} else if (id == 8) {
			worldtimer = 50;
			numCoins = 40;
		} else if (id == 9) {
			worldtimer = 50;
			numCoins = 52;
		} else if (id == 10) {
			worldtimer = 50;
			numCoins = 52;
		} else if (id == 11) {
			worldtimer = 50;
			numCoins = 52;
		} else if (id == 12) {
			worldtimer = 50;
			numCoins = 52;
		} else if (id == 13) {
			worldtimer = 50;
			numCoins = 52;
		} else if (id == 14) {
			worldtimer = 50;
			numCoins = 52;
		} else {
			worldtimer = 57;
			numCoins = 30;
		}


	}

	

	
	protected void init() {
		
	}
	private void buildLevel() {
		//assign filter to avoid artifact when it resized to match screen size
		TmxMapLoader.Parameters params = new TmxMapLoader.Parameters();
		params.generateMipMaps = true;
		params.textureMinFilter = TextureFilter.MipMapLinearNearest;
		params.textureMagFilter = TextureFilter.Linear;
		 
		//get and calculate map size
		TiledMap map = new TmxMapLoader().load(tmxFile, params);
		MapProperties prop = map.getProperties();
		mapWidth = prop.get("width", Integer.class);
		mapHeight = prop.get("height", Integer.class);
		tilePixelWidth = prop.get("tilewidth", Integer.class);
		tilePixelHeight = prop.get("tileheight", Integer.class);
		levelWidth  = mapWidth * tilePixelWidth;
		levelHeight = mapHeight * tilePixelHeight;


		TileLayer tLayer=null;
		int layerId=0;
		//inspect the layers of tmx files
		for (MapLayer layer : map.getLayers()) {


			if(layer.getObjects().getCount() > 0) {
				tLayer = null;

				String name = layer.getName();
				if(name.equals("fixed")) {
					//build fixed object : land & platform
					createPlatform(layer.getObjects());
				}
				else if(name.equals("brick")) {
					//create brick, brick is a breakable item
					createBricks(layer.getObjects());
				}
				else if(name.equals("entity")) {
					//create game entity : hero, enemies, coins, etc
					createEntities(layer.getObjects());
				}
			}
			else {
				//tiled layer only, no object
				if(tLayer == null) {
					tLayer = new TileLayer(camera, map, layerId,stage.getBatch());
				} else {
					tLayer.addLayerId(layerId);
				}
				addChild(tLayer);
			}
			layerId++;
		}
		
		//pulll enemies on top of other items
		for(Entity ent : getEntityList()) {
			if(ent instanceof Enemy) {
				stage.addActor(ent);
			}
		}
		
		
		//create world boundary wall 
		//left
		Entity wall = new Entity();
		wall.setSize(10, levelHeight);
		wall.setPosition(-5, levelHeight/2);
		addLand(wall, false);
		
		//right
		wall = new Entity();
		wall.setSize(10, levelHeight);
		wall.setPosition(levelWidth + 5, levelHeight/2);
		addLand(wall, false);
		
		//top
		wall = new Entity();
		wall.setSize(levelWidth, 10);
		wall.setPosition(levelWidth/2, levelHeight + 15);
		addLand(wall, false);
		
	}
	
	//brick based on tmx file
	private void createBricks(MapObjects objects) {
		Rectangle rect;
		for (MapObject obj : objects) {
			rect = ((RectangleMapObject) obj).getRectangle();
			
			Brick brick = new Brick(rect);
			brick.setPosition(rect.x + rect.width / 2, rect.y + rect.height / 2);
			addLand(brick, true);
			
		}
	}

	private void createEntities(MapObjects objects) {
		Rectangle rect;
		for (MapObject obj : objects) {
			rect = ((RectangleMapObject) obj).getRectangle();
			String name = obj.getName();
		
			
			if(name.equals("mario")) {
				//create hero
				mario = new Mario(this);
				mario.setPosition(rect.x + rect.width/2, rect.y + mario.getHeight()/2);
				addEntity(mario);
				mario.addListener(heroListener);
			}
			else if(name.equals("coin")) {
				//create coin
				Coin coin = poolCoins.obtain();
				coin.setPosition(rect.x + rect.width/2, rect.y + rect.height/2);
				addEntity(coin);
				coin.setFloat();
			}
			else if(name.equals("mystery")) {
				//mystery box
				MysteryBox box = new MysteryBox(this, rect);
				box.setPosition(rect.x + rect.width/2, rect.y + rect.height/2);
				
				//addEntity(box);
				addLand(box, false);
				
				//if has coins, number of coin
				if(obj.getProperties().get("coins") != null) {
					int numCoin = Integer.valueOf(obj.getProperties().get("coins").toString());
					box.setCoin(numCoin);
				} 
				
				// has bullets
				else if(obj.getProperties().get("bullets") != null) {
					int numBullets = Integer.valueOf(obj.getProperties().get("bullets").toString());
					box.setBullet(numBullets);
				}


				// has bombs
				else if(obj.getProperties().get("bombs") != null) {
					int numBombs = Integer.valueOf(obj.getProperties().get("bombs").toString());
					box.setBomb(numBombs);
				}

				// has buns
				else if(obj.getProperties().get("buns") != null) {
					int numBuns = Integer.valueOf(obj.getProperties().get("buns").toString());
					box.setBun(numBuns);
				}
			}
			
			else if(name.equals("bullet_stock")) {
				//get the amount, and create the object
				int amount = Integer.valueOf(obj.getProperties().get("amount").toString());
				BulletStock stock = new BulletStock(amount);
				stock.setPosition(rect.x + rect.width / 2, rect.y + rect.height / 2);
				
				//float in the air
				stock.setFloating(true);
				addEntity(stock);
			}
			else if(name.equals("health")) {
				//get the amount, and create the object
				int amount = Integer.valueOf(obj.getProperties().get("amount").toString());
				Health stock = new Health(amount);
				stock.setPosition(rect.x + rect.width / 2, rect.y + rect.height / 2);

				//float in the air
				stock.setFloating(true);
				addEntity(stock);
			}
			else if(name.equals("timer")) {
				//get the amount, and create the object
				int amount = Integer.valueOf(obj.getProperties().get("amount").toString());
				Timer stock = new Timer(amount);
				stock.setPosition(rect.x + rect.width / 2, rect.y + rect.height / 2);

				//float in the air
				stock.setFloating(true);
				addEntity(stock);
			}


			else if(name.equals("bomb_stock")) {
				//get the amount, and create the object
				int amount = Integer.valueOf(obj.getProperties().get("amount").toString());
				BombStock stock = new BombStock(amount);
				stock.setPosition(rect.x + rect.width / 2, rect.y + rect.height / 2);

				//float in the air
				stock.setFloating(true);
				addEntity(stock);
			}


			else if(name.equals("bun_stock")) {
				//get the amount, and create the object
				int amount = Integer.valueOf(obj.getProperties().get("amount").toString());
				BunStock stock = new BunStock(amount);
				stock.setPosition(rect.x + rect.width / 2, rect.y + rect.height / 2);

				//float in the air
				stock.setFloating(true);
				addEntity(stock);
			}


			//finish flag
			else if(name.equals("flag")) {
				flag = new Flag();
				flag.setX(rect.x);
				flag.setY(rect.y + flag.getHeight()/2 - 2);
				addEntity(flag);
				flag.addListener(flagListener);
			}
			
			else if(name.equals("enemy1")) {
				//create Enemy1, set the position, and listener
				Enemy1 enemy = new Enemy1();
				enemy.setX(rect.x + (rect.width - enemy.getWidth())/2);
				enemy.setY(rect.y + (enemy.getHeight())/2);
				addEntity(enemy);
				
				enemy.addListener(enemyListener);
			}
			else if(name.equals("enemy2")) {
				//create Enemy2, set the position, and listener
				Enemy2 enemy = new Enemy2();
				enemy.setX(rect.x + (rect.width - enemy.getWidth())/2);
				enemy.setY(rect.y + (enemy.getHeight())/2);
				addEntity(enemy);
				
				enemy.addListener(enemyListener);
			}
			else if(name.equals("enemy3")) {
				//create Enemy2, set the position, and listener
				Enemy3 enemy = new Enemy3();
				enemy.setX(rect.x + (rect.width - enemy.getWidth())/2);
				enemy.setY(rect.y + (enemy.getHeight())/2);
				addEntity(enemy);

				enemy.addListener(enemyListener);
			}else if(name.equals("enemy4")) {
				//create Enemy2, set the position, and listener
				Enemy4 enemy = new Enemy4();
				enemy.setX(rect.x + (rect.width - enemy.getWidth())/2);
				enemy.setY(rect.y + (enemy.getHeight())/2);
				addEntity(enemy);

				enemy.addListener(enemyListener);
			}else if(name.equals("enemy5")) {
				//create Enemy2, set the position, and listener
				Enemy5 enemy = new Enemy5();
				enemy.setX(rect.x + (rect.width - enemy.getWidth())/2);
				enemy.setY(rect.y + (enemy.getHeight())/2);
				addEntity(enemy);

				enemy.addListener(enemyListener);
			}else if(name.equals("enemy6")) {
				//create Enemy2, set the position, and listener
				Enemy6 enemy = new Enemy6();
				enemy.setX(rect.x + (rect.width - enemy.getWidth())/2);
				enemy.setY(rect.y + (enemy.getHeight())/2);
				addEntity(enemy);

				enemy.addListener(enemyListener);
			}

			else {
				createOtherEntity(obj);
			}
		}
	}
	protected void createOtherEntity(MapObject obj) {
		//sub class to implement
	}
	
	//the platform
	private void createPlatform(MapObjects objects) {
		for (MapObject obj : objects) {
			Rectangle rect = ((RectangleMapObject) obj).getRectangle();
				
			Entity ent = new Entity();
			ent.setSize(rect.width, rect.height);
			ent.setPosition(rect.x + rect.width / 2, rect.y + rect.height / 2);
			addLand(ent, true);
		}
	}
	
	// listeners
	private MessageListener heroListener = new MessageListener() {
		@Override
		protected void receivedMessage(int message, Actor actor) {
			if(message == Mario.HERO_DIE) {
				levelFailed();
			}
		}
	};
	private MessageListener enemyListener = new MessageListener(){
		@Override
		protected void receivedMessage(int message, Actor actor) {
			if(message == Enemy.DIE) {
				actor.removeListener(this);
			}
		}
		
	};
	private MessageListener labelListener = new MessageListener(){
		@Override
		protected void receivedMessage(int message, Actor actor) {
			if(message == ToastLabel.REMOVE) {
				removeChild(actor);
				actor.removeListener(this);
			}
		}
		
	};
	private MessageListener flagListener = new MessageListener() {
		@Override
		protected void receivedMessage(int message, Actor actor) {
			if(message == Flag.RAISED) { //if flag has been raised, it means level completed
				levelCompleted();
			}
		}
	};
	
	
	//update the value on panel items
	protected void updatePanel() {
		healthBar.setValue(mario.getHealthRatio());
		coinCounter.setCount(numCoins);
		bulletCounter.setCount(numBullets);
		bombCounter.setCount(numBombs);
		bunCounter.setCount(numBuns);
		scoreCounter.setScore(worldtimer);
		hp.setScore(score);

//		if(score%500>=0 && mario.health!=mario.fullHealth)
//		{
//			score=0;
//			mario.health++;
//		}

if(mario.isImmune())
	camController.setAutozoom(true);


//		if(mario.isDied())
//			camController.setDefaultZoom(0.6f);


		if(worldtimer==10)
			SuperMario.media.playSound("alarm");
		
		if(numBullets>0) {
			bulletBtn.setVisible(true);
		} else {
			bulletBtn.setVisible(false);
		}


		if(numBombs>0) {
			bombBtn.setVisible(true);
		} else {
			bombBtn.setVisible(false);
		}

		if(numBuns>0) {
			bunBtn.setVisible(true);
		} else {
			bunBtn.setVisible(false);
		}

	}
	public int getBulletCounts() {
		return numBullets;
	}

	public int getBombCounts() {
		return numBombs;
	}

	public int getBunCounts() {
		return numBuns;
	}
	
	
	
	//collision handling
	@Override
	public void onCollide(Entity entA, Entity entB, float delta) {
		
		//if hero touch something
		if(entA == mario) {
			heroHitObject(entB);
			return;
		} else if(entB == mario) {
			heroHitObject(entA);
			return;
		}
		
		//bullet hit enemy
		if(entA instanceof Bullet && entB instanceof Enemy) {
			bulletHitEnemy((Bullet)entA , (Enemy)entB);
			return;
		} else if(entB instanceof Bullet && entA instanceof Enemy) {
			bulletHitEnemy((Bullet)entB , (Enemy)entA);
			return;
		}

		//bomb hit enemy
		if(entA instanceof Bomb && entB instanceof Enemy) {
			bombHitEnemy((Bomb)entA , (Enemy)entB);
			return;
		} else if(entB instanceof Bomb && entA instanceof Enemy) {
			bombHitEnemy((Bomb)entB , (Enemy)entA);
			return;
		}


		//bun hit enemy
		if(entA instanceof Bun && entB instanceof Enemy) {
			bunHitEnemy((Bun)entA , (Enemy)entB);
			return;
		} else if(entB instanceof Bun && entA instanceof Enemy) {
			bunHitEnemy((Bun)entB , (Enemy)entA);
			return;
		}

		//enemy hit each other
		if(entA instanceof Enemy && entB instanceof Enemy) {
			enemyHitEnemy((Enemy)entA , (Enemy)entB);
			return;
		}
	}

	protected void toastLabel(String text, float x,float y) {
		toastLabel(text,x,y,0);
	}
	protected void toastLabel(String text, float x,float y,float time) {
		//get a label
		ToastLabel label = poolScoreLabels.obtain();
		//init
		label.init(text,time);
		
		//set position
		label.setPosition(x, y);
		addChild(label);
		
		label.addListener(labelListener);
	}
	

	private void enemyHitEnemy(Enemy enemyA , Enemy enemyB) {
		
		//turtle enemy slide other enemy
		if(enemyA instanceof Enemy2) {
			if(((Enemy2)enemyA).isSliding()) {
				enemySlideFriend(((Enemy2)enemyA) , enemyB);
				SuperMario.media.playSound("hit");
				return;
			}
		} 
		//turtle enemy slide other enemy
		if(enemyB instanceof Enemy2) {
			if(((Enemy2)enemyB).isSliding()) {
				enemySlideFriend(((Enemy2)enemyB) , enemyA);
				SuperMario.media.playSound("hit");
				return;
			}
		}
		
		//flip one or all enemy that hit between them, to prevent the display is stacked
		if(enemyA.getX() < enemyB.getX()) {
			if(enemyA.v.x > 0 ) {
				if(enemyB.v.x < 0) {
					enemyA.flip();
					enemyB.flip();
				} else {
					enemyA.flip();
				}
			} else {
				if(enemyB.v.x < 0) {
					enemyB.flip();
				} 
			}
		} else {
			if(enemyB.v.x > 0 ) {
				if(enemyA.v.x < 0) {
					enemyA.flip();
					enemyB.flip();
				} else {
					enemyB.flip();
				}
			} else {
				if(enemyA.v.x < 0) {
					enemyA.flip();
				} 
			}
		}
		
	}
	private void enemySlideFriend(Enemy2 slidingEnemy , Enemy enemy) {
		if(enemy.isHasDied()) {
			return;
		}
				
		if(enemy instanceof Enemy2) {
			enemy.attackedBy(slidingEnemy);
						
			Enemy2 enemy2 = (Enemy2) enemy;
			
			//if it also sliding, die both of them
			if(enemy2.isSliding()) {
				slidingEnemy.attackedBy(enemy2);
				heroBeatEnemy(slidingEnemy);
			}
			
			
		} else {
			//sliding enemy attack the friend
			enemy.attackedBy(slidingEnemy);
			
		}
		
		heroBeatEnemy(enemy);
	}
	private void bulletHitEnemy(Bullet bullet , Enemy enemy) {
		removeBullet(bullet);
		enemy.attackedBy(bullet);
		heroBeatEnemy(enemy);
		
		SuperMario.media.playSound("hit");
	}

	private void bombHitEnemy(Bomb bomb , Enemy enemy) {
		removeBomb(bomb);
		enemy.attackedBy(bomb);
		heroBeatEnemy(enemy);

		SuperMario.media.playSound("hit");
	}

	private void bunHitEnemy(Bun bun , Enemy enemy) {
		removeBun(bun);
		enemy.attackedBy(bun);
		heroBeatEnemy(enemy);

		SuperMario.media.playSound("hit");
	}

	private void heroHitObject(Entity obj) {
		if(mario.isDied()) return;
		
		//hit coin
		if(obj instanceof Coin) {
			Coin coin = (Coin) obj;
			//remove it
			removeEntity(coin);
			poolCoins.free(coin);
			if(numCoins>0)
				numCoins--;
			score += coin.getScore();
			
			SuperMario.media.playSound("coin");
			
			updatePanel();
			
			return;
		}
		
		if(obj instanceof BulletStock) {
			BulletStock stock = (BulletStock) obj;
			removeEntity(stock);
			
			//get the amount and update the display
			numBullets += stock.getAmount();
			updatePanel();
			
			toastLabel(String.valueOf(stock.getAmount())+" FOOTBALL", stock.getX(), stock.getY()+30,1);
			
			SuperMario.media.playSound("bullet_pack");
			
			return;
		}

		if(obj instanceof Health) {
			Health stock = (Health) obj;
			removeEntity(stock);

			//get the amount and update the display
			if(mario.health<mario.fullHealth){
			mario.health += stock.getAmount();
				toastLabel(String.valueOf(stock.getAmount())+" Health increased", stock.getX(), stock.getY()+30,1);

				SuperMario.media.playSound("bullet_pack");
			}else{
				toastLabel(" Full Health", stock.getX(), stock.getY()+30,1);

				SuperMario.media.playSound("bullet_pack");
			}
			updatePanel();



			return;
		}

		if(obj instanceof Timer) {
			Timer stock = (Timer) obj;
			removeEntity(stock);

			//get the amount and update the display
			worldtimer += stock.getAmount();
			updatePanel();

			toastLabel(String.valueOf(stock.getAmount())+" Plus Time", stock.getX(), stock.getY()+30,1);

			SuperMario.media.playSound("bullet_pack");

			return;
		}

		if(obj instanceof BombStock) {
			BombStock stock = (BombStock) obj;
			removeEntity(stock);

			//get the amount and update the display
			numBombs += stock.getAmount();
			updatePanel();

			toastLabel(String.valueOf(stock.getAmount())+" BOMB", stock.getX(), stock.getY()+30,1);

			SuperMario.media.playSound("bullet_pack");

			return;
		}

		if(obj instanceof BunStock) {
			BunStock stock = (BunStock) obj;
			removeEntity(stock);

			//get the amount and update the display
			numBuns += stock.getAmount();
			updatePanel();

			toastLabel(String.valueOf(stock.getAmount())+"FIRE", stock.getX(), stock.getY()+30,1);

			SuperMario.media.playSound("bullet_pack");

			return;
		}


		
		if(obj == flag) {
			if(flag.hasRaised()) return;
			heroReachFlag();
			
			return;
		}
		if(obj instanceof Enemy) {
			if(((Enemy)obj).isHasDied()) {
				return;
			}
			//touch enemy
			
			
			
			//is hero step the enemy?
			if(mario.v.y < -10 && mario.getBottom() > obj.getY()) {


				Enemy enemy = (Enemy)obj;
				mario.stepEnemy(enemy);
				enemy.attackedByHero(mario,1);
				
				heroBeatEnemy(enemy);
				SuperMario.media.playSound("hit");
				
				
			} 
			
			else {
				//enemy attack hero
				if(!mario.isImmune()) {
					
					mario.attackedBy(((Enemy)obj));
					((Enemy)obj).attackHero(mario);
					SuperMario.media.playSound("hit2");
					
					updatePanel();
				}
			}
			return;
		}
	}

	//level completed when hero reach the flag
	private void heroReachFlag() {
		if (numCoins <= 0) {
			state = GAME_COMPLETED;
			mario.gameCompleted();

			score += 5;
			updatePanel();
			toastLabel("Level Complete", flag.getX(), mario.getTop());

			flag.down();

			SuperMario.media.playSound("flag");

			SuperMario.media.stopMusic("level");

		}else{

			if(j==0){
				toastLabel(""+" Collect The Remaining Coin's ", flag.getX(),100,5);
				j++;
			}
			j++;
			if(j%99==0){
				toastLabel(""+" Collect The Remaining Blood's ", flag.getX(),100,5);
				j++;
			}

		}
	}
	
	
	
	//scoring after beating an enemy
	private void heroBeatEnemy(Enemy enemy) {
		if(!enemy.isHasDied()) return;
		
		score += enemy.getScore();
		updatePanel();
		
		//display the score
		toastLabel(String.valueOf(enemy.getScore()) , enemy.getX() , enemy.getY()+30);
		
	}



	//key handling
	//store the boolean values based on pressed keys,
	@Override
	public boolean keyDown(int keycode) {
		
		// on desktop only, while on android use the virtual button
		if(keycode == Keys.A) keyLeft = true;
		if(keycode == Keys.D) keyRight = true;
		if(keycode == Keys.L) keyJump = true;
		if(keycode == Keys.K) keyFire = true;
		if(keycode == Keys.J) keyBomb = true;
		if(keycode == Keys.J) keyBun = true;
				
		if(keycode == Keys.P) {
			if(state == GAME_PAUSED)
				resumeLevel();
			else
				pauseLevel(false);
				
		}
		if(keycode == Keys.ESCAPE || keycode == Keys.BACK){  //back key pressed
			if(state == GAME_PAUSED) {
				resumeLevel();
			} else {
				pauseLevel();
			}
			return true;
		}
		return super.keyDown(keycode);
	}
	@Override
	public boolean keyUp(int keycode) {
		if(keycode == Keys.A) keyLeft = false;
		if(keycode == Keys.D) keyRight = false;
		if(keycode == Keys.L) keyJump = false;
		if(keycode == Keys.K) keyFire = false;
		if(keycode == Keys.K) keyBomb = false;
		if(keycode == Keys.K) keyBun = false;

		return super.keyUp(keycode);
	}
	
	
	//resume game
	private void resumeLevel() {
		state = GAME_PLAY;
		resumeWorld();
		
		if(pauseDialog != null) {
			removeOverlayChild(pauseDialog);
		}
		
		SuperMario.media.unMuteAllMusic();
		call(SuperMario.LEVEL_RESUMED);
	}

	//pause game
	private void pauseLevel() {
		pauseLevel(true);
	}
	private void pauseLevel(boolean withdialog) {
		if(state != GAME_PLAY) return;
		
		state = GAME_PAUSED;
		pauseWorld();
		
		if(!withdialog) return;
		
		//show the paused dialog
		pauseDialog = new PauseDialog();
		addOverlayChild(pauseDialog);
		centerActorXY(pauseDialog);
		pauseDialog.addListener(new MessageListener(){

			//the dialog callback
			@Override
			protected void receivedMessage(int message, Actor actor) {
				if(actor == pauseDialog) {
					if(message == PauseDialog.ON_RESUME) {
						resumeLevel();
					}
					else if(message == PauseDialog.ON_QUIT) {
						call(Level.COMPLETED);
					}
				}
			}
			
		});
		
		SuperMario.media.muteAllMusic();
		//track the event
		call(SuperMario.LEVEL_PAUSED);
	}


	
	////////////

	// a brick destroyed, create the debris
	protected void destroyBrick(Brick brick) {
		//remove the brick
		removeLand(brick);
		
		//create 8 debris
		int num = 8;
		while(num-- > 0) {
			final Debris debris = poolDebris.obtain();
			debris.setPosition((float) (brick.getX()+Math.random()*60-30), (float) (brick.getY()+Math.random()*60-30));
			debris.start();
			addEntity(debris);
			
			//remove it once it's time has up
			debris.addListener(new MessageListener(){
				@Override
				protected void receivedMessage(int message, Actor actor) {
					if(message == Debris.ON_FINISHED) {
						debris.removeListener(this);
						removeEntity(debris);
						poolDebris.free(debris);
					}
				}
				
			});
		}
	}
	
	
	//hero fire a bullet
	public void heroFireBullet(boolean toRight) {
		//get bullet from pool, set position
		Bullet bullet = poolBullets.obtain();
		bullet.setPosition(mario.getX(), mario.getY());
		
		
		if(toRight) {
			bullet.moveBy(30, 0 );
		} else {
			bullet.moveBy(-30, 0);
		}
		
		//set listener and launch it
		bullet.addListener(bulletListener);
		bullet.launch(toRight);
		if(mario.v.y > 0)
			bullet.setVY(mario.v.y);
		
		
		addEntity(bullet);
		
		//reduce the stock
		numBullets--;
		updatePanel();
	}
	//hero fire a bomb
	public void heroFireBomb(boolean toRight) {
		//get bullet from pool, set position
		Bomb bomb = poolBombs.obtain();
		bomb.setPosition(mario.getX(), mario.getY());


		if(toRight) {
			bomb.moveBy(30, 0 );
		} else {
			bomb.moveBy(-30, 0);
		}

		//set listener and launch it
		bomb.addListener(bombListener);
		bomb.launch(toRight);
		if(mario.v.y > 0)
			bomb.setVY(mario.v.y);


		addEntity(bomb);

		//reduce the stock
		numBombs--;
		updatePanel();
	}
	//hero fire a bun
	public void heroFireBun(boolean toRight) {
		//get bullet from pool, set position
		Bun bun = poolBuns.obtain();
		bun.setPosition(mario.getX(), mario.getY());


		if(toRight) {
			bun.moveBy(30, 0 );
		} else {
			bun.moveBy(-30, 0);
		}

		//set listener and launch it
		bun.addListener(bunListener);
		bun.launch(toRight);
		if(mario.v.y > 0)
			bun.setVY(mario.v.y);


		addEntity(bun);

		//reduce the stock
		numBuns--;
		updatePanel();
	}
	private void removeBullet(Bullet bullet) {
		removeBullet(bullet , true);
	}
	private void removeBomb(Bomb bomb) {
		removeBomb(bomb , true);
	}

	private void removeBun(Bun bun) {
		removeBun(bun , true);
	}

	//remove bullet, replace it with explosion
	private void removeBullet(Bullet bullet,boolean withExplosion) {
		//remove bullet
		removeEntity(bullet);
		bullet.removeListener(bulletListener);
		poolBullets.free(bullet);
		
		
		//if need an explosion
		if(withExplosion) {
			//put the explosion			
			BulletExp exp = poolBulletExps.obtain();
			addEntity(exp);
			
			exp.setPosition(bullet.getX(), bullet.getY());
			exp.start();
	
		}
	}

	//remove bomb, replace it with explosion
	private void removeBomb(Bomb bomb,boolean withExplosion) {
		//remove bullet
		removeEntity(bomb);
		bomb.removeListener(bombListener);
		poolBombs.free(bomb);


		//if need an explosion
		if(withExplosion) {
			//put the explosion
			BombExp exp = poolBombExps.obtain();
			addEntity(exp);

			exp.setPosition(bomb.getX(), bomb.getY());
			exp.start();

		}
	}


	//remove bun, replace it with explosion
	private void removeBun(Bun bun,boolean withExplosion) {
		//remove bullet
		removeEntity(bun);
		bun.removeListener(bunListener);
		poolBuns.free(bun);


		//if need an explosion
		if(withExplosion) {
			//put the explosion
			BunExp exp = poolBunExps.obtain();
			addEntity(exp);

			exp.setPosition(bun.getX(), bun.getY());
			exp.start();

		}
	}


	//bullet listener
	private MessageListener bulletListener = new MessageListener(){
		
		@Override
		protected void receivedMessage(int message, Actor actor) { 
			if(message == Bullet.REMOVE) {     //bullet want to be removed 
				removeBullet((Bullet) actor);
			}
			else if(message == Bullet.REMOVE_NO_EXP) { //same above but not using explosion
				removeBullet((Bullet) actor,false);
			}
		}		
	};


	//bomb listener
	private MessageListener bombListener = new MessageListener(){

		@Override
		protected void receivedMessage(int message, Actor actor) {
			if(message == Bomb.REMOVE) {     //bullet want to be removed
				removeBomb((Bomb) actor);
			}
			else if(message == Bomb.REMOVE_NO_EXP) { //same above but not using explosion
				removeBomb((Bomb) actor,false);
			}
		}
	};

	//bun listener
	private MessageListener bunListener = new MessageListener(){

		@Override
		protected void receivedMessage(int message, Actor actor) {
			if(message == Bun.REMOVE) {     //bullet want to be removed
				removeBun((Bun) actor);
			}
			else if(message == Bun.REMOVE_NO_EXP) { //same above but not using explosion
				removeBun((Bun) actor,false);
			}
		}
	};

	//level failed, stop music and call levelfailed2() with a delay time 
	private void levelFailed() {
		delayCall("level_failed", 2f);
		camController.followObject(null);
		
		SuperMario.media.stopMusic("level");
		call(SuperMario.LEVEL_FAILED);
	}
	private void levelFailed2() {
		//show the level failed dialog
		LevelFailedDialog dialog = new LevelFailedDialog();
		addOverlayChild(dialog);
		centerActorXY(dialog);
		
		//listen to the button clicked
		dialog.addListener(new MessageListener(){
			@Override
			protected void receivedMessage(int message, Actor actor) {
				if(message == LevelFailedDialog.ON_CLOSE) {
					call(FAILED);
				}
			}
		});
	}
	@Override
	protected void onDelayCall(String code) {
		if(code.equals("level_failed")) {
			pauseWorld();
			levelFailed2();
		}
	}
	protected void levelCompleted() {
		//persistent data ...
		//update the level progress
		SuperMario.data.setLevelProgress(id);
		
		//update the score
		SuperMario.data.updateScore(id, score);
						
		//stop camera and pause the loop
		camController.followObject(null);
		pauseWorld();
		
		//show "level completed" dialog box
		LevelCompletedDialog dialog = new LevelCompletedDialog(score);
		addOverlayChild(dialog);
		centerActorXY(dialog);
		
		//do when 'ok' btn clicked
		dialog.addListener(new MessageListener(){
			@Override
			protected void receivedMessage(int message, Actor actor) {
				if(message == LevelFailedDialog.ON_CLOSE) {
					call(COMPLETED);
				}
			}
		});
		
		SuperMario.media.playSound("level_completed");
		SuperMario.media.stopMusic("level");
		call(SuperMario.LEVEL_COMPLETED);
	}
	

	//the game loop
	@Override
	protected void update(float delta) {
		if(worldtimer<1){
			levelFailed();
		}
		if(state == GAME_PLAY) {

			ms++;
			if(worldtimer>=1 && ms%99==0){
				worldtimer-=1;

				updatePanel();
			}

			//notify hero what keys are pressed
			boolean lKeyRight = keyRight || joyStick.isRight();
			boolean lKeyLeft = keyLeft || joyStick.isLeft();
			boolean lKeyJump = keyJump || jumpBtn.isPressed();
			boolean lKeyFire = keyFire || bulletBtn.isPressed();
			boolean lKeyBomb = keyBomb || bombBtn.isPressed();
			boolean lKeyBun = keyBun || bunBtn.isPressed();

			
			mario.onKey(lKeyLeft, lKeyRight,lKeyJump,lKeyFire,lKeyBomb,lKeyBun);
			//hero fall 
			if(mario.getBottom() < 0) {
				mario.fall();
			}
		
		}
		else if(state == GAME_COMPLETED) {
			mario.onKey(false, false,false,false,false,false);
			
			
		}
	}
	

	@Override
	public void render(float delta) {
		super.render(delta);
		camController.camera.position.x = (int)camController.camera.position.x ;
		camController.camera.position.y = (int)camController.camera.position.y ;
	}

	public void toastCoin(MysteryBox mysteryBox) {
		final Coin coin = poolCoins.obtain();
		coin.setPosition(mysteryBox.getX(), mysteryBox.getY() + mysteryBox.getHeight()/2 + coin.getHeight()/2);
		addEntity(coin);
		coin.throwUp();
		coin.addListener(new MessageListener(){

			@Override
			protected void receivedMessage(int message, Actor actor) {
				if(message == Coin.REQUEST_REMOVE) {
					coin.removeListener(this);
					
					//remove the coin
					removeEntity(coin);
					poolCoins.free(coin);
				}
			}
		});
		
		numCoins++;
		score += coin.getScore();
		updatePanel();
		
		
	}

	public void heroHitBrick(Brick brick) {
		destroyBrick(brick);
		SuperMario.media.playSound("hit");
	}

	public void heroHitMystery(MysteryBox box) {
		box.hit();
		
		
	}


	@Override
	public void pause() {
		pauseLevel();
		
		super.pause();
	}


	@Override
	public void resume() {
	
		super.resume();
	}


	@Override
	public void hide() {
		SuperMario.media.stopAllMusic();
		super.hide();
	}


	
	
	
	
}
