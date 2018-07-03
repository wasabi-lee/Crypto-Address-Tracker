package io.incepted.cryptoaddresstracker.Activities;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.LinearLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.incepted.cryptoaddresstracker.Adapters.TokenTransferAdapter;
import io.incepted.cryptoaddresstracker.R;
import io.incepted.cryptoaddresstracker.Utils.SnackbarUtils;
import io.incepted.cryptoaddresstracker.Utils.ViewModelFactory;
import io.incepted.cryptoaddresstracker.ViewModels.TokenTransferViewModel;
import io.incepted.cryptoaddresstracker.databinding.ActivityTokenTransferBinding;

public class TokenTransferActivity extends AppCompatActivity {

    private static final String TAG = TokenTransferActivity.class.getSimpleName();

    public static final String TOKEN_TRANSFER_TX_HASH_EXTRA_KEY = "token_transfer_tx_hash_extra_key";

    @BindView(R.id.token_transfer_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.token_transfer_recycler_view)
    RecyclerView mTokenTransferList;

    private String mTxHash;

    private TokenTransferViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityTokenTransferBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_token_transfer);
        mViewModel = obtainViewModel(this);
        binding.setViewmodel(mViewModel);

        ButterKnife.bind(this);

        mTxHash = unpackExtra();

        initToolbar();
        setupSnackbar();
        setupRecyclerView();

    }

    private String unpackExtra() {
        return getIntent().getStringExtra(TOKEN_TRANSFER_TX_HASH_EXTRA_KEY);
    }

    private TokenTransferViewModel obtainViewModel(FragmentActivity activity) {
        ViewModelFactory factory = ViewModelFactory.getInstance(activity.getApplication());
        return ViewModelProviders.of(activity, factory).get(TokenTransferViewModel.class);
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

    private void setupRecyclerView() {
        LinearLayoutManager lm = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(mTokenTransferList.getContext(), lm.getOrientation());

        mTokenTransferList.setHasFixedSize(true);
        mTokenTransferList.setNestedScrollingEnabled(true);
        mTokenTransferList.setLayoutManager(lm);
        mTokenTransferList.addItemDecoration(dividerItemDecoration);
        mTokenTransferList.setItemAnimator(new DefaultItemAnimator());

        TokenTransferAdapter mAdapter = new TokenTransferAdapter(mViewModel);
        mTokenTransferList.setAdapter(mAdapter);
    }


    @Override
    protected void onResume() {
        super.onResume();
        mViewModel.start(mTxHash);
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


    private void showSnackbar(String s) {
        SnackbarUtils.showSnackbar(findViewById(android.R.id.content), s);
    }
}
