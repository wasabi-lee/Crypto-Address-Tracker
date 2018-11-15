package io.incepted.cryptoaddresstracker.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.paging.PagedList;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import io.incepted.cryptoaddresstracker.R;
import io.incepted.cryptoaddresstracker.databinding.TxListItemBinding;
import io.incepted.cryptoaddresstracker.listeners.TxItemActionListener;
import io.incepted.cryptoaddresstracker.network.deserializer.SimpleTxItem;
import timber.log.Timber;

public class TxAdapter extends PagedListAdapter<SimpleTxItem, TxAdapter.ViewHolder> {


    public TxItemActionListener mListener;
    private String mOriginalAddress;

    public TxAdapter(@NonNull DiffUtil.ItemCallback<SimpleTxItem> diffCallback,
                     TxItemActionListener listener) {
        super(diffCallback);
        this.mListener = listener;
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
        itemBinding.setAddress(mOriginalAddress);
        itemBinding.executePendingBindings();
    }

    @Override
    public void submitList(PagedList<SimpleTxItem> pagedList) {
        super.submitList(pagedList);
        Timber.d("real size: %s" , pagedList.size());
    }

    /**
     * Setting the PagedList and the original address value up to date.
     * @param pagedList An updated PagedList
     * @param originalAddress The address that the user is currently browsing.
     *                        This data is used for determining either this transaction is IN or OUT.
     *                        (e.g. If the API response of this Tx's "from" field is equal to this original address,
     *                        this transaction is considered outbound (OUT) and vice versa.)
     */
    public void submitList(PagedList<SimpleTxItem> pagedList, String originalAddress) {
        this.mOriginalAddress = originalAddress;
        submitList(pagedList);
    }
}
