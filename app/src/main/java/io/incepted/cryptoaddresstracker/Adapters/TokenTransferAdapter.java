package io.incepted.cryptoaddresstracker.Adapters;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import io.incepted.cryptoaddresstracker.Listeners.CopyListener;
import io.incepted.cryptoaddresstracker.Network.NetworkModel.TransactionInfo.Operation;
import io.incepted.cryptoaddresstracker.Network.NetworkModel.TransactionInfo.TransactionInfo;
import io.incepted.cryptoaddresstracker.R;
import io.incepted.cryptoaddresstracker.Utils.CopyUtils;
import io.incepted.cryptoaddresstracker.ViewModels.TokenTransferViewModel;
import io.incepted.cryptoaddresstracker.databinding.TokenTransferListHeaderBinding;
import io.incepted.cryptoaddresstracker.databinding.TokenTransferListItemBinding;

public class TokenTransferAdapter extends RecyclerView.Adapter<TokenTransferAdapter.ViewHolder> {

    private static final String TAG = TokenTransferAdapter.class.getSimpleName();

    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_ITEM = 1;

    private TokenTransferViewModel mViewModel;
    private TransactionInfo mTxInfo;
    private List<Operation> mOperations;
    private CopyListener mListener;

    public TokenTransferAdapter(TokenTransferViewModel viewModel) {
        this.mViewModel = viewModel;
        this.mTxInfo = new TransactionInfo();
        this.mOperations = new ArrayList<>();
        this.mListener = value -> CopyUtils.copyText(mViewModel.getApplication().getApplicationContext(), value);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TokenTransferListHeaderBinding tokenTransferListHeaderBinding;
        private TokenTransferListItemBinding tokenTransferListItemBinding;

        ViewHolder(TokenTransferListHeaderBinding headerBinding) {
            super(headerBinding.getRoot());
            tokenTransferListHeaderBinding = headerBinding;
        }

        ViewHolder(TokenTransferListItemBinding itemBinding) {
            super(itemBinding.getRoot());
            tokenTransferListItemBinding = itemBinding;
        }

        public TokenTransferListHeaderBinding getHeaderBinding() {
            return tokenTransferListHeaderBinding;
        }

        public TokenTransferListItemBinding getItemBinding() {
            return tokenTransferListItemBinding;
        }
    }

    private void setList(List<Operation> operations) {
        this.mOperations.clear();
        this.mOperations.add(new Operation()); // dummy data for header
        this.mOperations.addAll(operations);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? VIEW_TYPE_HEADER : VIEW_TYPE_ITEM;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewDataBinding binding;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case VIEW_TYPE_HEADER:
                binding = DataBindingUtil.inflate(inflater, R.layout.token_transfer_list_header, parent, false);
                return new ViewHolder((TokenTransferListHeaderBinding) binding);
            case VIEW_TYPE_ITEM:
                binding = DataBindingUtil.inflate(inflater, R.layout.token_transfer_list_item, parent, false);
                return new ViewHolder((TokenTransferListItemBinding) binding);
        }
        binding = DataBindingUtil.inflate(inflater, R.layout.token_transfer_list_item, parent, false);
        return new ViewHolder((TokenTransferListItemBinding) binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Operation currentItem = mOperations.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_HEADER:
                TokenTransferListHeaderBinding headerBinding = holder.getHeaderBinding();
                headerBinding.setTxInfo(mTxInfo);
                headerBinding.setListener(mListener);
                headerBinding.executePendingBindings();
                break;
            case VIEW_TYPE_ITEM:
                TokenTransferListItemBinding itemBinding = holder.getItemBinding();
                itemBinding.setOperation(currentItem);
                itemBinding.setListener(mListener);
                itemBinding.executePendingBindings();
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mOperations != null ? mOperations.size() : 0;
    }

    public void replaceData(TransactionInfo txInfo) {
        this.mTxInfo = txInfo;
        if (txInfo != null) {
            setList(txInfo.getOperations());
        }
    }

}
