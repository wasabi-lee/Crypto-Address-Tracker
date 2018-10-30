package io.incepted.cryptoaddresstracker.network.deserializer;

import android.util.Log;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.Map;

import io.incepted.cryptoaddresstracker.network.networkModel.remoteAddressInfo.ETH;
import io.incepted.cryptoaddresstracker.network.networkModel.remoteAddressInfo.RemoteAddressInfo;

public class SimpleAddressInfoDeserializer implements JsonDeserializer {

    /**
     * A simple deserializer to just extract ETH balance, total IN/OUT, and Transaction counts.
     */

    private static final String TAG = SimpleAddressInfoDeserializer.class.getSimpleName();

    @Override
    public Object deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        if (json == null)
            return null;

        final JsonObject data = json.getAsJsonObject();
        RemoteAddressInfo result = new RemoteAddressInfo();
        ETH balanceInfo = new ETH();

        if (data.has("error")) {
            balanceInfo.setBalance(0d);
            result.setEthBalanceInfo(balanceInfo);
            result.setCountTxs(0L);

        } else {
            JsonObject ethInfo = data.getAsJsonObject("ETH");

            double ethBalance = ethInfo.get("balance").getAsDouble();
            double ethTotalIn = ethInfo.get("totalIn").getAsDouble();
            double ethTotalOut = ethInfo.get("totalOut").getAsDouble();
            long txCount = data.get("countTxs").getAsLong();

            balanceInfo.setBalance(ethBalance);
            balanceInfo.setTotalIn(ethTotalIn);
            balanceInfo.setTotalOut(ethTotalOut);

            result.setEthBalanceInfo(balanceInfo);
            result.setCountTxs(txCount);
        }

        return result;
    }
}
