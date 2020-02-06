package com.PrivateRouter.PrivateMail.view.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.PrivateRouter.PrivateMail.R;
import com.PrivateRouter.PrivateMail.repository.SettingsRepository;

import java.text.DecimalFormat;

public class Utils {

    public static DisplayMetrics getDeviceMetrics(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        display.getMetrics(metrics);
        return metrics;
    }

    public static int getDP(Context context, int value) {
        final float scale = context.getResources().getDisplayMetrics().density;
        int pixels = (int) (value * scale + 0.5f);
        return pixels;
    }

    private static final DecimalFormat format = new DecimalFormat("#.#");
    private static final long GiB = 1024 * 1024 * 1024;
    private static final long MiB = 1024 * 1024;
    private static final long KiB = 1024;

    public static String getSizeText(Context context, int length) {
        if (length > GiB) {
            return format.format(length / GiB) + " " + context.getString(R.string.all_gb);
        }
        if (length > MiB) {
            return format.format(length / MiB) + " " + context.getString(R.string.all_mb);
        }
        if (length > KiB) {
            return format.format(length / KiB) + " " + context.getString(R.string.all_kb);
        }
        return format.format(length) + " " + context.getString(R.string.all_b);
    }

    public static void updateBottomMenuColors(BottomNavigationView bottomNavigationView) {


        Context context = bottomNavigationView.getContext();
        int color = SettingsRepository.getInstance().isNightMode(context) ? R.color.color_white : R.color.color_black;

        int colorChecked = context.getResources().getColor(color);

        ColorStateList iconsColorStates = new ColorStateList(
                new int[][]{
                        new int[]{-android.R.attr.state_checked},
                        new int[]{android.R.attr.state_checked}
                },
                new int[]{
                        colorChecked,
                        colorChecked
                });


        ColorStateList textColorStates = new ColorStateList(
                new int[][]{
                        new int[]{-android.R.attr.state_checked},
                        new int[]{android.R.attr.state_checked}
                },
                new int[]{
                        colorChecked,
                        colorChecked
                });

        bottomNavigationView.setItemIconTintList(iconsColorStates);
        bottomNavigationView.setItemTextColor(textColorStates);
    }
}
