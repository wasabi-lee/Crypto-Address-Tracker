package io.incepted.cryptoaddresstracker.Binding;

import android.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.incepted.cryptoaddresstracker.Adapters.AddressAdapter;
import io.incepted.cryptoaddresstracker.Adapters.TokenAdapter;
import io.incepted.cryptoaddresstracker.Adapters.TxAdapter;
import io.incepted.cryptoaddresstracker.Data.Model.Address;
import io.incepted.cryptoaddresstracker.Network.NetworkModel.RemoteAddressInfo.Token;
import io.incepted.cryptoaddresstracker.Network.NetworkModel.TransactionListInfo.OperationWrapper;
import io.incepted.cryptoaddresstracker.R;
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

    @BindingAdapter({"app:tx_items"})
    public static void setTxItems(RecyclerView recyclerView, List<OperationWrapper> items) {
        TxAdapter adapter = (TxAdapter) recyclerView.getAdapter();
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
            double editedValue = NumberUtils.moveDecimal(value, decimals);
            textView.setText(NumberUtils.formatDouble(editedValue));
        }
    }

    @BindingAdapter({"app:long_text"})
    public static void setLongText(TextView textView, long value) {
        textView.setText(String.valueOf(value));
    }

    @BindingAdapter({"app:timestamp"})
    public static void setTimestampText(TextView textView, Date date) {
        String formattedTimestamp = DateFormat.getDateInstance().format(date);
        textView.setText(formattedTimestamp);
    }

    @BindingAdapter({"app:timestamp"})
    public static void setTimestampText(TextView textView, Long timestamp) {
        if (timestamp == null) {
                textView.setText("");
            return;
        }
        Date date = new Date(timestamp * 1000);
        String formattedTimestamp = SimpleDateFormat.getDateInstance().format(date);
        textView.setText(formattedTimestamp);
    }

    @BindingAdapter({"app:tx_amount", "app:decimal", "app:from", "app:address"})
    public static void setAmount(TextView textView, String amount, int decimals, String from, String address) {

        String formattedAmount = NumberUtils.moveDecimal(amount, decimals);

        if (address == null) {
            textView.setText(formattedAmount);
            return;
        }

        boolean sent = (address.toLowerCase()).equals(from.toLowerCase());
        String result = (sent ? "-" : "+") + formattedAmount;
        String textColor = sent ? "#e65454" : "#3cb271";

        textView.setText(result);
        textView.setTextColor(Color.parseColor(textColor));
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

    @BindingAdapter({"app:success"})
    public static void setSuccess(TextView textView, Boolean success) {
        if (success == null) return;
        textView.setText(success ? "Success" : "Pending");
        textView.setTextColor(Color.parseColor(success ? "#3cb271" : "#e65454"));
    }
}
