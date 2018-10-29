package io.incepted.cryptoaddresstracker.network.deserializer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;

import io.incepted.cryptoaddresstracker.network.networkModel.ErrorResponse;
import io.incepted.cryptoaddresstracker.network.networkModel.remoteAddressInfo.TokenInfo;
import io.incepted.cryptoaddresstracker.network.networkModel.transactionListInfo.SimpleTxItemResult;

/**
 * A deserializer class for the API call getAddressHistory OR getAddressTrasactions.
 * <p>
 * 'getAddressHistory' API call returns a list of token transactions for an address.
 * 'getAddressTransactions' API call returns a list of ETH transactions for an address.
 * <p>
 * Since the structure of the two API calls share some common key-value pairs,
 * deserializing the response into a single aggregated object type (in this case, SimpleTxItem)
 * can avoid any unnecessary complications, rather than separating the result data type
 * for each API call.
 */

public class SimpleTxItemDeserializer implements JsonDeserializer {

    @Override
    public Object deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        SimpleTxItemResult result = new SimpleTxItemResult();
        ArrayList<SimpleTxItem> itemList = new ArrayList<>();

        if (json != null) {

            if (json.isJsonArray()) {
                // The response is a list of eth transactions (starts with an unnamed array)
                for (JsonElement element : json.getAsJsonArray()) {
                    JsonObject jsonItem = element.getAsJsonObject();
                    itemList.add(getSimpleTxItemForEth(jsonItem));
                }


            } else {
                JsonObject data = json.getAsJsonObject();

                // Check if the object is actually an error message object
                if (data.has("error")) {
                    result.setError(getErrorResponse(data));

                } else {
                    // The response is a list of token transactions
                    // (starts with an object that wraps the array with the key 'operations')
                    for (JsonElement element : data.get("operations").getAsJsonArray()) {
                        JsonObject jsonItem = element.getAsJsonObject();
                        itemList.add(getSimpleTxItemForToken(jsonItem));
                    }
                }
            }
        }

        result.setItems(itemList);
        return result;
    }


    private ErrorResponse getErrorResponse(JsonObject json) {
        JsonObject error = json.getAsJsonObject("error");
        return new ErrorResponse(error.get("code").getAsInt(),
                error.get("message").getAsString());
    }


    /**
     * A method for deserializing the result from the 'getAddressTransactions' api call.
     * This api call returns the Ethereum transaction list of the specific address.
     *
     * @param json A JSON object containing the ETH transaction info.
     * @return An object that contains the deseralized info from the given JSON object.
     */
    private SimpleTxItem getSimpleTxItemForEth(JsonObject json) {
        SimpleTxItem txItem = new SimpleTxItem();
        txItem.setHash(json.get("hash").getAsString());
        txItem.setFrom(json.get("from").getAsString());
        txItem.setTo(json.get("to").getAsString());
        txItem.setTimestamp(json.get("timestamp").getAsLong());
        txItem.setValue(json.get("value").getAsString());
        return txItem;
    }


    /**
     * A method for deserializing the result from the 'getAddressHistory' api call.
     * This api call returns the transaction list of the specific address for the specific tokens.
     *
     * @param json A JSON object containing the token transaction info.
     * @return An object that contains the deseralized info from the given JSON object.
     */
    private SimpleTxItem getSimpleTxItemForToken(JsonObject json) {
        SimpleTxItem txItem = new SimpleTxItem();
        txItem.setHash(json.get("TransactionHash").getAsString());
        txItem.setFrom(json.get("from").getAsString());
        txItem.setTo(json.get("to").getAsString());
        txItem.setTimestamp(json.get("timestamp").getAsLong());
        txItem.setValue(json.get("value").getAsString());

        JsonObject jsonTokenInfo = json.get("tokenInfo").getAsJsonObject();
        txItem.setTokenInfo(getTokenInfo(jsonTokenInfo));

        return txItem;
    }

    /**
     * A method for deserializing the tokenInfo element in the 'getAddressHistory' api call.
     *
     * @param tokenJson A JSON object containing the token info.
     * @return An object that contains the deseralized info from the given JSON object.
     */
    private TokenInfo getTokenInfo(JsonObject tokenJson) {
        TokenInfo tokenInfo = new TokenInfo();
        tokenInfo.setName(tokenJson.get("name").getAsString());
        tokenInfo.setDecimals(tokenJson.get("decimals").getAsInt());
        tokenInfo.setSymbol(tokenJson.get("symbol").getAsString());
        return tokenInfo;
    }
}
