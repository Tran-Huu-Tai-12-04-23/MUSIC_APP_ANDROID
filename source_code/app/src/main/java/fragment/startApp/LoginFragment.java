package fragment.startApp;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.musicplayer.R;
import com.example.musicplayer.activity.HomeActivity;
import com.example.musicplayer.activity.forGotPassword.ForgotPasswordActivity;
import com.google.gson.Gson;

import Response.UserResponse;
import constanst.Constanst;
import Service.FetchDataAsync;
import io.github.muddz.styleabletoast.StyleableToast;
import model.User;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment implements LoaderManager.LoaderCallbacks<String> {
    private FragmentManager fragMgr ;
    private ConstraintLayout loadingLayout;
    private String json = "";
//    private SendRequest sendRequest;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LoginFragment() {
//        sendRequest = new SendRequest(Constanst.BASE_URL_API);
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragement.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login_fragement, container, false);

        //hidden loading
        loadingLayout = view.findViewById(R.id.loading_sign_in);
        loadingLayout.setVisibility(View.GONE);
//

        Button btnRegister,btnForgotPassword, btnLogin;
        EditText inputUsername, inputPassword;

//
        inputUsername = view.findViewById(R.id.inputUsername);
        inputPassword = view.findViewById(R.id.inputPassword);

        btnRegister =  view.findViewById(R.id.buttonRegister);
        btnForgotPassword =  view.findViewById(R.id.btnForgotPassword);
        btnLogin = view.findViewById(R.id.btnActionLogin);


//

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                call api

                String username = inputUsername.getText().toString();
                String password = inputPassword.getText().toString();

                Boolean error = false;
                if( username.equals("")) {
                    StyleableToast.makeText(getContext(), "Please enter username of account!", Toast.LENGTH_LONG, R.style.toast_warn).show();
                    error = true;
                } else if( password.equals("")) {
                    error = true;
                    StyleableToast.makeText(getContext(), "Please enter password of account!", Toast.LENGTH_LONG, R.style.toast_warn).show();
                };
//
                if( error ) return;
                loadingLayout.setVisibility(View.VISIBLE);
                Gson gson = new Gson();
                User user = new User();
                user.setUsername(username);
                user.setPassword(password);
                json = gson.toJson(user);

                LoaderManager.getInstance(LoginFragment.this).destroyLoader(0);
                LoaderManager.getInstance(LoginFragment.this).initLoader(0, null, (LoaderManager.LoaderCallbacks<String>)LoginFragment.this).forceLoad();

            }
        });


        btnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if( fragMgr == null) return;

                RegisterFragment registerFragment = new RegisterFragment();

                Fragment fragment = fragMgr.findFragmentById(R.id.register_fragment);

                if (fragment != null && fragment.isAdded()) {
                  return;
                }

                FragmentTransaction transaction = fragMgr.beginTransaction();
                transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                if (fragment != null) {
                    transaction.replace(R.id.mainLayoutStartScreen, fragment, Constanst.TAG_FRAGMENT_REGISTER);
                }else {
                    transaction.replace(R.id.mainLayoutStartScreen, registerFragment, Constanst.TAG_FRAGMENT_REGISTER);
                    transaction.addToBackStack(null);
                }

                transaction.commit();
            }
        });

        return view;
    }


    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, @Nullable Bundle args) {
        FetchDataAsync fetchDataAsync = new FetchDataAsync(getContext());
        fetchDataAsync.setJson(json);
        fetchDataAsync.setLinkAPI(Constanst.URL_SIGN_IN);
        return fetchDataAsync;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {
        if(loadingLayout != null ) {
            loadingLayout.setVisibility(View.GONE);
        }

        if( data == null) {
            StyleableToast.makeText(getContext(), "Login failed!", Toast.LENGTH_LONG, R.style.toast_error).show();
            return;
        }

        Gson gson = new Gson();
        UserResponse userResponse = gson.fromJson(data, UserResponse.class);

        if( userResponse.getData() == null ) return;

        SharedPreferences preferences = getContext().getSharedPreferences("LoginData", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("loggedIn", true);
        editor.putString("userName", userResponse.getData().getUsername());
        editor.apply();
        StyleableToast.makeText(getContext(), "Login successfully!", Toast.LENGTH_LONG, R.style.toast_success).show();

        loadingLayout.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getContext(), HomeActivity.class);
                startActivity(intent);
            }
        }, 1000);

    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }
}