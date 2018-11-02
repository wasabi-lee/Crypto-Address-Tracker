package io.incepted.cryptoaddresstracker.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.incepted.cryptoaddresstracker.R;
import io.incepted.cryptoaddresstracker.activities.DetailActivity;
import io.incepted.cryptoaddresstracker.adapters.TokenAdapter;
import io.incepted.cryptoaddresstracker.databinding.FragmentOverviewBinding;
import io.incepted.cryptoaddresstracker.viewModels.DetailViewModel;


/**
 * A simple {@link Fragment} subclass.
 */
public class OverviewFragment extends Fragment {

    private static final String TAG = OverviewFragment.class.getSimpleName();

    private DetailViewModel mViewModel;
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
        LinearLayoutManager lm = new LinearLayoutManager(getContext());
        mTokenListView.setHasFixedSize(true);
        mTokenListView.setNestedScrollingEnabled(true);
        mTokenListView.setItemAnimator(new DefaultItemAnimator());
        mTokenListView.setLayoutManager(lm);
        mTokenListView.addItemDecoration(new DividerItemDecoration(getContext(), lm.getOrientation()));

        TokenAdapter adapter = new TokenAdapter(mViewModel);
        mTokenListView.setAdapter(adapter);
    }


}
