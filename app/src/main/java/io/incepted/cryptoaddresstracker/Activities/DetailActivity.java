package io.incepted.cryptoaddresstracker.Activities;

import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.incepted.cryptoaddresstracker.Adapters.TokenAdapter;
import io.incepted.cryptoaddresstracker.Fragments.OverviewFragment;
import io.incepted.cryptoaddresstracker.Navigators.DeletionStateNavigator;
import io.incepted.cryptoaddresstracker.R;
import io.incepted.cryptoaddresstracker.Fragments.TokenFragment;
import io.incepted.cryptoaddresstracker.Adapters.ViewPagerAdapter;
import io.incepted.cryptoaddresstracker.Utils.SnackbarUtils;
import io.incepted.cryptoaddresstracker.Utils.ViewModelFactory;
import io.incepted.cryptoaddresstracker.ViewModels.DetailViewModel;
import io.incepted.cryptoaddresstracker.databinding.ActivityDetailBinding;

public class DetailActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener {

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
        setContentView(R.layout.activity_detail);
        ActivityDetailBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_detail);
        mViewModel = obtainViewModel(this);
        binding.setViewmodel(mViewModel);

        ButterKnife.bind(this);


        mAddressId = unpackExtra();
        Log.d(TAG, "onCreate: Address id: " + mAddressId);

        initToolbar();
        initAppbar();
        initFragments();
        setupObservers();
        setupSnackbar();


    }

    public static DetailViewModel obtainViewModel(FragmentActivity activity) {
        ViewModelFactory factory = ViewModelFactory.getInstance(activity.getApplication());
        return ViewModelProviders.of(activity, factory).get(DetailViewModel.class);
    }

    private int unpackExtra() {
        return getIntent().getIntExtra(ADDRESS_ID_EXTRA_KEY, 0);
    }

    private void initToolbar() {
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_black);
            //TODO Change back button icon based on the theme
        }
    }

    private void initAppbar() {
        mAppBarLayout.addOnOffsetChangedListener(this);
        startAlphaAnimation(mTitle, 0, View.INVISIBLE);
    }

    private void initFragments() {
        ViewPagerAdapter mAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mAdapter.addFragment(ViewPagerAdapter.FARG_POSITION_OVERVIEW, new OverviewFragment(), "Overview");
        mAdapter.addFragment(ViewPagerAdapter.FARG_POSITION_TOKEN, new TokenFragment(), "Token");
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
    }


    private void setupSnackbar() {
        mViewModel.getSnackbarText().observe(this, this::showSnackbar);

        mViewModel.getSnackbarTextResource().observe(this, stringResource -> {
            if (stringResource != null)
                showSnackbar(getString(stringResource));
        });
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
            case R.id.menu_detail_delete:
                launchDeletionDialog();
                break;
        }


        return super.onOptionsItemSelected(item);
    }

    public void launchDeletionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("Are you sure?");
        builder.setMessage("This address will be deleted from your watchlist and cannot be revoked");
        builder.setPositiveButton("OK", (dialog, which) -> {
            dialog.dismiss();
            mViewModel.deleteAddress();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }


    private void showSnackbar(String s) {
        SnackbarUtils.showSnackbar(findViewById(android.R.id.content), s);
    }
}
