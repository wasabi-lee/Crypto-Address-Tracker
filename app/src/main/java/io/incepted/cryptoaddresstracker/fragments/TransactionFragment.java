package io.incepted.cryptoaddresstracker.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import butterknife.BindView;
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

    private static final String ETH_FRAG_TAG = "eth_frag_tag";
    private static final String TOKEN_FRAG_TAG = "token_frag_tag";

    private DetailViewModel mViewModel;
    private FragmentTransactionBinding mBinding;

    @BindView(R.id.tx_frag_type_toggle_switch)
    Switch mSwitch;
    @BindView(R.id.tx_frag_switch_description)
    TextView mSwitchDescription;

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

        mSwitch.setOnCheckedChangeListener((buttonView, showEthTx) -> swapFragment(showEthTx));

        return mBinding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TxListTokenFragment tokenFragment = new TxListTokenFragment();
        TxListEthFragment ethFragment = new TxListEthFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.tx_frag_child_frag_container, ethFragment, ETH_FRAG_TAG);
        transaction.add(R.id.tx_frag_child_frag_container, tokenFragment, TOKEN_FRAG_TAG);
        transaction.hide(tokenFragment);
        transaction.show(ethFragment);
        transaction.commit();
        mSwitchDescription.setText(getText(R.string.latest_eth_transactions));

        setupObservers();

    }


    private void setupObservers() {
        mViewModel.getIsTokenAddress().observe(this, isTokenAddress -> {
            mSwitch.setChecked(false);
            mSwitch.setEnabled(false);
        });
    }


    private void swapFragment(boolean showEthTx) {
        FragmentManager fragManager = getChildFragmentManager();

        if (showEthTx) {
            mSwitchDescription.setText(getText(R.string.latest_eth_transactions));
            swapFragment(fragManager,
                    fragManager.findFragmentByTag(TOKEN_FRAG_TAG),
                    fragManager.findFragmentByTag(ETH_FRAG_TAG));
        } else {
            mSwitchDescription.setText(getText(R.string.latest_token_transactions));
            swapFragment(fragManager,
                    fragManager.findFragmentByTag(ETH_FRAG_TAG),
                    fragManager.findFragmentByTag(TOKEN_FRAG_TAG));
        }
    }


    private void swapFragment(FragmentManager fragManager, Fragment oldFrag, Fragment newFrag) {
        FragmentTransaction transaction = fragManager.beginTransaction();
        if (oldFrag != null)
            transaction.hide(oldFrag);
        if (newFrag != null && newFrag.isAdded()) {
            transaction.show(newFrag);
        } else {
            if (newFrag == null)
                newFrag = new TxListEthFragment();
            transaction.add(R.id.tx_frag_child_frag_container, newFrag, ETH_FRAG_TAG);
        }
        transaction.commit();
    }

}

