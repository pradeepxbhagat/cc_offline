package com.pradeep.cc;

import com.pradeep.cc.parser.HdfcSmsParser1;
import com.pradeep.cc.parser.SmsParser;
import com.pradeep.cc.transaction.Transaction;

import org.junit.Assert;
import org.junit.Test;

public class HdfcSmsParser1Test {
    final static String data = "ALERT: You've spent Rs.150.00  on CREDIT Card xx0280 at " +
            "PAYTMWAL1210203 on 2018-12-02:17:30:22.Avl bal - Rs.184674.83, curr o/s - Rs.20325.17.Not you? Call 18002586161.";

    final static String data1 = "ALERT: You've spent Rs.600.00  on CREDIT Card xx0280 at " +
            "ONE97 COMMUNICATIONS L on 2018-12-02:13:31:33.Avl bal - Rs.184824.00, curr o/s - Rs.20176.00.Not you? Call 18002586161.";
    @Test
    public void testNegative() {
        String data = "OTP is 132746 for txn of INR 500.00 " +
                "at ONE97 COMMUNICATIONS LIMI on card ending 0280. Valid till 10:48:41. Do not share OTP for security reasons.";
        SmsParser hdfcSmsParser = new HdfcSmsParser1();
        Transaction transaction = new Transaction("1", "2321321321", data);
        hdfcSmsParser.parse(transaction);
        Assert.assertNull(transaction.getBankName());
    }

    @Test
    public void testAmount1() {
        SmsParser hdfcSmsParser = new HdfcSmsParser1();
        Transaction transaction = new Transaction("1", "2321321321", data1);
        hdfcSmsParser.parse(transaction);
        Assert.assertEquals(600.00,transaction.getAmount(), 0);
    }

    @Test
    public void testBankName1() {
        SmsParser hdfcSmsParser = new HdfcSmsParser1();
        Transaction transaction = new Transaction("1", "2321321321", data1);
        hdfcSmsParser.parse(transaction);
        Assert.assertEquals("HDFCBank",transaction.getBankName());
    }

    @Test
    public void testCcDetail1() {
        SmsParser hdfcSmsParser = new HdfcSmsParser1();
        Transaction transaction = new Transaction("1", "2321321321", data1);
        hdfcSmsParser.parse(transaction);
        Assert.assertEquals("xx0280",transaction.getCcDetail());
    }

    @Test
    public void testTimeStamp1() {
        SmsParser hdfcSmsParser = new HdfcSmsParser1();
        Transaction transaction = new Transaction("1", "2321321321", data1);
        hdfcSmsParser.parse(transaction);
        Assert.assertEquals("1543737693000",transaction.getTimeStamp());
    }

    @Test
    public void testPlace1() {
        SmsParser hdfcSmsParser = new HdfcSmsParser1();
        Transaction transaction = new Transaction("1", "2321321321", data1);
        hdfcSmsParser.parse(transaction);
        Assert.assertEquals("ONE97 COMMUNICATIONS L", transaction.getPlace());
    }

    @Test
    public void testCurrentOutstanding1() {
        SmsParser hdfcSmsParser = new HdfcSmsParser1();
        Transaction transaction = new Transaction("1", "2321321321", data1);
        hdfcSmsParser.parse(transaction);
        Assert.assertEquals(20176.00, transaction.getCurrentOutStanding(), 0.0);
    }

    @Test
    public void testAvailableBalance1() {
        SmsParser hdfcSmsParser = new HdfcSmsParser1();
        Transaction transaction = new Transaction("1", "2321321321", data1);
        hdfcSmsParser.parse(transaction);
        Assert.assertEquals(184824.00, transaction.getAvailableBalance(), 0.0);
    }

    @Test
    public void testAmount() {
        SmsParser hdfcSmsParser = new HdfcSmsParser1();
        Transaction transaction = new Transaction("1", "2321321321", data);
        hdfcSmsParser.parse(transaction);
        Assert.assertEquals(150.00,transaction.getAmount(), 0);
    }

    @Test
    public void testBankName() {
        SmsParser hdfcSmsParser = new HdfcSmsParser1();
        Transaction transaction = new Transaction("1", "2321321321", data);
        hdfcSmsParser.parse(transaction);
        Assert.assertEquals("HDFCBank",transaction.getBankName());
    }

    @Test
    public void testCcDetail() {
        SmsParser hdfcSmsParser = new HdfcSmsParser1();
        Transaction transaction = new Transaction("1", "2321321321", data);
        hdfcSmsParser.parse(transaction);
        Assert.assertEquals("xx0280",transaction.getCcDetail());
    }

    @Test
    public void testTimeStamp() {
        SmsParser hdfcSmsParser = new HdfcSmsParser1();
        Transaction transaction = new Transaction("1", "2321321321", data);
        hdfcSmsParser.parse(transaction);
        Assert.assertEquals("1543752022000",transaction.getTimeStamp());
    }

    @Test
    public void testPlace() {
        SmsParser hdfcSmsParser = new HdfcSmsParser1();
        Transaction transaction = new Transaction("1", "2321321321", data);
        hdfcSmsParser.parse(transaction);
        Assert.assertEquals("PAYTMWAL1210203", transaction.getPlace());
    }

    @Test
    public void testCurrentOutstanding() {
        SmsParser hdfcSmsParser = new HdfcSmsParser1();
        Transaction transaction = new Transaction("1", "2321321321", data);
        hdfcSmsParser.parse(transaction);
        Assert.assertEquals(20325.17, transaction.getCurrentOutStanding(), 0.0);
    }

    @Test
    public void testAvailableBalance() {
        SmsParser hdfcSmsParser = new HdfcSmsParser1();
        Transaction transaction = new Transaction("1", "2321321321", data);
        hdfcSmsParser.parse(transaction);
        Assert.assertEquals(184674.83, transaction.getAvailableBalance(), 0.0);
    }
}
