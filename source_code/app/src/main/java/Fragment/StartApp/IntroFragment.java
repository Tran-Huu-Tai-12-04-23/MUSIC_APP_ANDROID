package Fragment.StartApp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.musicplayer.R;

import Constanst.Constant;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link IntroFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class IntroFragment extends Fragment {


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public IntroFragment() {
        // Required empty public constructor

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Intro.
     */
    // TODO: Rename and change types and number of parameters
    public static IntroFragment newInstance(String param1, String param2) {
        IntroFragment fragment = new IntroFragment();
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
        View view =  inflater.inflate(R.layout.fragment_intro, container, false);
        Button btnLogin = view.findViewById(R.id.btnLogin);
        Button btnRegister = view.findViewById(R.id.btnActionLogin);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterFragment registerFragment = new RegisterFragment();

                FragmentManager fragMgr = requireActivity().getSupportFragmentManager();

                Fragment fragment = fragMgr.findFragmentById(R.id.register_fragment);

                if (fragment != null && fragment.isAdded()) {
                    return;
                }
                FragmentTransaction transaction = fragMgr.beginTransaction();
                transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                if (fragment != null) {
                    transaction.replace(R.id.mainLayoutStartScreen, fragment, Constant.TAG_FRAGMENT_REGISTER);
                }else{
                    transaction.replace(R.id.mainLayoutStartScreen, registerFragment, Constant.TAG_FRAGMENT_REGISTER);
                    transaction.addToBackStack(null);
                }

                transaction.commit();

            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginFragment loginFragment = new LoginFragment();

                FragmentManager fragMgr = requireActivity().getSupportFragmentManager();
                Fragment fragment = fragMgr.findFragmentById(R.id.sign_in_fragment);


                if (fragment != null && fragment.isAdded()) {
                    return;
                }

                FragmentTransaction transaction = fragMgr.beginTransaction();
                transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);

                if (fragment != null) {
                    transaction.replace(R.id.mainLayoutStartScreen, fragment, Constant.TAG_FRAGMENT_LOGIN);
                }else{
                    transaction.replace(R.id.mainLayoutStartScreen, loginFragment, Constant.TAG_FRAGMENT_LOGIN);
                    transaction.addToBackStack(null);
                }
                transaction.commit();

            }
        });

        return view;
    }


}