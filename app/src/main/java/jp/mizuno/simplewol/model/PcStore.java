package jp.mizuno.simplewol.model;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public final class PcStore {
    private static final Object LOCK = new Object();
    private static final String PREFS = "pc_settings";
    private static final String KEY = "pcs";
    private final SharedPreferences preferences;

    public PcStore(Context context) {
        preferences = context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public List<Pc> all() {
        synchronized (LOCK) { return read(); }
    }

    private List<Pc> read() {
        List<Pc> result = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(preferences.getString(KEY, "[]"));
            for (int i = 0; i < array.length(); i++) {
                JSONObject item = array.getJSONObject(i);
                try {
                    result.add(new Pc(item.getString("id"), WolInput.name(item.getString("name")),
                            WolInput.mac(item.getString("mac")), WolInput.broadcast(item.getString("broadcast")),
                            WolInput.port(item.getString("port"))));
                } catch (IllegalArgumentException | JSONException ignored) {
                    // Ignore a damaged entry while keeping the other PCs available.
                }
            }
        } catch (JSONException ignored) {
            // A damaged JSON document is treated as an empty collection.
        }
        return result;
    }

    public Pc find(String id) {
        if (id == null) return null;
        for (Pc pc : all()) if (pc.id.equals(id)) return pc;
        return null;
    }

    public void save(Pc pc) {
        synchronized (LOCK) {
            List<Pc> pcs = read();
            for (int i = 0; i < pcs.size(); i++) {
                if (pcs.get(i).id.equals(pc.id)) {
                    pcs.set(i, pc);
                    write(pcs);
                    return;
                }
            }
            pcs.add(pc);
            write(pcs);
        }
    }

    public void delete(String id) {
        synchronized (LOCK) {
            List<Pc> pcs = read();
            pcs.removeIf(pc -> pc.id.equals(id));
            write(pcs);
        }
    }

    private void write(List<Pc> pcs) {
        JSONArray array = new JSONArray();
        for (Pc pc : pcs) {
            JSONObject item = new JSONObject();
            try {
                item.put("id", pc.id);
                item.put("name", pc.name);
                item.put("mac", pc.mac);
                item.put("broadcast", pc.broadcast);
                item.put("port", pc.port);
                array.put(item);
            } catch (JSONException e) {
                throw new IllegalStateException(e);
            }
        }
        if (!preferences.edit().putString(KEY, array.toString()).commit()) {
            throw new IllegalStateException("Failed to save PC settings");
        }
    }
}
