package io.incepted.cryptoaddresstracker.Navigators;

public enum ActivityNavigator {

    /**
     *
     * Enum values for navigating activity transition events.
     * Each enum value represents the next activity
     *
     */

    NEW_ADDRESS, // NewAddressActivity
    ADDRESS_DETAIL, // DetailActivity
    TOKEN_ADDRESS, // TokenAddressActivity
    TRANSACTIONS, // TxActivity
    TRANSACTION_DETAIL, // TxDetailActivity
    SETTINGS // SettingsActivity
}
