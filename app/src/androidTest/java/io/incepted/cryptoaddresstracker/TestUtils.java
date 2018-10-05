package io.incepted.cryptoaddresstracker;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;

import java.util.Collection;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import androidx.test.runner.lifecycle.Stage;

import static androidx.test.InstrumentationRegistry.getInstrumentation;

public class TestUtils {

    private static void rotateToLandscape(AppCompatActivity activity) {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    private static void rotateToPortrait(AppCompatActivity activity) {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    public static void rotateOrientation(AppCompatActivity activity) {
        int currentOrientation = activity.getResources().getConfiguration().orientation;
        switch (currentOrientation) {
            case Configuration.ORIENTATION_LANDSCAPE:
                rotateToPortrait(activity);
                break;
            case Configuration.ORIENTATION_PORTRAIT:
                rotateToLandscape(activity);
                break;
            default:
                rotateToLandscape(activity);
        }
    }

    public static String getToolbarNavigationContentDescription(@NonNull AppCompatActivity activity, @IdRes int toolbar1) {
        Toolbar toolbar = activity.findViewById(toolbar1);
        if (toolbar != null) {
            return (String) toolbar.getNavigationContentDescription();
        } else {
            throw new RuntimeException("No toolbar found.");
        }
    }


    public static AppCompatActivity getCurrentActivity() throws IllegalStateException {
        final AppCompatActivity[] resumedActivity = new AppCompatActivity[1];

        getInstrumentation().runOnMainSync(() -> {
            Collection resumedActivities = ActivityLifecycleMonitorRegistry.getInstance()
                    .getActivitiesInStage(Stage.RESUMED);
            if (resumedActivities.iterator().hasNext()) {
                resumedActivity[0] = (AppCompatActivity) resumedActivities.iterator().next();
             } else {
                throw new IllegalStateException("No Activity in stage RESUMED");
            }
        });
        return resumedActivity[0];
    }
}
