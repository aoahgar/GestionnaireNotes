package sn.dev.android.gestionnairenotes;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity {
    public static final String EXTRA_NOTE_ID = "extra_note_id";
    public static final String EXTRA_NOTE_COLOR = "extra_note_color";

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.FRENCH);

    private NoteRepository repository;
    private FrameLayout rootFrame;
    private LinearLayout contentLayout;
    private LinearLayout notesLayout;
    private LinearLayout paletteLayout;
    private ScrollView scrollView;
    private TextView emptyText;
    private TextView counterText;
    private TextView sortButton;
    private TextView addButton;
    private boolean darkMode;
    private boolean showPreview;
    private boolean showFavoritesOnly;
    private boolean paletteVisible;
    private String searchQuery = "";
    private SortMode sortMode = SortMode.DATE_DESC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        repository = new NoteRepository(this);
        buildLayout();
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean currentDarkMode = UserPreferences.isDarkMode(this);
        boolean currentShowPreview = UserPreferences.isPreviewEnabled(this);
        if (currentDarkMode != darkMode || currentShowPreview != showPreview) {
            buildLayout();
        } else {
            renderNotes();
        }
    }

    private void buildLayout() {
        darkMode = UserPreferences.isDarkMode(this);
        showPreview = UserPreferences.isPreviewEnabled(this);
        configureSystemBars();

        rootFrame = new FrameLayout(this);
        rootFrame.setBackgroundColor(pageBackground());
        setContentView(rootFrame);

        contentLayout = new LinearLayout(this);
        contentLayout.setOrientation(LinearLayout.VERTICAL);
        contentLayout.setPadding(dp(16), dp(20), dp(16), 0);
        rootFrame.addView(contentLayout, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        ));

        createSearchRow();
        createInfoRow();
        createContentArea();
        configureInsets();
        renderNotes();
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

    private void configureInsets() {
        rootFrame.setOnApplyWindowInsetsListener((view, insets) -> {
            int top = insets.getSystemWindowInsetTop();
            int bottom = insets.getSystemWindowInsetBottom();
            contentLayout.setPadding(dp(16), top + dp(12), dp(16), 0);
            if (scrollView != null) {
                scrollView.setPadding(0, 0, 0, bottom + dp(118));
            }
            updateFloatingMargins(bottom);
            return insets;
        });
        rootFrame.requestApplyInsets();
    }

    private void createSearchRow() {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        contentLayout.addView(row, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(44)
        ));

        EditText searchInput = new EditText(this);
        searchInput.setSingleLine(true);
        searchInput.setTextSize(16);
        searchInput.setGravity(Gravity.CENTER_VERTICAL);
        searchInput.setHint("Rechercher");
        searchInput.setHintTextColor(subtleTextColor());
        searchInput.setTextColor(textColor());
        searchInput.setIncludeFontPadding(false);
        searchInput.setMinHeight(0);
        searchInput.setMinimumHeight(0);
        searchInput.setPadding(dp(14), 0, dp(14), 0);
        searchInput.setBackground(Ui.roundedStroke(surfaceColor(), strokeColor(), 14, 1, this));
        searchInput.setCompoundDrawables(searchIcon(), null, null, null);
        searchInput.setCompoundDrawablePadding(dp(8));
        searchInput.setText(searchQuery);

        LinearLayout.LayoutParams searchParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
        searchParams.setMargins(0, 0, dp(8), 0);
        row.addView(searchInput, searchParams);

        ImageButton favoritesButton = iconButton(
                showFavoritesOnly ? R.drawable.ic_star_filled : R.drawable.ic_star_outline,
                "Afficher les favoris",
                showFavoritesOnly
        );
        favoritesButton.setOnClickListener(view -> {
            animateTap(view);
            showFavoritesOnly = !showFavoritesOnly;
            buildLayout();
        });
        LinearLayout.LayoutParams favoritesParams = new LinearLayout.LayoutParams(dp(44), LinearLayout.LayoutParams.MATCH_PARENT);
        favoritesParams.setMargins(0, 0, dp(8), 0);
        row.addView(favoritesButton, favoritesParams);

        ImageButton settingsButton = iconButton(R.drawable.ic_settings, "Parametres");
        settingsButton.setOnClickListener(view -> {
            animateTap(view);
            startActivity(new Intent(this, SettingsActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
        row.addView(settingsButton, new LinearLayout.LayoutParams(dp(44), dp(44)));

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence text, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {
                searchQuery = text.toString();
                renderNotes();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private void createInfoRow() {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);

        LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(40)
        );
        rowParams.setMargins(0, dp(8), 0, dp(12));
        contentLayout.addView(row, rowParams);

        counterText = new TextView(this);
        counterText.setTextColor(textColor());
        counterText.setTextSize(13);
        counterText.setSingleLine(true);
        counterText.setGravity(Gravity.CENTER_VERTICAL);
        counterText.setEllipsize(TextUtils.TruncateAt.END);
        row.addView(counterText, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1));

        sortButton = Ui.textButton(this, "Tri: " + sortMode.label, false, darkMode);
        sortButton.setTextSize(13);
        sortButton.setOnClickListener(view -> {
            animateTap(view);
            showSortMenu(view);
        });
        row.addView(sortButton, new LinearLayout.LayoutParams(dp(120), dp(34)));
    }

    private void createContentArea() {
        FrameLayout contentFrame = new FrameLayout(this);
        contentLayout.addView(contentFrame, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1
        ));

        scrollView = new ScrollView(this);
        scrollView.setFillViewport(true);
        scrollView.setClipToPadding(false);
        scrollView.setPadding(0, 0, 0, dp(118));
        notesLayout = new LinearLayout(this);
        notesLayout.setOrientation(LinearLayout.VERTICAL);
        scrollView.addView(notesLayout, new ScrollView.LayoutParams(
                ScrollView.LayoutParams.MATCH_PARENT,
                ScrollView.LayoutParams.WRAP_CONTENT
        ));
        contentFrame.addView(scrollView, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        ));

        emptyText = new TextView(this);
        emptyText.setTextColor(textColor());
        emptyText.setTextSize(17);
        emptyText.setGravity(Gravity.CENTER);
        emptyText.setAlpha(0.9f);
        contentFrame.addView(emptyText, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        ));

        paletteLayout = createPalette();
        FrameLayout.LayoutParams paletteParams = new FrameLayout.LayoutParams(
                dp(56),
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM | Gravity.END
        );
        paletteParams.setMargins(0, 0, dp(20), dp(92));
        contentFrame.addView(paletteLayout, paletteParams);

        addButton = createAddButton();
        FrameLayout.LayoutParams addParams = new FrameLayout.LayoutParams(dp(64), dp(64), Gravity.BOTTOM | Gravity.END);
        addParams.setMargins(0, 0, dp(16), dp(22));
        contentFrame.addView(addButton, addParams);
    }

    private LinearLayout createPalette() {
        LinearLayout palette = new LinearLayout(this);
        palette.setOrientation(LinearLayout.VERTICAL);
        palette.setGravity(Gravity.CENTER);
        palette.setPadding(dp(8), dp(10), dp(8), dp(10));
        palette.setBackground(Ui.rounded(darkMode ? AppColors.DARK_SURFACE : AppColors.WHITE, 18, this));
        palette.setElevation(dp(8));
        palette.setVisibility(paletteVisible ? View.VISIBLE : View.GONE);
        palette.setAlpha(paletteVisible ? 1f : 0f);
        palette.setScaleX(paletteVisible ? 1f : 0.92f);
        palette.setScaleY(paletteVisible ? 1f : 0.92f);

        for (int color : AppColors.NOTE_COLORS) {
            View dot = new View(this);
            dot.setContentDescription(AppColors.colorName(color));
            dot.setBackground(Ui.circle(color));
            LinearLayout.LayoutParams dotParams = new LinearLayout.LayoutParams(dp(24), dp(24));
            dotParams.setMargins(0, dp(7), 0, dp(7));
            palette.addView(dot, dotParams);
            dot.setOnClickListener(view -> {
                animateTap(view);
                setPaletteVisible(false);
                openCreateScreen(color);
            });
        }
        return palette;
    }

    private TextView createAddButton() {
        TextView button = new TextView(this);
        button.setText("+");
        button.setTextColor(AppColors.WHITE);
        button.setTextSize(38);
        button.setGravity(Gravity.CENTER);
        button.setIncludeFontPadding(false);
        button.setPadding(0, 0, 0, dp(4));
        button.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        button.setBackground(Ui.circle(AppColors.BLACK));
        button.setElevation(dp(10));
        button.setRotation(paletteVisible ? 45f : 0f);
        button.setOnClickListener(view -> setPaletteVisible(!paletteVisible));
        return button;
    }

    private void updateFloatingMargins(int bottomInset) {
        if (addButton != null) {
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) addButton.getLayoutParams();
            params.setMargins(0, 0, dp(16), bottomInset + dp(22));
            addButton.setLayoutParams(params);
        }
        if (paletteLayout != null) {
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) paletteLayout.getLayoutParams();
            params.setMargins(0, 0, dp(20), bottomInset + dp(92));
            paletteLayout.setLayoutParams(params);
        }
    }

    private void setPaletteVisible(boolean visible) {
        paletteVisible = visible;
        if (visible) {
            paletteLayout.setVisibility(View.VISIBLE);
            paletteLayout.setAlpha(0f);
            paletteLayout.setScaleX(0.9f);
            paletteLayout.setScaleY(0.9f);
            paletteLayout.setTranslationY(dp(14));
            paletteLayout.animate()
                    .alpha(1f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .translationY(0f)
                    .setDuration(170)
                    .start();
            addButton.animate().rotation(45f).setDuration(160).start();
        } else {
            paletteLayout.animate()
                    .alpha(0f)
                    .scaleX(0.9f)
                    .scaleY(0.9f)
                    .translationY(dp(14))
                    .setDuration(130)
                    .withEndAction(() -> paletteLayout.setVisibility(View.GONE))
                    .start();
            addButton.animate().rotation(0f).setDuration(160).start();
        }
    }

    private void renderNotes() {
        if (notesLayout == null || counterText == null) {
            return;
        }

        List<Note> allNotes = repository.getAllNotes();
        List<Note> visibleNotes = filterAndSort(allNotes);

        counterText.setText(formatCounter(allNotes));
        if (sortButton != null) {
            sortButton.setText("Tri: " + sortMode.label);
        }

        notesLayout.removeAllViews();
        emptyText.setVisibility(visibleNotes.isEmpty() ? View.VISIBLE : View.GONE);
        emptyText.setText(emptyMessage(allNotes));

        for (int index = 0; index < visibleNotes.size(); index++) {
            View card = createNoteCard(visibleNotes.get(index));
            notesLayout.addView(card);
            card.setAlpha(0f);
            card.setTranslationY(dp(10));
            card.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setStartDelay(Math.min(index, 6) * 26L)
                    .setDuration(150)
                    .start();
        }
    }

    private List<Note> filterAndSort(List<Note> notes) {
        List<Note> filtered = new ArrayList<>();
        String normalizedSearch = searchQuery.trim().toLowerCase(Locale.ROOT);

        for (Note note : notes) {
            boolean matchesTitle = normalizedSearch.isEmpty()
                    || note.getTitle().toLowerCase(Locale.ROOT).contains(normalizedSearch);
            boolean matchesFavorite = !showFavoritesOnly || note.isFavorite();
            if (matchesTitle && matchesFavorite) {
                filtered.add(note);
            }
        }

        Collections.sort(filtered, comparatorForCurrentSort());
        return filtered;
    }

    private Comparator<Note> comparatorForCurrentSort() {
        if (sortMode == SortMode.TITLE) {
            return (first, second) -> first.getTitle().compareToIgnoreCase(second.getTitle());
        }
        if (sortMode == SortMode.COLOR) {
            return (first, second) -> AppColors.colorName(first.getColor()).compareTo(AppColors.colorName(second.getColor()));
        }
        if (sortMode == SortMode.FAVORITES) {
            return (first, second) -> {
                if (first.isFavorite() != second.isFavorite()) {
                    return first.isFavorite() ? -1 : 1;
                }
                return Long.compare(second.getUpdatedAt(), first.getUpdatedAt());
            };
        }
        return (first, second) -> Long.compare(second.getUpdatedAt(), first.getUpdatedAt());
    }

    private View createNoteCard(Note note) {
        FrameLayout card = new FrameLayout(this) {
            @Override
            public boolean performClick() {
                super.performClick();
                return true;
            }
        };
        card.setPadding(dp(14), dp(10), dp(14), dp(10));
        card.setMinimumHeight(dp(78));
        card.setBackground(Ui.rounded(note.getColor(), 8, this));
        card.setClickable(true);
        card.setElevation(dp(2));

        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(0, 0, 0, dp(10));
        card.setLayoutParams(cardParams);

        LinearLayout textColumn = new LinearLayout(this);
        textColumn.setOrientation(LinearLayout.VERTICAL);
        textColumn.setGravity(Gravity.CENTER_VERTICAL);
        FrameLayout.LayoutParams textParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER_VERTICAL
        );
        textParams.setMargins(0, 0, note.isFavorite() ? dp(36) : 0, 0);
        card.addView(textColumn, textParams);

        TextView title = new TextView(this);
        title.setText(note.getTitle());
        title.setTextColor(AppColors.WHITE);
        title.setTextSize(23);
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        title.setSingleLine(true);
        title.setEllipsize(TextUtils.TruncateAt.END);
        textColumn.addView(title, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        TextView date = new TextView(this);
        date.setText(formatDate(note.getUpdatedAt()));
        date.setTextColor(AppColors.WHITE);
        date.setAlpha(0.95f);
        date.setTextSize(12);
        textColumn.addView(date, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        if (showPreview && !note.getContent().trim().isEmpty()) {
            TextView preview = new TextView(this);
            preview.setText(note.getContent().trim().replace('\n', ' '));
            preview.setTextColor(AppColors.WHITE);
            preview.setAlpha(0.78f);
            preview.setTextSize(12);
            preview.setSingleLine(true);
            preview.setEllipsize(TextUtils.TruncateAt.END);
            LinearLayout.LayoutParams previewParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            previewParams.setMargins(0, dp(2), 0, 0);
            textColumn.addView(preview, previewParams);
        }

        if (note.isFavorite()) {
            View badge = favoriteBadge();
            FrameLayout.LayoutParams badgeParams = new FrameLayout.LayoutParams(dp(32), dp(32), Gravity.TOP | Gravity.END);
            badgeParams.setMargins(0, dp(12), dp(12), 0);
            card.addView(badge, badgeParams);
        }

        GestureDetector detector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent event) {
                openEditScreen(note.getId());
                return true;
            }

            @Override
            public boolean onDoubleTap(MotionEvent event) {
                repository.toggleFavorite(note.getId());
                Toast.makeText(MainActivity.this, "Favori mis a jour", Toast.LENGTH_SHORT).show();
                renderNotes();
                return true;
            }
        });

        card.setOnTouchListener((view, event) -> {
            detector.onTouchEvent(event);
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                view.animate().scaleX(0.985f).scaleY(0.985f).setDuration(70).start();
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                view.animate().scaleX(1f).scaleY(1f).setDuration(90).start();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    view.performClick();
                }
            }
            return true;
        });

        return card;
    }

    private View favoriteBadge() {
        FrameLayout badge = new FrameLayout(this);
        badge.setBackground(Ui.circle(Color.argb(235, 255, 255, 255)));
        badge.setElevation(dp(4));

        ImageView icon = new ImageView(this);
        icon.setImageResource(R.drawable.ic_star_filled);
        icon.setColorFilter(AppColors.YELLOW);
        icon.setPadding(dp(5), dp(5), dp(5), dp(5));
        badge.addView(icon, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT,
                Gravity.CENTER
        ));
        return badge;
    }

    private void showSortMenu(View anchor) {
        PopupMenu popupMenu = new PopupMenu(this, anchor);
        for (SortMode mode : SortMode.values()) {
            popupMenu.getMenu().add(0, mode.ordinal(), mode.ordinal(), mode.menuLabel);
        }
        popupMenu.setOnMenuItemClickListener(item -> onSortMenuItemClicked(item));
        popupMenu.show();
    }

    private boolean onSortMenuItemClicked(MenuItem item) {
        int itemId = item.getItemId();
        for (SortMode mode : SortMode.values()) {
            if (mode.ordinal() == itemId) {
                sortMode = mode;
                renderNotes();
                return true;
            }
        }
        return false;
    }

    private String formatCounter(List<Note> notes) {
        int favoriteCount = 0;
        for (Note note : notes) {
            if (note.isFavorite()) {
                favoriteCount++;
            }
        }

        String noteLabel = notes.size() > 1 ? "notes" : "note";
        String favoriteLabel = favoriteCount > 1 ? "favoris" : "favori";
        return notes.size() + " " + noteLabel + " | " + favoriteCount + " " + favoriteLabel;
    }

    private String emptyMessage(List<Note> allNotes) {
        if (allNotes.isEmpty()) {
            return "Aucune notes";
        }
        if (showFavoritesOnly) {
            return "Aucune note favorite";
        }
        return "Aucun resultat";
    }

    private String formatDate(long timestamp) {
        String formatted = dateFormat.format(new Date(timestamp));
        if (formatted.isEmpty()) {
            return formatted;
        }
        return formatted.substring(0, 1).toUpperCase(Locale.FRENCH) + formatted.substring(1);
    }

    private void openCreateScreen(int color) {
        Intent intent = new Intent(this, NoteEditorActivity.class);
        intent.putExtra(EXTRA_NOTE_COLOR, color);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void openEditScreen(String noteId) {
        Intent intent = new Intent(this, NoteEditorActivity.class);
        intent.putExtra(EXTRA_NOTE_ID, noteId);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private ImageButton iconButton(int drawableRes, String description) {
        return iconButton(drawableRes, description, false);
    }

    private ImageButton iconButton(int drawableRes, String description, boolean selected) {
        ImageButton button = new ImageButton(this);
        button.setContentDescription(description);
        button.setImageResource(drawableRes);
        button.setColorFilter(selected ? AppColors.YELLOW : textColor());
        button.setPadding(dp(10), dp(10), dp(10), dp(10));
        button.setScaleType(ImageButton.ScaleType.CENTER);
        button.setBackground(Ui.roundedStroke(selected ? AppColors.BLACK : surfaceColor(), strokeColor(), 22, 1, this));
        return button;
    }

    private Drawable searchIcon() {
        Drawable drawable = getDrawable(R.drawable.ic_search);
        if (drawable != null) {
            drawable.setTint(subtleTextColor());
            drawable.setBounds(0, 0, dp(18), dp(18));
        }
        return drawable;
    }

    private void animateTap(View view) {
        view.animate()
                .scaleX(0.94f)
                .scaleY(0.94f)
                .setDuration(70)
                .withEndAction(() -> view.animate().scaleX(1f).scaleY(1f).setDuration(90).start())
                .start();
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

    private int subtleTextColor() {
        return darkMode ? AppColors.LIGHT_STROKE : AppColors.GREY;
    }

    private int dp(float value) {
        return Ui.dp(this, value);
    }

    private enum SortMode {
        DATE_DESC("Date", "Plus recent"),
        TITLE("Titre", "Titre A-Z"),
        COLOR("Couleur", "Couleur"),
        FAVORITES("Favoris", "Favoris d'abord");

        private final String label;
        private final String menuLabel;

        SortMode(String label, String menuLabel) {
            this.label = label;
            this.menuLabel = menuLabel;
        }
    }
}
