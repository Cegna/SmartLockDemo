package com.example.srijith.smartlockdemo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.CredentialRequest;
import com.google.android.gms.auth.api.credentials.CredentialRequestResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.pixplicity.easyprefs.library.Prefs;

public class NewsFeedActivity extends BaseActivity implements ResultCallback<CredentialRequestResult> {

    private static final String TAG = "NewsFeedActivity";
    private static final int RC_REQUEST = 12;

    private CredentialRequest credentialRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_feed);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sign_out_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out:
                signOut();
                return true;
            case R.id.delete_account:
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setMessage("This will sign you out of the account and delete it.")
                        .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                requestCredentials();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create();

                dialog.show();

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void signOut() {
        Prefs.clear();
        Auth.CredentialsApi.disableAutoSignIn(googleApiClient);
        startActivity(new Intent(this, SignUpActivity.class));
        finish();
    }

    private void requestCredentials() {
        credentialRequest = new CredentialRequest.Builder()
                .setPasswordLoginSupported(true)
                .build();

        Auth.CredentialsApi.request(googleApiClient, credentialRequest).setResultCallback(this);
    }

    @Override
    public void onResult(@NonNull CredentialRequestResult credentialRequestResult) {
        Status status = credentialRequestResult.getStatus();
        if (status.isSuccess()) {
            onCredentialSuccess(credentialRequestResult.getCredential());
        } else {
            if (status.hasResolution()) {
                try {
                    status.startResolutionForResult(this, RC_REQUEST);
                } catch (IntentSender.SendIntentException e) {
                    Toast.makeText(this, "Save failed", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Request failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_REQUEST) {
            if (resultCode == RESULT_OK) {
                Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
                onCredentialSuccess(credential);
            } else {
                Log.d(TAG, "Request failed");
            }
        }
    }

    private void onCredentialSuccess(Credential credential) {
        if (credential.getAccountType() == null) {
            Auth.CredentialsApi.delete(googleApiClient, credential).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status) {
                    if (status.isSuccess()) {
                        signOut();
                    } else {
                        Toast.makeText(NewsFeedActivity.this, "Account deletion failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }

}
