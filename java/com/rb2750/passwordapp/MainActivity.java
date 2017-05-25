package com.rb2750.passwordapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import static com.rb2750.passwordapp.Variables.google;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    private MenuItem currentItem;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View view)
//            {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);

        google = new GoogleAPI(this);
        google.setOnLogin(new GoogleAPI.SignInResultRunnable()
        {
            @Override
            public void run()
            {
                if (result.isSuccess())
                {
                    // Signed in successfully, show authenticated UI.
                    GoogleSignInAccount acct = result.getSignInAccount();
                    assert acct != null;
                    String name = acct.getDisplayName();
                    String email = acct.getEmail();
                    ((TextView) findViewById(R.id.logged_in_name_label)).setText(name);
                    ((TextView) findViewById(R.id.logged_in_email_label)).setText(email);
                    setSignedIn();
                    setFragment(R.id.nav_home, new FragmentMain(), "Home");
                    Toast.makeText(MainActivity.this, "Sign in successful", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Sign in failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
        setFragment(R.id.nav_home, new FragmentMain(), "Home");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        google.auth(requestCode, data);
    }

    public static GoogleAPI getGoogle()
    {
        return google;
    }

    public void setSignedIn()
    {
        MenuItem signInOutBtn = currentItem;
        if (signInOutBtn == null) return;
        if (google.isSignedIn())
        {
            signInOutBtn.setTitle("Sign Out");
            signInOutBtn.setIcon(getDrawable(R.drawable.ic_lock));
        }
        else
        {
            signInOutBtn.setTitle("Sign In");
            signInOutBtn.setIcon(getDrawable(R.drawable.ic_unlock));
            ((TextView) findViewById(R.id.logged_in_name_label)).setText("Not logged in");
            ((TextView) findViewById(R.id.logged_in_email_label)).setText("Please log in");
        }
    }

    private void signOut()
    {
        google.signOut();
        setSignedIn();
    }

    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        else
        {
            super.onBackPressed();
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu)
//    {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(passwordadd, menu);
//        return true;
//    } //This is the settings button (3 dot menu)

    private void setFragment(int newSel, Fragment fragment, String... name)
    {
        FragmentManager fragman = getSupportFragmentManager();
        FragmentTransaction transaction = fragman.beginTransaction();
        transaction.replace(R.id.content_main, fragment);
        transaction.commit();
        if (name.length > 0) getSupportActionBar().setTitle(name[0]);
        navigationView.setCheckedItem(newSel);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        this.currentItem = item;
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_sign_in_out)
        {
            if (!google.isSignedIn()) google.callSignIn();
            else
            {
                signOut();
                Toast.makeText(this, "Signed out succesfully", Toast.LENGTH_SHORT).show();
                setFragment(R.id.nav_home, new FragmentMain(), "Home");
            }
        }
        else if (id == R.id.nav_home)
        {
            setFragment(R.id.nav_home, new FragmentMain(), "Home");
        }
        else if (id == R.id.nav_passwords)
        {
            setFragment(R.id.nav_passwords, new FragmentPasswords(), "Passwords");
        }
        else if (id == R.id.nav_settings)
        {
            setFragment(R.id.nav_settings, new SettingsFragment(), "Settings");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
