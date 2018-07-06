package io.incepted.cryptoaddresstracker.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import io.incepted.cryptoaddresstracker.R;
import io.incepted.cryptoaddresstracker.Utils.SharedPreferenceHelper;

public class BaseActivity extends AppCompatActivity {

    private static final String TAG = BaseActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean isDarkTheme = SharedPreferenceHelper.getThemeFlag(this);
        setTheme(isDarkTheme ? R.style.AppTheme_dark : R.style.AppTheme_light);

    }
}
