package fragment.startApp;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import com.example.musicplayer.R;
import com.google.gson.Gson;

import Service.FetchDataAsync;
import constanst.Constanst;
import io.github.muddz.styleabletoast.StyleableToast;
import model.User;
import utils.Util;

public class RegisterFragment extends Fragment implements LoaderManager.LoaderCallbacks<String> {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private FragmentManager fragMgr ;
    private ConstraintLayout loadingLayout;
    private String json = "";
    private String mParam1;
    private String mParam2;

    public RegisterFragment() {
        // Required empty public constructor
    }

    public static RegisterFragment newInstance(@Nullable String param1, @Nullable String param2) {
        RegisterFragment fragment = new RegisterFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        fragMgr = requireActivity().getSupportFragmentManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_register, container, false);

        Button btnNavigateLogin, btnRegister ;
        EditText inputUsername, inputPhoneNumber, inputPassword, inputConfirmPassword;


        //find var
        btnNavigateLogin = view.findViewById(R.id.btnNavigateLogin);
        btnRegister = view.findViewById(R.id.btnActionRegister);
        inputUsername = view.findViewById(R.id.inputUsername);
        inputPhoneNumber = view.findViewById(R.id.inputYourPhone);
        inputPassword = view.findViewById(R.id.inputPassword);
        inputConfirmPassword = view.findViewById(R.id.inputConfirmPassword);
        loadingLayout = view.findViewById(R.id.loading_sign_up);
//
        loadingLayout.setVisibility(View.GONE);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = inputUsername.getText().toString();
                String phoneNumber = inputPhoneNumber.getText().toString();
                String password = inputPassword.getText().toString();
                String confirmPassword = inputConfirmPassword.getText().toString();

                Log.i("Password", password);
                Log.i("confirm Password", confirmPassword);
                if( username.trim().equals("") && phoneNumber.trim().equals("") && password.trim().equals("") && confirmPassword.trim().equals("")) {
                    StyleableToast.makeText(getContext(), "Please, provide enough information to register!", Toast.LENGTH_LONG, R.style.toast_warn).show();
                    return;
                }else if( !password.trim().equals(confirmPassword.trim())) {
                    StyleableToast.makeText(getContext(), "Confirm password is not match!", Toast.LENGTH_LONG, R.style.toast_warn).show();
                    return;
                }else if(!Util.isValidVietnamesePhoneNumber(phoneNumber)) {
                    StyleableToast.makeText(getContext(), "Your phone invalid!", Toast.LENGTH_LONG, R.style.toast_error).show();
                    return;
                }
                User user = new User();
                user.setPassword(password);
                user.setUsername(username);
                user.setPhoneNumber(phoneNumber);
                Gson gson = new Gson();
                json = gson.toJson(user);


                if( loadingLayout != null ) {
                    loadingLayout.setVisibility(View.GONE);
                }


                LoaderManager.getInstance(RegisterFragment.this).destroyLoader(1);
                LoaderManager.getInstance(RegisterFragment.this).initLoader(1, null, (LoaderManager.LoaderCallbacks<String>)RegisterFragment.this).forceLoad();
            }
        });

        btnNavigateLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateLogin();
            }
        });
        return view;
    }

    public void navigateLogin() {
        if( fragMgr == null) return;

        LoginFragment loginFragment = new LoginFragment();

        Fragment fragment = fragMgr.findFragmentById(R.id.sign_in_fragment);

        if (fragment != null && fragment.isAdded()) {
            return;
        }

        FragmentTransaction transaction = fragMgr.beginTransaction();
        transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        if (fragment != null) {
            transaction.replace(R.id.mainLayoutStartScreen, fragment, Constanst.TAG_FRAGMENT_LOGIN);
        }else {
            transaction.replace(R.id.mainLayoutStartScreen, loginFragment, Constanst.TAG_FRAGMENT_LOGIN);
            transaction.addToBackStack(null);
        }

        transaction.commit();
    }


    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, @Nullable Bundle args) {
        FetchDataAsync fetchDataAsync = new FetchDataAsync(getContext());

        fetchDataAsync.setLinkAPI(Constanst.URL_SIGN_UP);
        fetchDataAsync.setJson(json);

        return fetchDataAsync;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {
        if( loadingLayout != null ) {
            loadingLayout.setVisibility(View.VISIBLE);
        }

        if( data == null ) {
            StyleableToast.makeText(getContext(), "Register failed!", Toast.LENGTH_LONG, R.style.toast_error).show();
            return;
        }
        loadingLayout.setVisibility(View.VISIBLE);
        StyleableToast.makeText(getContext(), "Register successfully!", Toast.LENGTH_LONG, R.style.toast_success).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                navigateLogin();
                loadingLayout.setVisibility(View.GONE);
            }
        }, 1000);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }
}