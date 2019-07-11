package com.PrivateRouter.PrivateMail.repository;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.PrivateRouter.PrivateMail.PrivateMailApplication;
import com.PrivateRouter.PrivateMail.model.PGPKey;
import com.PrivateRouter.PrivateMail.view.utils.EmailUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class KeysRepository {

    public KeysRepository() {
        load();
    }

    private static final String TAG = "KeysRepository";

    private ArrayList<PGPKey> keys = null;

    private void load() {
        Log.d(TAG, "load");
        try {
            Context context = PrivateMailApplication.getContext();
            File file = new File(context.getDir("data", MODE_PRIVATE), "map");
            if (!file.exists()) {
                Log.d(TAG, "empty");
                keys = new ArrayList<>();
                return;
            }
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file ));

            keys = (ArrayList<PGPKey>) ois.readObject();

            Log.d(TAG, "load complete keys.size="+keys.size());

        } catch (Exception ex) {
            ex.printStackTrace();
            keys = new ArrayList<>();
        }
    }

    public void save() {
        try {
            Context context = PrivateMailApplication.getContext();
            File file = new File(context.getDir("data", MODE_PRIVATE), "map");
            ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file));
            outputStream.writeObject(keys);
            outputStream.flush();
            outputStream.close();

            Log.d(TAG, "save");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<PGPKey> getKeys() {
        if (keys==null)
            load();
        if (keys==null)
            return  new ArrayList<>();
        return keys;
    }

    public ArrayList<PGPKey> getPublicKeys() {
        ArrayList<PGPKey> publicKey = new ArrayList<>();
        for (PGPKey pgpKey : keys) {
            if (PGPKey.PUBLIC.equals( pgpKey.getType() ) )
                publicKey.add(pgpKey);
        }
        return publicKey;
    }

    public ArrayList<PGPKey> getPrivateKeys() {
        ArrayList<PGPKey> privateKey = new ArrayList<>();
        for (PGPKey pgpKey : keys) {
            if (PGPKey.PRIVATE.equals( pgpKey.getType() ) )
                privateKey.add(pgpKey);
        }
        return privateKey;
    }

    public void addKeys( List<PGPKey> keys) {
        this.keys.addAll(keys);
        save();
    }

    public boolean hasKey(PGPKey pgpKey) {
        for (PGPKey refKey : keys) {
            String refKeyData = refKey.getKeyObject().toString().trim();
            String keyData = pgpKey.getKeyObject().toString().trim();

            if ( refKeyData.equals( keyData ) && refKey.getId() == pgpKey.getId() )
                return true;
        }

        return false;
    }

    public boolean removeKey(PGPKey pgpKey) {
        for (PGPKey refKey : keys) {
            String refKeyData = refKey.getKeyObject().toString().trim();
            String keyData = pgpKey.getKeyObject().toString().trim();

            if ( refKeyData.equals( keyData ) && refKey.getId() == pgpKey.getId() ) {
                keys.remove(refKey);
                save();
                return true;
            }
        }

        return false;
    }

    public PGPKey getPublicKey(String userID) {
        return getKey(userID, PGPKey.PUBLIC);
    }

    public PGPKey getPrivateKey(String userID) {
        return getKey(userID, PGPKey.PRIVATE);
    }

    public PGPKey getKey(String userID, String type) {
        String clearUserId = EmailUtils.decapsulationEmail(userID);

        for (PGPKey refKey : keys) {
            if (TextUtils.isEmpty(type) || type.equals(refKey.getType()  )) {
                String refUserID = EmailUtils.decapsulationEmail(refKey.getUserID());

                if ( refUserID.equalsIgnoreCase(clearUserId))
                    return refKey;
            }
        }

        return null;
    }
}
