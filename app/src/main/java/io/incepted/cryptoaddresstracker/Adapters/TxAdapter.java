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

import io.incepted.cryptoaddresstracker.Listeners.TxItemActionListener;
import io.incepted.cryptoaddresstracker.Network.NetworkModel.TransactionInfo.Operation;
import io.incepted.cryptoaddresstracker.R;
import io.incepted.cryptoaddresstracker.ViewModels.TxViewModel;
import io.incepted.cryptoaddresstracker.databinding.TxListItemBinding;

public class TxAdapter extends RecyclerView.Adapter<TxAdapter.ViewHolder> {

    private static final String TAG = TxAdapter.class.getSimpleName();

    private TxViewModel mViewModel;
    private List<Operation> mTxOperations;
    private TxItemActionListener mListener;

    private String mTxHoldingAddress;

    public TxAdapter(TxViewModel viewModel) {
        this.mViewModel = viewModel;
        this.mListener = transactionHash -> mViewModel.toTxDetailActivity(transactionHash);
        this.mTxOperations = new ArrayList<>();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TxListItemBinding txListItemBinding;

        ViewHolder(TxListItemBinding binding) {
            super(binding.getRoot());
            txListItemBinding = binding;
        }

        public TxListItemBinding getItemBinding() {
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
        Operation currentItem = mTxOperations.get(position);
        TxListItemBinding itemBinding = holder.getItemBinding();
        itemBinding.setOperation(currentItem);
        itemBinding.setListener(mListener);
        itemBinding.setTxHolderAddress(mTxHoldingAddress);
        itemBinding.executePendingBindings();
        Log.d(TAG, "onBindViewHolder: " + currentItem.getValue() + "\n " + currentItem.getTokenInfo().getAddress());
    }

    @Override
    public int getItemCount() {
        return mTxOperations != null ? mTxOperations.size() : 0;
    }

    private void setList(List<Operation> txInfoList) {
        this.mTxOperations = txInfoList;
        notifyDataSetChanged();
    }

    public void setTxHoldingAddress(String txHoldingAddress) {
        this.mTxHoldingAddress = txHoldingAddress;
    }

    public void replaceData(List<Operation> txInfoList) {
        setList(txInfoList);
    }

}
