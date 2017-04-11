package com.boontaran.games.supermario;

import com.boontaran.MessageEvent;
import com.boontaran.games.Clip;
import com.boontaran.games.platformerLib.CameraController;
import com.boontaran.games.platformerLib.Entity;
import com.boontaran.games.supermario.enemies.Enemy;
import com.boontaran.games.supermario.enemies.Enemy2;

public class Mario extends Entity {
	
	//events
	public static final int HERO_DIE = 3;
	
	private float ax = 3000;
	
	//the states
	private int state=-1;
	private static final int IDLE=0;
	private static final int WALK=1;
	private static final int JUMP_UP=2;
	private static final int JUMP_DOWN=3;
	private static final int ATTACKED_BY_ENEMY=4;
	private static final int FIRE=5;
	private static final int BOMB=7;
	private static final int BUN=8;
	private static final int DIE=6;
	
	//hero animation
	private Clip clip ;
	
	
	//hero's full health
	public float fullHealth = 10;
	public float health = fullHealth;
	
	//damage to take if hero attack enemy
	private float damage = 1;
	
	//time when hero in star power
	private float starTime = 10; //sec
	private float starTimer;
	
	//star hilite blinking
	private float hiliteAlpha=1;
	private boolean hiliteUp=false;
	
	//indicate the hero is just attacked
	private float justAttackedTime;
	//hero has died, hero has completed the mission
	private boolean hasDied,hasCompleted;

	public static int getRandomInteger(int max,int min){
		return ((int)(Math.random()*(max-min)))+min;
	}
	//state that the fire key has released, to prevent multiple bullet firing at a time
	private boolean fireKeyHasUp,bombKeyHasUp,bunKeyHasUp;
	
	//frames
	private int idleFrames[] =new int[]{0,0};
	private int walkFrames[] =new int[]{4,5,6,7,6,5};
	private int JumpFrames[] =new int[]{2,2,2,6,6,2};
	private int JumpFrames2[] =new int[]{2,2,2,7,7,2};
	private int JumpFrames3[] =new int[]{2,2,2,2,2,2};
	private int JumpFrames4[] =new int[]{2,2,2,4,5,2};
	private int JumpFrames5[] =new int[]{2,2,2,5,6,2};
	private int fireFrames[] =new int[]{1,1,1};
	private int fireInAirFrames[] =new int[]{3,3,3};

	private int bombFrames[] =new int[]{1,1,1};
	private int bombInAirFrames[] =new int[]{3,3,3};

	private int bunFrames[] =new int[]{1,1,1};
	private int bunInAirFrames[] =new int[]{3,3,3};
	
	//if true, the state will be maintained until animation completed
	private boolean waitingOnComplete;
	
	//reference to the Level class
	private Level level;
	
	
	public Mario(Level level) {
		this.level = level;
		int tmp=getRandomInteger(10,1);

		if(tmp==1)
		//construct the clip, and clip listener
		clip = new Clip(SuperMario.atlas.findRegion("mario") , 120,120);
		else if(tmp==2)
			clip = new Clip(SuperMario.atlas.findRegion("mario2") , 120,120);
		else if(tmp==3)
			clip = new Clip(SuperMario.atlas.findRegion("mario3") , 120,120);
		else if(tmp==4)
		clip = new Clip(SuperMario.atlas.findRegion("mario4") , 120,120);
		else if(tmp==5)
			clip = new Clip(SuperMario.atlas.findRegion("mario5") , 120,120);
		else if(tmp==6)
			clip = new Clip(SuperMario.atlas.findRegion("mario6") , 120,120);
		else if(tmp==7)
			clip = new Clip(SuperMario.atlas.findRegion("mario7") , 120,120);
		else if(tmp==8)
			clip = new Clip(SuperMario.atlas.findRegion("mario") , 120,120);
		else if(tmp==9)
		clip = new Clip(SuperMario.atlas.findRegion("mario3") , 120,120);
		else if(tmp==10)
			clip = new Clip(SuperMario.atlas.findRegion("mario6") , 120,120);
		setSize(50	, 95);
		setClip(clip);
		clip.setFPS(12);
		clip.addListener(new Clip.ClipListener() {
			
			@Override
			public void onFrame(int num) {}
			
			@Override
			public void onComplete() {
				waitingOnComplete = false;
			}
		});
		
		//no bouncing
		restitution = 0;
		
		//walk speed 
		maxSpeedX = Settings.WALK_SPEED;
		changeState(IDLE);
	}
	
