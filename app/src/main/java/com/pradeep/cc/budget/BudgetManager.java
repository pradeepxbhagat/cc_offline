package com.pradeep.cc.budget;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.pradeep.cc.MainActivity;
import com.pradeep.cc.R;
import com.pradeep.cc.transaction.Transaction;
import com.pradeep.cc.transaction.TransactionManager;
import com.pradeep.cc.transaction.Transactions;
import com.pradeep.cc.util.Date;

import java.util.Calendar;

public class BudgetManager {
    private static final String CHANNEL_ID = "com.pradeep.cc.SmsReceiver.CHANNEL_ID";

    public void setTransactionManager(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    private TransactionManager transactionManager;

    public BudgetManager(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public void showDailyNotification(Context context) {
        Transactions transactions = transactionManager.getTransactionsFromDay(String.valueOf(Date.getTodaysDate().getZeroHourTimeStamp()), Date.getTodaysDate().toString());
        if (transactions != null && transactions.getTransactions().size() > 0) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            int billingCycleDay = Integer.parseInt(prefs.getString(context.getString(R.string.key_billing_cycle_date), "1"));
            float budgetAmount = Float.parseFloat(prefs.getString(context.getString(R.string.key_budget_amount), "0"));
            double total = transactionManager.getTransactionTotalForBillingCycle(billingCycleDay);
            double todaySpentAmount = (double) transactions.getExtraInformation(Transactions.TOTAL_AMOUNT);

            String message = "Today you have spent Rs." + todaySpentAmount + ", total amount spent for bill cycle is Rs." + total
                    + ", your budget amount is Rs." + budgetAmount;
            showNotification(context, message);
        }
    }

    public void checkBudget(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        int billingCycleDay = Integer.parseInt(prefs.getString(context.getString(R.string.key_billing_cycle_date), "1"));
        float budgetAmount = Float.parseFloat(prefs.getString(context.getString(R.string.key_budget_amount), "0"));
        double total = transactionManager.getTransactionTotalForBillingCycle(billingCycleDay);
        Log.i("@@@", "Total amount: " + total);

        String message = null;
        if (budgetAmount == total) {
            message = "You have reached your budget which is Rs." + budgetAmount;
        }

        if (budgetAmount < total) {
            message = "You have exceeded your budget by Rs." + (total - budgetAmount);
        }

        if (message != null) {
            message += ". Your total expenditure is Rs." + total + " and your budget set is Rs." + budgetAmount;
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            showNotification(context, message);
        }
    }

    private void showNotification(Context context, String message) {
        createNotificationChannel(context);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_action_date_range)
                .setContentTitle("Credit card budget alert")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message));

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pi);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, mBuilder.build());
    }


    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.channel_name);
            String description = context.getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static class BudgetAlarm {
        private final AlarmManager alarmManager;
        private final PendingIntent pendingIntent;

        public BudgetAlarm(Context context) {
            alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, BudgetBroadcastReceiver.class);
            pendingIntent = PendingIntent.getBroadcast(context, BudgetBroadcastReceiver.REQUEST_CODE, intent, 0);
        }

        public void set() {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);

            if (Calendar.getInstance().after(cal)) {
                cal.add(Calendar.DAY_OF_MONTH, 1);
            }

            alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    cal.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent);
           /* long currentTime = System.currentTimeMillis();
            long oneMinute = 60 * 1000;

            alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    currentTime + oneMinute,
                    oneMinute,
                    pendingIntent);*/
        }

        public void cancel(){
            alarmManager.cancel(pendingIntent);
        }
    }
}
