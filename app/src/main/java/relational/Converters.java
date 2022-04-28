package relational;

import android.net.Uri;

import androidx.room.TypeConverter;

import java.util.Date;

public class Converters {
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public static Uri stringToUri(String value) { return (value == null || value.length()==0) ? null : Uri.parse(value); }

    @TypeConverter
    public static String UriToString(Uri uri) { return uri == null ? "" : uri.toString(); }
}