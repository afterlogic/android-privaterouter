package com.PrivateRouter.PrivateMail.encryption;

import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.bcpg.BCPGOutputStream;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPSignature;
import org.bouncycastle.openpgp.PGPSignatureGenerator;
import org.bouncycastle.openpgp.operator.bc.BcPGPContentSignerBuilder;
import org.pgpainless.key.generation.KeyRingBuilder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class SignHelper {
    private static final int BUFFER_SIZE = 4096;

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
}
