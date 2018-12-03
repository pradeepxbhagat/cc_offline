package com.pradeep.cc.parser;

import android.support.annotation.NonNull;

import com.pradeep.cc.transaction.Transaction;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HdfcSmsParser1 extends HdfcSmsParser {
    final static String data = "ALERT: You've spent Rs.150.00  on CREDIT Card xx0280 at " +
            "PAYTMWAL1210203 on 2018-12-02:17:30:22.Avl bal - Rs.184674.83, curr o/s - Rs.20325.17.Not you? Call 18002586161.";

    @Override
    public Transaction parse(Transaction transaction) {
        String[] words = transaction.getRawData().split(" ");
        if (!words[0].contains("ALERT:")) {
            return null;
        }
        try {
            double amount = getAmount(words[3]);
            transaction.setAmount(amount);
            transaction.setBankName("HDFCBank");
            transaction.setCcDetail(words[8]);
            transaction.setTimeStamp(String.valueOf(getParsedDate(words[words.length - 11]).getTime()));
            transaction.setPlace(getPlace(words));
            transaction.setCurrentOutStanding(getCurrentOutstanding(words[words.length - 4]));
            transaction.setAvailableBalance(getAvailableBalance(words[words.length - 8]));
            return transaction;
        } catch (Exception e) {
            return null;
        }
    }

    private double getAmount(String word) {
        return Double.parseDouble(word.replace("Rs.", ""));
    }

    private Date getParsedDate(String word) throws ParseException {
        String dateAndTime = word.replace(".Avl","");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd:HH:mm:ss");
        return simpleDateFormat.parse(dateAndTime);
    }

    @NonNull
    private String getPlace(String[] words) {
        StringBuilder placeBuilder = new StringBuilder();
        int i = 10;
        while (!words[i].equals("on")) {
            placeBuilder.append(words[i]);
            placeBuilder.append(" ");
            i++;
        }
        return placeBuilder.toString().trim();
    }

    private double getCurrentOutstanding(String word) {
        return Double.parseDouble(word.replace("Rs.", "").replace(".Not",""));
    }

    private double getAvailableBalance(String word) {
        return Double.parseDouble(word.replace("Rs.", "")
                .replace(",", ""));
    }
}
