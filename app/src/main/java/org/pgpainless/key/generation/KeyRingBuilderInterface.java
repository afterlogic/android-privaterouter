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
package org.pgpainless.key.generation;

import androidx.annotation.NonNull;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;

import org.bouncycastle.openpgp.PGPException;
import org.pgpainless.key.collection.PGPKeyRing;
import org.pgpainless.util.Passphrase;

public interface KeyRingBuilderInterface {

    KeyRingBuilderInterface withSubKey(@NonNull KeySpec keySpec);

    WithPrimaryUserId withMasterKey(@NonNull KeySpec keySpec);

    interface WithPrimaryUserId {

        WithPassphrase withPrimaryUserId(@NonNull String userId);

        WithPassphrase withPrimaryUserId(@NonNull byte[] userId);

    }

    interface WithPassphrase {

        Build withPassphrase(@NonNull Passphrase passphrase);

        Build withoutPassphrase();
    }

    interface Build {

        PGPKeyRing build() throws NoSuchAlgorithmException, PGPException,
                InvalidAlgorithmParameterException;

    }
}
