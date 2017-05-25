package com.rb2750.passwordapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

class GoogleAPI implements GoogleApiClient.OnConnectionFailedListener
{
    private GoogleApiClient mGoogleApiClient;
    private GoogleSignInAccount account;
    private final int RC_SIGN_IN = 9001;
    private FragmentActivity main;
    private SignInResultRunnable resultRunnable;

    GoogleAPI(FragmentActivity main)
    {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(main)
                .enableAutoManage(main, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        this.main = main;
    }

    void callSignIn()
    {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        main.startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    void auth(int requestCode, Intent data)
    {
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN)
        {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    void signOut()
    {
        if (mGoogleApiClient.isConnected()) mGoogleApiClient.clearDefaultAccountAndReconnect();
        account = null;
    }

    void setOnLogin(SignInResultRunnable runnable)
    {
        this.resultRunnable = runnable;
    }

    private void handleSignInResult(GoogleSignInResult result)
    {
        if (result.isSuccess())
        {
            account = result.getSignInAccount();

            new AsyncRunnable().execute(new Runnable()
            {
                @Override
                public void run()
                {
                    System.out.println(Variables.sendGetRequest("http://passwordapp.rb2750.com/get.php?googleId=" + account.getId()));
                }
            }, null);
        }
        if (resultRunnable != null)
        {
            resultRunnable.setResult(result);
            resultRunnable.run();
        }
    }

    public GoogleSignInAccount getAccount()
    {
        return account;
    }

    boolean isSignedIn()
    {
        return account != null;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {
        Toast.makeText(main, "Failed to connect to google sign in", Toast.LENGTH_SHORT).show();
    }

    static abstract class SignInResultRunnable implements Runnable
    {
        GoogleSignInResult result;

        private void setResult(GoogleSignInResult result)
        {
            this.result = result;
        }
    }
}
