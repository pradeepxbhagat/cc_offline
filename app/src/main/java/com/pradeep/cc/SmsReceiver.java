package com.pradeep.cc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsMessage;
import android.util.Log;

import com.pradeep.cc.budget.BudgetManager;
import com.pradeep.cc.parser.SmsParser;
import com.pradeep.cc.transaction.TransactionManager;
import com.pradeep.cc.util.PersistentStore;

public class SmsReceiver extends BroadcastReceiver {
    static final String CC_MESSAGE_RECEIVED = "CC_MESSAGE_RECEIVED";
    private SmsParser smsParser;
    private TransactionManager transactionManager;
    private BudgetManager budgetManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle data = intent.getExtras();
        Object[] pdus = (Object[]) data.get("pdus");

        for (int i = 0; i < pdus.length; i++) {
            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);

            String sender = smsMessage.getDisplayOriginatingAddress();
            if (sender.contains("HDFC")) {
                String messageBody = smsMessage.getMessageBody();
                readSms(context);
                Log.i("@@@", messageBody);
//                Toast.makeText(context, messageBody, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void readSms(Context context) {
        if (transactionManager == null) {
            transactionManager = new TransactionManager(context);
        }
        transactionManager.insertAllTransactions("HDFC", PersistentStore.getStore().getLastAccessedTime(context));
        PersistentStore.getStore().putLastAccessedTime(context, String.valueOf(System.currentTimeMillis()));
        if (budgetManager == null) {
            budgetManager = new BudgetManager(transactionManager);
        } else {
            budgetManager.setTransactionManager(transactionManager);
        }

        budgetManager.checkBudget(context);
        broadcastNewMessage(context);
    }

    private void broadcastNewMessage(Context context) {
        Intent intent = new Intent(CC_MESSAGE_RECEIVED);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

}
