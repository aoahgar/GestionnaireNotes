package sn.dev.android.gestionnairenotes;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class NoteRepository {
    private static final String PREFERENCES_NAME = "notes_storage";
    private static final String KEY_NOTES = "notes";

    private final SharedPreferences preferences;

    public NoteRepository(Context context) {
        preferences = context.getApplicationContext().getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public List<Note> getAllNotes() {
        List<Note> notes = new ArrayList<>();
        String rawJson = preferences.getString(KEY_NOTES, "[]");

        try {
            JSONArray array = new JSONArray(rawJson);
            for (int index = 0; index < array.length(); index++) {
                notes.add(Note.fromJson(array.getJSONObject(index)));
            }
        } catch (JSONException ignored) {
            preferences.edit().putString(KEY_NOTES, "[]").apply();
        }

        return notes;
    }

    public Note findById(String id) {
        for (Note note : getAllNotes()) {
            if (note.getId().equals(id)) {
                return note;
            }
        }
        return null;
    }

    public void add(Note note) {
        List<Note> notes = getAllNotes();
        notes.add(note);
        saveNotes(notes);
    }

    public void update(Note updatedNote) {
        List<Note> notes = getAllNotes();
        for (int index = 0; index < notes.size(); index++) {
            if (notes.get(index).getId().equals(updatedNote.getId())) {
                notes.set(index, updatedNote);
                break;
            }
        }
        saveNotes(notes);
    }

    public void delete(String id) {
        List<Note> notes = getAllNotes();
        for (int index = notes.size() - 1; index >= 0; index--) {
            if (notes.get(index).getId().equals(id)) {
                notes.remove(index);
            }
        }
        saveNotes(notes);
    }

    public void toggleFavorite(String id) {
        List<Note> notes = getAllNotes();
        for (Note note : notes) {
            if (note.getId().equals(id)) {
                note.setFavorite(!note.isFavorite());
                note.touch();
                break;
            }
        }
        saveNotes(notes);
    }

    private void saveNotes(List<Note> notes) {
        JSONArray array = new JSONArray();
        for (Note note : notes) {
            try {
                array.put(note.toJson());
            } catch (JSONException ignored) {
                // A malformed note is skipped instead of breaking the whole storage file.
            }
        }
        preferences.edit().putString(KEY_NOTES, array.toString()).apply();
    }
}
