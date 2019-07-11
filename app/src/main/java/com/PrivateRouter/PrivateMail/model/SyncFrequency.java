package com.PrivateRouter.PrivateMail.model;

public enum SyncFrequency {
    NEVER(0),
    ONE_MIN(1),
    FIVE_MIN(5),
    ONE_HOUR(60),
    TWO_HOUR(2*60),
    DAILY(24*60),
    MOUNTHLY(30*24*60);

    int minutes;
    SyncFrequency(int months) {
        this.minutes = months;
    }

    public int getMinutes() {
        return minutes;
    }

}
