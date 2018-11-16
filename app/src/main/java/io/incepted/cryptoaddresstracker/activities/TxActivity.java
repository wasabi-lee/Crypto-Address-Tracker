package io.incepted.cryptoaddresstracker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.appbar.AppBarLayout;

import java.util.Objects;

import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.incepted.cryptoaddresstracker.R;
import io.incepted.cryptoaddresstracker.adapters.TxAdapter;
import io.incepted.cryptoaddresstracker.databinding.ActivityTxBinding;
import io.incepted.cryptoaddresstracker.listeners.TxItemActionListener;
import io.incepted.cryptoaddresstracker.network.deserializer.SimpleTxItem;
import io.incepted.cryptoaddresstracker.utils.SnackbarUtils;
import io.incepted.cryptoaddresstracker.utils.ViewModelFactory;
import io.incepted.cryptoaddresstracker.viewModels.TxViewModel;
import timber.log.Timber;

public class TxActivity extends BaseActivity implements AppBarLayout.OnOffsetChangedListener {


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
    @BindView(R.id.tx_progress_bar)
    ProgressBar mProgessBar;
    @BindView(R.id.tx_no_tx_found)
    TextView mNoTxFound;

    private boolean mIsTheTitleVisible = false;
    private boolean mIsTheTitleContainerVisible = true;

    private TxViewModel mViewModel;

    private int mAddressId;
    private String mTokenName;
    private String mTokenAddress;
    //    private boolean mIsContractAddress;
    private TxAdapter mAdapter;

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


    }

    public static TxViewModel obtainViewModel(FragmentActivity activity) {
        ViewModelFactory factory = ViewModelFactory.getInstance(activity.getApplication());
        return ViewModelProviders.of(activity, factory).get(TxViewModel.class);
    }

    private void unpackExtras() {
        mAddressId = getIntent().getIntExtra(TX_ADDRESS_ID_EXTRA_KEY, 0);
        mTokenName = getIntent().getStringExtra(TX_TOKEN_NAME_EXTRA_KEY);
        mTokenAddress = getIntent().getStringExtra(TX_TOKEN_ADDRESS_EXTRA_KEY);
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
        mViewModel.getSnackbarText().observe(this, this::showSnackbar);

        mViewModel.getSnackbarTextResource().observe(this, message ->
                showSnackbar(getString(message)));

    }

    private void setupObservers() {
        mViewModel.getOpenTxDetail().observe(this, this::toTxDetailActivity);

        mViewModel.getEthTxList().observe(this, items -> {
            String addrValue = Objects.requireNonNull(mViewModel.getmAddress().get()).getAddrValue();
            mAdapter.submitList(items, addrValue);
        });

        mViewModel.getItemExists().observe(this, itemExists ->
                mNoTxFound.setVisibility(itemExists ? View.GONE : View.VISIBLE));

        mViewModel.getIsLoading().observe(this, isLoading ->
                mProgessBar.setVisibility(isLoading ? View.VISIBLE : View.GONE));

        mViewModel.getNetworkError().observe(this, this::showSnackbar);
    }

    private void setupRecyclerView() {
        mTxList.setHasFixedSize(true);
        mTxList.setNestedScrollingEnabled(true);
        mTxList.setItemAnimator(new DefaultItemAnimator());
        mTxList.setLayoutManager(new LinearLayoutManager(this));

        TxItemActionListener listener = transactionHash -> mViewModel.toTxDetailActivity(transactionHash);

        mAdapter = new TxAdapter(SimpleTxItem.DIFF_CALLBACK, listener);
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
        if (percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {
            if (!mIsTheTitleVisible) {
                startAlphaAnimation(mTitle, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleVisible = true;
            }

        } else {

            if (mIsTheTitleVisible) {
                startAlphaAnimation(mTitle, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleVisible = false;
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
        mViewModel.start(mAddressId, mTokenName, mTokenAddress);
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
