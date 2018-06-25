package io.incepted.cryptoaddresstracker.Adapters;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import io.incepted.cryptoaddresstracker.Data.Model.Address;
import io.incepted.cryptoaddresstracker.Listeners.AddressItemActionListener;
import io.incepted.cryptoaddresstracker.R;
import io.incepted.cryptoaddresstracker.ViewModels.MainViewModel;
import io.incepted.cryptoaddresstracker.databinding.AddressListItemBinding;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {

    private static final String TAG = AddressAdapter.class.getSimpleName();

    private MainViewModel mViewModel;
    private List<Address> mAddresses;
    private AddressItemActionListener mListener;

    public AddressAdapter(List<Address> addresses, MainViewModel viewModel) {
        this.mViewModel = viewModel;
        this.mListener = addressId -> mViewModel.openAddressDetail(addressId);
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
        itemBinding.setListener(mListener);
        itemBinding.executePendingBindings();
        Log.d(TAG, "onBindViewHolder: " + mAddresses.get(position).getName());
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
}
