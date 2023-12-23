package Fragment.ForgotPassword;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.loader.content.Loader;

import android.app.Application;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.musicplayer.R;
import com.example.musicplayer.activity.ForGotPassword.ChangePassword;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import io.github.muddz.styleabletoast.StyleableToast;
import utils.LoadingDialog;
import utils.Util;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EnterOTP#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EnterOTP extends Fragment {
    boolean isResend;
    Long timeoutSeconds = 60L;
    String verificationCode;
    PhoneAuthProvider.ForceResendingToken  resendingToken;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    String phoneNumber = "";
    private LoadingDialog loadingDialog;
    TextView resendOtpView;
    MaterialButton btnResendCode;
    private final Handler handler = new Handler();
    private String verificationId;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public EnterOTP() {
    }

    public static EnterOTP newInstance(String param1, String param2) {
        EnterOTP fragment = new EnterOTP();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_enter_o_t_p, container, false);
        loadingDialog = new LoadingDialog(requireActivity());

        MaterialButton btnVerifyOTP;
        TextInputEditText number1, number2, number3, number4, number5, number6;
        number1 = view.findViewById(R.id.number_1);
        number2 = view.findViewById(R.id.number_2);
        number3 = view.findViewById(R.id.number_3);
        number4 = view.findViewById(R.id.number_4);
        number5 = view.findViewById(R.id.number_5);
        number6 = view.findViewById(R.id.number_6);
        btnVerifyOTP = view.findViewById(R.id.btn_verify_otp);
        resendOtpView = view.findViewById(R.id.second_code_valid);
        btnResendCode = view.findViewById(R.id.btn_resend_code);

        number1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() >= 1) {
                    number2.requestFocus();
                }
            }
        });

        number2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() >= 1) {
                    number3.requestFocus();
                }
            }
        });

        number3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() >= 1) {
                    number4.requestFocus();
                }
            }
        });

        number4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() >= 1) {
                    number5.requestFocus();
                }
            }
        });

        number5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() >= 1) {
                    number6.requestFocus();
                }
            }
        });
        if( !phoneNumber.equals(""))
            sendOtp(phoneNumber, isResend);

        btnVerifyOTP.setOnClickListener((v) -> {
            String num1 = Objects.requireNonNull(number1.getText()).toString();
            String num2 = Objects.requireNonNull(number2.getText()).toString();
            String num3 = Objects.requireNonNull(number3.getText()).toString();
            String num4 = Objects.requireNonNull(number4.getText()).toString();
            String num5 = Objects.requireNonNull(number5.getText()).toString();
            String num6 = Objects.requireNonNull(number6.getText()).toString();

            if( num1.equals("") || num2.equals("") || num3.equals("")
                    || num4.equals("") || num5.equals("") || num6.equals("")) {
                StyleableToast.makeText(requireContext(),"Vui lòng nhập mã OTP!", Toast.LENGTH_LONG, R.style.toast_warn).show();
                return;
            }
            String OTP = num1+num2+num3+num4+num5+num6;
            if( OTP.length() > 6 ) {
                StyleableToast.makeText(requireContext(),"OTP chỉ 6 số!", Toast.LENGTH_LONG, R.style.toast_warn).show();
                return;
            }
            String enteredOtp  = num1+num2+num3+num4+num5+num6;
            PhoneAuthCredential credential =  PhoneAuthProvider.getCredential(verificationCode,enteredOtp);
            signIn(credential);
        });

        btnResendCode.setOnClickListener((v)->{
            sendOtp(phoneNumber,true);
        });


        return view;
    }

    void sendOtp(String phoneNumber,boolean isResend){
        startResendTimer();
        loadingDialog.startLoading();
        PhoneAuthOptions.Builder builder =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(timeoutSeconds, TimeUnit.SECONDS)
                        .setActivity(requireActivity())
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                signIn(phoneAuthCredential);
                                loadingDialog.stopLoading();
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
//                                AndroidUtil.showToast(getApplicationContext(),"OTP verification failed");
                                loadingDialog.stopLoading();
                            }

                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(s, forceResendingToken);
                                verificationCode = s;
                                resendingToken = forceResendingToken;
                                StyleableToast.makeText(requireContext(), "Mã OTP đã được gửi! Vui lòng kiểm tra!", Toast.LENGTH_LONG, R.style.toast_success).show();
                                loadingDialog.stopLoading();
                            }
                        });
        if(isResend){
            PhoneAuthProvider.verifyPhoneNumber(builder.setForceResendingToken(resendingToken).build());
        }else{
            PhoneAuthProvider.verifyPhoneNumber(builder.build());
        }


    }



    void signIn(PhoneAuthCredential phoneAuthCredential){
        loadingDialog.startLoading();
        //login and go to next activity
        mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    StyleableToast.makeText(requireContext(), "Xác thực thành công!", Toast.LENGTH_LONG, R.style.toast_success).show();
                    Intent intent = new Intent(requireContext(), ChangePassword.class);
                    intent.putExtra("phone", phoneNumber.substring(3));
                    startActivity(intent);
//                    requireActivity().finish();
                }else{
                    StyleableToast.makeText(requireContext(), "OTP không hợp lệ! Thử lại!", Toast.LENGTH_LONG, R.style.toast_error).show();
                }
                loadingDialog.stopLoading();

            }
        });


    }


    void startResendTimer() {
        resendOtpView.setEnabled(false);
        btnResendCode.setEnabled(false);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                timeoutSeconds--;
                resendOtpView.setText(" OTP gửi lại " + timeoutSeconds + " giây");

                if (timeoutSeconds <= 0) {
                    timeoutSeconds = 60L;
                    resendOtpView.setEnabled(true);
                    btnResendCode.setEnabled(true);
                } else {
                    handler.postDelayed(this, 1000);
                }
            }
        }, 1000);
    }



}