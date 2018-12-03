package com.pradeep.cc.transaction;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.pradeep.cc.ParserFactory;
import com.pradeep.cc.parser.SmsParser;
import com.pradeep.cc.util.CCDatabaseHelper;
import com.pradeep.cc.util.Date;

import java.util.List;

public class TransactionManager {

    private final TransactionTableHelper transactionTableHelper;
    private final CCDatabaseHelper databaseHelper;
    private final Context context;

    public TransactionManager(Context context) {
        this.context = context;
        transactionTableHelper = new TransactionTableHelper();
        databaseHelper = new CCDatabaseHelper(context);
    }

    public void insertAllTransactions(String sender, String timeStamp) {
        Transactions transactions = getAllTransaction(sender, timeStamp);
        if (transactions != null) {
            List<Transaction> transactionList = transactions.getTransactions();
            if (transactionList != null && transactionList.size() > 0) {
                transactionTableHelper.insertTransactions(databaseHelper.getWritableDatabase(), transactions);
            }
        } else {
            Log.i("@@@", "No transactions");
        }
    }

    private Transactions getAllTransaction(String sender, String timeStamp) {
        final String SMS_URI_INBOX = "content://sms/inbox";
        try {
            Uri uri = Uri.parse(SMS_URI_INBOX);
            String[] projection = new String[]{"_id", "body", "date", "address"};
            String selection = "address like '%" + sender + "%'";
            if (timeStamp != null) {
                selection += " and date >= " + timeStamp;
            }
            Cursor cur = context.getContentResolver().query(uri, projection, selection, null, "date desc");
            if (cur != null && cur.moveToFirst()) {
                Transactions transactions = new Transactions();
                do {
                    String id = cur.getString(cur.getColumnIndex("_id"));
                    String strbody = cur.getString(cur.getColumnIndex("body"));
                    long longDate = cur.getLong(cur.getColumnIndex("date"));
                    Transaction transaction = new Transaction(id, String.valueOf(longDate), strbody);
                    SmsParser smsParser = ParserFactory.getParser(sender, strbody);
                    smsParser.parse(transaction);
                    if (transaction != null) {
                        Log.i("@@@", transaction.toString());
                        transactions.addTransaction(transaction);
                    }
                } while (cur.moveToNext());

                if (!cur.isClosed()) {
                    cur.close();
                }
                return transactions;
            }
        } catch (SQLiteException ex) {
            Log.d("SQLiteException", ex.getMessage());
        }
        return null;
    }

    Transactions getTransactionsByTime() {
        return transactionTableHelper.getTransactionsSortByTime(databaseHelper.getReadableDatabase());
    }

    public Transactions getTransactionsForBillingCycle(int day) {
        Date[] dates = getBillingCycleStartAndEndDate(day);
        return getTransactionsByPeriod(dates[0], dates[1]);
    }

    Transactions getTransactionsFromDay(int day) {
        Date date = new Date(day);
        return getTransactionsFromDay(date);
    }

    @Nullable
    private Transactions getTransactionsFromDay(Date date) {
        Transactions transactions = transactionTableHelper.getTransactionByDateOfCurrentMonth(databaseHelper.getReadableDatabase(), String.valueOf(date.getTimeStamp()));
        if (transactions != null) {
            transactions.putExtraInformation(Transactions.DURATION, date.toString());
        }
        return transactions;
    }

    @Nullable
    public Transactions getTransactionsFromDay(String timeStamp, String date) {
        Transactions transactions = transactionTableHelper.getTransactionByDateOfCurrentMonth(databaseHelper.getReadableDatabase(), timeStamp);
        if (transactions != null) {
            transactions.putExtraInformation(Transactions.DURATION, date);
        }
        return transactions;
    }

    public Transactions getTransactionByPeriod(Date startDate, Date endDate) {
        if (startDate.equals(endDate)) {
            return getTransactionsFromDay(startDate);
        }

        return getTransactionsByPeriod(startDate, endDate);
    }

    @Nullable
    private Transactions getTransactionsByPeriod(Date startDate, Date endDate) {
        String startTimeStamp = String.valueOf(startDate.getTimeStamp());
        String endTimeStamp = String.valueOf(endDate.getTimeStamp());
        Transactions transactions = transactionTableHelper.getTransactionForPeriod(databaseHelper.getReadableDatabase(), startTimeStamp, endTimeStamp);
        if (transactions != null) {
            transactions.putExtraInformation(Transactions.DURATION, startDate.toString() + " To " + endDate.toString());
        }
        return transactions;
    }

    public double getTransactionTotalForBillingCycle(int day) {
        Date[] dates = getBillingCycleStartAndEndDate(day);
        return transactionTableHelper.getTransactionTotalForBillingCycle(databaseHelper.getReadableDatabase(),
                String.valueOf(dates[0].getTimeStamp()), String.valueOf(dates[1].getTimeStamp()));
    }

    private Date[] getBillingCycleStartAndEndDate(int day) {
        Date endDate;
        Date startDate;
        if (day == 1) {
            startDate = new Date(day);
            endDate = startDate.getLastDate();
        } else if (Date.getTodaysDate().getDay() < day) {
            startDate = new Date(day);
            startDate.convertToPreviousMonth();
            endDate = new Date(--day);
        } else {
            startDate = new Date(day);
            endDate = new Date(--day);
            endDate.convertToNextMonth();
        }

        return new Date[]{startDate, endDate};
    }

}
