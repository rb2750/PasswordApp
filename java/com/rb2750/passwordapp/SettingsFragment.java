package com.rb2750.passwordapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArraySet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsFragment extends Fragment
{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        if (container != null) container.removeAllViews();
        if (!MainActivity.getGoogle().isSignedIn()) return inflater.inflate(R.layout.no_login, container, false);

        final View view = inflater.inflate(R.layout.fragment_settings, container, false);
        Button clear = (Button) view.findViewById(R.id.clearPassBtn);

        updateView(view);

        clear.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Variables.setData(getActivity(), new ArraySet<String>());
                updateView(view);
                Toast.makeText(getContext(), "Passwords cleared!", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    public void updateView(View view)
    {
        TextView stored = (TextView) view.findViewById(R.id.storedView);
        stored.setText("You have " + Variables.getAllData(getActivity()).size() + " passwords stored.");
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
    }
}
