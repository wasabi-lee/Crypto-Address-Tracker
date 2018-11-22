package io.incepted.cryptoaddresstracker.fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Objects;

import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.incepted.cryptoaddresstracker.R;
import io.incepted.cryptoaddresstracker.activities.DetailActivity;
import io.incepted.cryptoaddresstracker.adapters.TxAdapter;
import io.incepted.cryptoaddresstracker.listeners.TxItemActionListener;
import io.incepted.cryptoaddresstracker.network.deserializer.SimpleTxItem;
import io.incepted.cryptoaddresstracker.utils.ViewModelFactory;
import io.incepted.cryptoaddresstracker.viewModels.DetailViewModel;
import io.incepted.cryptoaddresstracker.viewModels.TxListTokenViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class TxListTokenFragment extends Fragment {
    private DetailViewModel mSharedViewModel;
    private TxListTokenViewModel mViewModel;
    private TxAdapter mAdapter;

    @BindView(R.id.tx_token_recycler_view)
    RecyclerView mTokenTxListView;
    @BindView(R.id.tx_token_no_tx_found)
    TextView mNoTxFound;

    public TxListTokenFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tx_list_token, container, false);
        ButterKnife.bind(this, view);


        mSharedViewModel = DetailActivity.obtainViewModel(getActivity());
        mViewModel = obtainViewModel(this);

        setRetainInstance(false);

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();
        setupObservers();
    }


    private TxListTokenViewModel obtainViewModel(Fragment fragment) {
        ViewModelFactory factory = ViewModelFactory.getInstance(fragment.getActivity().getApplication());
        return ViewModelProviders.of(fragment, factory).get(TxListTokenViewModel.class);
    }


    private void setupRecyclerView() {
        LinearLayoutManager lm = new LinearLayoutManager(getContext());
        mTokenTxListView.setHasFixedSize(true);
        mTokenTxListView.setNestedScrollingEnabled(true);
        mTokenTxListView.setItemAnimator(new DefaultItemAnimator());
        mTokenTxListView.setLayoutManager(lm);

        TxItemActionListener listener = transactionHash -> mSharedViewModel.toTxDetailActivity(transactionHash);

        mAdapter = new TxAdapter(SimpleTxItem.DIFF_CALLBACK, listener);
        mTokenTxListView.setAdapter(mAdapter);
    }


    private void setupObservers() {

        mSharedViewModel.getAddressSLE().observe(this, address -> {
            mViewModel.getAddressValue().setValue(address.getAddrValue());
        });

        mViewModel.getTokenTxList().observe(this, simpleTxItems -> {
                    try {
                        String addrValue = Objects.requireNonNull(mViewModel.getAddressValueStr());
                        mAdapter.submitList(simpleTxItems, addrValue);
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                        mViewModel.getSnackbarTextRes().setValue(R.string.unexpected_error);
                    }
                }
        );

        mViewModel.getTokenTxExists().observe(this, txExists -> mNoTxFound.setVisibility(txExists ? View.GONE : View.VISIBLE));

        mViewModel.getTokenNetworkError().observe(this, error ->
                mSharedViewModel.getSnackbarText().setValue(error));
    }

}
