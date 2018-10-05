package io.incepted.cryptoaddresstracker.fragments;


import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.incepted.cryptoaddresstracker.activities.DetailActivity;
import io.incepted.cryptoaddresstracker.R;
import io.incepted.cryptoaddresstracker.viewModels.DetailViewModel;
import io.incepted.cryptoaddresstracker.databinding.FragmentOverviewBinding;


/**
 * A simple {@link Fragment} subclass.
 */
public class OverviewFragment extends Fragment {

    private static final String TAG = OverviewFragment.class.getSimpleName();

    private DetailViewModel mViewModel;
    private FragmentOverviewBinding mBinding;

    public OverviewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_overview, container, false);

        if (mBinding == null) {
            mBinding = FragmentOverviewBinding.bind(view);
        }

        mViewModel = DetailActivity.obtainViewModel(getActivity());
        mBinding.setViewmodel(mViewModel);

        setRetainInstance(false);

        return mBinding.getRoot();
    }



}