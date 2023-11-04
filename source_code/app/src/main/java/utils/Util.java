package utils;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.musicplayer.R;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util  {

    public static void switchFragment(FragmentTransaction transactionFG, Fragment fragment,boolean isHasNotBack, String tag) {

        if( tag.trim().equals("")) {
            transactionFG.replace(R.id.mainLayoutStartScreen, fragment, tag);
        }else {
            transactionFG.replace(R.id.mainLayoutStartScreen, fragment);
        }
        if( isHasNotBack ==  false ) {
            transactionFG.addToBackStack(null);
        }

        transactionFG.commit();
    }

    public static boolean isValidVietnamesePhoneNumber(String phoneNumber) {
        // Define the regular expression pattern for a Vietnamese phone number
        String regex = "^(0[1-9][0-9]{8}|\\+84[1-9][0-9]{8})$";

        // Create a Pattern object
        Pattern pattern = Pattern.compile(regex);

        // Create a Matcher object
        Matcher matcher = pattern.matcher(phoneNumber);

        // Check if the phone number matches the pattern
        return matcher.matches();


    }
    }
