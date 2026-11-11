package jp.mizuno.simplewol.widget;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;
import jp.mizuno.simplewol.R;
import jp.mizuno.simplewol.model.Pc;
import jp.mizuno.simplewol.model.WolSender;
import java.io.IOException;

public final class SendReceiver extends BroadcastReceiver {
    private static final long RESULT_VISIBLE_MS = 3000;
    private static final WidgetSendTracker SENDS = new WidgetSendTracker();

    static void forget(int widgetId) { SENDS.forget(widgetId); }

    @Override public void onReceive(Context context, Intent intent) {
        if (!WolWidgetProvider.ACTION_SEND.equals(intent.getAction())) return;
        int id = intent.getIntExtra(WolWidgetProvider.EXTRA_WIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        Pc pc = WolWidgetProvider.target(context, id);
        if (pc == null) {
            WolWidgetProvider.refreshAll(context);
            return;
        }
        PendingResult pending = goAsync();
        Context app = context.getApplicationContext();
        long sequence = SENDS.begin(id);
        try { WolWidgetProvider.showStatus(app, id, WolWidgetProvider.SendState.SENDING); }
        catch (RuntimeException ignored) { /* Sending remains available if the launcher cannot redraw. */ }
        new Thread(() -> {
            boolean success;
            try { WolSender.send(pc); success = true; }
            catch (IOException | IllegalArgumentException e) { success = false; }
            boolean result = success;
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(() -> {
                if (!SENDS.isCurrent(id, sequence)) {
                    pending.finish();
                    return;
                }
                try {
                    WolWidgetProvider.showStatus(app, id, result ? WolWidgetProvider.SendState.SENT : WolWidgetProvider.SendState.FAILED);
                    Toast.makeText(app, result ? R.string.sent : R.string.send_failed, Toast.LENGTH_LONG).show();
                    handler.postDelayed(() -> {
                        try {
                            if (SENDS.finish(id, sequence)) WolWidgetProvider.refresh(app, id);
                        } finally {
                            pending.finish();
                        }
                    }, RESULT_VISIBLE_MS);
                } catch (RuntimeException e) {
                    SENDS.finish(id, sequence);
                    pending.finish();
                }
            });
        }, "wol-widget-send").start();
    }
}
