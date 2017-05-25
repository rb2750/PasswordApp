package com.rb2750.passwordapp;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.util.ArraySet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Set;

public class Variables
{
    public static char splitChar = 'ﻼ';
    public static GoogleAPI google;

    public static Set<String> getAllData(Activity activity)
    {
        return getPreferences(activity).getStringSet(getKey(activity), new ArraySet<String>());
    }

    private static String getKey(Activity activity)
    {
        return activity.getString(R.string.password_storage) + "." + google.getAccount().getId();
    }

    /**
     * Send a request to a url
     *
     * @param urlString - the target url with get params
     * @return The returned data from the webpage.
     */

    public static String sendGetRequest(String urlString)
    {
        StringBuilder chaine = new StringBuilder("");
        try
        {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", "");
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.connect();

            InputStream inputStream = connection.getInputStream();

            BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while ((line = rd.readLine()) != null)
            {
                chaine.append(line);
            }
        }
        catch (IOException e)
        {
            // Writing exception to log
            e.printStackTrace();
        }
        return chaine.toString();
    }

    public static void addData(Activity activity, String location, String username, String password)
    {
        Set<String> values = getAllData(activity);
        String dat = location + Variables.splitChar + username + Variables.splitChar + password;
        if (!values.contains(dat)) values.add(dat);
        setData(activity, values);
    }

    public static void removeData(Activity activity, String location, String username)
    {
        Set<String> values = getAllData(activity);
        String dat = location + Variables.splitChar + username + Variables.splitChar;
        for (String value : new ArraySet<>(values)) if (value.startsWith(dat)) values.add(dat);
        setData(activity, values);
    }

    public static void setData(Activity activity, Set<String> values)
    {
        getPreferences(activity).edit().putStringSet(getKey(activity), values).commit();
    }

    public static SharedPreferences getPreferences(Activity activity)
    {
        return PreferenceManager.getDefaultSharedPreferences(activity.getBaseContext());//.Context.MODE_PRIVATE);
    }
}
