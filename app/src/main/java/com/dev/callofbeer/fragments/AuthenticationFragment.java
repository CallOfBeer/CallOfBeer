package com.dev.callofbeer.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.dev.callofbeer.R;
import com.dev.callofbeer.activities.CallOfBeerActivity;
import com.rengwuxian.materialedittext.MaterialEditText;

/**
 * A simple {@link Fragment} subclass.
 * to handle interaction events.
 */
public class AuthenticationFragment extends Fragment {

    private MaterialEditText usernameEditText;
    private MaterialEditText passwordEditText;
    private Button loginButton;
    private Button toRegisterButton;

    private MaterialEditText usernameRegisterEditText;
    private MaterialEditText emailRegisterEditText;
    private MaterialEditText passwordRegisterEditText;
    private MaterialEditText passwordRepeatRegisterEditText;
    private Button registerButton;
    private Button toLoginButton;

    private boolean toLogout = false;

    public AuthenticationFragment() {
    }

    public static AuthenticationFragment newInstance(boolean loginAction) {
        AuthenticationFragment myFragment = new AuthenticationFragment();

        Bundle args = new Bundle();
        args.putBoolean("loginAction", loginAction);
        myFragment.setArguments(args);

        return myFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_authentication, container, false);

        usernameEditText = (MaterialEditText) root.findViewById(R.id.cob_authent_login_username);
        passwordEditText = (MaterialEditText) root.findViewById(R.id.cob_authent_login_password);
        passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        usernameRegisterEditText = (MaterialEditText) root.findViewById(R.id.cob_authent_register_username);
        emailRegisterEditText = (MaterialEditText) root.findViewById(R.id.cob_authent_register_email);
        passwordRegisterEditText = (MaterialEditText) root.findViewById(R.id.cob_authent_register_password);
        passwordRepeatRegisterEditText = (MaterialEditText) root.findViewById(R.id.cob_authent_register_password_repeat);

        passwordRegisterEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        passwordRepeatRegisterEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        loginButton = (Button) root.findViewById(R.id.cob_authent_login_button);
        registerButton = (Button) root.findViewById(R.id.cob_authent_register_button);
        toLoginButton = (Button) root.findViewById(R.id.cob_authent_to_login_button);
        toRegisterButton = (Button) root.findViewById(R.id.cob_authent_to_register_button);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((CallOfBeerActivity) getActivity()).getUserManager().logByUsernamePassword(usernameEditText.getText().toString(), passwordEditText.getText().toString());
            }
        });

        toRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().findViewById(R.id.cob_authent_login_layout).setVisibility(View.GONE);
                getActivity().findViewById(R.id.cob_authent_register_layout).setVisibility(View.VISIBLE);
            }
        });

        toLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().findViewById(R.id.cob_authent_register_layout).setVisibility(View.GONE);
                getActivity().findViewById(R.id.cob_authent_login_layout).setVisibility(View.VISIBLE);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((CallOfBeerActivity) getActivity()).getUserManager().registerByUsernameEmailPassword(
                        usernameRegisterEditText.getText().toString(),
                        emailRegisterEditText.getText().toString(),
                        passwordRegisterEditText.getText().toString()
                );
            }
        });

        if (!getArguments().getBoolean("loginAction")) {
            if (getActivity() == null) {
                toLogout = true;
            } else {
                ((CallOfBeerActivity) getActivity()).getUserManager().logoutUser();
            }
        }

        return root;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            if (toLogout) {
                SharedPreferences preferences  = activity.getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("user", "null");
                editor.apply();
            }
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
