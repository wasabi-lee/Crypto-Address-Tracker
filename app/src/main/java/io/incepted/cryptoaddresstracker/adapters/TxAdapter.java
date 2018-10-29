package io.incepted.cryptoaddresstracker.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import io.incepted.cryptoaddresstracker.R;
import io.incepted.cryptoaddresstracker.databinding.TxListItemBinding;
import io.incepted.cryptoaddresstracker.network.deserializer.SimpleTxItem;
import io.incepted.cryptoaddresstracker.network.networkModel.transactionListInfo.EthOperation;
import io.incepted.cryptoaddresstracker.network.networkModel.transactionListInfo.OperationWrapper;
import io.incepted.cryptoaddresstracker.network.networkModel.transactionListInfo.TokenOperation;
import io.incepted.cryptoaddresstracker.viewModels.TxViewModel;

public class TxAdapter extends PagedListAdapter<SimpleTxItem, TxAdapter.ViewHolder> {

    private TxViewModel mViewModel;
//    private List<OperationWrapper> mOperations;

    public TxAdapter(@NonNull DiffUtil.ItemCallback<SimpleTxItem> diffCallback, TxViewModel mViewModel) {
        super(diffCallback);
        this.mViewModel = mViewModel;
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

        boolean fetchEthTx = currentItem.isEthTx();
        itemBinding.setFetchEthTx(fetchEthTx);
        if (fetchEthTx) {
            itemBinding.setItem(currentItem);
        }
//        else {
//            itemBinding.setTokenOperation((TokenOperation) currentItem);
//        }

        itemBinding.setViewmodel(mViewModel);
        itemBinding.executePendingBindings();
    }

//    @Override
//    public int getItemCount() {
//        return mOperations != null ? mOperations.size() : 0;
//    }
//
//    private void setList(List<OperationWrapper> txInfoList) {
//        this.mOperations = txInfoList;
//        notifyDataSetChanged();
//    }
//
//    public void replaceData(List<OperationWrapper> txInfoList) {
//        setList(txInfoList);
//    }

}
