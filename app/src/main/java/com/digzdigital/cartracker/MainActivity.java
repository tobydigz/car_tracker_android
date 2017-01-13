package com.digzdigital.cartracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements LoginFragment.LoginFragmentInteractionListener, InputIdFragment.OnFragmentInteractionListener {

    private static final int RC_SIGN_IN = 0;
    private FirebaseAuth auth;
    private Fragment loginFragment, inputIdFragment;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();

        fragmentManager = getSupportFragmentManager();


    }

    @Override
    public void onResume() {
        super.onResume();
        if (auth.getCurrentUser() == null) {
            switchFragment(getLoginFragment());
            return;
        }
        switchFragment(getInputIdFragment());
    }

    private Fragment getLoginFragment() {
        if (loginFragment == null) loginFragment = new LoginFragment();
        return loginFragment;
    }

    private Fragment getInputIdFragment() {
        if (inputIdFragment == null)  inputIdFragment = new InputIdFragment();
        return inputIdFragment;
    }


    @Override
    public void onGoogleLogin(GoogleApiClient googleApiClient) {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            LoginFragment loginFragment = (LoginFragment) getLoginFragment();
            loginFragment.firebaseAuthWithGoogle(result.getSignInAccount());
        } else {
            // Signed out, show unauthenticated UI.
            Toast.makeText(this, "error" + result.getStatus().getStatusMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onUserLoggedin() {

        switchFragment(getInputIdFragment());
    }

    @Override
    public FirebaseAuth getFirebaseAuth() {
        if (auth==null)auth = FirebaseAuth.getInstance();
        return auth;
    }

    private void switchFragment(Fragment fragment) {
        fragmentManager.beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.contentFrame, fragment)
                .commit();
    }


    @Override
    public void onGoPressed(String deviceId) {

        Intent intent = new Intent(MainActivity.this, MapsActivity.class);
        intent.putExtra("deviceId", deviceId);
        startActivity(intent);
    }

    @Override
    public void signOut() {
        auth.signOut();
        switchFragment(getLoginFragment());
    }

    @Override
    public void onWayFindPressed() {
        startActivity(new Intent(this, NavigationActivity.class));
    }


}
