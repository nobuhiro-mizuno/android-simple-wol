package jp.mizuno.simplewol.ui;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import jp.mizuno.simplewol.R;

final class Ui {
    private Ui() { }

    static int dp(Context context, float value) {
        return Math.round(context.getResources().getDisplayMetrics().density * value);
    }

    static GradientDrawable background(Context context, int color, int radius, int stroke) {
        GradientDrawable shape = new GradientDrawable();
        shape.setColor(context.getColor(color));
        shape.setCornerRadius(dp(context, radius));
        if (stroke != 0) shape.setStroke(dp(context, 1), context.getColor(stroke));
        return shape;
    }

    static LinearLayout column(Context context) {
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        return layout;
    }

    static TextView text(Context context, String value, int sp, int color, boolean bold) {
        TextView view = new TextView(context);
        view.setText(value);
        view.setTextSize(sp);
        view.setTextColor(context.getColor(color));
        if (bold) view.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        return view;
    }

    static TextView button(Context context, String value, boolean filled) {
        TextView view = text(context, value, 15, filled ? R.color.surface : R.color.accent, true);
        view.setGravity(Gravity.CENTER);
        view.setMinHeight(dp(context, 48));
        view.setPadding(dp(context, 18), 0, dp(context, 18), 0);
        view.setBackground(background(context, filled ? R.color.accent : R.color.accent_soft, 14, 0));
        view.setForeground(context.getDrawable(android.R.drawable.list_selector_background));
        view.setClickable(true);
        view.setFocusable(true);
        return view;
    }

    static LinearLayout.LayoutParams lp(Context context, int width, int height, int top) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width < 0 ? width : dp(context, width), height < 0 ? height : dp(context, height));
        params.topMargin = dp(context, top);
        return params;
    }

    static void pad(Context context, View view, int horizontal, int vertical) {
        view.setPadding(dp(context, horizontal), dp(context, vertical), dp(context, horizontal), dp(context, vertical));
    }
}
