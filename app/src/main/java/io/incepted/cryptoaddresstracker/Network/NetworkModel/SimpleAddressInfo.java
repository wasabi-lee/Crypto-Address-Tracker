package io.incepted.cryptoaddresstracker.Network.NetworkModel;

public class SimpleAddressInfo {

    private float mEthBalance;
    private float mEthTotalIn;
    private float mEthTotalOut;
    private int mTotalTxs;

    public SimpleAddressInfo(float mEthBalance, float mEthTotalIn, float mEthTotalOut, int mTotalTxs) {
        this.mEthBalance = mEthBalance;
        this.mEthTotalIn = mEthTotalIn;
        this.mEthTotalOut = mEthTotalOut;
        this.mTotalTxs = mTotalTxs;
    }

    public float getEthBalance() {
        return mEthBalance;
    }

    public void setEthBalance(float mEthBalance) {
        this.mEthBalance = mEthBalance;
    }

    public float getEthTotalIn() {
        return mEthTotalIn;
    }

    public void setEthTotalIn(float mEthTotalIn) {
        this.mEthTotalIn = mEthTotalIn;
    }

    public float getEthTotalOut() {
        return mEthTotalOut;
    }

    public void setEthTotalOut(float mEthTotalOut) {
        this.mEthTotalOut = mEthTotalOut;
    }

    public int getTotalTxs() {
        return mTotalTxs;
    }

    public void setTotalTxs(int mTotalTxs) {
        this.mTotalTxs = mTotalTxs;
    }
}
