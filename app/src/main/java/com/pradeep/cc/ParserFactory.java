package com.pradeep.cc;

import com.pradeep.cc.parser.HdfcSmsParser;
import com.pradeep.cc.parser.HdfcSmsParser1;
import com.pradeep.cc.parser.SmsParser;

public class ParserFactory {
    private static HdfcSmsParser1 hdfcSmsParser1;
    private static HdfcSmsParser hdfcSmsParser;

    public static SmsParser getParser(String sender, String strbody) {
        if (sender.contains("HDFC")) {
            if (strbody.startsWith("ALERT:")) {
                if(hdfcSmsParser1 == null){
                    hdfcSmsParser1 = new HdfcSmsParser1();
                }
                return hdfcSmsParser1;
            }
            if(hdfcSmsParser == null){
                hdfcSmsParser = new HdfcSmsParser();
            }
            return hdfcSmsParser;
        }
        return null;
    }
}
