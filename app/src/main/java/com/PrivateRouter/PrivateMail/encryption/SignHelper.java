package com.PrivateRouter.PrivateMail.encryption;

import com.PrivateRouter.PrivateMail.model.Message;

import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.bcpg.BCPGOutputStream;
import org.bouncycastle.bcpg.HashAlgorithmTags;
import org.bouncycastle.bcpg.PublicKeyAlgorithmTags;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPLiteralData;
import org.bouncycastle.openpgp.PGPLiteralDataGenerator;
import org.bouncycastle.openpgp.PGPObjectFactory;
import org.bouncycastle.openpgp.PGPOnePassSignature;
import org.bouncycastle.openpgp.PGPOnePassSignatureList;
import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyRingCollection;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.bouncycastle.openpgp.PGPSignature;
import org.bouncycastle.openpgp.PGPSignatureGenerator;
import org.bouncycastle.openpgp.PGPSignatureList;
import org.bouncycastle.openpgp.PGPSignatureSubpacketGenerator;
import org.bouncycastle.openpgp.PGPUtil;
import org.bouncycastle.openpgp.PGPV3SignatureGenerator;
import org.bouncycastle.openpgp.jcajce.JcaPGPObjectFactory;
import org.bouncycastle.openpgp.operator.bc.BcPGPContentSignerBuilder;
import org.bouncycastle.openpgp.operator.bc.BcPGPContentVerifierBuilderProvider;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPContentSignerBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPContentVerifierBuilderProvider;
import org.bouncycastle.openpgp.operator.jcajce.JcePBESecretKeyDecryptorBuilder;
import org.bouncycastle.util.test.UncloseableOutputStream;
import org.pgpainless.key.generation.KeyRingBuilder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Iterator;

import static org.jsoup.helper.Validate.fail;

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
        final PGPV3SignatureGenerator signatureGenerator = new PGPV3SignatureGenerator(
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
            sig.init(new BcPGPContentVerifierBuilderProvider()  , key);
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

/*
    public void generateTest(
            PGPSecretKeyRing sKey,
            PGPPublicKey     pgpPubKey,
            PGPPrivateKey    pgpPrivKey)
            throws Exception
    {
        String                  data = "hello world!";
        ByteArrayOutputStream   bOut = new ByteArrayOutputStream();
        ByteArrayInputStream    testIn = new ByteArrayInputStream(data.getBytes());
        PGPSignatureGenerator   sGen = new PGPSignatureGenerator(new BcPGPContentSignerBuilder(PublicKeyAlgorithmTags.DSA, HashAlgorithmTags.SHA1));

        sGen.init(PGPSignature.BINARY_DOCUMENT, pgpPrivKey);

        PGPSignatureSubpacketGenerator spGen = new PGPSignatureSubpacketGenerator();

        Iterator it = sKey.getSecretKey().getPublicKey().getUserIDs();
        String          primaryUserID = (String)it.next();

        spGen.setSignerUserID(true, primaryUserID);

        sGen.setHashedSubpackets(spGen.generate());

        sGen.generateOnePassVersion(false).encode(bOut);

        PGPLiteralDataGenerator lGen = new PGPLiteralDataGenerator();

        Date testDate = new Date((System.currentTimeMillis() / 1000) * 1000);
        OutputStream lOut = lGen.open(
                new UncloseableOutputStream(bOut),
                PGPLiteralData.BINARY,
                "_CONSOLE",
                data.getBytes().length,
                testDate);

        int ch;
        while ((ch = testIn.read()) >= 0)
        {
            lOut.write(ch);
            sGen.update((byte)ch);
        }

        lGen.close();

        sGen.generate().encode(bOut);

        PGPObjectFactory pgpFact = new PGPObjectFactory(bOut.toByteArray());

        PGPOnePassSignatureList p1 = (PGPOnePassSignatureList)pgpFact.nextObject();
        PGPOnePassSignature ops = p1.get(0);

        PGPLiteralData          p2 = (PGPLiteralData)pgpFact.nextObject();
        if (!p2.getModificationTime().equals(testDate))
        {
            fail("Modification time not preserved");
        }

        InputStream             dIn = p2.getInputStream();

        ops.init(new BcPGPContentVerifierBuilderProvider(), pgpPubKey);

        while ((ch = dIn.read()) >= 0)
        {
            ops.update((byte)ch);
        }

        PGPSignatureList p3 = (PGPSignatureList)pgpFact.nextObject();

        if (!ops.verify(p3.get(0)))
        {
            fail("Failed generated signature check");
        }
    }
*/

    protected String getProvider() {
        return "BC";
    }

    public void createSignature(OutputStream out, PGPSecretKey pgpSec ) throws Exception {
        PGPPrivateKey pgpPrivKey = pgpSec.extractPrivateKey(new JcePBESecretKeyDecryptorBuilder().setProvider(getProvider()).build(
                "1234".toCharArray()));
        PGPSignatureGenerator sGen = new PGPSignatureGenerator(new JcaPGPContentSignerBuilder(pgpSec.getPublicKey().getAlgorithm(),
                HashAlgorithmTags.SHA1).setProvider(getProvider()));

        sGen.init(PGPSignature.BINARY_DOCUMENT, pgpPrivKey);

        BCPGOutputStream bOut = new BCPGOutputStream(out);

        InputStream fIn = new ByteArrayInputStream("Test Signature".getBytes("UTF-8"));

        int ch;
        while ((ch = fIn.read()) >= 0) {
            sGen.update((byte) ch);
        }

        fIn.close();

        sGen.generate().encode(bOut);

    }
}
