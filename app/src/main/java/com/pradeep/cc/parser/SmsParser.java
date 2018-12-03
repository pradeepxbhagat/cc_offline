package com.pradeep.cc.parser;

import com.pradeep.cc.transaction.Transaction;

public abstract class SmsParser {
    public abstract Transaction parse(Transaction transaction);
}
