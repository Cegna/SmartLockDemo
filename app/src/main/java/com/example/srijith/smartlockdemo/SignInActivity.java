package com.example.srijith.smartlockdemo;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.CredentialRequest;
import com.google.android.gms.auth.api.credentials.CredentialRequestResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.pixplicity.easyprefs.library.Prefs;

public class SignInActivity extends BaseActivity implements ResultCallback<CredentialRequestResult>,
        View.OnClickListener {

    private static final String TAG = "SignInActivity";
    private static final int RC_REQUEST = 11;

    private CredentialRequest credentialRequest;

    private EditText usernameEditText, passwordEditText;
    private Button signInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        credentialRequest = new CredentialRequest.Builder()
                .setPasswordLoginSupported(true)
                .build();

        usernameEditText = (EditText) findViewById(R.id.username);
        passwordEditText = (EditText) findViewById(R.id.password);
        signInButton = (Button) findViewById(R.id.sign_in);
        signInButton.setOnClickListener(this);

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        Auth.CredentialsApi.request(googleApiClient, credentialRequest).setResultCallback(this);
    }

    @Override
    public void onResult(@NonNull CredentialRequestResult credentialRequestResult) {
        Status status = credentialRequestResult.getStatus();
        if (status.isSuccess()) {
            onCredentialSuccess(credentialRequestResult.getCredential());
        } else {
            if (status.getStatusCode() == CommonStatusCodes.RESOLUTION_REQUIRED) {
                try {
                    status.startResolutionForResult(this, RC_REQUEST);
                } catch (IntentSender.SendIntentException e) {
                    Toast.makeText(this, "Save failed", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void onCredentialSuccess(Credential credential) {
        if (credential.getAccountType() == null) {
            String id = credential.getId();
            String username = credential.getName();
            String password = credential.getPassword();

            Log.d(TAG, "ID: " + id + ", Username: " + username + ", Password: " + password);
            usernameEditText.setText(username);
            passwordEditText.setText(password);

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

    @Override
    public void onClick(View v) {
        Prefs.putString("username", usernameEditText.getText().toString());
        Intent intent = new Intent(this, NewsFeedActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
