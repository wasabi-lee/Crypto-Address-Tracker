package io.incepted.cryptoaddresstracker.Activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.incepted.cryptoaddresstracker.R;
import io.incepted.cryptoaddresstracker.Utils.SnackbarUtils;
import io.incepted.cryptoaddresstracker.Utils.ViewModelFactory;
import io.incepted.cryptoaddresstracker.ViewModels.NewAddressViewModel;
import io.incepted.cryptoaddresstracker.databinding.ActivityNewAddressBinding;

public class NewAddressActivity extends AppCompatActivity {

    private static final String TAG = NewAddressViewModel.class.getSimpleName();

    @BindView(R.id.new_addr_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.new_addr_name_edit_text)
    EditText mNameEdit;
    @BindView(R.id.new_addr_addr_edit_text)
    EditText mAddrEdit;
    @BindView(R.id.new_addr_save_button)
    TextView mSaveButton;
    @BindView(R.id.new_addr_progress_bar)
    ProgressBar mProgressBar;

    private NewAddressViewModel mViewModel;

    private static final int QR_SCAN_ACTIVITY_REQUEST_CODE = 132;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityNewAddressBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_new_address);
        mViewModel = obtainViewModel(this);
        binding.setViewmodel(mViewModel);

        ButterKnife.bind(this);

        initToolbar();
        setupAddressStateObserver();
        setupObservers();
        setupSnackbar();

    }

    private NewAddressViewModel obtainViewModel(FragmentActivity activity) {
        ViewModelFactory factory = ViewModelFactory.getInstance(activity.getApplication());
        return ViewModelProviders.of(activity, factory).get(NewAddressViewModel.class);
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupAddressStateObserver() {
        mViewModel.getAddressState().observe(this, addressStateNavigator -> {
            if (addressStateNavigator != null)
            switch (addressStateNavigator) {
                case SAVE_IN_PROGRESS:
                    mProgressBar.setVisibility(View.VISIBLE);
                    break;
                case SAVE_SUCCESSFUL:
                    mProgressBar.setVisibility(View.GONE);
                    // Close activity
                    finish();
                    break;
                case SAVE_ERROR:
                    mProgressBar.setVisibility(View.GONE);
                    break;
            }
        });
    }

    private void setupObservers() {
        mViewModel.getOpenQRScanActivity().observe(this, aVoid -> toQRScanActivity());
    }

    private void setupSnackbar() {
        mViewModel.getSnackbarText().observe(this, this::showSnackbar);

        mViewModel.getSnackbarTextResource().observe(this, stringResource -> {
            if (stringResource != null)
                showSnackbar(getString(stringResource));
        });

    }


    private void toQRScanActivity() {
        Intent intent = new Intent(this, QRScanActivity.class);
        startActivityForResult(intent, QR_SCAN_ACTIVITY_REQUEST_CODE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.new_addr_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.new_addr_menu_save:
                mViewModel.saveAddress();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSnackbar(String s) {
        SnackbarUtils.showSnackbar(findViewById(android.R.id.content), s);
    }
}
