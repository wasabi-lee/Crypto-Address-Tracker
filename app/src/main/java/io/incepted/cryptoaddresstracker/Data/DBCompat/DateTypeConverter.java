package io.incepted.cryptoaddresstracker.Data.DBCompat;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;

public class DateTypeConverter {

    /**
     * Converting java.util.Date to Long and vice versa for SQL database compatibility
     */

    @TypeConverter
    public static Date toDate(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long toLong(Date value) {
        return value == null ? null : value.getTime();
    }
}
