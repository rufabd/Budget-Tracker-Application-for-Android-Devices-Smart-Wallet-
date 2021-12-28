package com.example.app_dev_money_tracking;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;

class User_settings
{
    final static String cur_setting = "base_currency";
    final static String record_setting = "record";
    final static String account_setting = "accounts";
    final static String user_email = "email";
    final static String user_is_admin = "false";

    Context ctx;
    String uname;
    static User_settings singleton;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    public static User_settings instanciate(String uname, Context ctx)
    {
        if (singleton == null) {
            return singleton = new User_settings(uname, ctx);
        } else {
            return singleton;
        }
    }

    public User_settings(String uname, Context ctx)
    {
        this.uname = uname;
        this.ctx = ctx;
        pref = ctx.getSharedPreferences(uname, Context.MODE_PRIVATE);
    }

    public void set_currency(String currency)
    {
        pref = ctx.getSharedPreferences(uname, Context.MODE_PRIVATE);
        editor = pref.edit();
        editor.putString(cur_setting, currency);
        editor.apply();
    }
    public String get_currency()
    {
        pref = ctx.getSharedPreferences(uname, Context.MODE_PRIVATE);
        String result = "";
        pref = ctx.getSharedPreferences(uname, Context.MODE_PRIVATE);
        result = pref.getString(cur_setting,result);
        return result;
    }

    public void saveRecordList(ArrayList<Categories> List) {
        pref = ctx.getSharedPreferences(uname, Context.MODE_PRIVATE);
        editor = pref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(List);
        editor.putString(record_setting, json);
        editor.apply();
    }

    public ArrayList<Categories> retrieveRecordList() {
        pref = ctx.getSharedPreferences(uname, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = pref.getString(record_setting, null);
        Type type = new TypeToken<ArrayList<Categories>>() {}.getType();
        ArrayList<Categories> recordList = gson.fromJson(json, type);

//        if (recordList == null) {
//            recordList = new ArrayList<Categories>();

        return recordList;
    }

    public void SaveAccounts(ArrayList<Account> List) {
        pref = ctx.getSharedPreferences(uname, Context.MODE_PRIVATE);
        editor = pref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(List);
        editor.putString(account_setting, json);
        editor.apply();
    }

    public ArrayList<Account> retrieveAccounts() {
        pref = ctx.getSharedPreferences(uname, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = pref.getString(account_setting, null);
        Type type = new TypeToken<ArrayList<Account>>() {}.getType();
        ArrayList<Account> accounts = gson.fromJson(json, type);
        return accounts;
    }

    public void setUserEmail(String email)
    {
        pref = ctx.getSharedPreferences(uname, Context.MODE_PRIVATE);
        editor = pref.edit();
        editor.putString(user_email, email);
        editor.apply();
    }
    public String getUserEmail()
    {
        pref = ctx.getSharedPreferences(uname, Context.MODE_PRIVATE);
        String email = "";
        pref = ctx.getSharedPreferences(uname, Context.MODE_PRIVATE);
        email = pref.getString(user_email, email);
        return email;
    }

}
