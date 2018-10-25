package io.incepted.cryptoaddresstracker.activities;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.incepted.cryptoaddresstracker.R;
import io.incepted.cryptoaddresstracker.fragments.SettingsFragment;
import io.incepted.cryptoaddresstracker.listeners.OnThemeChangedListener;

public class SettingsActivity extends BaseActivity implements OnThemeChangedListener {

    private static final String TAG = SettingsActivity.class.getSimpleName();

    @BindView(R.id.settings_toolbar)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

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
    public void onThemeChanged(boolean isDarkMode) {
        recreate();
    }

    @Override
    public void onBackPressed() {
        setActivityResult();
        super.onBackPressed();
    }

    private void setActivityResult() {
        setResult(RESULT_OK);
    }
}
