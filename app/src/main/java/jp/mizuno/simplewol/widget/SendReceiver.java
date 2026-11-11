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
        new Thread(() -> {
            boolean success;
            try { WolSender.send(pc); success = true; }
            catch (IOException | IllegalArgumentException e) { success = false; }
            boolean result = success;
            new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(app,
                    result ? R.string.sent : R.string.send_failed, Toast.LENGTH_LONG).show());
            pending.finish();
        }, "wol-widget-send").start();
    }
}
