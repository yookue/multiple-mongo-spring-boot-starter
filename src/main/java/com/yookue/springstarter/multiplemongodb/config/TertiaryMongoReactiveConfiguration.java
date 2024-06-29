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
import javax.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoReactiveConfigurationUtils;
import org.springframework.boot.autoconfigure.data.mongo.MongoReactiveDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.autoconfigure.mongo.MongoPropertiesClientSettingsBuilderCustomizer;
import org.springframework.boot.autoconfigure.mongo.MongoReactiveAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.ReactiveMongoClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
 * Tertiary configuration for reactive mongo
 *
 * @author David Hsing
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = TertiaryMongoAutoConfiguration.PROPERTIES_PREFIX, name = "uri")
@ConditionalOnClass(value = {MongoClient.class, Flux.class})
@AutoConfigureAfter(value = TertiaryMongoAutoConfiguration.class)
@AutoConfigureBefore(value = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class, MongoReactiveAutoConfiguration.class, MongoReactiveDataAutoConfiguration.class})
public class TertiaryMongoReactiveConfiguration {
    public static final String MONGO_CLIENT_FACTORY = "tertiaryReactiveMongoClientFactory";    // $NON-NLS-1$
    public static final String MONGO_CLIENT = "tertiaryReactiveMongoClient";    // $NON-NLS-1$
    public static final String MONGO_DATABASE_FACTORY = "tertiaryReactiveMongoDatabaseFactory";    // $NON-NLS-1$
    public static final String MONGO_TEMPLATE = "tertiaryReactiveMongoTemplate";    // $NON-NLS-1$
    public static final String GRID_FS_TEMPLATE = "tertiaryReactiveMongoGridFsTemplate";    // $NON-NLS-1$

    @Bean(name = MONGO_CLIENT_FACTORY)
    @ConditionalOnBean(name = TertiaryMongoAutoConfiguration.MONGO_PROPERTIES_CUSTOMIZER)
    @ConditionalOnMissingBean(name = MONGO_CLIENT_FACTORY)
    public ReactiveMongoClientFactory mongoClientFactory(@Qualifier(value = TertiaryMongoAutoConfiguration.MONGO_PROPERTIES_CUSTOMIZER) @Nonnull MongoPropertiesClientSettingsBuilderCustomizer customizer) {
        return new ReactiveMongoClientFactory(Collections.singletonList(customizer));
    }

    @Bean(name = MONGO_CLIENT, destroyMethod = "close")
    @ConditionalOnBean(name = {MONGO_CLIENT_FACTORY, TertiaryMongoAutoConfiguration.MONGO_CLIENT_SETTINGS})
    @ConditionalOnMissingBean(name = MONGO_CLIENT)
    public MongoClient mongoClient(@Qualifier(value = MONGO_CLIENT_FACTORY) @Nonnull ReactiveMongoClientFactory factory, @Qualifier(value = TertiaryMongoAutoConfiguration.MONGO_CLIENT_SETTINGS) @Nonnull MongoClientSettings settings) {
        return factory.createMongoClient(settings);
    }

    @Bean(name = MONGO_DATABASE_FACTORY)
    @ConditionalOnBean(name = {MONGO_CLIENT, TertiaryMongoAutoConfiguration.MONGO_PROPERTIES})
    @ConditionalOnMissingBean(name = MONGO_DATABASE_FACTORY)
    public SimpleReactiveMongoDatabaseFactory mongoDatabaseFactory(@Qualifier(value = MONGO_CLIENT) @Nonnull MongoClient mongoClient, @Qualifier(value = TertiaryMongoAutoConfiguration.MONGO_PROPERTIES) @Nonnull MongoProperties properties) {
        return MongoReactiveConfigurationUtils.reactiveMongoDatabaseFactory(mongoClient, properties);
    }

    @Bean(name = MONGO_TEMPLATE)
    @ConditionalOnBean(name = {MONGO_DATABASE_FACTORY, TertiaryMongoAutoConfiguration.MONGO_MAPPING_CONVERTER})
    @ConditionalOnMissingBean(name = MONGO_TEMPLATE)
    public ReactiveMongoTemplate mongoTemplate(@Qualifier(value = MONGO_DATABASE_FACTORY) @Nonnull ReactiveMongoDatabaseFactory factory, @Qualifier(value = TertiaryMongoAutoConfiguration.MONGO_MAPPING_CONVERTER) @Nonnull MappingMongoConverter converter) {
        return MongoReactiveConfigurationUtils.reactiveMongoTemplate(factory, converter);
    }

    @Bean(name = GRID_FS_TEMPLATE)
    @ConditionalOnBean(name = {MONGO_DATABASE_FACTORY, TertiaryMongoAutoConfiguration.MONGO_MAPPING_CONVERTER, MongoReactivePreConfiguration.DATA_BUFFER_FACTORY, TertiaryMongoAutoConfiguration.MONGO_PROPERTIES})
    @ConditionalOnMissingBean(name = GRID_FS_TEMPLATE)
    public ReactiveGridFsTemplate mongoGridFsTemplate(@Qualifier(value = MONGO_DATABASE_FACTORY) @Nonnull ReactiveMongoDatabaseFactory databaseFactory, @Qualifier(value = TertiaryMongoAutoConfiguration.MONGO_MAPPING_CONVERTER) @Nonnull MappingMongoConverter converter, @Nonnull @Qualifier(value = MongoReactivePreConfiguration.DATA_BUFFER_FACTORY) DataBufferFactory bufferFactory, @Nonnull @Qualifier(value = TertiaryMongoAutoConfiguration.MONGO_PROPERTIES) MongoProperties properties) {
        return MongoReactiveConfigurationUtils.reactiveGridFsTemplate(databaseFactory, converter, bufferFactory, properties);
    }
}
