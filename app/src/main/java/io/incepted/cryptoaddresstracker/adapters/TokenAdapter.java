package io.incepted.cryptoaddresstracker.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;
import io.incepted.cryptoaddresstracker.R;
import io.incepted.cryptoaddresstracker.databinding.HoldingsListItemBinding;
import io.incepted.cryptoaddresstracker.databinding.OverviewCardBinding;
import io.incepted.cryptoaddresstracker.databinding.TokenListItemBinding;
import io.incepted.cryptoaddresstracker.listeners.TokenItemActionListener;
import io.incepted.cryptoaddresstracker.network.networkModel.remoteAddressInfo.Token;
import io.incepted.cryptoaddresstracker.viewModels.DetailViewModel;

public class TokenAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private DetailViewModel mViewModel;
    private List<Token> mTokens;
    private TokenItemActionListener mListener;

    private static final int TOKEN_HEADER = 0;
    private static final int TOKEN_ITEM = 1;

    public TokenAdapter(DetailViewModel viewModel) {
        this.mViewModel = viewModel;
        this.mListener = (tokenName, tokenAddress) -> mViewModel.toTxActivity(tokenName, tokenAddress);
        this.mTokens = new ArrayList<>();
    }


    static class ItemViewHolder extends RecyclerView.ViewHolder {

        private HoldingsListItemBinding tokenListItemBinding;

        ItemViewHolder(HoldingsListItemBinding binding) {
            super(binding.getRoot());
            tokenListItemBinding = binding;
        }

        public HoldingsListItemBinding getItemBinding() {
            return tokenListItemBinding;
        }
    }


    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        private OverviewCardBinding overviewCardBinding;

        HeaderViewHolder(OverviewCardBinding binding) {
            super(binding.getRoot());
            overviewCardBinding = binding;
        }

        public OverviewCardBinding getOverviewCardBinding() {
            return overviewCardBinding;
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == TOKEN_HEADER) {
            ViewDataBinding binding = DataBindingUtil.inflate(inflater, R.layout.overview_card, parent, false);
            return new HeaderViewHolder((OverviewCardBinding) binding);
        } else {
            ViewDataBinding binding = DataBindingUtil.inflate(inflater, R.layout.holdings_list_item, parent, false);
            return new ItemViewHolder((HoldingsListItemBinding) binding);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            OverviewCardBinding headerBinding = ((HeaderViewHolder) holder).getOverviewCardBinding();
            headerBinding.setViewmodel(mViewModel);
            headerBinding.executePendingBindings();
        } else {
            Token currentItem = mTokens.get(position);
            HoldingsListItemBinding itemBinding = ((ItemViewHolder) holder).getItemBinding();
            itemBinding.setToken(currentItem);
            itemBinding.setListener(mListener);
            itemBinding.executePendingBindings();
        }
    }

    @Override
    public int getItemCount() {
        return mTokens != null ? mTokens.size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        return TOKEN_ITEM;
    }

    private void setList(List<Token> tokens) {
        this.mTokens = tokens;
        notifyDataSetChanged();
    }

    public void replaceData(List<Token> tokens) {
        setList(tokens);
    }
}
