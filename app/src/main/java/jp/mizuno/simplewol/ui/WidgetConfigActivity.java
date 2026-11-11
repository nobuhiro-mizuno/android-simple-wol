package jp.mizuno.simplewol.ui;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import jp.mizuno.simplewol.R;
import jp.mizuno.simplewol.model.Pc;
import jp.mizuno.simplewol.model.PcStore;
import jp.mizuno.simplewol.widget.WolWidgetProvider;
import java.util.List;

public final class WidgetConfigActivity extends Activity {
    private int widgetId;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        setResult(RESULT_CANCELED);
        widgetId = getIntent().getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        if (widgetId == AppWidgetManager.INVALID_APPWIDGET_ID) { finish(); return; }
    }

    @Override protected void onResume() {
        super.onResume();
        if (widgetId != AppWidgetManager.INVALID_APPWIDGET_ID) render();
    }

    private void render() {
        ScrollView scroll = new ScrollView(this);
        LinearLayout root = Ui.column(this);
        Ui.pad(this, root, 24, 30);
        scroll.addView(root);
        root.addView(Ui.text(this, getString(R.string.choose_pc), 28, R.color.ink, true));
        root.addView(Ui.text(this, getString(R.string.choose_pc_detail), 14, R.color.muted, false), Ui.lp(this, -1, -2, 9));
        List<Pc> pcs = new PcStore(this).all();
        if (pcs.isEmpty()) {
            TextView add = Ui.button(this, getString(R.string.add_pc), true);
            add.setOnClickListener(v -> startActivity(new Intent(this, EditPcActivity.class)));
            root.addView(add, Ui.lp(this, -1, 52, 28));
        } else {
            for (Pc pc : pcs) {
                TextView choice = Ui.text(this, pc.name + "  ›", 18, R.color.ink, true);
                Ui.pad(this, choice, 20, 18);
                choice.setBackground(Ui.background(this, R.color.surface, 18, R.color.line));
                choice.setOnClickListener(v -> {
                    WolWidgetProvider.bind(this, widgetId, pc.id);
                    Intent result = new Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
                    setResult(RESULT_OK, result);
                    finish();
                });
                root.addView(choice, Ui.lp(this, -1, -2, 12));
            }
        }
        setContentView(scroll);
    }
}
