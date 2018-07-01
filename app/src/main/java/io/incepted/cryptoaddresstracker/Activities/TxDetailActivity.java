package io.incepted.cryptoaddresstracker.Activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.LinearLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.incepted.cryptoaddresstracker.R;
import io.incepted.cryptoaddresstracker.Utils.SnackbarUtils;
import io.incepted.cryptoaddresstracker.Utils.ViewModelFactory;
import io.incepted.cryptoaddresstracker.ViewModels.DetailViewModel;
import io.incepted.cryptoaddresstracker.ViewModels.TxDetailViewModel;
import io.incepted.cryptoaddresstracker.databinding.ActivityTxDetailBinding;

public class TxDetailActivity extends AppCompatActivity {

    private static final String TAG = TxDetailActivity.class.getSimpleName();

    public static final String TX_DETAIL_HASH_EXTRA_KEY = "tx_detail_hash_extra_key";

    @BindView(R.id.tx_detail_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.tx_detail_bottom_sheet)
    LinearLayout bottomSheet;
    private BottomSheetBehavior mBehavior;

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
        setupBottomSheet();
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

    private void setupBottomSheet() {
        mBehavior = BottomSheetBehavior.from(bottomSheet);
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
        mViewModel.getExpandBottomSheet().observe(this, aVoid -> toggleBottomSheetState());
    }

    private void toggleBottomSheetState() {
        boolean expanded = mBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED;
        mBehavior.setState(expanded ? BottomSheetBehavior.STATE_COLLAPSED : BottomSheetBehavior.STATE_EXPANDED);
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
