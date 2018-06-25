package io.incepted.cryptoaddresstracker.Binding;

import android.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.text.DecimalFormat;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.incepted.cryptoaddresstracker.Adapters.AddressAdapter;
import io.incepted.cryptoaddresstracker.Data.Model.Address;
import io.incepted.cryptoaddresstracker.Utils.Blockies;

public class AddressListBindings {

    @BindingAdapter({"app:items"})
    public static void setItems(RecyclerView recyclerView, List<Address> items) {
        AddressAdapter adapter = (AddressAdapter) recyclerView.getAdapter();
        if (adapter != null) {
            adapter.replaceData(items);
        }
    }

    @BindingAdapter({"app:float_text"})
    public static void setFloatText(TextView textView, float value) {
        if ((Float.isNaN(value))) {
            textView.setText("-");
        } else {
            DecimalFormat df = new DecimalFormat(value < 10 ? "#.####" : "#.##");
            textView.setText(df.format(value));
        }
    }

    @BindingAdapter({"app:int_text"})
    public static void setIntText(TextView textView, int value) {
        textView.setText(String.valueOf(value));
    }

    @BindingAdapter({"app:blockies"})
    public static void setBlockies(CircleImageView circleImageView, String address) {
        try {
            Bitmap blockies = Blockies.createIcon(address);

            Glide.with(circleImageView.getContext())
                    .load(blockies)
                    .into(circleImageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
