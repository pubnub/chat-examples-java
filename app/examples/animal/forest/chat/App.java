package animal.forest.chat;

import android.app.Application;
import android.content.Intent;

import animal.forest.chat.prefs.Prefs;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class App extends Application {

    private static App sInstance;

    public App() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sInstance = this;

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        Prefs.initialize(this);
    }

    public static synchronized App get() {
        if (sInstance == null) {
            sInstance = new App();
        }
        return sInstance;
    }

    public void clearCacheAndRestart(final AppCompatActivity activity) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Prefs.get().clearAllData();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        App.get().restartApplication(activity);
                    }
                });
            }
        }).start();
    }

    private void restartApplication(AppCompatActivity activity) {
        Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("restart", true);
        activity.finish();
        activity.startActivity(i);
        System.exit(0);
    }
}
