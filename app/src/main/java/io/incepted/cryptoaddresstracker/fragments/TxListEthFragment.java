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

import androidx.lifecycle.Observer;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.incepted.cryptoaddresstracker.R;
import io.incepted.cryptoaddresstracker.activities.DetailActivity;
import io.incepted.cryptoaddresstracker.adapters.TxAdapter;
import io.incepted.cryptoaddresstracker.databinding.FragmentTxListEthBinding;
import io.incepted.cryptoaddresstracker.listeners.TxItemActionListener;
import io.incepted.cryptoaddresstracker.network.deserializer.SimpleTxItem;
import io.incepted.cryptoaddresstracker.viewModels.DetailViewModel;
import timber.log.Timber;

public class TxListEthFragment extends Fragment {

    @BindView(R.id.tx_eth_recycler_view)
    RecyclerView mEthTxListView;
    @BindView(R.id.tx_eth_no_tx_found)
    TextView mNoTxFound;

    private DetailViewModel mViewModel;

    private FragmentTxListEthBinding mBinding;
    private TxAdapter mAdapter;

    public TxListEthFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tx_list_eth, container, false);
        ButterKnife.bind(this, view);

        if (mBinding == null) {
            mBinding = FragmentTxListEthBinding.bind(view);
        }

        mViewModel = DetailActivity.obtainViewModel(getActivity());
        mBinding.setViewmodel(mViewModel);

        setRetainInstance(false);

        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();
        setupObservers();
    }


    private void setupRecyclerView() {
        LinearLayoutManager lm = new LinearLayoutManager(getContext());
        mEthTxListView.setHasFixedSize(true);
        mEthTxListView.setNestedScrollingEnabled(true);
        mEthTxListView.setItemAnimator(new DefaultItemAnimator());
        mEthTxListView.setLayoutManager(lm);

        TxItemActionListener listener = transactionHash -> mViewModel.toTxDetailActivity(transactionHash);

        mAdapter = new TxAdapter(SimpleTxItem.DIFF_CALLBACK, listener);
        mEthTxListView.setAdapter(mAdapter);
    }


    private void setupObservers() {
        mViewModel.getEthTxList().observe(this, simpleTxItems -> {
                    String addrValue = Objects.requireNonNull(mViewModel.mAddress.get().getAddrValue());
                    mAdapter.submitList(simpleTxItems, addrValue);
                }
        );
        mViewModel.getEthTxExists().observe(this, txExists -> mNoTxFound.setVisibility(txExists ? View.GONE : View.VISIBLE));

    }

}
