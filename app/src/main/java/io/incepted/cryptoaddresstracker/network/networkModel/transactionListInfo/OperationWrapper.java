package io.incepted.cryptoaddresstracker.network.networkModel.transactionListInfo;

public abstract class OperationWrapper {

    /**
     * A wrapper class to define the type of the inheriting objects.
     */

    public static final int TX_TYPE_ETHEREUM = 0;
    public static final int TX_TYPE_TOKEN = 1;

    abstract public int getTxType();
}
