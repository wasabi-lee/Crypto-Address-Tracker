package io.incepted.cryptoaddresstracker.Activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.incepted.cryptoaddresstracker.Adapters.AddressAdapter;
import io.incepted.cryptoaddresstracker.R;
import io.incepted.cryptoaddresstracker.Utils.SnackbarUtils;
import io.incepted.cryptoaddresstracker.Utils.ViewModelFactory;
import io.incepted.cryptoaddresstracker.ViewModels.MainViewModel;
import io.incepted.cryptoaddresstracker.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.main_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.main_drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.main_recycler_view)
    RecyclerView mAddressList;

    private MainViewModel mViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mViewModel = obtainViewModel(this);
        binding.setViewmodel(mViewModel);

        ButterKnife.bind(this);

        initToolbar();
        setupTransitionObservers();
        setupSnackbar();
        setupRecyclerView();

    }

    private MainViewModel obtainViewModel(FragmentActivity activity) {
        ViewModelFactory factory = ViewModelFactory.getInstance(activity.getApplication());
        return ViewModelProviders.of(activity, factory).get(MainViewModel.class);
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        }
    }

    private void setupRecyclerView() {
        AddressAdapter adapter = new AddressAdapter(new ArrayList<>(0), mViewModel);
        mAddressList.setHasFixedSize(true);
        mAddressList.setItemAnimator(new DefaultItemAnimator());
        mAddressList.setLayoutManager(new LinearLayoutManager(this));
        mAddressList.setAdapter(adapter);
    }

    private void setupSnackbar() {
        mViewModel.getSnackbarText().observe(this, this::showSnackbar);

        mViewModel.getSnackbarTextResource().observe(this, stringResource -> {
            if (stringResource != null)
                showSnackbar(getString(stringResource));
        });
    }

    private void setupTransitionObservers() {
        mViewModel.getActivityNavigator().observe(this, activityNavigator -> {
            if (activityNavigator != null)
                switch (activityNavigator) {
                    case NEW_ADDRESS:
                        toNewAddressActivity();
                        break;
                    case TOKEN_ADDRESS:
                        toTokenAddressActivity();
                        break;
                    case TX_SCAN:
                        toTxScanActivity();
                        break;
                    case SETTINGS:
                        toSettingsActivity();
                        break;
                }
        });

        mViewModel.getOpenAddressDetail().observe(this, addressId -> {
            if (addressId != null)
                toDetailActivity(addressId);
        });
    }


    // ------------------------- Activity transition -------------------------

    private void toNewAddressActivity() {
        Intent intent = new Intent(this, NewAddressActivity.class);
        startActivity(intent);
    }

    private void toDetailActivity(int addressId) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(DetailActivity.ADDRESS_ID_EXTRA_KEY, addressId);
        startActivity(intent);
    }

    private void toTokenAddressActivity() {
        Log.d(TAG, "toTokenAddressActivity: ");
        Intent intent = new Intent(this, TokenAddressActivity.class);
        startActivity(intent);
    }

    private void toTxScanActivity() {
        Log.d(TAG, "toTxScanActivity: ");
        Intent intent = new Intent(this, TxScanActivity.class);
        startActivity(intent);
    }

    private void toSettingsActivity() {
        Log.d(TAG, "toSettingsActivity: ");
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }


    @Override
    protected void onResume() {
        super.onResume();
        mViewModel.start();
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
                mViewModel.addNewAddress();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showSnackbar(String s) {
        SnackbarUtils.showSnackbar(findViewById(android.R.id.content), s);
    }
}
