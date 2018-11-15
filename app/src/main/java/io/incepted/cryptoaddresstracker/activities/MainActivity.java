package io.incepted.cryptoaddresstracker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.incepted.cryptoaddresstracker.CryptoAddressTracker;
import io.incepted.cryptoaddresstracker.R;
import io.incepted.cryptoaddresstracker.adapters.AddressAdapter;
import io.incepted.cryptoaddresstracker.databinding.ActivityMainBinding;
import io.incepted.cryptoaddresstracker.utils.CurrencyUtils;
import io.incepted.cryptoaddresstracker.utils.SharedPreferenceHelper;
import io.incepted.cryptoaddresstracker.utils.SnackbarUtils;
import io.incepted.cryptoaddresstracker.utils.ViewModelFactory;
import io.incepted.cryptoaddresstracker.viewModels.MainViewModel;
import timber.log.Timber;

public class MainActivity extends BaseActivity {

    private static final int SETTINGS_REQUEST_CODE = 34;

    @BindView(R.id.main_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.main_drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.main_recycler_view)
    RecyclerView mAddressList;

    private MainViewModel mViewModel;
    private boolean mIsDarkMode;
    private boolean shouldShowEthInitial;
    private AddressAdapter mAdapter;

    private MenuItem mCurrencyMenu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mViewModel = obtainViewModel(this);
        binding.setViewmodel(mViewModel);

        ButterKnife.bind(this);

        mIsDarkMode = SharedPreferenceHelper.getThemeFlag(this);

        initToolbar();
        initPreferences();
        setupObservers();
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

    private void initPreferences() {
        shouldShowEthInitial = SharedPreferenceHelper.getIsLastShownCurrencyEth(getApplicationContext());
    }

    private String getBaseCurrency() {
        int baseCurrencyIntValue = SharedPreferenceHelper.getBaseCurrencyPrefValue(getApplicationContext());
        return CurrencyUtils.getBaseCurrencyString(baseCurrencyIntValue);
    }


    private void setupRecyclerView() {
        mAdapter = new AddressAdapter(new ArrayList<>(0),
                mViewModel, CurrencyUtils.getPlaceholderObject(shouldShowEthInitial));
        mAddressList.setHasFixedSize(true);
        mAddressList.setItemAnimator(new DefaultItemAnimator());
        mAddressList.setLayoutManager(new LinearLayoutManager(this));
        mAddressList.setAdapter(mAdapter);
    }


    private void setupObservers() {

        ((CryptoAddressTracker) getApplication()).getNetworkLiveData()
                .observe(this, connected -> Timber.d("Network: %s", connected ? "Connected" : "Disconnected"));

        mViewModel.getCurrentPrice().observe(this, currentPrice ->
                mAdapter.toggleDisplayCurrency(currentPrice));
    }


    private void setupSnackbar() {
        mViewModel.getSnackbarText().observe(this, this::showSnackbar);

        mViewModel.getSnackbarTextResource().observe(this, stringResource ->
                showSnackbar(getString(stringResource)));
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

        mViewModel.getOpenAddressDetail().observe(this, this::toDetailActivity);
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
        Intent intent = new Intent(this, TokenAddressActivity.class);
        startActivity(intent);
    }

    private void toTxScanActivity() {
        Intent intent = new Intent(this, TxScanActivity.class);
        startActivity(intent);
    }

    private void toSettingsActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivityForResult(intent, SETTINGS_REQUEST_CODE);
    }


    // ------------------------- currency setting --------------------------

    private void setCurrencyMenuTitleText() {
        if (mCurrencyMenu != null) {
            mCurrencyMenu.setTitle(mViewModel.getShouldDisplayEthPrice() ? "ETH" : getBaseCurrency());
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        setCurrencyMenuTitleText();
        mViewModel.start(getBaseCurrency(), shouldShowEthInitial);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        mCurrencyMenu = menu.findItem(R.id.main_currency_conversion);
        setCurrencyMenuTitleText();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.main_currency_conversion:
                item.setTitle(mViewModel.toggleDisplayCurrency() ? "ETH" : getBaseCurrency());
                return true;
            case R.id.main_add_new_address:
                mViewModel.addNewAddress();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferenceHelper.writeIsLastShownCurrencyEth(getApplicationContext(),
                mViewModel.getShouldDisplayEthPrice());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SETTINGS_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                boolean isDarkModeUpdated = SharedPreferenceHelper.getThemeFlag(this);
                boolean isThemeChanged = (mIsDarkMode != isDarkModeUpdated);
                if (isThemeChanged) {
                    recreate();
                }
            }
        }
    }

    private void showSnackbar(String s) {
        SnackbarUtils.showSnackbar(findViewById(android.R.id.content), s);
    }
}
