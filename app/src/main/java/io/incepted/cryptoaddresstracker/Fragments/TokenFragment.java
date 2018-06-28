package io.incepted.cryptoaddresstracker.Fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.incepted.cryptoaddresstracker.Activities.DetailActivity;
import io.incepted.cryptoaddresstracker.Adapters.TokenAdapter;
import io.incepted.cryptoaddresstracker.R;
import io.incepted.cryptoaddresstracker.ViewModels.DetailViewModel;
import io.incepted.cryptoaddresstracker.databinding.FragmentTokenBinding;


/**
 * A simple {@link Fragment} subclass.
 */
public class TokenFragment extends Fragment {

    private static final String TAG = TokenFragment.class.getSimpleName();

    @BindView(R.id.token_frag_recycler_view)
    RecyclerView mTokenList;

    private DetailViewModel mViewModel;
    private FragmentTokenBinding mBinding;

    public TokenFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_token, container, false);

        ButterKnife.bind(this, view);

        if (mBinding == null) {
            mBinding = FragmentTokenBinding.bind(view);
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
    }

    private void setupRecyclerView() {
        mTokenList.setHasFixedSize(true);
        mTokenList.setNestedScrollingEnabled(true);
        mTokenList.setItemAnimator(new DefaultItemAnimator());

        LinearLayoutManager lm = new LinearLayoutManager(getContext());

        mTokenList.setLayoutManager(lm);

        TokenAdapter adapter = new TokenAdapter(mViewModel);
        mTokenList.setAdapter(adapter);
    }
}

