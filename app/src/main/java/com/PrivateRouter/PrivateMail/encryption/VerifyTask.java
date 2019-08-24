package com.PrivateRouter.PrivateMail.encryption;

import android.content.Context;
import android.os.AsyncTask;

import com.PrivateRouter.PrivateMail.PrivateMailApplication;
import com.PrivateRouter.PrivateMail.R;
import com.PrivateRouter.PrivateMail.model.Email;
import com.PrivateRouter.PrivateMail.model.Message;
import com.PrivateRouter.PrivateMail.model.PGPKey;
import com.PrivateRouter.PrivateMail.repository.KeysRepository;
import com.PrivateRouter.PrivateMail.view.utils.MessageUtils;

import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPPublicKeyRingCollection;
import org.pgpainless.PGPainless;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class VerifyTask extends AsyncTask<Void,  Void, Boolean> {


    private String failMessage = "";
    private String clearMessage = "";

    public VerifyTaskCallback getVerifyTaskCallback() {
        return verifyTaskCallback;
    }

    public void setVerifyTaskCallback(VerifyTaskCallback verifyTaskCallback) {
        this.verifyTaskCallback = verifyTaskCallback;
    }

    public interface VerifyTaskCallback {
        void onSuccessVerify(String clearMessage);
        void onFail(String description);
    }


    private VerifyTaskCallback verifyTaskCallback;
    Message message;

    public VerifyTask(Message message) {
        this.message = message;
        Context context = PrivateMailApplication.getContext();
        failMessage = context.getString(R.string.view_mail_fail_verify);
    }

    @Override
    protected Boolean doInBackground(Void... voids) {

        String str =  message.getPlainRaw() ;
     //   str = str.replace("<br />", "\n");
        String signTitle = "-----BEGIN PGP SIGNATURE-----";
        String endSignTitle = "-----END PGP SIGNATURE-----";
        int signEndIndex = str.lastIndexOf(endSignTitle)+endSignTitle.length();
        int signIndex = str.indexOf(signTitle);
        if (signIndex == -1) {
            return null;
        }

        String hashText = "Hash: ";
        int index = str.indexOf(hashText);
        int indexClearText  = str.indexOf("\n", index+hashText.length());


        String signatureString = str.substring(signIndex, signEndIndex);


        clearMessage  = str.substring( indexClearText+3, signIndex -2);

        InputStream signData = new ByteArrayInputStream(clearMessage.getBytes(StandardCharsets.UTF_8));
        InputStream signature = new ByteArrayInputStream(signatureString.getBytes(StandardCharsets.UTF_8));
        PGPPublicKeyRingCollection publicKeys =  getPublicKeyRings(message);
        if (publicKeys == null)
            return false;

        return SignHelper.verify( signData, signature,   publicKeys);
    }

    private PGPPublicKeyRingCollection getPublicKeyRings(Message message) {
        KeysRepository keysRepository = PrivateMailApplication.getInstance().getKeysRepository();
        Context context = PrivateMailApplication.getContext();
        String publicKeysArmored = "";
        for (Email email : message.getTo().getEmails()) {
            PGPKey pgpkey = keysRepository.getKey(email.getEmail(), PGPKey.PUBLIC);
            if (pgpkey != null) {
                String data = pgpkey.getKeyObject().toString();

                publicKeysArmored = publicKeysArmored + data + "\n";
            } else {
                failMessage = String.format(context.getString(R.string.encrypt_error_not_found_public_key), email.getEmail());
                return null;
            }

        }
        PGPPublicKeyRingCollection pgpPublicKeyRings = null;

        try {
            pgpPublicKeyRings = PGPainless.readKeyRing().publicKeyRingCollection(publicKeysArmored);
        } catch (IOException e) {


        } catch (PGPException e) {
            e.printStackTrace();
        }
        return pgpPublicKeyRings;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (verifyTaskCallback !=null) {
            if (result) {
                verifyTaskCallback.onSuccessVerify(clearMessage );
            }
            else {
                verifyTaskCallback.onFail(failMessage);
            }
        }

    }

}
