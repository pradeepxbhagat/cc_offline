package com.pradeep.cc.transaction;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.util.Log;

public class TransactionTableHelper {
    static final String TABLE_NAME = "cc_transaction";
    public static final String CREATE_TABLE = "CREATE TABLE " + TransactionTableHelper.TABLE_NAME + " (" +
            Columns._ID + " INTEGER PRIMARY KEY," +
            Columns.CC_DETAIL + " TEXT," +
            Columns.AMOUNT + " REAL," +
            Columns.BANK_NAME + " TEXT," +
            Columns.TIME_STAMP + " TEXT," +
            Columns.PLACE + " TEXT," +
            Columns.SMS_TIMESTAMP + " TEXT," +
            Columns.AVAILABLE_BALANCE + " REAL," +
            Columns.CURRENT_OUTSTANDING + " REAL);";


    public void insertTransactions(SQLiteDatabase database, Transactions transactions) {
        database.beginTransaction();
        Log.i("@@@", "starting db update");
        for (Transaction transaction : transactions.getTransactions()) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(Columns.CC_DETAIL, transaction.getCcDetail());
            contentValues.put(Columns.AMOUNT, transaction.getAmount());
            contentValues.put(Columns.BANK_NAME, transaction.getBankName());
            contentValues.put(Columns.TIME_STAMP, transaction.getTimeStamp());
            contentValues.put(Columns.PLACE, transaction.getPlace());
            contentValues.put(Columns.AVAILABLE_BALANCE, transaction.getAvailableBalance());
            contentValues.put(Columns.CURRENT_OUTSTANDING, transaction.getCurrentOutStanding());
            contentValues.put(Columns._ID, transaction.getId());
            contentValues.put(Columns.SMS_TIMESTAMP, transaction.getSmsTimeStamp());
            database.insertWithOnConflict(TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_IGNORE);
        }
        database.setTransactionSuccessful();
        database.endTransaction();
        Log.i("@@@", "closing db update");
    }

    Transactions getTransactionsSortByTime(SQLiteDatabase database) {
        String query = "Select " + TransactionTableHelper.TABLE_NAME + ".* "
                + " , (select sum(" + Columns.AMOUNT + ") from " + TABLE_NAME + ") as AMOUNT_SUM "
                + "from " + TransactionTableHelper.TABLE_NAME + " order by " + Columns.TIME_STAMP + " DESC";
        Cursor cursor = database.rawQuery(query, null);
        return getTransactions(cursor);
    }

    Transactions getTransactionByDateOfCurrentMonth(SQLiteDatabase database, String timeStamp) {
        String query = "Select " + TransactionTableHelper.TABLE_NAME + ".* "
                + " , (select sum(" + Columns.AMOUNT + ") from " + TABLE_NAME + " where "
                + Columns.TIME_STAMP + " >= " + timeStamp + ") as AMOUNT_SUM "
                + "from " + TransactionTableHelper.TABLE_NAME + " where "
                + Columns.TIME_STAMP + " >= " + timeStamp + " order by " + Columns.TIME_STAMP + " DESC";
        Cursor cursor = database.rawQuery(query, null);
        return getTransactions(cursor);
    }

    double getTransactionTotalForBillingCycle(SQLiteDatabase database, String startTimeStamp, String endTimeStamp) {
        String query = "Select " + "  (select sum(" + Columns.AMOUNT + ") from " + TABLE_NAME + " where "
                + Columns.TIME_STAMP + " >= " + startTimeStamp + " AND "+Columns.TIME_STAMP + " < " + endTimeStamp+") as AMOUNT_SUM "
                + "from " + TransactionTableHelper.TABLE_NAME + " where "
                + Columns.TIME_STAMP + " >= " + startTimeStamp + " AND "+Columns.TIME_STAMP + " < " + endTimeStamp+ " order by " + Columns.TIME_STAMP + " DESC";
        Cursor cursor = database.rawQuery(query, null);
        if (cursor != null && cursor.moveToFirst()) {
            double totalAmount = cursor.getDouble(cursor.getColumnIndex("AMOUNT_SUM"));
            return totalAmount;
        }
        return -1;
    }

    Transactions getTransactionForPeriod(SQLiteDatabase database, String startTimeStamp, String endTimeStamp) {
        String query = "Select " + TransactionTableHelper.TABLE_NAME + ".* "
                + " , (select sum(" + Columns.AMOUNT + ") from " + TABLE_NAME + " where "
                + Columns.TIME_STAMP + " >= " + startTimeStamp + " and " + Columns.TIME_STAMP + " < " + endTimeStamp + ") as AMOUNT_SUM "
                + "from " + TransactionTableHelper.TABLE_NAME + " where "
                + Columns.TIME_STAMP + " >= " + startTimeStamp + " and " + Columns.TIME_STAMP + " < " + endTimeStamp + " order by " + Columns.TIME_STAMP + " DESC";
        Cursor cursor = database.rawQuery(query, null);
        return getTransactions(cursor);
    }


    @Nullable
    private Transactions getTransactions(Cursor cursor) {
        if (cursor != null && cursor.moveToFirst()) {
            Transactions transactions = new Transactions();
            double totalAmount = cursor.getDouble(cursor.getColumnIndex("AMOUNT_SUM"));
            transactions.putExtraInformation(Transactions.TOTAL_AMOUNT, totalAmount);
            do {
                String id = cursor.getString(cursor.getColumnIndex(Columns._ID));//4679.96
                double amount = cursor.getDouble(cursor.getColumnIndex(Columns.AMOUNT));
                String bankName = cursor.getString(cursor.getColumnIndex(Columns.BANK_NAME));
                String ccDetail = cursor.getString(cursor.getColumnIndex(Columns.CC_DETAIL));
                String timestamp = cursor.getString(cursor.getColumnIndex(Columns.TIME_STAMP));
                String place = cursor.getString(cursor.getColumnIndex(Columns.PLACE));
                double availableBalance = cursor.getDouble(cursor.getColumnIndex(Columns.AVAILABLE_BALANCE));
                double currentOutstanding = cursor.getDouble(cursor.getColumnIndex(Columns.CURRENT_OUTSTANDING));
                String smsTimeStamp = cursor.getString(cursor.getColumnIndex(Columns.SMS_TIMESTAMP));
                transactions.addTransaction(new Transaction(id, amount, bankName, ccDetail, timestamp, place, smsTimeStamp, availableBalance, currentOutstanding));
            } while (cursor.moveToNext());

            return transactions;
        }
        return null;
    }


    static final class Columns {
        public final static String _ID = "_ID";
        public final static String AMOUNT = "amount";
        public final static String BANK_NAME = "bankName";
        public final static String CC_DETAIL = "ccDetail";
        public final static String TIME_STAMP = "timestamp";
        public final static String PLACE = "place";
        public final static String AVAILABLE_BALANCE = "availableBalance";
        public final static String CURRENT_OUTSTANDING = "currentOutStanding";
        public final static String SMS_TIMESTAMP = "smsTimeStamp";
    }
}
