package com.pradeep.cc;

import com.pradeep.cc.parser.HdfcSmsParser;
import com.pradeep.cc.parser.SmsParser;
import com.pradeep.cc.transaction.Transaction;

import org.junit.Assert;
import org.junit.Test;

public class HdfcSmsParserTest {
    final static String data = "Rs.500.00 was spent on ur HDFCBank CREDIT " +
            "Card ending 0280 on 2018-09-06:10:45:46 at ONE97 COMMUNICATIONS L.Avl bal - Rs.132352.00, curr o/s - Rs.38648.00";

    @Test
    public void testNegative() {
        String data = "OTP is 132746 for txn of INR 500.00 " +
                "at ONE97 COMMUNICATIONS LIMI on card ending 0280. Valid till 10:48:41. Do not share OTP for security reasons.";
        SmsParser hdfcSmsParser = new HdfcSmsParser();
        Transaction transaction = new Transaction("1", "2321321321", data);
        hdfcSmsParser.parse(transaction);
        Assert.assertNull(transaction.getBankName());
    }

    @Test
    public void testAmount() {
        SmsParser hdfcSmsParser = new HdfcSmsParser();
        Transaction transaction = new Transaction("1", "2321321321", data);
        hdfcSmsParser.parse(transaction);
        Assert.assertEquals(transaction.getAmount(), 500.00, 0);
    }

    @Test
    public void testBankName() {
        SmsParser hdfcSmsParser = new HdfcSmsParser();
        Transaction transaction = new Transaction("1", "2321321321", data);
        hdfcSmsParser.parse(transaction);
        Assert.assertEquals(transaction.getBankName(), "HDFCBank");
    }

    @Test
    public void testCcDetail() {
        SmsParser hdfcSmsParser = new HdfcSmsParser();
        Transaction transaction = new Transaction("1", "2321321321", data);
        hdfcSmsParser.parse(transaction);
        Assert.assertEquals(transaction.getCcDetail(), "0280");
    }

    @Test
    public void testTimeStamp() {
        SmsParser hdfcSmsParser = new HdfcSmsParser();
        Transaction transaction = new Transaction("1", "2321321321", data);
        hdfcSmsParser.parse(transaction);
        Assert.assertEquals(transaction.getTimeStamp(), "1536210946000");
    }

    @Test
    public void testPlace() {
        SmsParser hdfcSmsParser = new HdfcSmsParser();
        Transaction transaction = new Transaction("1", "2321321321", data);
        hdfcSmsParser.parse(transaction);
        Assert.assertEquals(transaction.getPlace(), "ONE97 COMMUNICATIONS L");
    }

    @Test
    public void testPlace1() {
        SmsParser hdfcSmsParser = new HdfcSmsParser();
        String data = "Rs.600.00 was spent on ur HDFCBank CREDIT Card ending 0280 on " +
                "2018-07-02:19:40:50 at VITALIFE HEALTH.Avl bal - Rs.136596.00, curr o/s - Rs.34404.00";
        Transaction transaction = new Transaction("1", "2321321321", data);
        hdfcSmsParser.parse(transaction);
        Assert.assertEquals(transaction.getPlace(), "VITALIFE HEALTH");
    }

    @Test
    public void testPlace2() {
        SmsParser hdfcSmsParser = new HdfcSmsParser();
        String data = "Rs.1887.50 was spent on ur HDFCBank CREDIT Card ending 0280 on 2018-07-07:21:24:24 at " +
                "DMART Baner.Avl bal - Rs.131009.34, curr o/s - Rs.39990.66";
        Transaction transaction = new Transaction("1", "2321321321", data);
        hdfcSmsParser.parse(transaction);
        Assert.assertEquals(transaction.getPlace(), "DMART Baner");
    }

    @Test
    public void testCurrentOutstanding() {
        SmsParser hdfcSmsParser = new HdfcSmsParser();
        Transaction transaction = new Transaction("1", "2321321321", data);
        hdfcSmsParser.parse(transaction);
        Assert.assertEquals(transaction.getCurrentOutStanding(), 38648.00, 0.0);
    }

    @Test
    public void testAvailableBalance() {
        SmsParser hdfcSmsParser = new HdfcSmsParser();
        Transaction transaction = new Transaction("1", "2321321321", data);
        hdfcSmsParser.parse(transaction);
        Assert.assertEquals(transaction.getAvailableBalance(), 132352.00, 0.0);
    }
}