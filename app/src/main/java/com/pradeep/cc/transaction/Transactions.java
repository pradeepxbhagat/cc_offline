package com.pradeep.cc.transaction;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Transactions {
    private final List<Transaction> transactions;
    private final Map<Integer, Object> extraInformation;

    public Transactions() {
        this.transactions = new ArrayList<>();
        extraInformation = new HashMap<>();
    }

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    public void putExtraInformation(@ExtraKeys int key, Object value) {
        extraInformation.put(key, value);
    }

    public Object getExtraInformation(@ExtraKeys int key) {
        return extraInformation.get(key);
    }

    public Set<Integer> getExtraInformationKey() {
        return extraInformation.keySet();
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public static final int TOTAL_AMOUNT = 1301;
    public static final int DURATION = 1302;

    @IntDef({TOTAL_AMOUNT, DURATION})
    @Retention(RetentionPolicy.SOURCE)
    private @interface ExtraKeys {
    }
}
