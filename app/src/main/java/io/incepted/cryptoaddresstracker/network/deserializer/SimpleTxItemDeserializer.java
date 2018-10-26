package io.incepted.cryptoaddresstracker.network.deserializer;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import io.incepted.cryptoaddresstracker.network.networkModel.ErrorResponse;
import io.incepted.cryptoaddresstracker.network.networkModel.transactionListInfo.SimpleTxItemResult;

public class SimpleTxItemDeserializer implements JsonDeserializer {
    @Override
    public Object deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        SimpleTxItemResult result = new SimpleTxItemResult();

        if (json == null) {
            result.setError(new ErrorResponse(-1, "empty response"));

        } else if (json.isJsonArray()) {
            // if tx info is eth (starts with an unnamed array)
            JsonArray txs = json.getAsJsonArray();

            for (JsonElement element: txs) {
                JsonObject item = element.getAsJsonObject();
                //TODO construct the result
            }


        } else {
            JsonObject data = json.getAsJsonObject();

            // check if the object is actually an error message object
            if (data.has("error")) {
                JsonObject errorObj = data.getAsJsonObject("error");
                result.setError(new ErrorResponse(errorObj.get("code").getAsInt(),
                        errorObj.get("message").getAsString()));

            } else {
                // if tx info is token (starts with an object that wraps the array
                // with the key 'operations'.

                JsonArray txs = data.get("operations").getAsJsonArray();

                for (JsonElement element: txs) {
                    JsonObject item = element.getAsJsonObject();
                    //TODO construct the result
                }

            }
        }


        return result;
    }
}
