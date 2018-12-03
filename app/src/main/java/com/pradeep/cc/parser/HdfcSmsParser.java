package com.pradeep.cc.parser;

import android.support.annotation.NonNull;

import com.pradeep.cc.transaction.Transaction;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HdfcSmsParser extends SmsParser {

    @Override
    public Transaction parse(Transaction transaction) {
        String[] words = transaction.getRawData().split(" ");
        if (!words[0].contains("Rs.")) {
            return null;
        }
        try {
            double amount = getAmount(words[0]);
            transaction.setAmount(amount);
            transaction.setBankName(words[5]);
            transaction.setCcDetail(words[9]);
            transaction.setTimeStamp(String.valueOf(getParsedDate(words[11]).getTime()));
            transaction.setPlace(getPlace(words));
            transaction.setAvailableBalance(getAvailableBalance(words[words.length - 5]));
            transaction.setCurrentOutStanding(getCurrentOutstanding(words[words.length - 1]));
            return transaction;
        } catch (Exception e) {
            return null;
        }
    }

    private double getAvailableBalance(String word) {
        return Double.parseDouble(word.replace("Rs.", "")
                .replace(",", ""));
    }

    private double getCurrentOutstanding(String word) {
        return Double.parseDouble(word.replace("Rs.", ""));
    }

    @NonNull
    private String getPlace(String[] words) {
        StringBuilder placeBuilder = new StringBuilder();
        int i = 13;
        while (!words[i].equals("bal")) {
            placeBuilder.append(words[i]);
            placeBuilder.append(" ");
            i++;
        }
        return placeBuilder.toString().replace(".Avl", "").trim();
    }

    private String getTime(Date parsedDate) {
        return new SimpleDateFormat("HH:mm:ss").format(parsedDate);
    }

    private String getDate(Date parsedDate) {
        return new SimpleDateFormat("yyyy-MM-dd").format(parsedDate);
    }

    private Date getParsedDate(String word) throws ParseException {
        String dateAndTime = word;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd:HH:mm:ss");
        return simpleDateFormat.parse(dateAndTime);
    }

    private double getAmount(String word) {
        return Double.parseDouble(word.replace("Rs.", ""));
    }

}
