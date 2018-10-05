package io.incepted.cryptoaddresstracker.Activities;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import androidx.annotation.Nullable;
import com.google.android.material.appbar.AppBarLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.incepted.cryptoaddresstracker.Adapters.TxAdapter;
import io.incepted.cryptoaddresstracker.R;
import io.incepted.cryptoaddresstracker.Utils.SnackbarUtils;
import io.incepted.cryptoaddresstracker.Utils.ViewModelFactory;
import io.incepted.cryptoaddresstracker.ViewModels.TxViewModel;
import io.incepted.cryptoaddresstracker.databinding.ActivityTxBinding;

public class TxActivity extends BaseActivity implements AppBarLayout.OnOffsetChangedListener {

    private static final String TAG = TxActivity.class.getSimpleName();

    public static final String TX_ADDRESS_ID_EXTRA_KEY = "tx_address_id_extra_key";
    public static final String TX_TOKEN_NAME_EXTRA_KEY = "tx_token_name_extra_key";
    public static final String TX_TOKEN_ADDRESS_EXTRA_KEY = "tx_token_address_extra_key";
    public static final String TX_IS_CONTRACT_ADDRESS_EXTRA_KEY = "tx_is_contract_address_extra_key";

    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR = 0.6f;
    private static final float PERCENTAGE_TO_HIDE_TITLE_DETAILS = 0.3f;
    private static final int ALPHA_ANIMATIONS_DURATION = 200;

    @BindView(R.id.tx_title_container)
    LinearLayout mTitleContainer;
    @BindView(R.id.tx_textview_title)
    TextView mTitle;
    @BindView(R.id.tx_appbar)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.tx_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.tx_recycler_view)
    RecyclerView mTxList;

    private boolean mIsTheTitleVisible = false;
    private boolean mIsTheTitleContainerVisible = true;

    private TxViewModel mViewModel;
    private TxAdapter mAdapter;

    private int mAddressId;
    private String mTokenName;
    private String mTokenAddress;
    private boolean mIsContractAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityTxBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_tx);
        mViewModel = obtainViewModel(this);
        binding.setViewmodel(mViewModel);

        ButterKnife.bind(this);


        unpackExtras();
        initToolbar();
        initAppbar();
        setupSnackbar();
        setupObservers();
        setupRecyclerView();

        Log.d(TAG, "onCreate: Token address: " + mTokenAddress);

    }

    public static TxViewModel obtainViewModel(FragmentActivity activity) {
        ViewModelFactory factory = ViewModelFactory.getInstance(activity.getApplication());
        return ViewModelProviders.of(activity, factory).get(TxViewModel.class);
    }

    private void unpackExtras() {
        mAddressId = getIntent().getIntExtra(TX_ADDRESS_ID_EXTRA_KEY, 0);
        mTokenName = getIntent().getStringExtra(TX_TOKEN_NAME_EXTRA_KEY);
        mTokenAddress = getIntent().getStringExtra(TX_TOKEN_ADDRESS_EXTRA_KEY);
        mIsContractAddress = getIntent().getBooleanExtra(TX_IS_CONTRACT_ADDRESS_EXTRA_KEY, false);
    }

    private void initToolbar() {
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initAppbar() {
        mAppBarLayout.addOnOffsetChangedListener(this);
        startAlphaAnimation(mTitle, 0, View.INVISIBLE);
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
        mViewModel.getmAddress().observe(this, address ->
                mAdapter.setTxHoldingAddress(address != null ? address.getAddrValue() : null));

        mViewModel.getOpenTxDetail().observe(this, txHash -> {
            if (txHash != null)
                toTxDetailActivity(txHash);
            else
                showSnackbar(getString(R.string.unexpected_error));
        });
    }

    private void setupRecyclerView() {
        mTxList.setHasFixedSize(true);
        mTxList.setNestedScrollingEnabled(true);
        mTxList.setItemAnimator(new DefaultItemAnimator());
        mTxList.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new TxAdapter(mViewModel);
        mTxList.setAdapter(mAdapter);
    }

    private void toTxDetailActivity(String txHash) {
        Intent intent = new Intent(this, TxDetailActivity.class);
        intent.putExtra(TxDetailActivity.TX_DETAIL_HASH_EXTRA_KEY, txHash);
        startActivity(intent);
    }


    // ------------------------------- UI animation stuff -----------------------------------

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(offset) / (float) maxScroll;

        handleAlphaOnTitle(percentage);
        handleToolbarTitleVisibility(percentage);
    }

    private void handleToolbarTitleVisibility(float percentage) {
        Log.d(TAG, "Percentage: " + percentage + ", isTheTitleVisible: " + mIsTheTitleVisible);
        if (percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {

            if (!mIsTheTitleVisible) {
                startAlphaAnimation(mTitle, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleVisible = true;
                Log.d(TAG, "Showing toolbar");
            }

        } else {

            if (mIsTheTitleVisible) {
                startAlphaAnimation(mTitle, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleVisible = false;
                Log.d(TAG, "Hiding toolbar");
            }
        }
    }

    private void handleAlphaOnTitle(float percentage) {
        if (percentage >= PERCENTAGE_TO_HIDE_TITLE_DETAILS) {
            if (mIsTheTitleContainerVisible) {
                startAlphaAnimation(mTitleContainer, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleContainerVisible = false;
            }

        } else {

            if (!mIsTheTitleContainerVisible) {
                startAlphaAnimation(mTitleContainer, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleContainerVisible = true;
            }
        }
    }

    public static void startAlphaAnimation(View v, long duration, int visibility) {
        AlphaAnimation alphaAnimation = (visibility == View.VISIBLE)
                ? new AlphaAnimation(0f, 1f)
                : new AlphaAnimation(1f, 0f);

        alphaAnimation.setDuration(duration);
        alphaAnimation.setFillAfter(true);
        v.startAnimation(alphaAnimation);
    }


    // ------------------------------- Activity override method -----------------------------------


    @Override
    protected void onResume() {
        super.onResume();
        mViewModel.start(mAddressId, mTokenName, mTokenAddress, mIsContractAddress);
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
