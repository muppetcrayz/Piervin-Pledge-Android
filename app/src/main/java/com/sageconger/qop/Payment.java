package com.sageconger.qop;

import java.util.Date;

class Payment {

    Date date;
    Double amount;
    Double balance;
    String format;

    Payment() {
        date = new Date();
        amount = 0.0;
        balance = 0.0;
        format = "";
    }

    Payment(Date date, Double amount, Double balance, String format) {
        this.date = date;
        this.amount = amount;
        this.balance = balance;
        this.format = format;
    }

    public Date getDate() {
        return date;
    }

    public Double getAmount() {
        return amount;
    }

    public Double getBalance() {
        return balance;
    }

    public String getFormat() {
        return format;
    }
}
