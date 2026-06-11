package sn.dev.android.gestionnairenotes;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

public class SettingsActivity extends Activity {
    private LinearLayout rootLayout;
    private boolean darkMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buildLayout();
    }

    private void buildLayout() {
        darkMode = UserPreferences.isDarkMode(this);
        configureSystemBars();

        rootLayout = new LinearLayout(this);
        rootLayout.setOrientation(LinearLayout.VERTICAL);
        rootLayout.setPadding(dp(16), dp(18), dp(16), dp(16));
        rootLayout.setBackgroundColor(pageBackground());
        setContentView(rootLayout);

        rootLayout.setOnApplyWindowInsetsListener((view, insets) -> {
            int top = insets.getSystemWindowInsetTop();
            int bottom = insets.getSystemWindowInsetBottom();
            rootLayout.setPadding(dp(16), top + dp(12), dp(16), bottom + dp(16));
            return insets;
        });
        rootLayout.requestApplyInsets();

        rootLayout.addView(createHeader(), new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(46)
        ));

        TextView sectionTitle = new TextView(this);
        sectionTitle.setText("Parametres");
        sectionTitle.setTextColor(textColor());
        sectionTitle.setTextSize(26);
        sectionTitle.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        titleParams.setMargins(0, dp(22), 0, dp(14));
        rootLayout.addView(sectionTitle, titleParams);

        addOption("Mode sombre", "Utiliser une interface foncee.", UserPreferences.isDarkMode(this), (button, checked) -> {
            UserPreferences.setDarkMode(this, checked);
            buildLayout();
        });

        addOption("Apercu des notes", "Afficher une ligne du contenu dans la liste.", UserPreferences.isPreviewEnabled(this), (button, checked) -> {
            UserPreferences.setPreviewEnabled(this, checked);
        });
    }

    private void configureSystemBars() {
        Window window = getWindow();
        window.setStatusBarColor(pageBackground());
        window.setNavigationBarColor(pageBackground());
        int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        if (!darkMode) {
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            flags |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
        }
        window.getDecorView().setSystemUiVisibility(flags);
    }

    private LinearLayout createHeader() {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);

        ImageButton backButton = iconButton(R.drawable.ic_arrow_back, "Retour");
        backButton.setOnClickListener(view -> finishWithAnimation());
        row.addView(backButton, new LinearLayout.LayoutParams(dp(44), dp(44)));

        TextView title = new TextView(this);
        title.setText("Retour");
        title.setTextColor(textColor());
        title.setTextSize(16);
        title.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
        titleParams.setMargins(dp(12), 0, 0, 0);
        row.addView(title, titleParams);

        return row;
    }

    private void addOption(String title, String subtitle, boolean checked, CompoundButton.OnCheckedChangeListener listener) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(dp(14), dp(12), dp(12), dp(12));
        row.setBackground(Ui.roundedStroke(surfaceColor(), subtleStrokeColor(), 12, 1, this));

        LinearLayout textColumn = new LinearLayout(this);
        textColumn.setOrientation(LinearLayout.VERTICAL);
        textColumn.setGravity(Gravity.CENTER_VERTICAL);

        TextView titleView = new TextView(this);
        titleView.setText(title);
        titleView.setTextColor(textColor());
        titleView.setTextSize(16);
        titleView.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        textColumn.addView(titleView, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        TextView subtitleView = new TextView(this);
        subtitleView.setText(subtitle);
        subtitleView.setTextColor(subtleTextColor());
        subtitleView.setTextSize(12);
        LinearLayout.LayoutParams subtitleParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        subtitleParams.setMargins(0, dp(3), 0, 0);
        textColumn.addView(subtitleView, subtitleParams);

        row.addView(textColumn, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        Switch optionSwitch = new Switch(this);
        optionSwitch.setChecked(checked);
        optionSwitch.setOnCheckedChangeListener(listener);
        row.addView(optionSwitch, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        row.setOnClickListener(view -> optionSwitch.setChecked(!optionSwitch.isChecked()));

        LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        rowParams.setMargins(0, 0, 0, dp(10));
        rootLayout.addView(row, rowParams);
    }

    private void finishWithAnimation() {
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private ImageButton iconButton(int drawableRes, String description) {
        ImageButton button = new ImageButton(this);
        button.setContentDescription(description);
        button.setImageResource(drawableRes);
        button.setColorFilter(textColor());
        button.setPadding(dp(10), dp(10), dp(10), dp(10));
        button.setScaleType(ImageButton.ScaleType.CENTER);
        button.setBackground(Ui.roundedStroke(surfaceColor(), strokeColor(), 22, 1, this));
        return button;
    }

    private int pageBackground() {
        return darkMode ? AppColors.DARK_BACKGROUND : AppColors.WHITE;
    }

    private int surfaceColor() {
        return darkMode ? AppColors.DARK_SURFACE : AppColors.WHITE;
    }

    private int strokeColor() {
        return darkMode ? AppColors.GREY : AppColors.BLACK;
    }

    private int subtleStrokeColor() {
        return darkMode ? AppColors.GREY : AppColors.LIGHT_STROKE;
    }

    private int textColor() {
        return darkMode ? AppColors.WHITE : AppColors.BLACK;
    }

    private int subtleTextColor() {
        return darkMode ? AppColors.LIGHT_STROKE : AppColors.GREY;
    }

    private int dp(float value) {
        return Ui.dp(this, value);
    }
}
