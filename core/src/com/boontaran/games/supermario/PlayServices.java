package com.boontaran.games.supermario;

/**
 * Created by abdulla on 12/2/17.
 */

public interface PlayServices
{
    public void signIn();
    public void signOut();
    public void rateGame();
    public void unlockAchievement();
    public void submitScore(int highScore);
    public void showAchievement();
    public void showScore();
    public boolean isSignedIn();
    public void showBannerAd();
    public void hideBannerAd();
    public boolean isWifiConnected();
    public void showInterstitialAd ();
    public void showAds(boolean show);
}
