package io.incepted.cryptoaddresstracker.fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import io.incepted.cryptoaddresstracker.adapters.TokenAdapter;
import io.incepted.cryptoaddresstracker.adapters.TxAdapter;
import io.incepted.cryptoaddresstracker.databinding.FragmentTxListTokenBinding;
import io.incepted.cryptoaddresstracker.network.deserializer.SimpleTxItem;
import io.incepted.cryptoaddresstracker.viewModels.DetailViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class TxListTokenFragment extends Fragment {
    private FragmentTxListTokenBinding mBinding;
    private DetailViewModel mViewModel;
    private TxAdapter mAdapter;

    @BindView(R.id.tx_token_recycler_view)
    RecyclerView mTokenTxListView;

    public TxListTokenFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tx_list_token, container, false);
        ButterKnife.bind(this, view);

        if (mBinding == null) {
            mBinding = FragmentTxListTokenBinding.bind(view);
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
        mTokenTxListView.setHasFixedSize(true);
        mTokenTxListView.setNestedScrollingEnabled(true);
        mTokenTxListView.setItemAnimator(new DefaultItemAnimator());
        mTokenTxListView.setLayoutManager(lm);

        mAdapter = new TxAdapter(SimpleTxItem.DIFF_CALLBACK, mViewModel);
        mTokenTxListView.setAdapter(mAdapter);
    }

    private void setupObservers() {
        mViewModel.getTokenTxList().observe(this, simpleTxItems -> {
                    if (simpleTxItems != null)
                        mAdapter.submitList(simpleTxItems);
                }
        );
    }

}
