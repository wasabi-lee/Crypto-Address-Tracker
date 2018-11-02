package io.incepted.cryptoaddresstracker.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import io.incepted.cryptoaddresstracker.R;
import io.incepted.cryptoaddresstracker.databinding.TxListItemBinding;
import io.incepted.cryptoaddresstracker.listeners.TxItemActionListener;
import io.incepted.cryptoaddresstracker.network.deserializer.SimpleTxItem;
import io.incepted.cryptoaddresstracker.viewModels.DetailViewModel;
import io.incepted.cryptoaddresstracker.viewModels.TxViewModel;

public class TxAdapter extends PagedListAdapter<SimpleTxItem, TxAdapter.ViewHolder> {

    private TxViewModel mTxViewModel;
    private DetailViewModel mDetailViewModel;
    private TxItemActionListener mListener;

    public TxAdapter(@NonNull DiffUtil.ItemCallback<SimpleTxItem> diffCallback,
                     TxViewModel txViewModel,
                     DetailViewModel detailViewModel) {
        super(diffCallback);
        this.mTxViewModel = txViewModel;
        this.mDetailViewModel = detailViewModel;
        this.mListener = transactionHash -> {
            if (mDetailViewModel != null) {
                mDetailViewModel.toTxDetailActivity(transactionHash);
            } else if (mTxViewModel != null) {
                mTxViewModel.toTxDetailActivity(transactionHash);
            }
        };
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TxListItemBinding txListItemBinding;

        ViewHolder(TxListItemBinding binding) {
            super(binding.getRoot());
            txListItemBinding = binding;
        }

        TxListItemBinding getItemBinding() {
            return txListItemBinding;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ViewDataBinding binding = DataBindingUtil.inflate(inflater, R.layout.tx_list_item, parent, false);
        return new ViewHolder((TxListItemBinding) binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TxListItemBinding itemBinding = holder.getItemBinding();
        SimpleTxItem currentItem = getItem(position);
        itemBinding.setItem(currentItem);
        itemBinding.setListener(mListener);
        itemBinding.setAddress(mDetailViewModel != null ? mDetailViewModel.mAddress.get().getAddrValue() : mTxViewModel.mAddress.get().getAddrValue());
        itemBinding.executePendingBindings();
    }


}
