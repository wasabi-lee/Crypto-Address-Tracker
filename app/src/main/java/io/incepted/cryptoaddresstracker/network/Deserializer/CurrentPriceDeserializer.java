package io.incepted.cryptoaddresstracker.network.deserializer;

import android.util.Log;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import io.incepted.cryptoaddresstracker.network.networkModel.currentPrice.CurrentPrice;

public class CurrentPriceDeserializer implements JsonDeserializer {

    private static final String TAG = CurrentPriceDeserializer.class.getSimpleName();


    /**
     * <Sample Response>
     * {"USD":6701.6}
     */

    @Override
    public Object deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        JsonObject jsonObject = json.getAsJsonObject();

        String tsym = json.getAsJsonObject().keySet().iterator().next();
        Double price = json.getAsJsonObject().get(tsym).getAsDouble();

        Log.d(TAG, "deserialize: " + tsym + " " + price);

        return new CurrentPrice(tsym, price);
    }
}
