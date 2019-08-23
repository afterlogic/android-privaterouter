package com.PrivateRouter.PrivateMail.encryption;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.PrivateRouter.PrivateMail.PrivateMailApplication;
import com.PrivateRouter.PrivateMail.R;
import com.PrivateRouter.PrivateMail.model.Account;
import com.PrivateRouter.PrivateMail.model.Message;
import com.PrivateRouter.PrivateMail.model.PGPKey;
import com.PrivateRouter.PrivateMail.repository.KeysRepository;
import com.PrivateRouter.PrivateMail.repository.SettingsRepository;
import com.PrivateRouter.PrivateMail.view.utils.MessageUtils;


import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.bouncycastle.util.io.Streams;
import org.jetbrains.annotations.Nullable;
import org.pgpainless.PGPainless;
import org.pgpainless.algorithm.HashAlgorithm;
import org.pgpainless.algorithm.SymmetricKeyAlgorithm;
import org.pgpainless.decryption_verification.DecryptionStream;
import org.pgpainless.key.collection.PGPKeyRing;
import org.pgpainless.key.protection.KeyRingProtectionSettings;
import org.pgpainless.key.protection.PasswordBasedSecretKeyRingProtector;
import org.pgpainless.key.protection.SecretKeyPassphraseProvider;
import org.pgpainless.key.protection.SecretKeyRingProtector;
import org.pgpainless.key.protection.UnprotectedKeysProtector;
import org.pgpainless.util.BCUtil;
import org.pgpainless.util.Passphrase;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;


public class DecryptTask extends AsyncTask<Void, Void, Message> {
    Context context;
    Message message;
    String pass;
    String errorDescription;
    DecryptCallback callback;

    public DecryptTask(Context context, String pass, Message message, DecryptCallback callback) {
        this.context = context;
        this.message = message;
        this.callback = callback;
        this.pass = pass;
    }

    @Override
    protected Message doInBackground(Void... voids) {
        if (message!=null && !TextUtils.isEmpty( message.getPlain() ) ) { //TODO html handle

            try {
                KeysRepository keysRepository = PrivateMailApplication.getInstance().getKeysRepository();
                Account account = PrivateMailApplication.getInstance().getLoggedUserRepository().getActiveAccount();
                String privateKey = "";
                PGPKey pgpkey = keysRepository.getKey(account.getEmail(), PGPKey.PRIVATE);
                if (pgpkey!=null) {
                    privateKey = pgpkey.getKeyObject().toString();
                }


                String encryptedText = message.getPlain().trim();

                PGPSecretKeyRing secretKeys = PGPainless.readKeyRing().secretKeyRing(privateKey);

                KeyRingProtectionSettings settings = new KeyRingProtectionSettings(SymmetricKeyAlgorithm.AES_256, HashAlgorithm.MD5, 0 );
                SecretKeyRingProtector secretKeyDecryptor = new PasswordBasedSecretKeyRingProtector(settings, new SecretKeyPassphraseProvider() {
                    @Nullable
                    @Override
                    public Passphrase getPassphraseFor(Long keyId) {
                        Passphrase passphrase = new Passphrase(pass.toCharArray());
                        return passphrase;
                    }
                });


                InputStream sourceInputStream = new ByteArrayInputStream(encryptedText.getBytes(StandardCharsets.UTF_8));



                DecryptionStream decryptor = PGPainless.createDecryptor()
                        .onInputStream(sourceInputStream) // insert encrypted data here
                        //.doNotDecrypt()
                        .decryptWith(secretKeyDecryptor, BCUtil.keyRingsToKeyRingCollection(secretKeys))
                        .doNotVerify()

                        //.ignoreMissingPublicKeys()
                        .build();

                //.verifyWith(trustedKeyIds, senderKeys)

                ByteArrayOutputStream targetOutputStream = new ByteArrayOutputStream();

                Streams.pipeAll(decryptor, targetOutputStream);
                decryptor.close();

                message.setPlain( new String( targetOutputStream.toByteArray(), StandardCharsets.UTF_8)  );
                return message;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        errorDescription = context.getString(R.string.decrypt_error_common);
        return null;

    }



    @Override
    protected void onPostExecute( Message result) {
        if (callback!=null) {
            if (result!=null)
                callback.onDecrypt(result);
            else
                callback.onFail(errorDescription);
        }

    }
}
