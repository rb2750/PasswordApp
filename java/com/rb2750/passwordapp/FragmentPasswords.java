package com.rb2750.passwordapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.*;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.*;
import android.widget.*;

import java.util.ArrayList;
import java.util.List;

public class FragmentPasswords extends Fragment
{
    private TableLayout tl;
    private View view;
    private String filter = "";
    private AutoCompleteTextView filterBox;
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        if (container != null) container.removeAllViews();
        if (!MainActivity.getGoogle().isSignedIn()) return inflater.inflate(R.layout.no_login, container, false);

        view = inflater.inflate(R.layout.layout_passwords, container, false);

        setHasOptionsMenu(true);
        context = view.getContext();

        context.registerReceiver(new Recieve(), new IntentFilter("com.rb2750.UpdateTable"));

//        addData("Test", "User", "AndPass");

        filterBox = (AutoCompleteTextView) view.findViewById(R.id.filterTxt);

        updateAutoComplete();

        filterBox.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {

            }

            @Override
            public void afterTextChanged(Editable s)
            {
                updateFilter(s.toString());
            }
        });

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    private void updateAutoComplete()
    {
        List<String> autoComplete = new ArrayList<>();

        for (Entry entry : getInformation())
        {
            if (!autoComplete.contains(entry.getLocation())) autoComplete.add(entry.getLocation());
            if (!autoComplete.contains(entry.getUsername())) autoComplete.add(entry.getUsername());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, autoComplete);
        filterBox.setAdapter(adapter);
    }

    public void updateFilter(String filter)
    {
        if (!filter.equals(this.filter))
        {
            this.filter = filter;
            update();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        if (!MainActivity.getGoogle().isSignedIn()) return;
        tl = (TableLayout) getMainActivity().findViewById(R.id.password_table);
//        addHeaders();
//        for (int i = 0; i < 50; i++) addRow("Loc", "rb2750", "lolrekt");
        update();
    }

    public List<Entry> getInformation()
    {
        List<Entry> entries = new ArrayList<>();
        for (String dat : Variables.getAllData((Activity) context))
        {
            String[] split = dat.split(String.valueOf(Variables.splitChar));
            if (split.length < 3) continue;
            entries.add(new Entry(split[0], split[1], split[2]));
        }
        return entries;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.item_password_add)
        {
            Intent intent = new Intent(context, PasswordAddActivity.class);
            startActivity(intent);
            return true;
        }
        return false;
    }

    public FragmentActivity getMainActivity()
    {
        return (FragmentActivity) context;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.passwordadd, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
    }

    public void addHeaders()
    {
        addRow("Location", "Username", "Password", true);
    }

    private List<TextView> passwordViews = new ArrayList<>();

    public void addRow(final String location, final String username, final String password, boolean... header)
    {
        TableRow row = new TableRow(context);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        row.setLayoutParams(lp);
        final TextView locationTV = new TextView(context);
        final TextView usernameTV = new TextView(context);
        final TextView passwordTV = new TextView(context);

        locationTV.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        usernameTV.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        passwordTV.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        locationTV.setTextSize(18);
        usernameTV.setTextSize(18);
        passwordTV.setTextSize(18);

        locationTV.setText(location);
        usernameTV.setText(username);
        passwordTV.setText(password);

        TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);

        boolean sep = header.length == 0 || !header[0];
        if (sep) params.setMargins(0, 7, 0, 0);

//        row.setBackgroundColor(getActivity().getColor(R.color.colorAccent));//Border color

        TypedArray array = getMainActivity().getTheme().obtainStyledAttributes(new int[]{
                android.R.attr.colorBackground,
                android.R.attr.textColorHint
        });
        int backgroundColor = array.getColor(0, 0xFF00FF);
        int textColor = array.getColor(1, 0xFF00FF);
        array.recycle();

        row.setBackgroundColor(textColor);

        locationTV.setBackgroundColor(backgroundColor);
        usernameTV.setBackgroundColor(backgroundColor);
        passwordTV.setBackgroundColor(backgroundColor);

        if (!sep)//Is header
        {
            locationTV.setTypeface(Typeface.DEFAULT_BOLD);
            usernameTV.setTypeface(Typeface.DEFAULT_BOLD);
            passwordTV.setTypeface(Typeface.DEFAULT_BOLD);
        }

        passwordTV.setLayoutParams(params);
        params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
        params.setMargins(0, sep ? 7 : 0, 7, 0);
        locationTV.setLayoutParams(params);
        usernameTV.setLayoutParams(params);

        if (sep)//Not header
        {
            passwordTV.setTransformationMethod(new PasswordTransformationMethod());

            passwordTV.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (passwordTV.getTransformationMethod() instanceof PasswordTransformationMethod)
                    {
                        for (TextView pv : passwordViews) pv.setTransformationMethod(new PasswordTransformationMethod());
                        passwordTV.setTransformationMethod(null);
                    }
                    else passwordTV.setTransformationMethod(new PasswordTransformationMethod());
                }
            });

            View.OnLongClickListener onClick = new View.OnLongClickListener()
            {
                @Override
                public boolean onLongClick(View v)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Are you sure you want to delete this password?");
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {

                        }
                    });
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            Variables.removeData((Activity) context, location, username);
                            updateAutoComplete();
                            update();
                        }
                    });

                    builder.show();

                    return true;
                }
            };

            row.setOnLongClickListener(onClick);
        }

        row.addView(locationTV);
        row.addView(usernameTV);
        row.addView(passwordTV);

        if (sep) passwordViews.add(passwordTV);

        tl.addView(row);
    }

    public void update()
    {
        tl.removeAllViews();
        if (getInformation().isEmpty())
        {
            TextView tv = new TextView(context);
            tv.setTextSize(18);
            tv.setText("There are no passwords stored yet!\nPress the add button top right to add one.");
            tl.addView(tv);
            return;
        }
        List<Entry> toAdd = new ArrayList<>();

        for (Entry entry : getInformation())
            if (filter.isEmpty() || entry.getLocation().toLowerCase().startsWith(filter.toLowerCase()) || entry.getUsername().toLowerCase().startsWith(filter.toLowerCase()))
                toAdd.add(entry);

        if (toAdd.isEmpty())
        {
            TextView tv = new TextView(context);
            tv.setTextSize(18);
            tv.setText("Nothing was found for this filter!");
            tl.addView(tv);
            return;
        }

        addHeaders();
        for (Entry entry : toAdd) addRow(entry.getLocation(), entry.getUsername(), entry.getPassword());
    }

    private static class Entry
    {
        private String location;
        private String username;
        private String password;

        public Entry(String location, String username, String password)
        {
            this.location = location;
            this.username = username;
            this.password = password;
        }

        public String getLocation()
        {
            return location;
        }

        public String getUsername()
        {
            return username;
        }

        public String getPassword()
        {
            return password;
        }
    }

    public class Recieve extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            updateAutoComplete();
            update();
        }
    }
}
