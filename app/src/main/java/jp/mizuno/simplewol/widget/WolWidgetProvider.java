package jp.mizuno.simplewol.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;
import jp.mizuno.simplewol.R;
import jp.mizuno.simplewol.model.Pc;
import jp.mizuno.simplewol.model.PcStore;
import jp.mizuno.simplewol.ui.MainActivity;

public final class WolWidgetProvider extends AppWidgetProvider {
    public enum SendState { SENDING, SENT, FAILED }
    static final String ACTION_SEND = "jp.mizuno.simplewol.SEND";
    static final String EXTRA_WIDGET_ID = "widget_id";
    private static final String PREFS = "widget_targets";

    public static void bind(Context context, int widgetId, String pcId) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(String.valueOf(widgetId), pcId).apply();
        update(context, AppWidgetManager.getInstance(context), widgetId);
    }

    public static Pc target(Context context, int widgetId) {
        String id = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(String.valueOf(widgetId), null);
        return new PcStore(context).find(id);
    }

    public static void refreshAll(Context context) {
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        int[] ids = manager.getAppWidgetIds(new ComponentName(context, WolWidgetProvider.class));
        for (int id : ids) update(context, manager, id);
    }

    public static void refresh(Context context, int widgetId) {
        update(context, AppWidgetManager.getInstance(context), widgetId);
    }

    public static void showStatus(Context context, int widgetId, SendState state) {
        update(context, AppWidgetManager.getInstance(context), widgetId, state);
    }

    private static void update(Context context, AppWidgetManager manager, int widgetId) {
        update(context, manager, widgetId, null);
    }

    private static void update(Context context, AppWidgetManager manager, int widgetId, SendState state) {
        Pc pc = target(context, widgetId);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_wol);
        int label = 0;
        int description = 0;
        int icon = R.drawable.ic_power;
        if (pc != null && state != null) {
            switch (state) {
                case SENDING:
                    label = R.string.widget_sending;
                    description = R.string.widget_sending_description;
                    break;
                case SENT:
                    label = R.string.widget_sent;
                    description = R.string.widget_sent_description;
                    icon = R.drawable.ic_check;
                    break;
                case FAILED:
                    label = R.string.widget_failed;
                    description = R.string.widget_failed_description;
                    icon = R.drawable.ic_error;
                    break;
            }
        }
        views.setTextViewText(R.id.widget_label, pc == null ? context.getString(R.string.widget_setup)
                : label == 0 ? pc.name : context.getString(label));
        views.setImageViewResource(R.id.widget_icon, icon);
        Intent intent;
        if (pc == null) {
            intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        } else {
            intent = new Intent(context, SendReceiver.class).setAction(ACTION_SEND).putExtra(EXTRA_WIDGET_ID, widgetId);
            intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        }
        PendingIntent pending = pc == null
                ? PendingIntent.getActivity(context, widgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE)
                : PendingIntent.getBroadcast(context, widgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.widget_root, pending);
        views.setContentDescription(R.id.widget_root, pc == null ? context.getString(R.string.widget_setup)
                : description == 0 ? context.getString(R.string.widget_send_description, pc.name)
                : context.getString(description, pc.name));
        manager.updateAppWidget(widgetId, views);
    }

    @Override public void onUpdate(Context context, AppWidgetManager manager, int[] ids) {
        for (int id : ids) update(context, manager, id);
    }

    @Override public void onDeleted(Context context, int[] ids) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit();
        for (int id : ids) {
            editor.remove(String.valueOf(id));
            SendReceiver.forget(id);
        }
        editor.apply();
    }
}
