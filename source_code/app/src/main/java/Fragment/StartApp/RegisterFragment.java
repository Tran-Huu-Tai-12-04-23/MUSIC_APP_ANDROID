package Fragment.StartApp;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.musicplayer.R;

import DTO.ResponseData;
import Service.ApiService;
import Constanst.Constant;
import io.github.muddz.styleabletoast.StyleableToast;
import Model.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import utils.LoadingDialog;
import utils.Util;

public class RegisterFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private FragmentManager fragMgr;
    private String mParam1;
    private String mParam2;
    LoadingDialog loadingDialog;

    public RegisterFragment() {
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
        loadingDialog = new LoadingDialog(requireActivity());
        Button btnNavigateLogin, btnRegister;
        EditText inputUsername, inputPhoneNumber, inputPassword, inputConfirmPassword;


        //find var
        btnNavigateLogin = view.findViewById(R.id.btnNavigateLogin);
        btnRegister = view.findViewById(R.id.btnActionRegister);
        inputUsername = view.findViewById(R.id.inputUsername);
        inputPhoneNumber = view.findViewById(R.id.inputYourPhone);
        inputPassword = view.findViewById(R.id.inputPassword);
        inputConfirmPassword = view.findViewById(R.id.inputConfirmPassword);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = inputUsername.getText().toString();
                String phoneNumber = inputPhoneNumber.getText().toString();
                String password = inputPassword.getText().toString();
                String confirmPassword = inputConfirmPassword.getText().toString();

//                Log.i("Password", password);
//                Log.i("confirm Password", confirmPassword);
                if (username.trim().equals("") && phoneNumber.trim().equals("") && password.trim().equals("") && confirmPassword.trim().equals("")) {
                    StyleableToast.makeText(requireContext(), "Please, provide enough information to register!", Toast.LENGTH_LONG, R.style.toast_warn).show();
                    return;
                } else if (!password.trim().equals(confirmPassword.trim())) {
                    StyleableToast.makeText(requireContext(), "Confirm password is not match!", Toast.LENGTH_LONG, R.style.toast_warn).show();
                    return;
                } else if (!Util.isValidVietnamesePhoneNumber(phoneNumber)) {
                    StyleableToast.makeText(requireContext(), "Your phone invalid!", Toast.LENGTH_LONG, R.style.toast_error).show();
                    return;
                }
                User user = new User();
                user.setPassword(password);
                user.setUsername(username);
                user.setPhoneNumber(phoneNumber);

                register(user);


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

    private void register(User user) {
        ApiService.ApiService.register(user).enqueue(new Callback<ResponseData<User>>() {
            @Override
            public void onResponse(Call<ResponseData<User>> call, Response<ResponseData<User>> response) {
                loadingDialog.stopLoading();
                Toast.makeText(getContext(), response.toString(), Toast.LENGTH_SHORT).show();
                if (!response.isSuccessful()) {
                    StyleableToast.makeText(requireContext(), "Đăng ký thất bại!", Toast.LENGTH_LONG, R.style.toast_error).show();
                    return;
                }

                ResponseData<User> userResponse = response.body();

                assert userResponse != null;
                if (userResponse.getData() == null) {
                    StyleableToast.makeText(requireContext(), userResponse.getMessage(), Toast.LENGTH_LONG, R.style.toast_error).show();
                    return;
                }
                loadingDialog.startLoading();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        StyleableToast.makeText(requireContext(), "Đăng ký thành công!", Toast.LENGTH_LONG, R.style.toast_success).show();
                        navigateLogin();
                        loadingDialog.stopLoading();
                    }
                }, 1000);
            }

            @Override
            public void onFailure(Call<ResponseData<User>> call, Throwable t) {
                StyleableToast.makeText(requireContext(), t.getLocalizedMessage(), Toast.LENGTH_LONG, R.style.toast_success).show();
            }
        });
    }

    public void navigateLogin() {
        if (fragMgr == null) return;

        LoginFragment loginFragment = new LoginFragment();

        Fragment fragment = fragMgr.findFragmentById(R.id.sign_in_fragment);

        if (fragment != null && fragment.isAdded()) {
            return;
        }

        FragmentTransaction transaction = fragMgr.beginTransaction();
        transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        if (fragment != null) {
            transaction.replace(R.id.mainLayoutStartScreen, fragment, Constant.TAG_FRAGMENT_LOGIN);
        } else {
            transaction.replace(R.id.mainLayoutStartScreen, loginFragment, Constant.TAG_FRAGMENT_LOGIN);
            transaction.addToBackStack(null);
        }

        transaction.commit();
    }
}