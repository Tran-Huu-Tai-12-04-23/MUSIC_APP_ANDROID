package Fragment.ForgotPassword;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.musicplayer.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import io.github.muddz.styleabletoast.StyleableToast;
import utils.LoadingDialog;
import utils.Util;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EnterYourPhoneToVerify#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EnterYourPhoneToVerify extends Fragment {
    private  LoadingDialog loadingDialog ;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public EnterYourPhoneToVerify() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EnterYourPhoneToVerify.
     */
    // TODO: Rename and change types and number of parameters
    public static EnterYourPhoneToVerify newInstance(String param1, String param2) {
        EnterYourPhoneToVerify fragment = new EnterYourPhoneToVerify();
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
        // Inflate the layout for this fragment
        loadingDialog = new LoadingDialog(requireActivity());
        View view = inflater.inflate(R.layout.fragment_enter_your_phone_to_verify, container, false);
        Button btnNext = view.findViewById(R.id.btn_next_step);
        EditText inputPhoneNumber = view.findViewById(R.id.input_phone_number);
        MaterialButton btnGoBack = view.findViewById(R.id.btn_go_back);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String phoneNumber = "+84"+inputPhoneNumber.getText().toString();
                if(! Util.isValidVietnamesePhoneNumber(String.format("0%s", phoneNumber.substring(3, phoneNumber.length())))) {
                    StyleableToast.makeText(requireContext(), "Số điện thoại không hợp lệ!", Toast.LENGTH_LONG, R.style.toast_error).show();
                    return;
                }

                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                EnterOTP enterOTP = new EnterOTP();
                enterOTP.phoneNumber = phoneNumber;
                fragmentTransaction.replace(R.id.fragmentContainerView, enterOTP);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

            }
        });


        btnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().onBackPressed();
            }
        });

        return view ;
    }


}