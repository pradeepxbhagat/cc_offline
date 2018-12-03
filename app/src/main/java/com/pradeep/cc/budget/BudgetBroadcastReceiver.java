package com.pradeep.cc.budget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.pradeep.cc.transaction.TransactionManager;

public class BudgetBroadcastReceiver extends BroadcastReceiver {
    static final int REQUEST_CODE = 1399;
    private TransactionManager transactionManager;
    private BudgetManager budgetManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "This is testing", Toast.LENGTH_LONG).show();
        if (transactionManager == null) {
            transactionManager = new TransactionManager(context);
        }

        if (budgetManager == null) {
            budgetManager = new BudgetManager(transactionManager);
        } else {
            budgetManager.setTransactionManager(transactionManager);
        }

        budgetManager.showDailyNotification(context);
    }
}
