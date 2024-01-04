package Fragment.StartApp;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.musicplayer.R;
import com.example.musicplayer.activity.ForGotPassword.ForgotPasswordActivity;
import com.example.musicplayer.activity.HomeActivity;
import com.example.musicplayer.activity.IntroActivity;

import Service.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import utils.LoadingDialog;
import Constanst.Constant;
import io.github.muddz.styleabletoast.StyleableToast;
import Model.User;
import DTO.*;


public class LoginFragment extends Fragment{
    private FragmentManager fragMgr ;
    private String json = "";
    private LoadingDialog loadingDialog;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public LoginFragment() {
    }


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
        loadingDialog = new LoadingDialog(requireActivity());
        //hidden loading

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

                boolean error = false;
                if( username.equals("")) {
                    StyleableToast.makeText(requireContext(), "Please enter username of account!", Toast.LENGTH_LONG, R.style.toast_warn).show();
                    error = true;
                } else if( password.equals("")) {
                    error = true;
                    StyleableToast.makeText(requireContext(), "Please enter password of account!", Toast.LENGTH_LONG, R.style.toast_warn).show();
                };
//
                if( error ) return;
                loadingDialog.startLoading();
                User user = new User();
                user.setUsername(username);
                user.setPassword(password);
                login(user);


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
                    transaction.replace(R.id.mainLayoutStartScreen, fragment, Constant.TAG_FRAGMENT_REGISTER);
                }else {
                    transaction.replace(R.id.mainLayoutStartScreen, registerFragment, Constant.TAG_FRAGMENT_REGISTER);
                    transaction.addToBackStack(null);
                }

                transaction.commit();
            }
        });

        return view;
    }


    private void login(User user) {
        ApiService.ApiService.login(user).enqueue(new Callback<ResponseData<User>>() {
            @Override
            public void onResponse(Call<ResponseData<User>> call, Response<ResponseData<User>> response) {
                if( response.isSuccessful()) {
                    ResponseData<User> userResponse = response.body();
                    assert userResponse != null;
                    if( userResponse.getData() == null ) {
                        StyleableToast.makeText(requireContext(), userResponse.getMessage(), Toast.LENGTH_LONG, R.style.toast_error).show();
                        loadingDialog.stopLoading();
                        return;
                    }

                    SharedPreferences preferences = requireContext().getSharedPreferences("LoginData", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("loggedIn", true);
                    editor.putString("userName", userResponse.getData().getUsername());
                    editor.putString("userId", String.valueOf(userResponse.getData().getId()));
                    editor.apply();
                    StyleableToast.makeText(requireContext(), "Đăng nhập thành công!", Toast.LENGTH_LONG, R.style.toast_success).show();

                    loadingDialog.destroyDialog();
                    loadingDialog.startLoading();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            loadingDialog.stopLoading();
                            Intent intent = new Intent(getContext(), HomeActivity.class);
                            startActivity(intent);

                            IntroActivity introActivity = (IntroActivity) requireActivity();
                            introActivity.finish();
                        }
                    }, 1000);

                }else {
                    StyleableToast.makeText(requireContext(), "Đăng nhập thất bại!", Toast.LENGTH_LONG, R.style.toast_error).show();
                }
                loadingDialog.stopLoading();
            }

            @Override
            public void onFailure(Call<ResponseData<User>> call, Throwable t) {
                Toast.makeText(getContext(), t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                loadingDialog.stopLoading();
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if( loadingDialog != null ) {
            loadingDialog.destroyDialog();
        }
    }
}