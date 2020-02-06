package com.PrivateRouter.PrivateMail.repository;


import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import android.text.TextUtils;

import com.PrivateRouter.PrivateMail.PrivateMailApplication;
import com.PrivateRouter.PrivateMail.model.Account;
import com.PrivateRouter.PrivateMail.model.User;
import com.PrivateRouter.PrivateMail.network.ApiFactory;
import com.google.gson.Gson;


public class LoggedUserRepository {

    @NonNull
    public static LoggedUserRepository getInstance() {
        synchronized (LoggedUserRepository.class) {
            if (PrivateMailApplication.getInstance().getLoggedUserRepository() == null) {
                LoggedUserRepository loggedUserRepository = new LoggedUserRepository();
                PrivateMailApplication.getInstance().setLoggedUserRepository(loggedUserRepository);
            }
        }
        return PrivateMailApplication.getInstance().getLoggedUserRepository();
    }


    private String login;
    private String authToken;
    private User user;


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Account getActiveAccount() {
        if (user != null) {
            for (Account account : user.getAccounts()) {
                if (account.getEmail().toLowerCase().equals(login.toLowerCase()))
                    return account;
            }
        }
        return null;
    }


    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void save(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("login", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String userJson = gson.toJson(user);

        editor.putString("user", userJson);
        editor.putString("login", login);
        editor.putString("authToken", authToken);

        editor.apply();
    }

    public void logout(Context context) {
        authToken = "";
        ApiFactory.setToken( "" );
        user = null;
        login =  "";
        save(context);
    }

    public boolean load(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("login", Context.MODE_PRIVATE);

        login = sharedPreferences.getString("login", "");
        if (TextUtils.isEmpty(login)) {
            return false;
        }
        else {
            authToken = sharedPreferences.getString("authToken", "");
            ApiFactory.setToken( authToken );

            String userJson = sharedPreferences.getString("user", "");

            Gson gson = new Gson();
            user = gson.fromJson(userJson, User.class);

            return true;
        }

    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}


