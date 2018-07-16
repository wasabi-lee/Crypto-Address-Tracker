package io.incepted.cryptoaddresstracker;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.support.test.runner.lifecycle.Stage;
import android.support.v7.widget.Toolbar;

import java.util.Collection;

import static android.support.test.InstrumentationRegistry.getInstrumentation;

public class TestUtils {

    private static void rotateToLandscape(Activity activity) {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    private static void rotateToPortrait(Activity activity) {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    public static void rotateOrientation(Activity activity) {
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

    public static String getToolbarNavigationContentDescription(@NonNull Activity activity, @IdRes int toolbar1) {
        Toolbar toolbar = activity.findViewById(toolbar1);
        if (toolbar != null) {
            return (String) toolbar.getNavigationContentDescription();
        } else {
            throw new RuntimeException("No toolbar found.");
        }
    }


    public static Activity getCurrentActivity() throws IllegalStateException {
        final Activity[] resumedActivity = new Activity[1];

        getInstrumentation().runOnMainSync(() -> {
            Collection resumedActivities = ActivityLifecycleMonitorRegistry.getInstance()
                    .getActivitiesInStage(Stage.RESUMED);
            if (resumedActivities.iterator().hasNext()) {
                resumedActivity[0] = (Activity) resumedActivities.iterator().next();
             } else {
                throw new IllegalStateException("No Activity in stage RESUMED");
            }
        });
        return resumedActivity[0];
    }
}
