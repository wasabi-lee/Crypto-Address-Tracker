package io.incepted.cryptoaddresstracker.Adapters;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import io.incepted.cryptoaddresstracker.Listeners.TxItemActionListener;
import io.incepted.cryptoaddresstracker.Network.NetworkModel.TransactionListInfo.EthOperation;
import io.incepted.cryptoaddresstracker.Network.NetworkModel.TransactionListInfo.OperationWrapper;
import io.incepted.cryptoaddresstracker.Network.NetworkModel.TransactionListInfo.TokenOperation;
import io.incepted.cryptoaddresstracker.R;
import io.incepted.cryptoaddresstracker.ViewModels.TxViewModel;
import io.incepted.cryptoaddresstracker.databinding.TxListItemBinding;

public class TxAdapter extends RecyclerView.Adapter<TxAdapter.ViewHolder> {

    private static final String TAG = TxAdapter.class.getSimpleName();

    private TxViewModel mViewModel;
    List<OperationWrapper> mOperations;
    private TxItemActionListener mListener;

    private String mTxHoldingAddress;

    public TxAdapter(TxViewModel viewModel) {
        this.mViewModel = viewModel;
        this.mListener = transactionHash -> mViewModel.toTxDetailActivity(transactionHash);
        this.mOperations = new ArrayList<>();
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
        TxListItemBinding itemBinding = holder.getItemBinding();
        OperationWrapper currentItem = mOperations.get(position);

        boolean fetchEthTx = currentItem.getTxType() == OperationWrapper.TX_TYPE_ETHEREUM;
        itemBinding.setFetchEthTx(fetchEthTx);
        if (fetchEthTx) {
            itemBinding.setEthOperation((EthOperation) currentItem);
        } else {
            itemBinding.setTokenOperation((TokenOperation) currentItem);
        }

        itemBinding.setListener(mListener);
        itemBinding.setTxHolderAddress(mTxHoldingAddress);
        itemBinding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return mOperations != null ? mOperations.size() : 0;
    }

    private void setList(List<OperationWrapper> txInfoList) {
        this.mOperations = txInfoList;
        notifyDataSetChanged();
    }

    public void setTxHoldingAddress(String txHoldingAddress) {
        this.mTxHoldingAddress = txHoldingAddress;
    }

    public void replaceData(List<OperationWrapper> txInfoList) {
        setList(txInfoList);
    }

}
