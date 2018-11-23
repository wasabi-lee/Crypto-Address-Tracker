package io.incepted.cryptoaddresstracker.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.incepted.cryptoaddresstracker.R;
import io.incepted.cryptoaddresstracker.activities.DetailActivity;
import io.incepted.cryptoaddresstracker.adapters.TokenAdapter;
import io.incepted.cryptoaddresstracker.data.model.Address;
import io.incepted.cryptoaddresstracker.databinding.FragmentOverviewBinding;
import io.incepted.cryptoaddresstracker.utils.ViewModelFactory;
import io.incepted.cryptoaddresstracker.viewModels.DetailViewModel;
import io.incepted.cryptoaddresstracker.viewModels.OverviewViewModel;


/**
 * A simple {@link Fragment} subclass.
 */
public class OverviewFragment extends Fragment {

    private DetailViewModel mSharedViewModel;
    private OverviewViewModel mViewModel;
    private FragmentOverviewBinding mBinding;

    @BindView(R.id.overview_frag_token_recycler_view)
    RecyclerView mTokenListView;

    public OverviewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_overview, container, false);
        ButterKnife.bind(this, view);

        if (mBinding == null) {
            mBinding = FragmentOverviewBinding.bind(view);
        }

        mSharedViewModel = DetailActivity.obtainViewModel(getActivity());
        mViewModel = obtainViewModel(this);
        mBinding.setViewmodel(mViewModel);

        setRetainInstance(false);

        return mBinding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupObservers();
        setupRecyclerView();
    }


    private OverviewViewModel obtainViewModel(Fragment fragment) {
        ViewModelFactory factory = ViewModelFactory.getInstance(fragment.getActivity().getApplication());
        return ViewModelProviders.of(fragment, factory).get(OverviewViewModel.class);
    }


    private void setupObservers() {
        mSharedViewModel.getAddressSLE().observe(this, address ->
                mViewModel.loadDataFromNetwork(address));

        mViewModel.getIsTokenAddress().observe(this, isTokenAddress ->
                mSharedViewModel.getIsTokenAddress().setValue(isTokenAddress));

        mViewModel.getIsPriceLoading().observe(this, isLoading ->
                mSharedViewModel.getIsPriceLoading().setValue(isLoading));

        mViewModel.getIsAddressInfoLoading().observe(this, isLoading ->
                mSharedViewModel.getIsAddressInfoLoading().setValue(isLoading));

        mViewModel.getSnackbarTextRes().observe(this, textId ->
                mSharedViewModel.getSnackbarTextResource().setValue(textId));

        mViewModel.getSnackbarText().observe(this, text ->
                mSharedViewModel.getSnackbarText().setValue(text));
    }


    private void setupRecyclerView() {
        LinearLayoutManager lm = new LinearLayoutManager(getContext());
        mTokenListView.setHasFixedSize(true);
        mTokenListView.setNestedScrollingEnabled(true);
        mTokenListView.setItemAnimator(new DefaultItemAnimator());
        mTokenListView.setLayoutManager(lm);
        mTokenListView.addItemDecoration(new DividerItemDecoration(getContext(), lm.getOrientation()));

        TokenAdapter adapter = new TokenAdapter(mSharedViewModel);
        mTokenListView.setAdapter(adapter);
    }


}
