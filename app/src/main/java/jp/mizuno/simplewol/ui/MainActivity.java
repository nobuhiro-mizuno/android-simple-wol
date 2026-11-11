package jp.mizuno.simplewol.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import jp.mizuno.simplewol.R;
import jp.mizuno.simplewol.model.Pc;
import jp.mizuno.simplewol.model.PcStore;
import jp.mizuno.simplewol.model.WolSender;
import jp.mizuno.simplewol.widget.WolWidgetProvider;
import java.io.IOException;
import java.util.List;

public final class MainActivity extends Activity {
    private PcStore store;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        store = new PcStore(this);
    }

    @Override protected void onResume() {
        super.onResume();
        render();
    }

    private void render() {
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        LinearLayout root = Ui.column(this);
        Ui.pad(this, root, 24, 28);
        scroll.addView(root);

        TextView brand = Ui.text(this, getString(R.string.brand), 12, R.color.accent, true);
        brand.setLetterSpacing(0.15f);
        root.addView(brand);
        root.addView(Ui.text(this, getString(R.string.app_name), 34, R.color.ink, true), Ui.lp(this, -1, -2, 12));
        root.addView(Ui.text(this, getString(R.string.app_subtitle), 15, R.color.muted, false), Ui.lp(this, -1, -2, 2));

        TextView add = Ui.button(this, "+  " + getString(R.string.add_pc), true);
        add.setOnClickListener(v -> startActivity(new Intent(this, EditPcActivity.class)));
        root.addView(add, Ui.lp(this, -1, 52, 30));

        List<Pc> pcs = store.all();
        if (pcs.isEmpty()) {
            LinearLayout empty = Ui.column(this);
            Ui.pad(this, empty, 22, 28);
            empty.setBackground(Ui.background(this, R.color.surface, 22, R.color.line));
            empty.addView(Ui.text(this, getString(R.string.empty_title), 19, R.color.ink, true));
            empty.addView(Ui.text(this, getString(R.string.empty_detail), 14, R.color.muted, false), Ui.lp(this, -1, -2, 10));
            root.addView(empty, Ui.lp(this, -1, -2, 26));
        } else {
            TextView section = Ui.text(this, getString(R.string.pc_settings) + "  ·  " + pcs.size(), 13, R.color.muted, true);
            root.addView(section, Ui.lp(this, -1, -2, 28));
            for (Pc pc : pcs) root.addView(pcCard(pc), Ui.lp(this, -1, -2, 12));
        }

        TextView hint = Ui.text(this, getString(R.string.widget_hint), 13, R.color.muted, false);
        hint.setGravity(Gravity.CENTER);
        root.addView(hint, Ui.lp(this, -1, -2, 28));
        setContentView(scroll);
    }

    private LinearLayout pcCard(Pc pc) {
        LinearLayout card = Ui.column(this);
        Ui.pad(this, card, 20, 20);
        card.setBackground(Ui.background(this, R.color.surface, 20, R.color.line));
        card.addView(Ui.text(this, pc.name, 20, R.color.ink, true));
        card.addView(Ui.text(this, pc.mac, 13, R.color.muted, false), Ui.lp(this, -1, -2, 5));
        card.addView(Ui.text(this, pc.broadcast + "  ·  UDP " + pc.port, 12, R.color.muted, false), Ui.lp(this, -1, -2, 3));

        TextView send = Ui.button(this, getString(R.string.send), true);
        send.setOnClickListener(v -> {
            send.setEnabled(false);
            send.setAlpha(0.6f);
            new Thread(() -> {
                boolean success;
                try { WolSender.send(pc); success = true; }
                catch (IOException | IllegalArgumentException e) { success = false; }
                boolean result = success;
                runOnUiThread(() -> {
                    send.setEnabled(true);
                    send.setAlpha(1f);
                    Toast.makeText(this, result ? R.string.sent : R.string.send_failed, Toast.LENGTH_LONG).show();
                });
            }, "wol-send").start();
        });
        card.addView(send, Ui.lp(this, -1, 48, 18));

        LinearLayout actions = new LinearLayout(this);
        TextView edit = Ui.button(this, getString(R.string.edit), false);
        edit.setOnClickListener(v -> startActivity(new Intent(this, EditPcActivity.class).putExtra(EditPcActivity.EXTRA_PC_ID, pc.id)));
        actions.addView(edit, new LinearLayout.LayoutParams(0, Ui.dp(this, 44), 1));
        TextView delete = Ui.text(this, getString(R.string.delete), 14, R.color.danger, true);
        delete.setGravity(Gravity.CENTER);
        delete.setMinWidth(Ui.dp(this, 78));
        delete.setClickable(true);
        delete.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setMessage(R.string.delete_confirm)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.delete, (dialog, which) -> new Thread(() -> {
                    try {
                        store.delete(pc.id);
                        runOnUiThread(() -> {
                            WolWidgetProvider.refreshAll(this);
                            render();
                        });
                    } catch (IllegalStateException e) {
                        runOnUiThread(() -> Toast.makeText(this, R.string.save_failed, Toast.LENGTH_LONG).show());
                    }
                }, "delete-pc").start()).show());
        actions.addView(delete);
        card.addView(actions, Ui.lp(this, -1, -2, 10));
        return card;
    }
}
