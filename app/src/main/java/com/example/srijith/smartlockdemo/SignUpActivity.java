package com.example.srijith.smartlockdemo;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.pixplicity.easyprefs.library.Prefs;

public class SignUpActivity extends BaseActivity implements
        View.OnClickListener, ResultCallback<Result> {

    private static final String TAG = "SignUpActivity";
    private static final int RC_SAVE = 10;

    private EditText email, username, password;
    private Button signUp;
    private TextView signIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Prefs.getString("username", null) != null) {
            startActivity(new Intent(this, NewsFeedActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_sign_up);

        email = (EditText) findViewById(R.id.email);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        signUp = (Button) findViewById(R.id.sign_up);
        signIn = (TextView) findViewById(R.id.sign_in);

        signUp.setOnClickListener(this);
        signIn.setOnClickListener(this);
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_up:
                if (TextUtils.isEmpty(email.getText()) ||
                        TextUtils.isEmpty(username.getText()) ||
                        TextUtils.isEmpty(password.getText())) {
                    Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                String emailText = email.getText().toString();
                String usernameText = username.getText().toString();
                if (Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
                    Credential credential = new Credential.Builder(emailText)
                            .setName(usernameText)
                            .setPassword(password.getText().toString())
                            .build();

                    Prefs.putString("username", usernameText);

                    requestSaveCredentials(credential);

                } else {
                    Toast.makeText(this, "Email is incorrect", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.sign_in:
                startActivity(new Intent(this, SignInActivity.class));
                break;
        }
    }

    private void requestSaveCredentials(Credential credential) {
        Auth.CredentialsApi.save(googleApiClient, credential).setResultCallback(this);
    }

    @Override
    public void onResult(@NonNull Result result) {
        Status status = result.getStatus();
        if (status.isSuccess()) {
            Toast.makeText(this, "Credentials saved successfully", Toast.LENGTH_LONG).show();
            showNewsFeed();
        } else {
            if (status.hasResolution()) {
                try {
                    status.startResolutionForResult(this, RC_SAVE);
                } catch (IntentSender.SendIntentException e) {
                    Toast.makeText(this, "Save failed", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Save failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SAVE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Credentials saved successfully", Toast.LENGTH_LONG).show();
            } else {
                Log.d(TAG, "Save cancelled by user");
            }

            showNewsFeed();

        }
    }

    private void showNewsFeed() {
        startActivity(new Intent(this, NewsFeedActivity.class));
        finish();
    }
}
