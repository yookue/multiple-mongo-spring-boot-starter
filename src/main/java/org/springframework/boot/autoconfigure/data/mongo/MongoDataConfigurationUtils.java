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

package org.springframework.boot.autoconfigure.data.mongo;


import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.mongo.MongoConfigurationUtils;
import org.springframework.boot.autoconfigure.mongo.MongoConnectionDetails;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoManagedTypes;
import org.springframework.data.mongodb.core.MongoDatabaseFactorySupport;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import com.mongodb.client.MongoClient;
import com.yookue.commonplexus.springutil.support.SingletonObjectProvider;


/**
 * Utilities for configuring classic mongo data
 *
 * @author David Hsing
 * @see org.springframework.boot.autoconfigure.data.mongo.MongoDataConfiguration
 * @see org.springframework.boot.autoconfigure.data.mongo.MongoDatabaseFactoryConfiguration
 * @see org.springframework.boot.autoconfigure.data.mongo.MongoDatabaseFactoryDependentConfiguration
 */
@SuppressWarnings({"unused", "BooleanMethodIsAlwaysInverted", "UnusedReturnValue"})
public abstract class MongoDataConfigurationUtils {
    @Nonnull
    public static MongoCustomConversions mongoCustomConversions() {
        return new MongoDataConfiguration().mongoCustomConversions();
    }

    @Nonnull
    public static MongoManagedTypes mongoManagedTypes(@Nonnull ApplicationContext context) throws ClassNotFoundException {
        return MongoDataConfiguration.mongoManagedTypes(context);
    }

    @Nonnull
    public static MongoMappingContext mongoMappingContext(@Nonnull MongoProperties properties, @Nonnull MongoCustomConversions conversions, @Nonnull MongoManagedTypes types) {
        return new MongoDataConfiguration().mongoMappingContext(properties, conversions, types);
    }

    @Nonnull
    public static MongoDatabaseFactorySupport<?> mongoDatabaseFactory(@Nonnull MongoClient client, @Nonnull MongoProperties properties, @Nullable MongoConnectionDetails details) {
        MongoConnectionDetails alias = ObjectUtils.defaultIfNull(details, MongoConfigurationUtils.mongoConnectionDetails(properties));
        return new MongoDatabaseFactoryConfiguration().mongoDatabaseFactory(client, properties, alias);
    }

    @Nonnull
    public static MappingMongoConverter mappingMongoConverter(@Nonnull MongoDatabaseFactory factory, @Nonnull MongoMappingContext context, @Nonnull MongoCustomConversions conversions) {
        ObjectProvider<MongoDatabaseFactory> provider = SingletonObjectProvider.of(factory);
        return new MongoDataConfiguration().mappingMongoConverter(provider, context, conversions);
    }

    @Nonnull
    public static GridFsTemplate gridFsTemplate(@Nonnull MongoProperties properties, @Nonnull MongoDatabaseFactory factory, @Nonnull MongoTemplate template, @Nullable MongoConnectionDetails details) {
        MongoConnectionDetails alias = ObjectUtils.defaultIfNull(details, MongoConfigurationUtils.mongoConnectionDetails(properties));
        return new MongoDatabaseFactoryDependentConfiguration().gridFsTemplate(properties, factory, template, alias);
    }
}
