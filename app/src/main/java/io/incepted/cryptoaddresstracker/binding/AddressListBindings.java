package io.incepted.cryptoaddresstracker.binding;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import io.incepted.cryptoaddresstracker.adapters.AddressAdapter;
import io.incepted.cryptoaddresstracker.adapters.TokenAdapter;
import io.incepted.cryptoaddresstracker.adapters.TokenTransferAdapter;
import io.incepted.cryptoaddresstracker.adapters.TxAdapter;
import io.incepted.cryptoaddresstracker.data.model.Address;
import io.incepted.cryptoaddresstracker.network.networkModel.remoteAddressInfo.Token;
import io.incepted.cryptoaddresstracker.network.networkModel.transactionInfo.TransactionInfo;
import io.incepted.cryptoaddresstracker.network.networkModel.transactionListInfo.OperationWrapper;
import io.incepted.cryptoaddresstracker.utils.Blockies;
import io.incepted.cryptoaddresstracker.utils.NumberUtils;

public class AddressListBindings {


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
//            adapter.replaceData(items);
        }
    }

    @BindingAdapter({"app:token_transfer_info"})
    public static void setTokenTransferInfo(RecyclerView recyclerView, TransactionInfo txInfo) {
        TokenTransferAdapter adapter = (TokenTransferAdapter) recyclerView.getAdapter();
        if (adapter != null) {
            // Setting txInfo object itself to populate header content rather than passing the list only.
            adapter.replaceData(txInfo);
        }
    }

    @BindingAdapter({"app:int_text"})
    public static void setIntText(TextView textView, int value) {
        textView.setText(String.valueOf(value));
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

    @BindingAdapter({"app:tx_amount", "app:decimal"})
    public static void setDoubleText(TextView textView, String amount, int decimals) {
        String formattedAmount = NumberUtils.moveDecimal(amount, decimals);
        textView.setText(formattedAmount);
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
        if (address != null) {
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

    @BindingAdapter({"app:success"})
    public static void setSuccess(TextView textView, Boolean success) {
        if (success == null) return;
        textView.setText(success ? "Success" : "Pending");
        textView.setTextColor(Color.parseColor(success ? "#3cb271" : "#e65454"));
    }

    @BindingAdapter({"app:token_transfer_exists"})
    public static void setTokenTransfer(TextView textView, int transfers) {

        boolean noTransfer = transfers == 0;

        textView.setTextColor(Color.parseColor(noTransfer ? "#ababab" : "#3cb271"));

        String text = noTransfer ? "No token transfer found" : transfers + " token transfers found (click to see details)";

        if (noTransfer) {
            textView.setText(text);
        } else {
            SpannableString content = new SpannableString(text);
            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
            textView.setText(content);
        }
    }

    @BindingAdapter({"app:eth_balance", "app:base_currency_price", "app:base_currency_symbol"})
    public static void setBaseCurrencyValue(TextView textView, Double ethBalance, Double baseCurrencyPrice, String baseSymbol) {
        if (ethBalance != null && baseCurrencyPrice != null) {
            String formatted = NumberUtils.formatDouble(ethBalance * baseCurrencyPrice) + " " + baseSymbol;
            textView.setText(formatted);
        }
    }
}
