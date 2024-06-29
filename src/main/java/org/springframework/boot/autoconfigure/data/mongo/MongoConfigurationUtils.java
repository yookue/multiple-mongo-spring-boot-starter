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


import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.autoconfigure.mongo.MongoPropertiesClientSettingsBuilderCustomizer;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoDatabaseFactorySupport;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.util.Assert;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.yookue.commonplexus.javaseutil.constant.AssertMessageConst;
import com.yookue.springstarter.multiplemongodb.property.ExtendedMongoProperties;


/**
 * Utilities for configuring classic mongo
 *
 * @author David Hsing
 * @see org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration
 * @see org.springframework.boot.autoconfigure.data.mongo.MongoDataConfiguration
 * @see org.springframework.boot.autoconfigure.data.mongo.MongoDatabaseFactoryConfiguration
 * @see org.springframework.boot.autoconfigure.data.mongo.MongoDatabaseFactoryDependentConfiguration
 */
@SuppressWarnings({"unused", "BooleanMethodIsAlwaysInverted", "UnusedReturnValue"})
public abstract class MongoConfigurationUtils {
    @Nonnull
    public static MongoCustomConversions mongoCustomConversions() {
        return new MongoDataConfiguration().mongoCustomConversions();
    }

    @Nonnull
    public static MongoMappingContext mongoMappingContext(@Nonnull ApplicationContext context, @Nonnull MongoCustomConversions conversions, @Nonnull MongoProperties properties) throws ClassNotFoundException {
        return new MongoDataConfiguration().mongoMappingContext(context, properties, conversions);
    }

    @Nonnull
    public static MongoDatabaseFactorySupport<?> mongoDatabaseFactory(@Nonnull MongoClient mongoClient, @Nonnull MongoProperties properties) {
        return new MongoDatabaseFactoryConfiguration().mongoDatabaseFactory(mongoClient, properties);
    }

    @Nonnull
    public static MappingMongoConverter mappingMongoConverter(@Nonnull MongoDatabaseFactory factory, @Nonnull MongoMappingContext context, @Nonnull MongoCustomConversions conversions, @Nonnull MongoProperties properties) {
        MappingMongoConverter result = new MongoDatabaseFactoryDependentConfiguration(properties).mappingMongoConverter(factory, context, conversions);
        Assert.notNull(result, AssertMessageConst.NOT_NULL);
        // Remove the "_class" field from serialized json content
        if (properties instanceof ExtendedMongoProperties && BooleanUtils.isTrue(((ExtendedMongoProperties) properties).getNullTypeKey())) {
            result.setTypeMapper(new DefaultMongoTypeMapper(null));
        }
        return result;
    }

    @Nonnull
    public static GridFsTemplate gridFsTemplate(@Nonnull MongoDatabaseFactory factory, @Nonnull MongoTemplate mongoTemplate, @Nonnull MongoProperties properties) {
        return new MongoDatabaseFactoryDependentConfiguration(properties).gridFsTemplate(factory, mongoTemplate);
    }

    @Nonnull
    public static MongoPropertiesClientSettingsBuilderCustomizer mongoPropertiesCustomizer(@Nonnull Environment environment, @Nonnull MongoProperties properties) {
        return new MongoPropertiesClientSettingsBuilderCustomizer(properties, environment);
    }

    @Nonnull
    public static MongoClientSettings mongoClientSettings(@Nullable MongoPropertiesClientSettingsBuilderCustomizer customizer) {
        MongoClientSettings.Builder builder = MongoClientSettings.builder();
        if (customizer != null) {
            customizer.customize(builder);
        }
        return builder.build();
    }
}