	//notified the keys being pressed 
	public void onKey(boolean left, boolean right, boolean jump, boolean fire, boolean bomb,boolean bun) {
		if(hasDied || hasCompleted) return;
		
		a.x = 0;
		friction = 0.5f;
				
		boolean inAir = isInAir();
		
		
		//unable to move when the hero is just attacked
	if(justAttackedTime <= 0) {
			
			//set the acceleration and state based on direction
		if(left) {
				a.x  = -ax;
				friction = 0;
				
				if(!inAir) {
					changeState(WALK);
				} 
			}
			if(right) {
				a.x = ax;
				friction = 0;
				
				if(!inAir) {
					changeState(WALK);
				} 
			}

			if(!right && !left && inAir) {
				v.x /=1.1f;
			}

			//fire a bullet
			if(fire && fireKeyHasUp) {
				fireBullet();
				fireKeyHasUp = false;
			}

		//fire a bomb
		if(bomb && bombKeyHasUp) {
			fireBomb();
			bombKeyHasUp = false;
		}
		//fire a bun
		if(bun && bunKeyHasUp) {
			fireBun();
			bunKeyHasUp = false;
		}
	}
		
		
		if(!fire) {
			fireKeyHasUp = true;
		}




	if(!bomb) {
		bombKeyHasUp = true;
	}
		if(!bun) {
			bunKeyHasUp = true;
		}

		//state if in air
		if(inAir) {
			if(v.y < 0)
				changeState(JUMP_DOWN);
			else
				changeState(JUMP_UP);
		} else {
			if(!right && !left) {
				changeState(IDLE);
			}	
		}
		
		//hero jump
		if(jump) {
			
			//do if hero touch ground only
			if(!isInAir()) {
				jump();
			}
		}
		
		//flip the display if moving left	
		if(v.x > 0) {
			setScaleX(1);
		} else if(v.x < 0) {
			setScaleX(-1);
		}
		
	}
	
	//jumping
	private void jump() {
		//speed to jump
		setVY(Settings.JUMP_SPEED);
		
		//sound
		//SuperMario.media.playSound("jump");
	}
	private void fireBullet() {
		if(level.getBulletCounts() == 0) return; //not have a bullet
		
		//firing the bullet
		level.heroFireBullet(getScaleX()==1);
		changeState(FIRE);
		
		SuperMario.media.playSound("bullet");
	}



	private void fireBomb() {
		if(level.getBombCounts() == 0) return; //not have a bullet

		//firing the bullet
		level.heroFireBomb(getScaleX()==1);
		changeState(BOMB);

		SuperMario.media.playSound("bullet");
	}
	private void fireBun() {
		if(level.getBunCounts() == 0) return; //not have a bullet

		//firing the bullet
		level.heroFireBun(getScaleX()==1);
		changeState(BUN);

		SuperMario.media.playSound("bullet");
	}
	
	@Override
	protected void hitCeil(Entity ent) {
		super.hitCeil(ent);
		
		if(ent instanceof Brick) {	
			//hit a brick
			level.heroHitBrick((Brick) ent);
		}
		else if(ent instanceof MysteryBox) {	
			//hit a mystery box
			level.heroHitMystery((MysteryBox) ent);
		}
	}
	@Override
	public void hitLand(Entity land) {
		super.hitLand(land);
		
		if(justAttackedTime > 0) {
			justAttackedTime = 0.01f;
		}
	}
	
	
	//is still in attacked time, hero can't be attacked by enemy twice
	public boolean isImmune() {
		return justAttackedTime > 0;
	}
	@Override
	public void update(float delta) {
		super.update(delta);
		
		if(hasCompleted) {
			if(!isInAir()) {
				a.x = ax;
				changeState(WALK);
			}
		}
		
		//star hilite animation (alpha value), based on elapsed time
		if(starTimer > 0) {
			starTimer -= delta;
			
			if(hiliteUp) {
				hiliteAlpha += delta*5;
				if(hiliteAlpha > 1) {
					hiliteAlpha = 1;
					hiliteUp = false;
				}
			} else {
				hiliteAlpha -= delta*5;
				
				if(hiliteAlpha < 0)  {
					hiliteAlpha = 0;
					hiliteUp = true;
				}
			}
			
			
			if(starTimer <= 0) {
				setImage(null);
				
				SuperMario.media.playMusic("level");
				SuperMario.media.stopMusic("star");
			}
		}
		
		
		if(justAttackedTime > 0) {
			justAttackedTime -= delta;
			
			
			if(justAttackedTime <= 0) {
				
			}
		}
	}
	private void changeState(int newState) {
		changeState(newState, false);
	}	
	
