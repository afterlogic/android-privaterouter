package com.PrivateRouter.PrivateMail.view.utils;

import android.content.Context;

import com.PrivateRouter.PrivateMail.model.Email;
import com.PrivateRouter.PrivateMail.model.EmailCollection;

public class EmailUtils {
    public static String getString(Context context, EmailCollection emailCollection) {
        return  getString(context, emailCollection, true);
    }
    public static String getString(Context context, EmailCollection emailCollection, boolean useName) {
        String string = "";
        if (emailCollection!=null && emailCollection.getEmails()!=null) {
            for (Email email : emailCollection.getEmails()) {
                if (!string.isEmpty())
                    string = string + ", ";

                if (email.getDisplayName()!=null && !email.getDisplayName().isEmpty() && useName ) {
                    string = string + email.getDisplayName();
                }
                else {
                    string = string + email.getEmail();
                }
            }
        }

        return string;
    }

    public static String encapsulationEmail (String email) {
        return  "<"+email+">";
    }

    public static String decapsulationEmail (String email) {
        String str =email.trim();
        if (str.charAt(0)== '<')
            str = str.substring(1);

        if (str.charAt(str.length()-1)== '>')
            str = str.substring(0, str.length()-1);

        return  str;
    }
}
