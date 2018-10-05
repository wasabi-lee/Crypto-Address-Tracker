package io.incepted.cryptoaddresstracker.activities;

import androidx.lifecycle.ViewModelProviders;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.incepted.cryptoaddresstracker.adapters.TokenTransferAdapter;
import io.incepted.cryptoaddresstracker.R;
import io.incepted.cryptoaddresstracker.utils.SnackbarUtils;
import io.incepted.cryptoaddresstracker.utils.ViewModelFactory;
import io.incepted.cryptoaddresstracker.viewModels.TokenTransferViewModel;
import io.incepted.cryptoaddresstracker.databinding.ActivityTokenTransferBinding;

public class TokenTransferActivity extends BaseActivity {

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