package io.incepted.cryptoaddresstracker.Activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import io.incepted.cryptoaddresstracker.Fragments.OverviewFragment;
import io.incepted.cryptoaddresstracker.R;
import io.incepted.cryptoaddresstracker.Fragments.TokenFragment;
import io.incepted.cryptoaddresstracker.Adapters.ViewPagerAdapter;

public class DetailActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener {

    private static final String TAG = DetailActivity.class.getSimpleName();

    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR = 0.6f;
    private static final float PERCENTAGE_TO_HIDE_TITLE_DETAILS = 0.3f;
    private static final int ALPHA_ANIMATIONS_DURATION = 200;


    private boolean mIsTheTitleVisible = false;
    private boolean mIsTheTitleContainerVisible = true;

    private LinearLayout mTitleContainer;
    private TextView mTitle;
    private AppBarLayout mAppBarLayout;
    private Toolbar mToolbar;
    private TabLayout mTabLayout;

    private ViewPagerAdapter mAdapter;
    private ViewPager mViewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        SharedPreferences sharedPref = getSharedPreferences("pref_key", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("dark_mode", false);
        editor.commit();

        boolean isDarkMode = sharedPref.getBoolean("dark_mode", false);


        mToolbar = findViewById(R.id.detail_toolbar);
        mTitle = findViewById(R.id.detail_textview_title);
        mTitleContainer = findViewById(R.id.detail_appbar_content_inner_container);
        mAppBarLayout = findViewById(R.id.detail_app_bar);
        mTabLayout = findViewById(R.id.detail_tab_layout);
        mViewPager = findViewById(R.id.detail_view_pager);

        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(isDarkMode ? R.drawable.ic_arrow_back_white : R.drawable.ic_arrow_back_black);
        //TODO Change back button icon based on the theme

        mAppBarLayout.addOnOffsetChangedListener(this);
        startAlphaAnimation(mTitle, 0, View.INVISIBLE);

        mAdapter = new ViewPagerAdapter(getSupportFragmentManager());
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

}
