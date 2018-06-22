package io.incepted.cryptoaddresstracker.Activities;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import butterknife.BindView;
import io.incepted.cryptoaddresstracker.R;
import io.incepted.cryptoaddresstracker.Utils.ViewModelFactory;
import io.incepted.cryptoaddresstracker.ViewModels.MainViewModel;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.main_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.main_drawer_layout)
    DrawerLayout mDrawerLayout;

    private MainViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mViewModel = obtainViewModel(this);
        initToolbar();

    }

    private MainViewModel obtainViewModel(FragmentActivity activity) {
        ViewModelFactory factory = ViewModelFactory.getInstance(activity.getApplication());
        return ViewModelProviders.of(activity, factory).get(MainViewModel.class);
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.main_add_new_address:
                //TODO Add a new address
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
