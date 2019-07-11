package com.PrivateRouter.PrivateMail.view.utils;

import android.content.Context;

import com.PrivateRouter.PrivateMail.R;
import com.PrivateRouter.PrivateMail.repository.SettingsRepository;

import java.text.SimpleDateFormat;
import java.time.Year;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    public static String getShortDate(Context context, long dateLong) {

        Date date = new Date(dateLong*1000);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        Date currentDayStart = calendar.getTime();

        calendar.add(Calendar.DAY_OF_YEAR, -1);
        Date yesterdayStart = calendar.getTime();

        calendar.add(Calendar.DAY_OF_YEAR, 1);
        calendar.set( calendar.get(Calendar.YEAR), 0, 1);
        Date currentYearStart = calendar.getTime();

        SimpleDateFormat simpleDateFormat;
        if (SettingsRepository.getInstance().isTime24Format(context)) {
            simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.US);
        }
        else {
            simpleDateFormat = new SimpleDateFormat("h:mm aa", Locale.US);
        }

        String str = "";
        if (date.after(currentDayStart))
            str = simpleDateFormat.format(date);
        else if (date.after(yesterdayStart))
            str = context.getString(R.string.all_yesterday) +" "+ simpleDateFormat.format(date);
        else if (date.after(currentYearStart))
            str = new SimpleDateFormat("dd MMMM", Locale.US).format(date);
        else
            str = new SimpleDateFormat("dd MMMM yyyy", Locale.US).format(date);

        return str;
    }


    public static String getFullDate(Context context, long dateLong) {
        SimpleDateFormat simpleDateFormat;
        if (SettingsRepository.getInstance().isTime24Format(context)) {
            simpleDateFormat = new SimpleDateFormat("EEE, MMM d, yyyy, HH:mm", Locale.US);
        }
        else {
            simpleDateFormat = new SimpleDateFormat("EEE, MMM d, yyyy, h:mm aa", Locale.US);
        }
        return  simpleDateFormat.format(  new Date(dateLong*1000)  );
    }
}
