package io.incepted.cryptoaddresstracker.Network.Deserializer;

import android.util.Log;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.Map;

import io.incepted.cryptoaddresstracker.Network.NetworkModel.SimpleAddressInfo;

public class SimpleAddressInfoDeserializer implements JsonDeserializer {

    private static final String TAG = SimpleAddressInfoDeserializer.class.getSimpleName();

    @Override
    public Object deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        if (json == null)
            return null;

        final JsonObject data = json.getAsJsonObject();

        for (Map.Entry<String, JsonElement> entry : data.entrySet()) {
            String key = entry.getKey();
            if (key.equals("error")) {
                JsonElement errorInfo = entry.getValue();
                String errorCode = errorInfo.getAsJsonObject().getAsJsonObject("code").getAsString();
                String errorMessage = errorInfo.getAsJsonObject().getAsJsonObject("message").getAsString();
                Log.d(TAG, "deserialize: Error occurred! " +
                        "\nError code: " + errorCode +
                        "\nError message: " + errorMessage);
                return null;
            }
        }

        JsonObject ethInfo = data.getAsJsonObject("ETH");

        float ethBalance = ethInfo.get("balance").getAsFloat();
        float ethTotalIn = ethInfo.get("totalIn").getAsFloat();
        float ethTotalOut = ethInfo.get("totalOut").getAsFloat();
        int txCount = data.get("countTxs").getAsInt();

        return new SimpleAddressInfo(ethBalance, ethTotalIn, ethTotalOut, txCount);
    }
}
