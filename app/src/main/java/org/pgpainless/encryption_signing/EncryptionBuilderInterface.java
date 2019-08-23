/*
 * Copyright 2018 Paul Schaub.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.pgpainless.encryption_signing;

import androidx.annotation.NonNull;
import java.io.IOException;
import java.io.OutputStream;

import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPPublicKeyRingCollection;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.bouncycastle.openpgp.PGPSecretKeyRingCollection;
import org.pgpainless.algorithm.CompressionAlgorithm;
import org.pgpainless.algorithm.HashAlgorithm;
import org.pgpainless.algorithm.SymmetricKeyAlgorithm;
import org.pgpainless.exception.SecretKeyNotFoundException;
import org.pgpainless.key.protection.SecretKeyRingProtector;
import org.pgpainless.key.selection.keyring.PublicKeyRingSelectionStrategy;
import org.pgpainless.key.selection.keyring.SecretKeyRingSelectionStrategy;
import org.pgpainless.util.MultiMap;

public interface EncryptionBuilderInterface {

    ToRecipients onOutputStream(@NonNull OutputStream outputStream);

    interface ToRecipients {

        WithAlgorithms toRecipients(@NonNull PGPPublicKey... keys);

        WithAlgorithms toRecipients(@NonNull PGPPublicKeyRing... keys);

        WithAlgorithms toRecipients(@NonNull PGPPublicKeyRingCollection... keys);

        <O> WithAlgorithms toRecipients(@NonNull PublicKeyRingSelectionStrategy<O> selectionStrategy,
                                       @NonNull MultiMap<O, PGPPublicKeyRingCollection> keys);

        SignWith doNotEncrypt();

    }

    interface WithAlgorithms {

        WithAlgorithms andToSelf(@NonNull PGPPublicKey... keys);

        WithAlgorithms andToSelf(@NonNull PGPPublicKeyRing... keys);

        WithAlgorithms andToSelf(@NonNull PGPPublicKeyRingCollection keys);

        <O> WithAlgorithms andToSelf(@NonNull PublicKeyRingSelectionStrategy<O> selectionStrategy,
                                    @NonNull MultiMap<O, PGPPublicKeyRingCollection> keys);

        SignWith usingAlgorithms(@NonNull SymmetricKeyAlgorithm symmetricKeyAlgorithm,
                                 @NonNull HashAlgorithm hashAlgorithm,
                                 @NonNull CompressionAlgorithm compressionAlgorithm);

        SignWith usingSecureAlgorithms();

    }

    interface SignWith {

        <O> Armor signWith(@NonNull SecretKeyRingProtector decryptor, @NonNull PGPSecretKey... keys);

        <O> Armor signWith(@NonNull SecretKeyRingProtector decryptor, @NonNull PGPSecretKeyRing... keyRings);

        <O> Armor signWith(@NonNull SecretKeyRingSelectionStrategy<O> selectionStrategy,
                          @NonNull SecretKeyRingProtector decryptor,
                          @NonNull MultiMap<O, PGPSecretKeyRingCollection> keys)
                throws SecretKeyNotFoundException;

        Armor doNotSign();

    }

    interface Armor {

        EncryptionStream asciiArmor() throws IOException, PGPException;

        EncryptionStream noArmor() throws IOException, PGPException;

    }

}
