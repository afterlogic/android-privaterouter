package com.PrivateRouter.PrivateMail.encryption;

import android.os.AsyncTask;

import com.PrivateRouter.PrivateMail.PrivateMailApplication;
import com.PrivateRouter.PrivateMail.model.PGPKey;

import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.openpgp.PGPException;
import org.pgpainless.PGPainless;
import org.pgpainless.key.collection.PGPKeyRing;
import org.pgpainless.key.generation.KeySpec;
import org.pgpainless.key.generation.type.RSA_GENERAL;
import org.pgpainless.key.generation.type.length.RsaLength;
import org.pgpainless.util.Passphrase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;

public class GenerateTask extends AsyncTask<Void, Void, Void>  {

    String userId;
    String strength;
    String password;
    private Runnable runnableOnFinish;

    public GenerateTask(String userId, String strength, String password ) {
        this.userId = userId;
        this.strength = strength;
        this.password = password;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        generateKey();
        return null;
    }

    private void generateKey() {
        try {
            String selectedLengthStr = strength;
            RsaLength rsaLength;

            if (selectedLengthStr.equals("1024"))
                rsaLength = RsaLength._1024;
            else if (selectedLengthStr.equals("2048"))
                rsaLength = RsaLength._2048;
            else if (selectedLengthStr.equals("3072"))
                rsaLength = RsaLength._3072;
            else if (selectedLengthStr.equals("4096"))
                rsaLength = RsaLength._4096;
            else
                rsaLength = RsaLength._8192;


            PGPKeyRing keyRing = PGPainless.generateKeyRing().withMasterKey(
                    KeySpec.getBuilder(RSA_GENERAL.withLength(rsaLength))
                            .withDefaultKeyFlags()
                            .withDefaultAlgorithms())
                    .withPrimaryUserId(userId)
                    .withPassphrase(new Passphrase(password.toCharArray()) )
                    .build();


            ArrayList<PGPKey> pgpKeys = new ArrayList<>(2);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ArmoredOutputStream armoredOutput = new ArmoredOutputStream(outputStream);
            armoredOutput.write(keyRing.getSecretKeys().getEncoded() );
            armoredOutput.flush();
            armoredOutput.close();



            String secretCode  = new String( outputStream.toByteArray(),  "UTF-8" );
            PGPKey pgpKey = new PGPKey();
            pgpKey.setUserID( userId );
            pgpKey.setStrength(rsaLength.getLength());
            pgpKey.setType(PGPKey.PRIVATE);
            pgpKey.setKeyObject(secretCode );
            pgpKeys.add(pgpKey);


            outputStream = new ByteArrayOutputStream();
            armoredOutput = new ArmoredOutputStream(outputStream);
            armoredOutput.write(keyRing.getPublicKeys().getEncoded() );
            armoredOutput.flush();
            armoredOutput.close();

            String publicCode  = new String( outputStream.toByteArray(),  "UTF-8" );
            PGPKey pgpPubKey = new PGPKey();
            pgpPubKey.setUserID( userId );
            pgpPubKey.setStrength(rsaLength.getLength());
            pgpPubKey.setType(PGPKey.PUBLIC);
            pgpPubKey.setKeyObject(publicCode );
            pgpKeys.add(pgpPubKey);

            PrivateMailApplication.getInstance().getKeysRepository().addKeys(pgpKeys);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (PGPException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPostExecute(Void result) {
        if (runnableOnFinish!=null)
            runnableOnFinish.run();
    }


    public Runnable getRunnableOnFinish() {
        return runnableOnFinish;
    }

    public void setRunnableOnFinish(Runnable runnableOnFinish) {
        this.runnableOnFinish = runnableOnFinish;
    }
}
