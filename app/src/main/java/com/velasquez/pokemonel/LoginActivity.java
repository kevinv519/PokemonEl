package com.velasquez.pokemonel;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 123;

    private TextView mTvError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mTvError = findViewById(R.id.tv_error_sign_in);

        findViewById(R.id.btn_sign_in).setOnClickListener(v -> signInIntent());

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            signInIntent();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                mTvError.setVisibility(View.GONE);
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Log.d(TAG, "onActivityResult: user display name " + user.getDisplayName());
            } else {
                mTvError.setVisibility(View.VISIBLE);
                if (response == null) {
                    mTvError.setText(R.string.sign_in_cancelled);
                } else if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    mTvError.setText(R.string.no_internet_connection);
                } else {
                    mTvError.setText("Error: " + response.getError().getMessage());
                }
            }
        }
    }

    private void signInIntent() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.FacebookBuilder().build());

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setIsSmartLockEnabled(false)
                        .setLogo(R.drawable.pokemon_logo)
                        .build(), RC_SIGN_IN
        );
    }
}
