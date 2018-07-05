package io.incepted.cryptoaddresstracker.Activities;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.incepted.cryptoaddresstracker.Fragments.SettingsFragment;
import io.incepted.cryptoaddresstracker.Listeners.OnThemeChangedListener;
import io.incepted.cryptoaddresstracker.R;
import io.incepted.cryptoaddresstracker.Utils.SharedPreferenceHelper;

public class SettingsActivity extends BaseActivity implements OnThemeChangedListener {

    private static final String TAG = SettingsActivity.class.getSimpleName();

    private boolean mThemeChanged = false;

    @BindView(R.id.settings_toolbar)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        boolean isDarkTheme = SharedPreferenceHelper.getThemeFlag(this);

        ButterKnife.bind(this);

        initToolbar();

        // Initiate SettingsFragment
        if (savedInstanceState == null) {
            Fragment preferenceFragment = new SettingsFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.pref_container, preferenceFragment);
            ft.commit();
        }

    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onThemeChanged() {
        mThemeChanged = !mThemeChanged;
        Log.d(TAG, "onThemeChanged: " + mThemeChanged);
        recreate();
    }

    @Override
    public void onBackPressed() {
        setActivityResult();
        super.onBackPressed();
    }

    private void setActivityResult() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(MainActivity.IS_THEME_CHANGED_EXTRA_KEY, mThemeChanged);
        Log.d(TAG, "setActivityResult: themeChanged" + mThemeChanged);
        setResult(RESULT_OK, returnIntent);
    }
}
