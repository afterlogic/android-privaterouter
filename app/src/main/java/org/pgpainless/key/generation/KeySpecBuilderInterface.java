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

import org.pgpainless.algorithm.CompressionAlgorithm;
import org.pgpainless.algorithm.Feature;
import org.pgpainless.algorithm.HashAlgorithm;
import org.pgpainless.algorithm.KeyFlag;
import org.pgpainless.algorithm.SymmetricKeyAlgorithm;

public interface KeySpecBuilderInterface {

    WithDetailedConfiguration withKeyFlags(@NonNull KeyFlag... flags);

    WithDetailedConfiguration withDefaultKeyFlags();

    KeySpec withInheritedSubPackets();

    interface WithDetailedConfiguration {

        WithPreferredSymmetricAlgorithms withDetailedConfiguration();

        KeySpec withDefaultAlgorithms();
    }

    interface WithPreferredSymmetricAlgorithms {

        WithPreferredHashAlgorithms withPreferredSymmetricAlgorithms(@NonNull SymmetricKeyAlgorithm... algorithms);

        WithPreferredHashAlgorithms withDefaultSymmetricAlgorithms();

        WithFeatures withDefaultAlgorithms();

    }

    interface WithPreferredHashAlgorithms {

        WithPreferredCompressionAlgorithms withPreferredHashAlgorithms(@NonNull HashAlgorithm... algorithms);

        WithPreferredCompressionAlgorithms withDefaultHashAlgorithms();

    }

    interface WithPreferredCompressionAlgorithms {

        WithFeatures withPreferredCompressionAlgorithms(@NonNull CompressionAlgorithm... algorithms);

        WithFeatures withDefaultCompressionAlgorithms();

    }

    interface WithFeatures {

        WithFeatures withFeature(@NonNull Feature feature);

        KeySpec done();
    }

}
