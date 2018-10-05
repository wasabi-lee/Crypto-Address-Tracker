package io.incepted.cryptoaddresstracker.activities;

import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.incepted.cryptoaddresstracker.R;
import io.incepted.cryptoaddresstracker.utils.SnackbarUtils;
import io.incepted.cryptoaddresstracker.utils.ViewModelFactory;
import io.incepted.cryptoaddresstracker.viewModels.TxDetailViewModel;
import io.incepted.cryptoaddresstracker.databinding.ActivityTxDetailBinding;

public class TxDetailActivity extends BaseActivity {

    private static final String TAG = TxDetailActivity.class.getSimpleName();

    public static final String TX_DETAIL_HASH_EXTRA_KEY = "tx_detail_hash_extra_key";

    @BindView(R.id.tx_detail_toolbar)
    Toolbar mToolbar;

    private TxDetailViewModel mViewModel;

    private String mTxHash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityTxDetailBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_tx_detail);
        mViewModel = obtainViewModel(this);
        binding.setViewmodel(mViewModel);

        ButterKnife.bind(this);

        mTxHash = unpackExtra();
        Log.d(TAG, "onCreate: TxHash:" + mTxHash);

        initToolbar();
        setupSnackbar();
        setupObservers();
    }

    public static TxDetailViewModel obtainViewModel(FragmentActivity activity) {
        ViewModelFactory factory = ViewModelFactory.getInstance(activity.getApplication());
        return ViewModelProviders.of(activity, factory).get(TxDetailViewModel.class);
    }

    private String unpackExtra() {
        return getIntent().getStringExtra(TX_DETAIL_HASH_EXTRA_KEY);
    }

    private void initToolbar() {
        mToolbar.setTitle("Transaction Detail");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setupSnackbar() {
        mViewModel.getSnackbarText().observe(this, message -> {
            if (message != null)
                showSnackbar(message);
        });

        mViewModel.getSnackbarTextResource().observe(this, message -> {
            if (message != null)
                showSnackbar(getString(message));
        });

    }

    private void setupObservers() {
        mViewModel.getOpenTokenOperations().observe(this, this::toTokenTransferActivity);
    }

    private void toTokenTransferActivity(String txHash) {
        Intent intent = new Intent(this, TokenTransferActivity.class);
        intent.putExtra(TokenTransferActivity.TOKEN_TRANSFER_TX_HASH_EXTRA_KEY, txHash);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mViewModel.start(mTxHash);
    }

    private void showSnackbar(String s) {
        SnackbarUtils.showSnackbar(findViewById(android.R.id.content), s);
    }

}