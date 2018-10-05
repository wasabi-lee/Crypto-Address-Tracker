package io.incepted.cryptoaddresstracker.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.widget.Toast;

public class CopyUtils {

    public static void copyText(Context context, String value) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(null, value);
        clipboard.setPrimaryClip(clip);

        Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show();
    }
}
