package io.incepted.cryptoaddresstracker.Activities;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

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
