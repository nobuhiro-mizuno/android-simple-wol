package jp.mizuno.simplewol.ui;

import android.app.Activity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import jp.mizuno.simplewol.R;
import jp.mizuno.simplewol.model.Pc;
import jp.mizuno.simplewol.model.PcStore;
import jp.mizuno.simplewol.model.WolInput;
import jp.mizuno.simplewol.widget.WolWidgetProvider;
import java.util.UUID;

public final class EditPcActivity extends Activity {
    public static final String EXTRA_PC_ID = "pc_id";
    private EditText name, mac, broadcast, port;
    private Pc original;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        original = new PcStore(this).find(getIntent().getStringExtra(EXTRA_PC_ID));
        render();
    }

    private void render() {
        ScrollView scroll = new ScrollView(this);
        LinearLayout root = Ui.column(this);
        Ui.pad(this, root, 24, 26);
        scroll.addView(root);

        TextView back = Ui.text(this, "‹  " + getString(R.string.back), 15, R.color.accent, true);
        back.setGravity(Gravity.CENTER_VERTICAL);
        back.setMinHeight(Ui.dp(this, 44));
        back.setOnClickListener(v -> finish());
        root.addView(back);
        root.addView(Ui.text(this, getString(original == null ? R.string.new_pc : R.string.edit_pc), 29, R.color.ink, true), Ui.lp(this, -1, -2, 22));
        root.addView(Ui.text(this, getString(R.string.form_intro), 14, R.color.muted, false), Ui.lp(this, -1, -2, 6));

        LinearLayout form = Ui.column(this);
        Ui.pad(this, form, 20, 22);
        form.setBackground(Ui.background(this, R.color.surface, 20, R.color.line));
        name = field(form, R.string.pc_name, original == null ? "" : original.name, InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES, 0);
        mac = field(form, R.string.mac_address, original == null ? "" : original.mac, InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS, 18);
        broadcast = field(form, R.string.broadcast_address, original == null ? "255.255.255.255" : original.broadcast, InputType.TYPE_CLASS_PHONE, 18);
        port = field(form, R.string.udp_port, original == null ? "9" : String.valueOf(original.port), InputType.TYPE_CLASS_NUMBER, 18);
        root.addView(form, Ui.lp(this, -1, -2, 24));
        root.addView(Ui.text(this, getString(R.string.network_hint), 13, R.color.muted, false), Ui.lp(this, -1, -2, 14));

        TextView save = Ui.button(this, getString(R.string.save), true);
        save.setOnClickListener(v -> save());
        root.addView(save, Ui.lp(this, -1, 54, 26));
        setContentView(scroll);
    }

    private EditText field(LinearLayout parent, int label, String value, int inputType, int top) {
        parent.addView(Ui.text(this, getString(label), 13, R.color.ink, true), Ui.lp(this, -1, -2, top));
        EditText edit = new EditText(this);
        edit.setSingleLine(true);
        edit.setTextSize(16);
        edit.setTextColor(getColor(R.color.ink));
        edit.setHintTextColor(getColor(R.color.muted));
        edit.setInputType(inputType);
        edit.setText(value);
        edit.setSelectAllOnFocus(true);
        parent.addView(edit, Ui.lp(this, -1, 50, 4));
        return edit;
    }

    private void save() {
        String checkedName = check(name, () -> WolInput.name(name.getText().toString()), R.string.invalid_name);
        String checkedMac = check(mac, () -> WolInput.mac(mac.getText().toString()), R.string.invalid_mac);
        String checkedBroadcast = check(broadcast, () -> WolInput.broadcast(broadcast.getText().toString()), R.string.invalid_ip);
        String checkedPort = check(port, () -> String.valueOf(WolInput.port(port.getText().toString())), R.string.invalid_port);
        if (checkedName == null || checkedMac == null || checkedBroadcast == null || checkedPort == null) return;
        Pc pc = new Pc(original == null ? UUID.randomUUID().toString() : original.id,
                checkedName, checkedMac, checkedBroadcast, Integer.parseInt(checkedPort));
        new Thread(() -> {
            boolean success;
            try { new PcStore(this).save(pc); success = true; }
            catch (IllegalStateException e) { success = false; }
            boolean result = success;
            runOnUiThread(() -> {
                if (result) {
                    WolWidgetProvider.refreshAll(this);
                    finish();
                } else {
                    Toast.makeText(this, R.string.save_failed, Toast.LENGTH_LONG).show();
                }
            });
        }, "save-pc").start();
    }

    private String check(EditText field, Value value, int error) {
        try {
            String result = value.get();
            field.setError(null);
            return result;
        } catch (IllegalArgumentException e) {
            field.setError(getString(error));
            return null;
        }
    }

    private interface Value { String get(); }
}
