package io.incepted.cryptoaddresstracker.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;
import io.incepted.cryptoaddresstracker.R;
import io.incepted.cryptoaddresstracker.databinding.TxListItemBinding;
import io.incepted.cryptoaddresstracker.network.networkModel.transactionListInfo.EthOperation;
import io.incepted.cryptoaddresstracker.network.networkModel.transactionListInfo.OperationWrapper;
import io.incepted.cryptoaddresstracker.network.networkModel.transactionListInfo.TokenOperation;
import io.incepted.cryptoaddresstracker.viewModels.TxViewModel;

public class TxAdapter extends RecyclerView.Adapter<TxAdapter.ViewHolder> {

    private TxViewModel mViewModel;
    private List<OperationWrapper> mOperations;


    public TxAdapter(TxViewModel viewModel) {
        this.mViewModel = viewModel;
        this.mOperations = new ArrayList<>();
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
        OperationWrapper currentItem = mOperations.get(position);

        boolean fetchEthTx = currentItem.getTxType() == OperationWrapper.TX_TYPE_ETHEREUM;
        itemBinding.setFetchEthTx(fetchEthTx);
        if (fetchEthTx) {
            itemBinding.setEthOperation((EthOperation) currentItem);
        } else {
            itemBinding.setTokenOperation((TokenOperation) currentItem);
        }

        itemBinding.setViewmodel(mViewModel);
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

    public void replaceData(List<OperationWrapper> txInfoList) {
        setList(txInfoList);
    }

}
