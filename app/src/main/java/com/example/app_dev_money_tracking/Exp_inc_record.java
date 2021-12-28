package com.example.app_dev_money_tracking;

import java.util.Date;

class Exp_inc_record
{
    Date date;  // date when record happened
    double amount;
    Account account;
    char type; // Income or expanse
    String category;   //food, medical etc.

    public Exp_inc_record(Date date, double amount, Account account, char type, String category)
    {
        this.date = date;
        this.amount = amount;
        this.account = account;
        this.type = type;
        this.category = category;
    }

    public Date getDate()
    {
        return date;
    }

    public void setDate(Date date)
    {
        this.date = date;
    }

    public double getAmount()
    {
        return amount;
    }

    public void setAmount(double amount)
    {
        this.amount = amount;
    }

    public Account getAccount()
    {
        return account;
    }

    public void setAccount(Account account)
    {
        this.account = account;
    }

    public char getType()
    {
        return type;
    }

    public void setType(char type)
    {
        this.type = type;
    }

    public String getCategory()
    {
        return category;
    }

    public void setCategory(String category)
    {
        this.category = category;
    }
}
