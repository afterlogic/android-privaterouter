package com.PrivateRouter.PrivateMail.encryption;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.PrivateRouter.PrivateMail.PrivateMailApplication;
import com.PrivateRouter.PrivateMail.R;
import com.PrivateRouter.PrivateMail.model.Account;
import com.PrivateRouter.PrivateMail.model.Email;
import com.PrivateRouter.PrivateMail.model.Message;
import com.PrivateRouter.PrivateMail.model.PGPKey;
import com.PrivateRouter.PrivateMail.repository.KeysRepository;
import com.PrivateRouter.PrivateMail.repository.SettingsRepository;


import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPPublicKeyRingCollection;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.bouncycastle.util.io.Streams;
import org.pgpainless.PGPainless;
import org.pgpainless.algorithm.HashAlgorithm;
import org.pgpainless.algorithm.SymmetricKeyAlgorithm;
import org.pgpainless.encryption_signing.EncryptionBuilderInterface;
import org.pgpainless.encryption_signing.EncryptionStream;
import org.pgpainless.key.collection.PGPKeyRing;
import org.pgpainless.key.generation.type.length.RsaLength;
import org.pgpainless.key.protection.KeyRingProtectionSettings;
import org.pgpainless.key.protection.PasswordBasedSecretKeyRingProtector;
import org.pgpainless.key.protection.SecretKeyPassphraseProvider;
import org.pgpainless.key.protection.SecretKeyRingProtector;
import org.pgpainless.key.protection.UnprotectedKeysProtector;
import org.pgpainless.util.BCUtil;
import org.pgpainless.util.Passphrase;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.PublicKey;
import java.security.Security;

import javax.annotation.Nullable;


public class EncryptTask extends AsyncTask<Void, Void, Message> {
    private Context context;
    private Message message;
    private String pass;
    private boolean useSign;
    private String failMessage;

    EncryptCallback callback;

    public EncryptTask(Context context,  String pass, boolean useSign, Message message, EncryptCallback callback) {
        this.context = context;
        this.message = message;
        this.callback = callback;
        this.pass = pass;
        this.useSign = useSign;
    }

    @Override
    protected Message doInBackground(Void... voids) {
        if (message == null) {
            failMessage = context.getString(R.string.encrypt_error_null_message);
            return null;
        } else if (TextUtils.isEmpty(message.getPlain())) {
            failMessage = context.getString(R.string.encrypt_error_empty_message);
            return null;
        }
        if (message.getTo() == null) {
            failMessage = context.getString(R.string.encrypt_error_empty_receiver);
            return null;
        } else {
            try {
                KeysRepository keysRepository = PrivateMailApplication.getInstance().getKeysRepository();

                String publicKeysArmored = "";


                for (Email email : message.getTo().getEmails()) {
                    PGPKey pgpkey = keysRepository.getKey(email.getEmail(), PGPKey.PUBLIC);
                    if (pgpkey != null) {
                        String data = pgpkey.getKeyObject().toString();

                        publicKeysArmored = publicKeysArmored + data + "\n";
                    } else {
                        failMessage = String.format(context.getString(R.string.encrypt_error_not_found_key), email.getEmail());
                        return null;
                    }

                }


                Account account = PrivateMailApplication.getInstance().getLoggedUserRepository().getActiveAccount();
                String privateKey = "";
                PGPKey pgpkey = keysRepository.getKey(account.getEmail(), PGPKey.PRIVATE);
                if (pgpkey != null) {
                    privateKey = pgpkey.getKeyObject().toString();
                }

                String sourceText = message.getPlain();

                byte[] secretMessage = sourceText.getBytes(Charset.forName("UTF-8"));


                PGPPublicKeyRingCollection publicKeys = PGPainless.readKeyRing().publicKeyRingCollection(publicKeysArmored);
                PGPSecretKeyRing secretKeys = PGPainless.readKeyRing().secretKeyRing(privateKey);

                KeyRingProtectionSettings settings = new KeyRingProtectionSettings(SymmetricKeyAlgorithm.AES_256, HashAlgorithm.MD5, 0);
                SecretKeyRingProtector secretKeyDecryptor = new PasswordBasedSecretKeyRingProtector(settings, new SecretKeyPassphraseProvider() {
                    @Nullable
                    @Override
                    public Passphrase getPassphraseFor(Long keyId) {
                        Passphrase passphrase = new Passphrase(pass.toCharArray());
                        return passphrase;
                    }
                });

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

                EncryptionBuilderInterface.SignWith signWith = PGPainless.createEncryptor()
                        .onOutputStream(outputStream)
                        .toRecipients(publicKeys)
                        .usingSecureAlgorithms();

                EncryptionBuilderInterface.Armor armor;
                if (useSign) {
                    armor = signWith.signWith(secretKeyDecryptor, secretKeys);
                } else {
                    armor = signWith.doNotSign();
                }

                EncryptionStream encryptor = armor.asciiArmor();


                Streams.pipeAll(new ByteArrayInputStream(secretMessage), encryptor);
                encryptor.close();

                byte[] encryptedSecretMessage = outputStream.toByteArray();
                String encrypted = new String(encryptedSecretMessage);

                message.setPlain(encrypted);
                message.setHtml("");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return message;


    }


    @Override
    protected void onPostExecute( Message result) {
        if (callback!=null) {
            if (result!=null)
                callback.onEncrypt(result);
            else
                callback.onFail(failMessage);
        }
    }
}
