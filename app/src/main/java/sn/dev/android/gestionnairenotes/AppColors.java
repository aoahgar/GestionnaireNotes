package sn.dev.android.gestionnairenotes;

import android.graphics.Color;

public final class AppColors {
    public static final int BLACK = Color.rgb(0, 0, 0);
    public static final int WHITE = Color.rgb(255, 255, 255);
    public static final int GREY = Color.rgb(130, 130, 130);
    public static final int GREEN = Color.rgb(33, 150, 83);
    public static final int RED = Color.rgb(235, 87, 87);
    public static final int BLUE = Color.rgb(47, 128, 237);
    public static final int YELLOW = Color.rgb(242, 201, 76);
    public static final int ORANGE = Color.rgb(242, 153, 74);

    public static final int DARK_BACKGROUND = Color.rgb(18, 18, 18);
    public static final int DARK_SURFACE = Color.rgb(32, 32, 32);
    public static final int LIGHT_STROKE = Color.rgb(220, 220, 220);

    public static final int[] NOTE_COLORS = {
            GREEN,
            RED,
            BLUE,
            YELLOW,
            ORANGE,
            GREY
    };

    private AppColors() {
    }

    public static String colorName(int color) {
        if (color == GREEN) {
            return "Vert";
        }
        if (color == RED) {
            return "Rouge";
        }
        if (color == BLUE) {
            return "Bleu";
        }
        if (color == YELLOW) {
            return "Jaune";
        }
        if (color == ORANGE) {
            return "Orange";
        }
        if (color == GREY) {
            return "Gris";
        }
        return "Couleur";
    }
}
