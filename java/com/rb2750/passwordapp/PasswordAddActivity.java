package com.rb2750.passwordapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PasswordAddActivity extends AppCompatActivity implements TextWatcher
{
    Button addPasswordBtn;
    EditText locationTxt;
    EditText usernameTxt;
    EditText passwordTxt;
    Runnable onFinish;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_add);

        addPasswordBtn = (Button) findViewById(R.id.addPasswordBtn);
        locationTxt = (EditText) findViewById(R.id.locationTxt);
        usernameTxt = (EditText) findViewById(R.id.usernameTxt);
        passwordTxt = (EditText) findViewById(R.id.passwordTxt);

        locationTxt.addTextChangedListener(this);
        usernameTxt.addTextChangedListener(this);
        passwordTxt.addTextChangedListener(this);

        addPasswordBtn.setEnabled(false);

        addPasswordBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Variables.addData(PasswordAddActivity.this, locationTxt.getText().toString(), usernameTxt.getText().toString(), passwordTxt.getText().toString());
                Toast.makeText(PasswordAddActivity.this, "Password Added.", Toast.LENGTH_SHORT).show();
                sendBroadcast(new Intent("com.rb2750.UpdateTable"));
                finish();
            }
        });
    }

    @Override
    public void afterTextChanged(Editable s)
    {

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after)
    {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count)
    {
        addPasswordBtn.setEnabled(!locationTxt.getText().toString().isEmpty() && !usernameTxt.getText().toString().isEmpty() && !passwordTxt.getText().toString().isEmpty());
    }
}
