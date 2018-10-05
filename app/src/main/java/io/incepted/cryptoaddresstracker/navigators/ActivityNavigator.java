package io.incepted.cryptoaddresstracker.navigators;

public enum ActivityNavigator {

    /**
     *
     * Enum values for navigating activity transition events.
     * Each enum value represents the next activity
     *
     */

    NEW_ADDRESS, // NewAddressActivity
    TX_SCAN, // Tx scan activity
    ADDRESS_DETAIL, // DetailActivity
    TOKEN_ADDRESS, // TokenAddressActivity
    TRANSACTIONS, // TxActivity
    TRANSACTION_DETAIL, // TxDetailActivity
    SETTINGS // SettingsActivity
}
