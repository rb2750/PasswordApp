package com.rb2750.passwordapp;

import android.os.AsyncTask;

public class AsyncRunnable extends AsyncTask<Runnable, Void, Void>
{
    @Override
    protected Void doInBackground(Runnable... params)
    {
        for (Runnable param : params) if (param != null) param.run();
        return null;
    }
}
