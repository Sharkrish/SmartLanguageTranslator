package com.thinksmarter786.smartLanguageTranslator;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.accessibilityservice.AccessibilityService;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.ApplicationInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textview.MaterialTextView;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;
import com.google.mlkit.common.model.RemoteModelManager;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.TranslateRemoteModel;
import com.thinksmarter786.smartLanguageTranslator.Adapters.BottomSheetAdapterModel;
import com.thinksmarter786.smartLanguageTranslator.Service.BackgroundService;
import com.thinksmarter786.smartLanguageTranslator.Service.Myservice;
import com.thinksmarter786.smartLanguageTranslator.Utils.SharedPrefs;
import com.thinksmarter786.smartLanguageTranslator.Utils.TranslateUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import hotchemi.android.rate.AppRate;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomIcons;
    Toolbar toolbar;
    private long long_pressed;
    private SharedPrefs sharedPrefs;
    private int FLEXIBLE_APP_UPDATE_REQ_CODE = 1851;
    private final static int REQUEST_CODE = 1011;
    AlertDialog dialog = null;
    AppUpdateManager appUpdateManager;
    RelativeLayout mainActivityLayout;
    private AdView mAdView;
    private RewardedAd mRewardedAd;
    ActivityResultLauncher<Intent> callTheActitvity = null;
    private boolean shownedAd;
    @Override
    protected void onStart() {
        super.onStart();
        checkForUpdates();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (appUpdateManager != null) {
            appUpdateManager.unregisterListener(installStateUpdatedListener);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPrefs = SharedPrefs.getInstance(this);
        sharedPrefs.putBoolean("splashDone",true);
        sharedPrefs.putBoolean("LoadedRewardedAds", false);
        callTheActitvity = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK){
                            //launchMainService();
                        }
                    }
                }
        );
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {

            }
        });
        checkOverlayPermission();
        checkInternetConnectivity();
        moveToAction();
    }

    private void callAds() {
        callRewardedAds();
        callBannerAds();
    }

    private void callRewardedAds() {
        // Sample AdMob app ID: ca-app-pub-3940256099942544/5224354917
        // Production app id  ca-app-pub-9611309669090513/3804559933
        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(this, "ca-app-pub-3940256099942544/5224354917",
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        mRewardedAd = null;
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                        boolean shownAds = sharedPrefs.getBoolean("LoadedRewardedAds");
                        Log.d("ShowAds",""+shownAds);
                        if (!shownAds){
                            mRewardedAd = rewardedAd;
                            mRewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                @Override
                                public void onAdShowedFullScreenContent() {
                                    sharedPrefs.putBoolean("LoadedRewardedAds", true);
                                    sharedPrefs.putBoolean("LoadedRewardedAdsfromPaude", true);

                                }

                                @Override
                                public void onAdFailedToShowFullScreenContent(AdError adError) {

                                }

                                @Override
                                public void onAdDismissedFullScreenContent() {
                                    mRewardedAd = null;
                                    sharedPrefs.putBoolean("LoadedRewardedAds",true);
                                    sharedPrefs.putBoolean("LoadedRewardedAdsfromPaude",true);

                                }
                            });
                            if (mRewardedAd != null) {
                                showAds();
                            }
                        }
                    }
                });
    }

    private void showAds() {

        if (mRewardedAd != null) {
            sharedPrefs.putBoolean("LoadedRewardedAds",true);
            sharedPrefs.putBoolean("LoadedRewardedAdsfromPaude",true);
            Activity thisActivity = MainActivity.this;
            mRewardedAd.show(thisActivity, new OnUserEarnedRewardListener() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {

                    int rewardAmount = rewardItem.getAmount();
                    String rewardType = rewardItem.getType();
                }
            });

        }
    }

    private void callBannerAds() {
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    private void checkInternetConnectivity() {
        if(dialog != null)
            dialog.dismiss();
        if(!isConnected(MainActivity.this)){
            showCustomDialog();
        }
    }

    private void showCustomDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Please connect to internet.")
                .setCancelable(false)
                .setPositiveButton("Connect", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        dialog = builder.create();
        dialog.show();

    }

    private boolean isConnected(MainActivity languageSelection) {
        ConnectivityManager connectivityManager = (ConnectivityManager) languageSelection.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if((wifiInfo!=null && wifiInfo.isConnected()) || (mobileInfo!=null && mobileInfo.isConnected()))
            return true;
        else
            return false;

    }
    private void checkOverlayPermission() {

        launchMainService();

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                callTheActitvity.launch(intent);
            }else{
                launchMainService();

            }*/

    }


    private void checkForUpdates() {
        appUpdateManager = AppUpdateManagerFactory.create(MainActivity.this);
        appUpdateManager.registerListener(installStateUpdatedListener);
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                startUpdateFlow(appUpdateInfo);
            } else if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                popupSnackbarForCompleteUpdate();
            }
        });
    }
    InstallStateUpdatedListener installStateUpdatedListener = new
            InstallStateUpdatedListener() {
                @Override
                public void onStateUpdate(InstallState state) {
                    if (state.installStatus() == InstallStatus.DOWNLOADED){
                        //CHECK THIS if AppUpdateType.FLEXIBLE, otherwise you can skip
                        popupSnackbarForCompleteUpdate();
                    } else if (state.installStatus() == InstallStatus.INSTALLED){
                        if (appUpdateManager != null){
                            appUpdateManager.unregisterListener(installStateUpdatedListener);
                        }

                    } else {
                        Log.i("InstallationLog", "InstallStateUpdatedListener: state: " + state.installStatus());
                    }
                }
            };
    private void popupSnackbarForCompleteUpdate() {
        Snackbar snackbar =
                Snackbar.make(
                        findViewById(R.id.mainActivityLayout),
                        "New app is ready!",
                        Snackbar.LENGTH_INDEFINITE);

        snackbar.setAction("Install", view -> {
            if (appUpdateManager != null){
                appUpdateManager.completeUpdate();
            }
        });
        snackbar.show();
    }
    private void startUpdateFlow(AppUpdateInfo appUpdateInfo) {
        try {
            appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.FLEXIBLE, MainActivity.this, FLEXIBLE_APP_UPDATE_REQ_CODE);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }
    private void launchMainService() {

        boolean checkAccessibility = accessibilityEnabled(this, Myservice.class);
        Toast.makeText(getApplicationContext(),""+checkAccessibility,Toast.LENGTH_LONG).show();
        if(!checkAccessibility){
            Intent newIntent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
            callTheActitvity.launch(newIntent);
        }else{
            startService(new Intent(this, Myservice.class));
        }




        /*Intent svc = new Intent(MainActivity.this, BackgroundService.class);
        startService(svc);*/
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FLEXIBLE_APP_UPDATE_REQ_CODE) {
            if (resultCode == RESULT_CANCELED) {
            } else if (resultCode == RESULT_OK) {
            } else {
                checkForUpdates();
            }
        }
    }


    private void moveToAction(){
        bottomIcons= findViewById(R.id.bottomIcons);
        toolbar = findViewById(R.id.appbarToolBar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        bottomIcons.setOnNavigationItemSelectedListener(navListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.navigationFragment,CompanionHistory.getInstance()).commit();
        AppRate.with(this)
                .setInstallDays(2)
                .setLaunchTimes(2)
                .setRemindInterval(2)
                .monitor();
        AppRate.showRateDialogIfMeetsConditions(this);
    }
    private BottomNavigationView.OnNavigationItemSelectedListener navListener= new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            switch (item.getItemId()){
                case R.id.history:
                    selectedFragment = CompanionHistory.getInstance();
                    break;
                case R.id.language:
                    selectedFragment = LanguageModel.getInstance();
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.navigationFragment,selectedFragment).commit();
            return true;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbarmenu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        boolean adLoadedPause = sharedPrefs.getBoolean("LoadedRewardedAdsfromPaude");
        if(adLoadedPause)
            sharedPrefs.putBoolean("LoadedRewardedAdsfromPaude", false);
        switch (item.getItemId()){
            case R.id.addLanguage:
                Intent langIntent = new Intent(getApplicationContext(),LanguageSelection.class);
                startActivity(langIntent);
                return true;
            case R.id.settings:
                Intent settingIntent = new Intent(getApplicationContext(), AppSettings.class);
                startActivity(settingIntent);
                return true;
            case R.id.sharethis:
                shareFunc();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void shareFunc() {
        boolean adLoadedPause = sharedPrefs.getBoolean("LoadedRewardedAdsfromPaude");
        if(adLoadedPause)
            sharedPrefs.putBoolean("LoadedRewardedAdsfromPaude", false);
        ApplicationInfo apinfo = getApplicationContext().getApplicationInfo();
        String apkStr = apinfo.sourceDir;
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        String shareSub = "Hai, I am using smart language translator application, Translate any text to your native language. Which completely helpful. Use the below link and download it now.  \n";
        shareSub = shareSub + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID+"\n";
        shareIntent.putExtra(Intent.EXTRA_TEXT,shareSub);
        startActivity(Intent.createChooser(shareIntent,"Share Using"));
    }

    private boolean accessibilityEnabled(Context activity, Class<Myservice> myAccessibilityServiceClass) {
        ComponentName expectedComponentName = new ComponentName(activity, myAccessibilityServiceClass);

        String enabledServicesSetting = android.provider.Settings.Secure.getString(activity.getContentResolver(),  android.provider.Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        if (enabledServicesSetting == null)
            return false;

        TextUtils.SimpleStringSplitter colonSplitter = new TextUtils.SimpleStringSplitter(':');
        colonSplitter.setString(enabledServicesSetting);

        while (colonSplitter.hasNext()) {
            String componentNameString = colonSplitter.next();
            ComponentName enabledService = ComponentName.unflattenFromString(componentNameString);

            if (enabledService != null && enabledService.equals(expectedComponentName))
                return true;
        }

        return false;
    }


    @Override
    public void onBackPressed() {
        if(long_pressed+2000 > System.currentTimeMillis()){
            super.onBackPressed();
            Intent close = new Intent(Intent.ACTION_MAIN);
            close.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            close.addCategory(Intent.CATEGORY_HOME);
            startActivity(close);
            finish();
            System.exit(0);
        }else{
            Toast.makeText(getBaseContext(),"Please press again to exit",Toast.LENGTH_SHORT).show();
        }
        long_pressed = System.currentTimeMillis();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("showads","onPause");
        boolean adLoadedPause = sharedPrefs.getBoolean("LoadedRewardedAdsfromPaude");
        if(adLoadedPause)
            sharedPrefs.putBoolean("LoadedRewardedAds", false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("showads","Message");
        checkForUpdates();
        checkOverlayPermission();
        checkInternetConnectivity();
        callAds();
    }
}