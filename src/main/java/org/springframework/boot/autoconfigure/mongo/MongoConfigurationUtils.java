/*
 * Copyright (c) 2020 Yookue Ltd. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot.autoconfigure.mongo;


import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.boot.ssl.SslBundles;
import com.mongodb.MongoClientSettings;
import com.yookue.commonplexus.springutil.support.SingletonObjectProvider;


/**
 * Utilities for configuring classic mongo
 *
 * @author David Hsing
 * @see org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration
 */
@SuppressWarnings({"unused", "BooleanMethodIsAlwaysInverted", "UnusedReturnValue"})
public abstract class MongoConfigurationUtils {
    @Nonnull
    public static MongoConnectionDetails mongoConnectionDetails(@Nonnull MongoProperties properties) {
        return new PropertiesMongoConnectionDetails(properties);
    }

    @Nonnull
    public static MongoClientSettings mongoClientSettings(@Nullable MongoClientSettingsBuilderCustomizer customizer) {
        MongoClientSettings.Builder builder = MongoClientSettings.builder();
        if (customizer != null) {
            customizer.customize(builder);
        }
        return builder.build();
    }

    @Nonnull
    public static MongoClientSettingsBuilderCustomizer mongoClientSettingsCustomizer(@Nonnull MongoProperties properties, @Nullable MongoConnectionDetails details, @Nullable SslBundles bundles) {
        MongoConnectionDetails alias = ObjectUtils.defaultIfNull(details, mongoConnectionDetails(properties));
        return new MongoAutoConfiguration.MongoClientSettingsConfiguration().standardMongoSettingsCustomizer(properties, details, SingletonObjectProvider.ofNullable(bundles));
    }
}
