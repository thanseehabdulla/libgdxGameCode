package com.boontaran.games.supermario.enemies;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Pool;
import com.boontaran.MessageEvent;
import com.boontaran.MessageListener;
import com.boontaran.games.platformerLib.Entity;
import com.boontaran.games.supermario.Bomb;
import com.boontaran.games.supermario.Bullet;
import com.boontaran.games.supermario.BulletExp;
import com.boontaran.games.supermario.Bun;
import com.boontaran.games.supermario.Mario;
import com.boontaran.games.supermario.SuperMario;

public abstract class Enemy extends Entity {
	//events
	public static final int DIE = 1;
	
	//
	protected int score = 5;
	
	
	//enemy's health level
	protected float health = 1;
	protected boolean hasDied;
	
	//damage to take when attack hero
	protected float damage = 1;
	
	public Enemy() {
		restitution = 0;
		edgeUpdateLimRatio = 0.2f;
	}
	
	//reduce health, die if out of health
	public void attackedByHero(Mario hero,float damageMultiplier) {
		health -= hero.getDamage()*damageMultiplier;
		if(health <=0) die();		
	}
	public void attackHero(Mario hero) {
		//implemented on subclass







	}
	public boolean isHasDied() {
		return hasDied;
	}
	public int getScore() {
		return score;
	}
	
	
	protected void die() {
		hasDied = true;
		setNoCollision(true);
		setNoLandCollision(true);
		
		//fire event indicate the enemy has die
		fire(new MessageEvent(DIE));
	}
	
	//hero's bullet hit the enemy
	public void attackedBy(Bullet bullet) {
		//reduce health
		health -= bullet.getDamage();
		if(health <=0) die();		
	}

	//hero's bullet hit the enemy
	public void attackedBy(Bomb bullet) {
		//reduce health
		health -= bullet.getDamage();
		if(health <=0) die();
	}

	//hero's bullet hit the enemy
	public void attackedBy(Bun bullet) {
		//reduce health
		health -= bullet.getDamage();
		if(health <=0) die();
	}

	//slide by turtle type enemy
	public void attackedBy(Enemy2 slidingEnemy) {
		health = 0;
		die();
	}
	
	
	public float getDamage() {
		return damage;
	}
	public void flip() {
		//implemented on subclass
	}





}
