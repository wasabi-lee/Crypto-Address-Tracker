package io.incepted.cryptoaddresstracker.fragments;


import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.incepted.cryptoaddresstracker.activities.DetailActivity;
import io.incepted.cryptoaddresstracker.adapters.TokenAdapter;
import io.incepted.cryptoaddresstracker.R;
import io.incepted.cryptoaddresstracker.viewModels.DetailViewModel;
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
        mTokenList.setLayoutManager(new LinearLayoutManager(getContext()));

        TokenAdapter adapter = new TokenAdapter(mViewModel);
        mTokenList.setAdapter(adapter);
    }
}

