package io.incepted.cryptoaddresstracker.Binding;

import android.databinding.BindingAdapter;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import io.incepted.cryptoaddresstracker.Adapters.AddressAdapter;
import io.incepted.cryptoaddresstracker.Data.Model.Address;

public class AddressListBindings {
    @BindingAdapter({"app:items"})
    public static void setItems(RecyclerView recyclerView, List<Address> items) {
        AddressAdapter adapter = (AddressAdapter) recyclerView.getAdapter();
        if (adapter != null) {
            adapter.replaceData(items);
        }
    }
}
