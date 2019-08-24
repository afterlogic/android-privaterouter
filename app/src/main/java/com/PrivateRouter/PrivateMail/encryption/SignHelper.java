package com.PrivateRouter.PrivateMail.encryption;

import com.PrivateRouter.PrivateMail.model.Message;

import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.bcpg.BCPGOutputStream;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyRingCollection;
import org.bouncycastle.openpgp.PGPSignature;
import org.bouncycastle.openpgp.PGPSignatureGenerator;
import org.bouncycastle.openpgp.PGPSignatureList;
import org.bouncycastle.openpgp.PGPUtil;
import org.bouncycastle.openpgp.jcajce.JcaPGPObjectFactory;
import org.bouncycastle.openpgp.operator.bc.BcPGPContentSignerBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPContentVerifierBuilderProvider;
import org.pgpainless.key.generation.KeyRingBuilder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class SignHelper {
    private static final int BUFFER_SIZE = 4096;

    private Runnable runnableOnFinish;


    public Runnable getRunnableOnFinish() {
        return runnableOnFinish;
    }

    public void setRunnableOnFinish(Runnable runnableOnFinish) {
        this.runnableOnFinish = runnableOnFinish;
    }

    public interface StreamHandler {
        void handleStreamBuffer(byte[] buffer, int offset, int length) throws IOException;
    }

    public static KeyRingBuilder generateKeyRing() {
        return new KeyRingBuilder();
    }


    static void processStringAsStream(String data, StreamHandler handler) throws IOException {
        InputStream is = new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8.name()));
        processStream(is, handler);
    }

    static void processStream(InputStream is, StreamHandler handler) throws IOException {
        int read;
        byte[] buffer = new byte[BUFFER_SIZE];
        while( (read = is.read(buffer)) != -1 ) {
            handler.handleStreamBuffer(buffer, 0, read);
        }
    }

    static String signArmoredAscii(PGPPrivateKey privateKey, String data, int signatureAlgo) throws IOException, PGPException {
        String signature = null;
        final PGPSignatureGenerator signatureGenerator = new PGPSignatureGenerator(
                new BcPGPContentSignerBuilder(privateKey.getPublicKeyPacket().getAlgorithm(), signatureAlgo));
        signatureGenerator.init(PGPSignature.BINARY_DOCUMENT, privateKey);
        ByteArrayOutputStream signatureOutput = new ByteArrayOutputStream();
        try( BCPGOutputStream outputStream = new BCPGOutputStream( new ArmoredOutputStream(signatureOutput)) ) {
            processStringAsStream(data, new StreamHandler() {
                @Override
                public void handleStreamBuffer(byte[] buffer, int offset, int length) throws IOException {
                    signatureGenerator.update(buffer, offset, length);
                }
            });
            signatureGenerator.generate().encode(outputStream);
        }

        signature = new String(signatureOutput.toByteArray(), "UTF-8");

        return signature;
    }


    public static  boolean verify(InputStream signedData, InputStream signature, PGPPublicKeyRingCollection pgpPublicKeyRings) {
        try {
            signature = PGPUtil.getDecoderStream(signature);
            JcaPGPObjectFactory pgpFact = new JcaPGPObjectFactory(signature);
            PGPSignature sig = ((PGPSignatureList) pgpFact.nextObject()).get(0);
            PGPPublicKey key = pgpPublicKeyRings.getPublicKey(sig.getKeyID());
            sig.init(new JcaPGPContentVerifierBuilderProvider() , key);
            byte[] buff = new byte[1024];
            int read = 0;
            while ((read = signedData.read(buff)) != -1) {
                sig.update(buff, 0, read);
            }
            signedData.close();
            return sig.verify();
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

}
