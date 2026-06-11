package sn.dev.android.gestionnairenotes;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class NoteEditorActivity extends Activity {
    private NoteRepository repository;
    private Note currentNote;
    private EditText titleInput;
    private EditText contentInput;
    private LinearLayout rootLayout;
    private int selectedColor = AppColors.GREEN;
    private boolean editMode;
    private boolean darkMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        repository = new NoteRepository(this);

        String noteId = getIntent().getStringExtra(MainActivity.EXTRA_NOTE_ID);
        if (noteId != null) {
            currentNote = repository.findById(noteId);
            if (currentNote == null) {
                Toast.makeText(this, "Note introuvable", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            editMode = true;
            selectedColor = currentNote.getColor();
            buildLayout(currentNote.getTitle(), currentNote.getContent());
        } else {
            editMode = false;
            selectedColor = getIntent().getIntExtra(MainActivity.EXTRA_NOTE_COLOR, AppColors.GREEN);
            buildLayout("", "");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (titleInput == null || contentInput == null) {
            return;
        }

        boolean currentDarkMode = UserPreferences.isDarkMode(this);
        if (currentDarkMode != darkMode) {
            String title = titleInput.getText().toString();
            String content = contentInput.getText().toString();
            buildLayout(title, content);
        }
    }

    private void buildLayout(String titleValue, String contentValue) {
        darkMode = UserPreferences.isDarkMode(this);
        configureSystemBars();

        rootLayout = new LinearLayout(this);
        rootLayout.setOrientation(LinearLayout.VERTICAL);
        rootLayout.setPadding(dp(14), dp(18), dp(14), dp(12));
        rootLayout.setBackgroundColor(pageBackground());
        setContentView(rootLayout);

        rootLayout.setOnApplyWindowInsetsListener((view, insets) -> {
            int top = insets.getSystemWindowInsetTop();
            int bottom = insets.getSystemWindowInsetBottom();
            rootLayout.setPadding(dp(14), top + dp(12), dp(14), bottom + dp(12));
            return insets;
        });
        rootLayout.requestApplyInsets();

        rootLayout.addView(createHeader(), new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(44)
        ));

        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1
        );
        cardParams.setMargins(0, dp(10), 0, dp(10));
        rootLayout.addView(createFormCard(titleValue, contentValue), cardParams);

        rootLayout.addView(createColorSelector(), new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(48)
        ));

        rootLayout.addView(createActionRow(), new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                editMode ? dp(42) : dp(4)
        ));

        TextView primaryButton = Ui.primaryButton(this, editMode ? "Modifier" : "Creer");
        primaryButton.setOnClickListener(view -> {
            animateTap(view);
            saveNote();
        });
        LinearLayout.LayoutParams primaryParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(54)
        );
        primaryParams.setMargins(0, dp(8), 0, 0);
        rootLayout.addView(primaryButton, primaryParams);
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
        backButton.setOnClickListener(view -> {
            animateTap(view);
            finishWithAnimation();
        });
        row.addView(backButton, new LinearLayout.LayoutParams(dp(44), dp(44)));

        TextView title = new TextView(this);
        title.setText(editMode ? "Modifier une note" : "Creer une note");
        title.setTextColor(textColor());
        title.setTextSize(18);
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        title.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
        titleParams.setMargins(dp(12), 0, dp(12), 0);
        row.addView(title, titleParams);

        ImageButton settingsButton = iconButton(R.drawable.ic_settings, "Parametres");
        settingsButton.setOnClickListener(view -> {
            animateTap(view);
            startActivity(new Intent(this, SettingsActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
        row.addView(settingsButton, new LinearLayout.LayoutParams(dp(44), dp(44)));

        return row;
    }

    private View createFormCard(String titleValue, String contentValue) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(14), dp(12), dp(14), dp(12));
        card.setBackground(Ui.rounded(selectedColor, 10, this));
        card.setElevation(dp(3));

        titleInput = new EditText(this);
        titleInput.setText(titleValue);
        titleInput.setHint("Titre");
        titleInput.setSingleLine(true);
        titleInput.setTextSize(24);
        titleInput.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        titleInput.setTextColor(AppColors.BLACK);
        titleInput.setHintTextColor(Color.argb(135, 0, 0, 0));
        titleInput.setPadding(0, 0, 0, 0);
        titleInput.setBackgroundColor(Color.TRANSPARENT);
        card.addView(titleInput, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(44)
        ));

        contentInput = new EditText(this);
        contentInput.setText(contentValue);
        contentInput.setHint("Contenu de la note");
        contentInput.setGravity(Gravity.TOP);
        contentInput.setTextSize(14);
        contentInput.setTextColor(AppColors.BLACK);
        contentInput.setHintTextColor(Color.argb(135, 0, 0, 0));
        contentInput.setPadding(0, dp(4), 0, 0);
        contentInput.setBackgroundColor(Color.TRANSPARENT);
        contentInput.setInputType(InputType.TYPE_CLASS_TEXT
                | InputType.TYPE_TEXT_FLAG_MULTI_LINE
                | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        contentInput.setMinLines(8);
        card.addView(contentInput, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1
        ));

        return card;
    }

    private LinearLayout createColorSelector() {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER);

        for (int color : AppColors.NOTE_COLORS) {
            View dot = colorDot(color);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dp(30), dp(30));
            params.setMargins(dp(6), dp(8), dp(6), dp(8));
            row.addView(dot, params);
        }

        return row;
    }

    private View colorDot(int color) {
        View dot = new View(this);
        dot.setContentDescription(AppColors.colorName(color));
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.OVAL);
        drawable.setColor(color);
        if (color == selectedColor) {
            drawable.setStroke(dp(3), darkMode ? AppColors.WHITE : AppColors.BLACK);
        }
        dot.setBackground(drawable);
        dot.setOnClickListener(view -> {
            animateTap(view);
            String title = titleInput.getText().toString();
            String content = contentInput.getText().toString();
            selectedColor = color;
            buildLayout(title, content);
        });
        return dot;
    }

    private LinearLayout createActionRow() {
        LinearLayout row = new LinearLayout(this);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setOrientation(LinearLayout.HORIZONTAL);

        if (editMode) {
            TextView shareButton = Ui.textButton(this, "Partager", false, darkMode);
            shareButton.setTextSize(13);
            shareButton.setOnClickListener(view -> {
                animateTap(view);
                shareNote();
            });
            LinearLayout.LayoutParams shareParams = new LinearLayout.LayoutParams(0, dp(34), 1);
            shareParams.setMargins(0, dp(4), dp(8), dp(4));
            row.addView(shareButton, shareParams);

            TextView deleteButton = Ui.textButton(this, "Supprimer", false, darkMode);
            deleteButton.setTextColor(AppColors.RED);
            deleteButton.setTextSize(13);
            deleteButton.setOnClickListener(view -> {
                animateTap(view);
                confirmDelete();
            });
            LinearLayout.LayoutParams deleteParams = new LinearLayout.LayoutParams(0, dp(34), 1);
            deleteParams.setMargins(0, dp(4), 0, dp(4));
            row.addView(deleteButton, deleteParams);
        }

        return row;
    }

    private void saveNote() {
        String title = titleInput.getText().toString().trim();
        String content = contentInput.getText().toString().trim();

        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, "Le titre et le contenu sont obligatoires", Toast.LENGTH_SHORT).show();
            return;
        }

        if (editMode) {
            currentNote.setTitle(title);
            currentNote.setContent(content);
            currentNote.setColor(selectedColor);
            currentNote.touch();
            repository.update(currentNote);
            Toast.makeText(this, "Note modifiee", Toast.LENGTH_SHORT).show();
        } else {
            repository.add(new Note(title, content, selectedColor));
            Toast.makeText(this, "Note creee", Toast.LENGTH_SHORT).show();
        }

        finishWithAnimation();
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Supprimer la note ?")
                .setMessage("Cette action est definitive.")
                .setNegativeButton("Annuler", null)
                .setPositiveButton("Supprimer", (dialog, which) -> {
                    repository.delete(currentNote.getId());
                    Toast.makeText(this, "Note supprimee", Toast.LENGTH_SHORT).show();
                    finishWithAnimation();
                })
                .show();
    }

    private void shareNote() {
        String title = titleInput.getText().toString().trim();
        String content = contentInput.getText().toString().trim();

        if (title.isEmpty() && content.isEmpty()) {
            Toast.makeText(this, "Rien a partager", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, title);
        shareIntent.putExtra(Intent.EXTRA_TEXT, title + "\n\n" + content);
        startActivity(Intent.createChooser(shareIntent, "Partager la note"));
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

    private void animateTap(View view) {
        view.animate()
                .scaleX(0.94f)
                .scaleY(0.94f)
                .setDuration(70)
                .withEndAction(() -> view.animate().scaleX(1f).scaleY(1f).setDuration(90).start())
                .start();
    }

    private void finishWithAnimation() {
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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

    private int textColor() {
        return darkMode ? AppColors.WHITE : AppColors.BLACK;
    }

    private int dp(float value) {
        return Ui.dp(this, value);
    }
}
