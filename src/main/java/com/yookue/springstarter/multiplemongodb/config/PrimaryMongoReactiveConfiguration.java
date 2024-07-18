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

package com.yookue.springstarter.multiplemongodb.config;


import java.util.Collections;
import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoReactiveDataConfigurationUtils;
import org.springframework.boot.autoconfigure.data.mongo.MongoReactiveDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoClientSettingsBuilderCustomizer;
import org.springframework.boot.autoconfigure.mongo.MongoConnectionDetails;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.autoconfigure.mongo.MongoReactiveAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.ReactiveMongoClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.SimpleReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.gridfs.ReactiveGridFsTemplate;
import com.mongodb.MongoClientSettings;
import com.mongodb.reactivestreams.client.MongoClient;
import reactor.core.publisher.Flux;


/**
 * Primary configuration for reactive mongo
 *
 * @author David Hsing
 * @see org.springframework.boot.autoconfigure.mongo.MongoReactiveAutoConfiguration
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = PrimaryMongoAutoConfiguration.PROPERTIES_PREFIX, name = "uri")
@ConditionalOnClass(value = {MongoClient.class, Flux.class})
@AutoConfigureAfter(value = PrimaryMongoAutoConfiguration.class)
@AutoConfigureBefore(value = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class, MongoReactiveAutoConfiguration.class, MongoReactiveDataAutoConfiguration.class})
public class PrimaryMongoReactiveConfiguration {
    public static final String CLIENT_FACTORY = "primaryReactiveMongoClientFactory";    // $NON-NLS-1$
    public static final String MONGO_CLIENT = "primaryReactiveMongoClient";    // $NON-NLS-1$
    public static final String DATABASE_FACTORY = "primaryReactiveMongoDatabaseFactory";    // $NON-NLS-1$
    public static final String MONGO_TEMPLATE = "primaryReactiveMongoTemplate";    // $NON-NLS-1$
    public static final String GRID_FS_TEMPLATE = "primaryReactiveMongoGridFsTemplate";    // $NON-NLS-1$

    @Primary
    @Bean(name = CLIENT_FACTORY)
    @ConditionalOnBean(name = PrimaryMongoAutoConfiguration.SETTINGS_BUILDER_CUSTOMIZER)
    @ConditionalOnMissingBean(name = CLIENT_FACTORY)
    public ReactiveMongoClientFactory mongoClientFactory(@Qualifier(value = PrimaryMongoAutoConfiguration.SETTINGS_BUILDER_CUSTOMIZER) @Nonnull MongoClientSettingsBuilderCustomizer customizer) {
        return new ReactiveMongoClientFactory(Collections.singletonList(customizer));
    }

    @Primary
    @Bean(name = MONGO_CLIENT, destroyMethod = "close")
    @ConditionalOnBean(name = {CLIENT_FACTORY, PrimaryMongoAutoConfiguration.CLIENT_SETTINGS})
    @ConditionalOnMissingBean(name = MONGO_CLIENT)
    public MongoClient mongoClient(@Qualifier(value = CLIENT_FACTORY) @Nonnull ReactiveMongoClientFactory factory,
        @Qualifier(value = PrimaryMongoAutoConfiguration.CLIENT_SETTINGS) @Nonnull MongoClientSettings settings) {
        return factory.createMongoClient(settings);
    }

    @Primary
    @Bean(name = DATABASE_FACTORY)
    @ConditionalOnBean(name = {MONGO_CLIENT, PrimaryMongoAutoConfiguration.MONGO_PROPERTIES})
    @ConditionalOnMissingBean(name = DATABASE_FACTORY)
    public SimpleReactiveMongoDatabaseFactory mongoDatabaseFactory(@Qualifier(value = MONGO_CLIENT) @Nonnull MongoClient client,
        @Qualifier(value = PrimaryMongoAutoConfiguration.MONGO_PROPERTIES) @Nonnull MongoProperties properties) {
        return MongoReactiveDataConfigurationUtils.reactiveMongoDatabaseFactory(client, properties);
    }

    @Primary
    @Bean(name = MONGO_TEMPLATE)
    @ConditionalOnBean(name = {DATABASE_FACTORY, PrimaryMongoAutoConfiguration.MAPPING_CONVERTER})
    @ConditionalOnMissingBean(name = MONGO_TEMPLATE)
    public ReactiveMongoTemplate mongoTemplate(@Qualifier(value = PrimaryMongoAutoConfiguration.CONNECTION_DETAILS) @Nonnull MongoConnectionDetails details,
        @Qualifier(value = DATABASE_FACTORY) @Nonnull ReactiveMongoDatabaseFactory factory,
        @Qualifier(value = PrimaryMongoAutoConfiguration.MAPPING_CONVERTER) @Nonnull MappingMongoConverter converter) {
        return MongoReactiveDataConfigurationUtils.reactiveMongoTemplate(details, factory, converter);
    }

    @Primary
    @Bean(name = GRID_FS_TEMPLATE)
    @ConditionalOnBean(name = {DATABASE_FACTORY, PrimaryMongoAutoConfiguration.MAPPING_CONVERTER, MongoReactivePreConfiguration.DATA_BUFFER_FACTORY, PrimaryMongoAutoConfiguration.MONGO_PROPERTIES})
    @ConditionalOnMissingBean(name = GRID_FS_TEMPLATE)
    public ReactiveGridFsTemplate mongoGridFsTemplate(@Qualifier(value = PrimaryMongoAutoConfiguration.CONNECTION_DETAILS) @Nonnull MongoConnectionDetails details,
        @Qualifier(value = DATABASE_FACTORY) @Nonnull ReactiveMongoDatabaseFactory databaseFactory,
        @Qualifier(value = PrimaryMongoAutoConfiguration.MAPPING_CONVERTER) @Nonnull MappingMongoConverter converter,
        @Qualifier(value = MongoReactivePreConfiguration.DATA_BUFFER_FACTORY) @Nonnull DataBufferFactory bufferFactory) {
        return MongoReactiveDataConfigurationUtils.reactiveGridFsTemplate(details, databaseFactory, converter, bufferFactory);
    }
}
