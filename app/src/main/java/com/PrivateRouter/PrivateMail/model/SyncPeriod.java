package com.PrivateRouter.PrivateMail.model;

public enum SyncPeriod {
    ALL_TIME(0),
    ONE_MONTH(1),
    THREE_MONTHS(3),
    SIX_MONTHS(6),
    ONE_YEAR(12);

    int month;
    SyncPeriod(int months) {
        this.month = months;
    }

    public int getMonth() {
        return month;
    }
}
