package io.incepted.cryptoaddresstracker.Binding;

import android.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.text.DecimalFormat;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.incepted.cryptoaddresstracker.Adapters.AddressAdapter;
import io.incepted.cryptoaddresstracker.Adapters.TokenAdapter;
import io.incepted.cryptoaddresstracker.Data.Model.Address;
import io.incepted.cryptoaddresstracker.Network.NetworkModel.RemoteAddressInfo.Token;
import io.incepted.cryptoaddresstracker.Utils.Blockies;
import io.incepted.cryptoaddresstracker.Utils.NumberUtils;

public class AddressListBindings {

    private static final String TAG = AddressListBindings.class.getSimpleName();

    @BindingAdapter({"app:address_items"})
    public static void setAddressItems(RecyclerView recyclerView, List<Address> items) {
        AddressAdapter adapter = (AddressAdapter) recyclerView.getAdapter();
        if (adapter != null) {
            adapter.replaceData(items);
        }
    }

    @BindingAdapter({"app:token_items"})
    public static void setTokenItems(RecyclerView recyclerView, List<Token> items) {
        TokenAdapter adapter = (TokenAdapter) recyclerView.getAdapter();
        if (adapter != null) {
            adapter.replaceData(items);
        }
    }

    @BindingAdapter({"app:double_text"})
    public static void setDoubleText(TextView textView, double value) {
        if ((Double.isNaN(value))) {
            textView.setText("-");
        } else {
            DecimalFormat df = new DecimalFormat(value < 10 ? "#.####" : "#.##");
            textView.setText(df.format(value));
        }
    }

    @BindingAdapter({"app:double_text", "app:decimal"})
    public static void setDoubleText(TextView textView, double value, int decimals) {
        if ((Double.isNaN(value))) {
            textView.setText("-");
        } else {
            double editedValue =  NumberUtils.moveDecimal(value, decimals);
            DecimalFormat df = new DecimalFormat(editedValue < 10 ? "#.####" : "#.##");
            textView.setText(df.format(editedValue));
        }
    }

    @BindingAdapter({"app:long_text"})
    public static void setIntText(TextView textView, long value) {
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
