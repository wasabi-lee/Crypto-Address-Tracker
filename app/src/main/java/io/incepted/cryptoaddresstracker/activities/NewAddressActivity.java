package io.incepted.cryptoaddresstracker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.incepted.cryptoaddresstracker.R;
import io.incepted.cryptoaddresstracker.databinding.ActivityNewAddressBinding;
import io.incepted.cryptoaddresstracker.utils.SnackbarUtils;
import io.incepted.cryptoaddresstracker.utils.ViewModelFactory;
import io.incepted.cryptoaddresstracker.viewModels.NewAddressViewModel;

public class NewAddressActivity extends BaseActivity {

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
        mViewModel.getEditTextErrorText().observe(this, message ->
                mAddrEdit.setError(getString(message)));
    }

    private void setupSnackbar() {
        mViewModel.getSnackbarText().observe(this, this::showSnackbar);
        mViewModel.getSnackbarTextResource().observe(this, stringResource ->
                showSnackbar(getString(stringResource)));

    }


    private void toQRScanActivity() {
        IntentIntegrator intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.setPrompt("Scan a QR code");
        intentIntegrator.setBeepEnabled(true);
        intentIntegrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            mViewModel.handleActivityResult(result);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
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