	//changing the state
	private void changeState(int newState,boolean force) {
		if(state == newState) return; // already in that state
		if(justAttackedTime > 0) return; //still attacked
		
		
		if(waitingOnComplete && !force) {  //waiitng to complete pref animation
			return;
		} else {
			waitingOnComplete = false;  //it forced to change
		}
		
		
		if(hasDied) return;
		
		state = newState;
		
		//change the clip animation based on state
		switch (state) {
		case IDLE:
			clip.playFrames(idleFrames, true);
			break;
		case WALK:
			clip.playFrames(walkFrames, true);
			break;
		case JUMP_UP:
			int ran=getRandomInteger(5,1);
			if(ran==1)
			clip.playFrames(JumpFrames,true);
			else if(ran==2)
				clip.playFrames(JumpFrames2,true);
			else if(ran==3)
				clip.playFrames(JumpFrames3,true);
			else if(ran==4)
				clip.playFrames(JumpFrames4,true);
			else if(ran==5)
				clip.playFrames(JumpFrames5,true);

			break;
		case JUMP_DOWN:
			int rann=getRandomInteger(2,1);
			if(rann==1)
			clip.singleFrame(2);
else
				clip.playFrames(JumpFrames,true);
			break;
		case ATTACKED_BY_ENEMY:
			clip.singleFrame(8);
			break;		
		case DIE:
			clip.singleFrame(9);
			break;
		case FIRE:
			if(isInAir()) {
				clip.playFrames(fireInAirFrames, false);
			} else {
				clip.playFrames(fireFrames, false);
			}
			waitingOnComplete = true;
			break;
			case BOMB:
				if(isInAir()) {
					clip.playFrames(bombInAirFrames, false);
				} else {
					clip.playFrames(bombFrames, false);
				}
				waitingOnComplete = true;
				break;
			case BUN:
				if(isInAir()) {
					clip.playFrames(bunInAirFrames, false);
				} else {
					clip.playFrames(bunFrames, false);
				}
				waitingOnComplete = true;
				break;
			default:
			break;
		}
	}
	
	
	public float getDamage() {
		return damage;
	}
	
	//hero step an enemy, bounce up
	public void stepEnemy(Enemy enemy) {
		setY(enemy.getTop() + getHeight()/2);
		setVY(500); //bounce up
	}
	
	//hero attacked by enemy
	public void attackedBy(Enemy enemy) {
		//check if enemy type is Enemy2 (turtle enemy)
		//if he is hiding, do nothing with hero
		if(enemy instanceof Enemy2) {
			Enemy2 enemy2 = (Enemy2) enemy;
			
			if(enemy2.isSliding()) {
				
			}
			else if(enemy2.isHiding()) {
				return;
			}
		}
		
		//reduce health
		health -= enemy.getDamage();
		
		if(health <=0) {
			//out of health, hero will die
			if(v.x > 0) {
				setScaleX(1);
			} else if(v.x < 0) {
				setScaleX(-1);
			}
			
			die();
		} else {
			//still has health
			changeState(ATTACKED_BY_ENEMY,true);
			justAttackedTime = 2;  //within 2 sec they can't attack hero again
			if(getX() > enemy.getX()) {
				setVX(350);
			} else {
				setVX(-350);
			}
		}
		
		//move up
		setVY(350);
	}
	
	
	
	public boolean isDied() {
		return hasDied;
	}
	
	//hero died, 
	private void die() {
		
		a.x = 0;
		setVX(0);
		
		//change state
		changeState(DIE,true);
		hasDied = true;
		setNoCollision(true);
		setNoLandCollision(true);
		
		//notify the Level by firing an event
		fire(new MessageEvent(HERO_DIE));
	}
	
	//falling to a cliff
	public void fall() {
		if(hasDied) return;
		
		die();
		setVY(0);
	}
	
	
	public float getHealthRatio() {
		return health/fullHealth;
	}
	public void gameCompleted() {
		hasCompleted=true;		
	}
	public void justBeatBoss() {
		a.x = 0;
	}
	
	
}
