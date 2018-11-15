package io.incepted.cryptoaddresstracker.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.incepted.cryptoaddresstracker.R;
import io.incepted.cryptoaddresstracker.adapters.ViewPagerAdapter;
import io.incepted.cryptoaddresstracker.data.txExtraWrapper.TxExtraWrapper;
import io.incepted.cryptoaddresstracker.databinding.ActivityDetailBinding;
import io.incepted.cryptoaddresstracker.fragments.OverviewFragment;
import io.incepted.cryptoaddresstracker.fragments.TransactionFragment;
import io.incepted.cryptoaddresstracker.utils.SnackbarUtils;
import io.incepted.cryptoaddresstracker.utils.ViewModelFactory;
import io.incepted.cryptoaddresstracker.viewModels.DetailViewModel;
import timber.log.Timber;

public class DetailActivity extends BaseActivity implements AppBarLayout.OnOffsetChangedListener {

    private static final String TAG = DetailActivity.class.getSimpleName();

    public static final String ADDRESS_ID_EXTRA_KEY = "address_id_extra_key";

    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR = 0.6f;
    private static final float PERCENTAGE_TO_HIDE_TITLE_DETAILS = 0.3f;
    private static final int ALPHA_ANIMATIONS_DURATION = 200;

    private boolean mIsTheTitleVisible = false;
    private boolean mIsTheTitleContainerVisible = true;

    @BindView(R.id.detail_appbar_content_inner_container)
    LinearLayout mTitleContainer;
    @BindView(R.id.detail_textview_title)
    TextView mTitle;
    @BindView(R.id.detail_app_bar)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.detail_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.detail_tab_layout)
    TabLayout mTabLayout;
    @BindView(R.id.detail_view_pager)
    ViewPager mViewPager;
    @BindView(R.id.detail_progress_bar)
    ProgressBar mProgressBar;

    private DetailViewModel mViewModel;

    private int mAddressId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityDetailBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_detail);
        mViewModel = obtainViewModel(this);
        binding.setViewmodel(mViewModel);

        ButterKnife.bind(this);

        mAddressId = unpackExtra();

        initToolbar();
        initAppbar();
        initFragments();
        setupObservers();
        setupSnackbar();

    }


    public static DetailViewModel obtainViewModel(FragmentActivity activity) {
        ViewModelFactory factory = ViewModelFactory.getInstance(activity.getApplication());
        DetailViewModel a = ViewModelProviders.of(activity, factory).get(DetailViewModel.class);
        return a;
    }


    private int unpackExtra() {
        return getIntent().getIntExtra(ADDRESS_ID_EXTRA_KEY, 0);
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


    private void initFragments() {
        ViewPagerAdapter mAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mAdapter.addFragment(ViewPagerAdapter.FARG_POSITION_OVERVIEW, new OverviewFragment(), "Overview");
        mAdapter.addFragment(ViewPagerAdapter.FARG_POSITION_TOKEN, new TransactionFragment(), "Transactions");
        mViewPager.setAdapter(mAdapter);
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                /* empty */
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                /* empty */
            }
        });
        mTabLayout.setupWithViewPager(mViewPager);
    }


    private void setupObservers() {
        mViewModel.getOpenTokenTransactions().observe(this, this::toTxActivity);

        mViewModel.getOpenTxDetailActivity().observe(this, this::toTxDetailActivity);

        mViewModel.getDeletionState().observe(this, deletionStateNavigator -> {
            if (deletionStateNavigator != null)
                switch (deletionStateNavigator) {
                    case DELETION_IN_PROGRESS:
                        mProgressBar.setVisibility(View.VISIBLE);
                        break;
                    case DELETION_SUCCESSFUL:
                        mProgressBar.setVisibility(View.GONE);
                        finish();
                        break;
                    case DELETION_FAILED:
                        mProgressBar.setVisibility(View.GONE);
                        break;
                }
        });

        mViewModel.getEthNetworkError().observe(this, this::showSnackbar);

        mViewModel.getTokenNetworkError().observe(this, this::showSnackbar);
    }


    private void setupSnackbar() {
        mViewModel.getSnackbarText().observe(this, this::showSnackbar);

        mViewModel.getSnackbarTextResource().observe(this, stringResource ->
                showSnackbar(getString(stringResource)));
    }


    // ------------------------------- Activity transition methods --------------------------

    private void toTxActivity(TxExtraWrapper wrapper) {
        Intent intent = new Intent(this, TxActivity.class);
        intent.putExtra(TxActivity.TX_ADDRESS_ID_EXTRA_KEY, wrapper.getAddressId());
        intent.putExtra(TxActivity.TX_TOKEN_NAME_EXTRA_KEY, wrapper.getTokenName());
        intent.putExtra(TxActivity.TX_TOKEN_ADDRESS_EXTRA_KEY, wrapper.getTokenAddress());
        startActivity(intent);
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
        mViewModel.start(mAddressId);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.menu_detail_edit:
                launchEditDialog();
                break;
            case R.id.menu_detail_delete:
                launchDeletionDialog();
                break;
        }


        return super.onOptionsItemSelected(item);
    }


    private void launchEditDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View dialogView = getLayoutInflater().inflate(R.layout.address_name_edit_dialog, null);
        builder.setView(dialogView);

        final EditText nameEdit = dialogView.findViewById(R.id.address_name_edit);
        builder.setTitle("Edit Address Name")
                .setPositiveButton("DONE", (dialog, which) -> {
                    mViewModel.updateAddressNewName(nameEdit.getText().toString());
                    dialog.dismiss();
                })
                .setNegativeButton("CANCEL", (dialog, which) -> dialog.dismiss())
                .create()
                .show();

    }


    private void launchDeletionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false)
                .setTitle("Are you sure?")
                .setMessage("This address wil be deleted from your watchlist and cannot be revoked")
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.dismiss();
                    mViewModel.deleteAddress();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create().show();
    }


    private void showSnackbar(String s) {
        SnackbarUtils.showSnackbar(findViewById(android.R.id.content), s);
    }
}
