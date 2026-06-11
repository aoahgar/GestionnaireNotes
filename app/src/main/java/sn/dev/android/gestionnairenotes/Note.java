package sn.dev.android.gestionnairenotes;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

public class Note {
    private static final String JSON_ID = "id";
    private static final String JSON_TITLE = "title";
    private static final String JSON_CONTENT = "content";
    private static final String JSON_COLOR = "color";
    private static final String JSON_FAVORITE = "favorite";
    private static final String JSON_CREATED_AT = "createdAt";
    private static final String JSON_UPDATED_AT = "updatedAt";

    private final String id;
    private String title;
    private String content;
    private int color;
    private boolean favorite;
    private final long createdAt;
    private long updatedAt;

    public Note(String title, String content, int color) {
        long now = System.currentTimeMillis();
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.content = content;
        this.color = color;
        this.favorite = false;
        this.createdAt = now;
        this.updatedAt = now;
    }

    private Note(String id, String title, String content, int color, boolean favorite, long createdAt, long updatedAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.color = color;
        this.favorite = favorite;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Note fromJson(JSONObject object) throws JSONException {
        return new Note(
                object.getString(JSON_ID),
                object.getString(JSON_TITLE),
                object.getString(JSON_CONTENT),
                object.getInt(JSON_COLOR),
                object.optBoolean(JSON_FAVORITE, false),
                object.optLong(JSON_CREATED_AT, System.currentTimeMillis()),
                object.optLong(JSON_UPDATED_AT, System.currentTimeMillis())
        );
    }

    public JSONObject toJson() throws JSONException {
        JSONObject object = new JSONObject();
        object.put(JSON_ID, id);
        object.put(JSON_TITLE, title);
        object.put(JSON_CONTENT, content);
        object.put(JSON_COLOR, color);
        object.put(JSON_FAVORITE, favorite);
        object.put(JSON_CREATED_AT, createdAt);
        object.put(JSON_UPDATED_AT, updatedAt);
        return object;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void touch() {
        updatedAt = System.currentTimeMillis();
    }
}
