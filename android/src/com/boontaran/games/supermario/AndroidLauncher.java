package com.boontaran.games.supermario;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.GameHelper;
import com.google.firebase.analytics.FirebaseAnalytics;

public class AndroidLauncher extends AndroidApplication implements PlayServices{

	private FirebaseAnalytics mFirebaseAnalytics;
	private GameHelper gameHelper;
	private final static int requestCode = 1;
	private static final String BANNER_AD_UNIT_ID = "ca-app-pub-1618559347306270/8877181743 ";
	private static final String INTERSTITIAL_UNIT_ID = "ca-app-pub-1618559347306270/8877181743 ";
	private InterstitialAd mInterstitialAd;
	private android.view.View bannerAd;



	protected AdView adView;

	private final int SHOW_ADS = 1;
	private final int HIDE_ADS = 0;

	protected Handler handler = new Handler()
	{
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what) {
				case SHOW_ADS:
				{
					adView.setVisibility(View.VISIBLE);
					break;
				}
				case HIDE_ADS:
				{
					adView.setVisibility(View.GONE);
					break;
				}
			}
		}
	};


	@Override
	protected void onCreate (Bundle savedInstanceState) {
// Do the stuff that initialize() would do for you
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		super.onCreate(savedInstanceState);
		gameHelper = new GameHelper(this, GameHelper.CLIENT_GAMES);
		gameHelper.enableDebugLog(false);
		GameHelper.GameHelperListener gameHelperListener = new GameHelper.GameHelperListener()
		{
			@Override
			public void onSignInFailed(){ }

			@Override
			public void onSignInSucceeded(){ }
		};

		gameHelper.setup(gameHelperListener);

		mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
		AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
		cfg.useAccelerometer=false;
		cfg.useCompass=false;
		// Create the layout
		RelativeLayout layout = new RelativeLayout(this);



		// Create the libgdx View
		View gameView = initializeForView(new SuperMario(this), cfg);

		// Create and setup the AdMob view
		AdView adView = new AdView(this);
		adView.setAdSize(AdSize.BANNER);
		adView.setAdUnitId(" ca-app-pub-1618559347306270/1748927348");
		AdRequest adRequest = new AdRequest.Builder().build();
		adView.loadAd(adRequest);

		// Add the libgdx view
		layout.addView(gameView);

		// Add the AdMob view
		RelativeLayout.LayoutParams adParams =
				new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
						RelativeLayout.LayoutParams.WRAP_CONTENT);
		adParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		adParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

		layout.addView(adView, adParams);

		// Hook it all up
		setContentView(layout);
	}

	@Override
	public void showAds(boolean show) {
		handler.sendEmptyMessage(show ? SHOW_ADS : HIDE_ADS);
	}
	public void setupAds() {
//		bannerAd = new AdView(this);
//		bannerAd.setVisibility(View.INVISIBLE);
//		bannerAd.setBackgroundColor(0xff000000); // black
//		bannerAd.setAdUnitId(BANNER_AD_UNIT_ID);
//		bannerAd.setAdSize(AdSize.SMART_BANNER);
//		interstitialAd = new InterstitialAd(this);
//		interstitialAd.setAdUnitId(INTERSTITIAL_UNIT_ID);
//
//		AdRequest.Builder builder = new AdRequest.Builder();
//		AdRequest ad = builder.build();
//		interstitialAd.loadAd(ad);
	}

	@Override
	public void showInterstitialAd() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mInterstitialAd = new InterstitialAd(getApplicationContext());
				mInterstitialAd.setAdUnitId(INTERSTITIAL_UNIT_ID);

				mInterstitialAd.setAdListener(new AdListener() {
					@Override
					public void onAdClosed() {
						requestNewInterstitial();
//						beginPlayingGame();
					}
				});

				requestNewInterstitial();

			}
		});
	}
	private void requestNewInterstitial() {
		AdRequest adRequest = new AdRequest.Builder()
				.addTestDevice("SEE_YOUR_LOGCAT_TO_GET_YOUR_DEVICE_ID")
				.build();

		mInterstitialAd.loadAd(adRequest);
	}

	@Override
	protected void onStart()
	{
		super.onStart();
		gameHelper.onStart(this);
	}

	@Override
	protected void onStop()
	{
		super.onStop();
		gameHelper.onStop();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		gameHelper.onActivityResult(requestCode, resultCode, data);
	}


	@Override
	public void signIn()
	{
		try
		{
			runOnUiThread(new Runnable()
			{
				@Override
				public void run()
				{
					gameHelper.beginUserInitiatedSignIn();
				}
			});
		}
		catch (Exception e)
		{
			Gdx.app.log("MainActivity", "Log in failed: " + e.getMessage() + ".");
		}
	}

	@Override
	public void signOut()
	{
		try
		{
			runOnUiThread(new Runnable()
			{
				@Override
				public void run()
				{
					gameHelper.signOut();
				}
			});
		}
		catch (Exception e)
		{
			Gdx.app.log("MainActivity", "Log out failed: " + e.getMessage() + ".");
		}
	}

	@Override
	public void rateGame()
	{
		String str = "Your PlayStore Link";
		startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(str)));
	}

	@Override
	public void unlockAchievement()
	{
		Games.Achievements.unlock(gameHelper.getApiClient(),
				getString(R.string.achievement_dum_dum));
	}

	@Override
	public void submitScore(int highScore)
	{
		if (isSignedIn() == true)
		{
			Games.Leaderboards.submitScore(gameHelper.getApiClient(),
					getString(R.string.leaderboard_highest), highScore);
		}
	}

	@Override
	public void showAchievement()
	{
		if (isSignedIn() == true)
		{
			startActivityForResult(Games.Achievements.getAchievementsIntent(gameHelper.getApiClient()), requestCode);
		}
		else
		{
			signIn();
		}
	}

	@Override
	public void showScore()
	{
		if (isSignedIn() == true)
		{
			startActivityForResult(Games.Leaderboards.getLeaderboardIntent(gameHelper.getApiClient(),
					getString(R.string.leaderboard_highest)), requestCode);
		}
		else
		{
			signIn();
		}
	}

	@Override
	public boolean isSignedIn()
	{
		return gameHelper.isSignedIn();
	}


	@Override
	public void showBannerAd() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
//				bannerAd.setVisibility(View.VISIBLE);
//				AdRequest.Builder builder = new AdRequest.Builder();
//				AdRequest ad = builder.build();
//				bannerAd.loadAd(ad);
			}
		});
	}

	@Override
	public void hideBannerAd() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
//				bannerAd.setVisibility(View.INVISIBLE);
			}
	});
	}

	@Override
	public boolean isWifiConnected() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		return (ni != null && ni.isConnected());
	}


}
