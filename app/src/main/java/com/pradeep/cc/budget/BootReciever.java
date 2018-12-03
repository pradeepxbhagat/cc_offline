package com.pradeep.cc.budget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReciever extends BroadcastReceiver {
    private BudgetManager.BudgetAlarm budgetAlarm;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            if (budgetAlarm == null) {
                budgetAlarm = new BudgetManager.BudgetAlarm(context);
            }
            budgetAlarm.set();
        }

    }
}
