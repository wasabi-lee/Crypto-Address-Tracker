package io.incepted.cryptoaddresstracker.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.ButterKnife;
import io.incepted.cryptoaddresstracker.R;
import io.incepted.cryptoaddresstracker.activities.DetailActivity;
import io.incepted.cryptoaddresstracker.databinding.FragmentTransactionBinding;
import io.incepted.cryptoaddresstracker.viewModels.DetailViewModel;


/**
 * A simple {@link Fragment} subclass.
 */
public class TransactionFragment extends Fragment {

    private static final String TAG = TransactionFragment.class.getSimpleName();

    private DetailViewModel mViewModel;
    private FragmentTransactionBinding mBinding;

    public TransactionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_transaction, container, false);

        ButterKnife.bind(this, view);

        if (mBinding == null) {
            mBinding = FragmentTransactionBinding.bind(view);
        }

        mViewModel = DetailActivity.obtainViewModel(getActivity());
        mBinding.setViewmodel(mViewModel);

        setRetainInstance(false);

        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

}

