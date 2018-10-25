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
import io.incepted.cryptoaddresstracker.databinding.TokenListItemBinding;
import io.incepted.cryptoaddresstracker.listeners.TokenItemActionListener;
import io.incepted.cryptoaddresstracker.network.networkModel.remoteAddressInfo.Token;
import io.incepted.cryptoaddresstracker.viewModels.DetailViewModel;

public class TokenAdapter extends RecyclerView.Adapter<TokenAdapter.ViewHolder> {

    private static final String TAG = TokenAdapter.class.getSimpleName();

    private DetailViewModel mViewModel;
    private List<Token> mTokens;
    private TokenItemActionListener mListener;

    public TokenAdapter(DetailViewModel viewModel) {
        this.mViewModel = viewModel;
        this.mListener = (tokenName, tokenAddress) -> mViewModel.toTxActivity(tokenName, tokenAddress);
        this.mTokens = new ArrayList<>();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {

        private TokenListItemBinding tokenListItemBinding;

        ViewHolder(TokenListItemBinding binding) {
            super(binding.getRoot());
            tokenListItemBinding = binding;
        }

        public TokenListItemBinding getItemBinding() {
            return tokenListItemBinding;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ViewDataBinding binding = DataBindingUtil.inflate(inflater, R.layout.token_list_item, parent, false);
        return new ViewHolder((TokenListItemBinding) binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Token currentItem = mTokens.get(position);
        TokenListItemBinding itemBinding = holder.getItemBinding();
        itemBinding.setToken(currentItem);
        itemBinding.setListener(mListener);
        itemBinding.executePendingBindings();
        Log.d(TAG, "onBindViewHolder: " + currentItem.getTokenInfo().getName());
    }

    @Override
    public int getItemCount() {
        return mTokens != null ? mTokens.size() : 0;
    }

    private void setList(List<Token> tokens) {
        this.mTokens = tokens;
        notifyDataSetChanged();
    }

    public void replaceData(List<Token> tokens) {
        setList(tokens);
    }
}
