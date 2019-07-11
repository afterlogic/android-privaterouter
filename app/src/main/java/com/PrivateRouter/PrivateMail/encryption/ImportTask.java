package com.PrivateRouter.PrivateMail.encryption;

import android.text.TextUtils;

import com.PrivateRouter.PrivateMail.model.PGPKey;

import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.pgpainless.PGPainless;

import java.util.ArrayList;
import java.util.Iterator;

public class ImportTask {


    public ArrayList<PGPKey> importKeys(String key  ) {
        ArrayList<PGPKey> arrayList = new ArrayList<>();

        boolean hasKey = true;
        while ( hasKey) {

            boolean hasPrivateKey = false;
            boolean hasPublicKey = false;

            int index1 = key.indexOf("-----BEGIN PGP PRIVATE KEY BLOCK-----");
            int index2 = key.indexOf("-----END PGP PRIVATE KEY BLOCK-----");

            if (index1>=0 && index2>=0) {
                String privateKey = key.substring(index1, index2 + "-----END PGP PRIVATE KEY BLOCK-----".length() );
                //key = key.substring(0, index1)+ key.substring(index2+1);
                key = key.replace(privateKey, "");

                PGPKey pgpKey = importPrivateKey(privateKey);
                if (pgpKey!=null)
                    arrayList.add(pgpKey);

                hasPrivateKey = true;
            }

            index1 = key.indexOf("-----BEGIN PGP PUBLIC KEY BLOCK-----");
            index2 = key.indexOf("-----END PGP PUBLIC KEY BLOCK-----");

            if (index1>=0 && index2>=0) {
                String publicKey = key.substring(index1, index2+"-----END PGP PUBLIC KEY BLOCK-----".length());
                //key = key.substring(0, index1)+ key.substring(index2+1);
                key = key.replace(publicKey, "");

                PGPKey pgpKey = importPublicKey(publicKey);
                if (pgpKey!=null)
                    arrayList.add(pgpKey);

                hasPublicKey = true;
            }

            hasKey = hasPublicKey || hasPrivateKey;
        }

        return arrayList;
    }

    private PGPKey importPublicKey(String key  ) {
        try {
            PGPPublicKeyRing publicKeys = PGPainless.readKeyRing().publicKeyRing( key );
            Iterator<PGPPublicKey> iterable = publicKeys.getPublicKeys();
            while (iterable.hasNext()) {
                PGPKey pgpKey = new PGPKey();
                PGPPublicKey pgpPublicKey = iterable.next();

                if (pgpPublicKey.isEncryptionKey() )
                    pgpKey.setType("private");

                pgpKey.setType(PGPKey.PUBLIC);
                pgpKey.setId( pgpPublicKey.getKeyID());

                pgpKey.setStrength( pgpPublicKey.getBitStrength() );
                pgpKey.setKeyObject( key );

                Iterator<String> userIdIter = pgpPublicKey.getUserIDs();
                while (userIdIter.hasNext()) {
                    String str = userIdIter.next();
                    if (!TextUtils.isEmpty(str)) {
                        pgpKey.setUserID(str);
                        return pgpKey;
                    }
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private PGPKey importPrivateKey(String key  ) {
        try {
            PGPSecretKeyRing secretKeys = PGPainless.readKeyRing().secretKeyRing( key );
            Iterator<PGPSecretKey> iterable = secretKeys.getSecretKeys();
            while (iterable.hasNext()) {
                PGPKey pgpKey = new PGPKey();
                PGPSecretKey pgpSecretKey = iterable.next();


                pgpKey.setType(PGPKey.PRIVATE);
                pgpKey.setId( pgpSecretKey.getKeyID());

                pgpKey.setStrength( 2048 ); //TODO
                pgpKey.setKeyObject( key );

                Iterator<String> userIdIter = pgpSecretKey.getUserIDs();
                while (userIdIter.hasNext()) {
                    String str = userIdIter.next();
                    if (!TextUtils.isEmpty(str)) {
                        pgpKey.setUserID(str);
                        return pgpKey;
                    }
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
