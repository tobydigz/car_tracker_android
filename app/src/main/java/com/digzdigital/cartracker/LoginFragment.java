package com.digzdigital.cartracker;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LoginFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class LoginFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {
    private LoginFragmentInteractionListener listener;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private static final String TAG = "car_tracker_digz";

    private GoogleApiClient googleApiClient;
    public LoginFragment() {
        // Required empty public constructor
    }
@Override
public void onCreate(Bundle savedInstanceState){
    super.onCreate(savedInstanceState);
    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("124184582217-gacf7taojbig90k7u6vnpajogcgbpuhe.apps.googleusercontent.com")
            .requestEmail()
            .build();

    googleApiClient = new GoogleApiClient.Builder(getActivity())
            .enableAutoManage(getActivity() /* FragmentActivity */, this /* OnConnectionFailedListener */)
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .build();
    auth = listener.getFirebaseAuth();
    setupAuthListener();
}

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authStateListener != null) {
            auth.removeAuthStateListener(authStateListener);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        SignInButton signInButton = (SignInButton) view.findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(this);
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (listener != null) {
            listener.onUserLoggedin();
        }
    }

    private void setupAuthListener() {
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    listener.onUserLoggedin();
                    user.getUid();

                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof LoginFragmentInteractionListener) {
            listener = (LoginFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement LoginFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onClick(View view) {
        listener.onGoogleLogin(googleApiClient);
    }

    public void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(getActivity(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }





                        // ...
                    }
                });
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface LoginFragmentInteractionListener {
        // TODO: Update argument type and name
        void onGoogleLogin(GoogleApiClient googleApiClient);
        void onUserLoggedin();
        FirebaseAuth getFirebaseAuth();
    }
}
