package sn.dev.android.gestionnairenotes;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public final class Ui {
    private Ui() {
    }

    public static int dp(Context context, float value) {
        return Math.round(value * context.getResources().getDisplayMetrics().density);
    }

    public static GradientDrawable rounded(int color, float radiusDp, Context context) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(dp(context, radiusDp));
        return drawable;
    }

    public static GradientDrawable roundedStroke(int fillColor, int strokeColor, float radiusDp, float strokeDp, Context context) {
        GradientDrawable drawable = rounded(fillColor, radiusDp, context);
        drawable.setStroke(dp(context, strokeDp), strokeColor);
        return drawable;
    }

    public static GradientDrawable circle(int color) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.OVAL);
        drawable.setColor(color);
        return drawable;
    }

    public static TextView textButton(Context context, String text, boolean selected, boolean darkMode) {
        TextView button = new TextView(context);
        button.setText(text);
        button.setGravity(Gravity.CENTER);
        button.setTextSize(14);
        button.setSingleLine(true);
        button.setPadding(dp(context, 12), 0, dp(context, 12), 0);

        int background = selected ? AppColors.BLACK : (darkMode ? AppColors.DARK_SURFACE : AppColors.WHITE);
        int foreground = selected ? AppColors.WHITE : (darkMode ? AppColors.WHITE : AppColors.BLACK);
        int stroke = darkMode ? AppColors.WHITE : AppColors.BLACK;
        button.setTextColor(foreground);
        button.setBackground(roundedStroke(background, stroke, 12, 1, context));
        return button;
    }

    public static TextView primaryButton(Context context, String text) {
        TextView button = new TextView(context);
        button.setText(text);
        button.setTextColor(AppColors.WHITE);
        button.setTextSize(24);
        button.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        button.setGravity(Gravity.CENTER);
        button.setBackground(rounded(AppColors.BLACK, 6, context));
        return button;
    }

    public static void setMargins(View view, int left, int top, int right, int bottom) {
        ViewGroup.LayoutParams rawParams = view.getLayoutParams();
        if (rawParams instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) rawParams;
            params.setMargins(left, top, right, bottom);
            view.setLayoutParams(params);
        }
    }
}
