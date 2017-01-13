package com.digzdigital.cartracker;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link InputIdFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class InputIdFragment extends Fragment implements View.OnClickListener{

    private OnFragmentInteractionListener listener;
    private EditText deviceIdText;

    public InputIdFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_input_id, container, false);
        Button go = (Button) view.findViewById(R.id.go);
        Button wayFinder = (Button) view.findViewById(R.id.wayFinder);
        Button signOut = (Button) view.findViewById(R.id.signOut);
        deviceIdText = (EditText)view.findViewById(R.id.deviceIdText);
        go.setOnClickListener(this);
        wayFinder.setOnClickListener(this);
        signOut.setOnClickListener(this);
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            listener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.go:
                listener.onGoPressed(deviceIdText.getText().toString());
                break;
            case R.id.signOut:
                listener.signOut();
                break;
            case R.id.wayFinder:
                listener.onWayFindPressed();
        }
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onGoPressed(String deviceId);
        void signOut();
        void onWayFindPressed();
    }
}
