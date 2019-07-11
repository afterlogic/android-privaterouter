package com.PrivateRouter.PrivateMail.view;

import android.text.TextUtils;

public class EmailValidator {
    public final static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }



    public final static boolean isValidPass(CharSequence target) {
        if (target==null || target.length()<8) {
            return false;
        }


        boolean haveDigit = false;
        boolean haveUpperAlphabet = false;
        boolean haveLowerAlphabet = false;

        for (int i = 0; i< target.length();i++) {
            char ch = target.charAt(i);
            if (Character.isDigit(ch) )
                haveDigit = true;
            if (Character.isLowerCase(ch) )
                haveLowerAlphabet = true;
            if (Character.isUpperCase(ch) )
                haveUpperAlphabet = true;
        }

        return haveDigit && haveLowerAlphabet && haveUpperAlphabet;

    }
}
