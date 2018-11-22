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

import androidx.fragment.app.FragmentActivity;
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
import io.incepted.cryptoaddresstracker.viewModels.TxListEthViewModel;

public class TxListEthFragment extends Fragment {

    @BindView(R.id.tx_eth_recycler_view)
    RecyclerView mEthTxListView;
    @BindView(R.id.tx_eth_no_tx_found)
    TextView mNoTxFound;

    private DetailViewModel mSharedViewModel;
    private TxListEthViewModel mViewModel;

    private TxAdapter mAdapter;

    public TxListEthFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tx_list_eth, container, false);
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


    private TxListEthViewModel obtainViewModel(Fragment fragment) {
        ViewModelFactory factory = ViewModelFactory.getInstance(fragment.getActivity().getApplication());
        return ViewModelProviders.of(fragment, factory).get(TxListEthViewModel.class);
    }


    private void setupRecyclerView() {
        LinearLayoutManager lm = new LinearLayoutManager(getContext());
        mEthTxListView.setHasFixedSize(true);
        mEthTxListView.setNestedScrollingEnabled(true);
        mEthTxListView.setItemAnimator(new DefaultItemAnimator());
        mEthTxListView.setLayoutManager(lm);

        TxItemActionListener listener = transactionHash -> mSharedViewModel.toTxDetailActivity(transactionHash);

        mAdapter = new TxAdapter(SimpleTxItem.DIFF_CALLBACK, listener);
        mEthTxListView.setAdapter(mAdapter);
    }


    private void setupObservers() {
        mSharedViewModel.getEthTxList().observe(this, simpleTxItems -> {
                    String addrValue = Objects.requireNonNull(mSharedViewModel.mAddress.get().getAddrValue());
                    mAdapter.submitList(simpleTxItems, addrValue);
                }
        );
        mSharedViewModel.getEthTxExists().observe(this, txExists -> mNoTxFound.setVisibility(txExists ? View.GONE : View.VISIBLE));

    }

}
