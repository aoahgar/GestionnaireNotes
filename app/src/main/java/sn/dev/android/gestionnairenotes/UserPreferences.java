package sn.dev.android.gestionnairenotes;

import android.content.Context;
import android.content.SharedPreferences;

public final class UserPreferences {
    private static final String PREFERENCES_NAME = "ui_preferences";
    private static final String KEY_DARK_MODE = "dark_mode";
    private static final String KEY_SHOW_PREVIEW = "show_preview";

    private UserPreferences() {
    }

    public static boolean isDarkMode(Context context) {
        return getPreferences(context).getBoolean(KEY_DARK_MODE, false);
    }

    public static void setDarkMode(Context context, boolean enabled) {
        getPreferences(context).edit().putBoolean(KEY_DARK_MODE, enabled).apply();
    }

    public static boolean isPreviewEnabled(Context context) {
        return getPreferences(context).getBoolean(KEY_SHOW_PREVIEW, true);
    }

    public static void setPreviewEnabled(Context context, boolean enabled) {
        getPreferences(context).edit().putBoolean(KEY_SHOW_PREVIEW, enabled).apply();
    }

    private static SharedPreferences getPreferences(Context context) {
        return context.getApplicationContext().getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }
}
