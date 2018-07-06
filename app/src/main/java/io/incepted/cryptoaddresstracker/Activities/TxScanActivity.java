package io.incepted.cryptoaddresstracker.Activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.incepted.cryptoaddresstracker.R;
import io.incepted.cryptoaddresstracker.Utils.SnackbarUtils;
import io.incepted.cryptoaddresstracker.Utils.ViewModelFactory;
import io.incepted.cryptoaddresstracker.ViewModels.TxScanViewModel;
import io.incepted.cryptoaddresstracker.databinding.ActivityTxScanBinding;

public class TxScanActivity extends BaseActivity {

    private static final String TAG = TxScanActivity.class.getSimpleName();

    private static final int TX_SCAN_TO_DETAIL_REQUEST_CODE = 6512;

    @BindView(R.id.tx_scan_toolbar)
    Toolbar mToolbar;

    private TxScanViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityTxScanBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_tx_scan);
        mViewModel = obtainViewModel(this);
        binding.setViewmodel(mViewModel);

        ButterKnife.bind(this);

        initToolbar();
        setupSnackbar();
        setupObservers();
    }

    private TxScanViewModel obtainViewModel(FragmentActivity activity) {
        ViewModelFactory factory = ViewModelFactory.getInstance(activity.getApplication());
        return ViewModelProviders.of(activity, factory).get(TxScanViewModel.class);
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupSnackbar() {
        mViewModel.getSnackbarText().observe(this, this::showSnackbar);
        mViewModel.getSnackbarTextResource().observe(this, stringResource -> {
            if (stringResource != null)
                showSnackbar(getString(stringResource));
        });
    }

    private void setupObservers() {
        mViewModel.getInitiateScan().observe(this, aVoid -> initiateScan());
        mViewModel.getOpenTxDetail().observe(this, this::toTxDetailActivity);
    }

    private void initiateScan() {
        IntentIntegrator intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.setPrompt("Scan a QR code");
        intentIntegrator.setBeepEnabled(true);
        intentIntegrator.initiateScan();
    }

    private void toTxDetailActivity(String txHash) {
        Intent intent = new Intent(this, TxDetailActivity.class);
        intent.putExtra(TxDetailActivity.TX_DETAIL_HASH_EXTRA_KEY, txHash);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IntentIntegrator.REQUEST_CODE) {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (result != null) {
                mViewModel.handleActivityResult(result);
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
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

    private void showSnackbar(String s) {
        SnackbarUtils.showSnackbar(findViewById(android.R.id.content), s);
    }
}