package com.PrivateRouter.PrivateMail.repository;

import android.util.Log;

import com.PrivateRouter.PrivateMail.model.Group;
import com.PrivateRouter.PrivateMail.model.Identities;
import com.PrivateRouter.PrivateMail.model.errors.ErrorType;
import com.PrivateRouter.PrivateMail.network.requests.CallGetGroups;
import com.PrivateRouter.PrivateMail.network.requests.CallGetIdentities;
import com.PrivateRouter.PrivateMail.network.requests.CallRequestResult;
import com.PrivateRouter.PrivateMail.view.utils.Logger;

import java.util.ArrayList;

public class IdentitiesRepository  {

    private ArrayList<Identities> identities = new ArrayList<>();

    public void init() {

        loadIdentitiesFromDB();
        requestIdentities();
    }

    private void loadIdentitiesFromDB() {

    }

    private void cacheIdentitiesInDB() {

    }

    public ArrayList<Identities> getIdentities() {return identities; }

    private void requestIdentities() {
        CallGetIdentities callGetIdentities = new CallGetIdentities(new CallRequestResult() {
            @Override
            public void onSuccess(Object result) {
                synchronized (IdentitiesRepository.class) {
                    identities = (ArrayList<Identities>) result;
                }
                cacheIdentitiesInDB();
            }

            @Override
            public void onFail(ErrorType errorType, int serverCode) {
                Logger.e("Identities", "Identities errorType ="+errorType + " serverError = "+serverCode);
            }
        });
        callGetIdentities.start();
    }

    public void clear() {
        identities.clear();
    }
}
