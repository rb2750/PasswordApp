package com.rb2750.passwordapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArraySet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Set;

public class FragmentMain extends Fragment
{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        if (container != null) container.removeAllViews();
        if (!MainActivity.getGoogle().isSignedIn()) return inflater.inflate(R.layout.no_login, container, false);

        View view = inflater.inflate(R.layout.content_main, container, false);
        ((TextView) view.findViewById(R.id.welcomeLbl)).setText("Welcome back, " + MainActivity.getGoogle().getAccount().getDisplayName() + ".");
        ((TextView) view.findViewById(R.id.lblStoredPasswords)).setText("You have " + Variables.getAllData(getActivity()).size() + " passwords stored. To view them, click on the side menu and tap 'Passwords'.");
        return view;
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
    }
}
