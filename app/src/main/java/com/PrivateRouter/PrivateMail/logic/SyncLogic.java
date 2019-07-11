package com.PrivateRouter.PrivateMail.logic;

import android.os.Handler;

import com.PrivateRouter.PrivateMail.PrivateMailApplication;
import com.PrivateRouter.PrivateMail.model.SyncFrequency;
import com.PrivateRouter.PrivateMail.repository.SettingsRepository;

import java.util.Date;

public class SyncLogic {
    final Handler handlerTimer = new Handler();

    public void pause() {
        handlerTimer.removeCallbacksAndMessages(null);
    }

    private Runnable requestMessagesRunnable;

    public  void updateTimer() {
        handlerTimer.removeCallbacksAndMessages(null);
        int sync = SettingsRepository.getInstance().getSyncFrequency(PrivateMailApplication.getContext());
        SyncFrequency syncFrequency = SyncFrequency.values()[sync];
        if (syncFrequency.getMinutes() > 0 ) {
            long lastSyncDate = SettingsRepository.getInstance().getLastSyncDate(PrivateMailApplication.getContext());
            long nowTime = new Date().getTime();
            if (lastSyncDate==0)
                lastSyncDate = nowTime;


            long time = lastSyncDate + syncFrequency.getMinutes() *60*1000 - nowTime;
            if (time<=0)
                requestMessages();
            else {
                handlerTimer.postDelayed(() -> requestMessages(), time);
            }
        }
    }

    private void requestMessages() {
        if (requestMessagesRunnable!=null)
            requestMessagesRunnable.run();
    }

    public Runnable getRequestMessagesRunnable() {
        return requestMessagesRunnable;
    }

    public void setRequestMessagesRunnable(Runnable requestMessagesRunnable) {
        this.requestMessagesRunnable = requestMessagesRunnable;
    }
}
