package io.incepted.cryptoaddresstracker.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;
import io.incepted.cryptoaddresstracker.R;
import io.incepted.cryptoaddresstracker.data.model.Address;
import io.incepted.cryptoaddresstracker.databinding.AddressListItemBinding;
import io.incepted.cryptoaddresstracker.listeners.AddressItemActionListener;
import io.incepted.cryptoaddresstracker.network.networkModel.currentPrice.CurrentPrice;
import io.incepted.cryptoaddresstracker.viewModels.MainViewModel;
import timber.log.Timber;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {

    private static final String TAG = AddressAdapter.class.getSimpleName();

    private MainViewModel mViewModel;
    private List<Address> mAddresses;
    private AddressItemActionListener mListener;
    private CurrentPrice mCurrentPrice;

    public AddressAdapter(List<Address> addresses, MainViewModel viewModel, CurrentPrice currentPrice) {
        this.mViewModel = viewModel;
        this.mListener = addressId -> mViewModel.openAddressDetail(addressId);
        this.mCurrentPrice = currentPrice;
        setList(addresses);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private AddressListItemBinding addressListItemBinding;

        ViewHolder(AddressListItemBinding binding) {
            super(binding.getRoot());
            addressListItemBinding = binding;
        }

        public AddressListItemBinding getItemBinding() {
            return addressListItemBinding;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ViewDataBinding binding = DataBindingUtil.inflate(inflater, R.layout.address_list_item, parent, false);
        return new ViewHolder((AddressListItemBinding) binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Address currentItem = mAddresses.get(position);
        AddressListItemBinding itemBinding = holder.getItemBinding();
        itemBinding.setAddress(currentItem);
        itemBinding.setPrice(mCurrentPrice);
        itemBinding.setListener(mListener);
        itemBinding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return mAddresses != null ? mAddresses.size() : 0;
    }

    private void setList(List<Address> addresses) {
        this.mAddresses = addresses;
        notifyDataSetChanged();
    }

    public void replaceData(List<Address> addresses) {
        setList(addresses);
    }

    public void toggleDisplayCurrency(CurrentPrice currentPrice) {
        mCurrentPrice = currentPrice;
        notifyDataSetChanged();
    }
}
