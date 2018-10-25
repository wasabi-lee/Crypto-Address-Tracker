package io.incepted.cryptoaddresstracker.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import io.incepted.cryptoaddresstracker.R;
import io.incepted.cryptoaddresstracker.utils.SharedPreferenceHelper;

public class BaseActivity extends AppCompatActivity {

    private static final String TAG = BaseActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        boolean isDarkTheme = SharedPreferenceHelper.getThemeFlag(this);
        setTheme(isDarkTheme ? R.style.AppTheme_dark : R.style.AppTheme_light);
        super.onCreate(savedInstanceState);


    }
}
