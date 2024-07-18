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
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataConfigurationUtils;
import org.springframework.boot.autoconfigure.data.mongo.MongoReactiveDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoClientFactory;
import org.springframework.boot.autoconfigure.mongo.MongoClientSettingsBuilderCustomizer;
import org.springframework.boot.autoconfigure.mongo.MongoConfigurationUtils;
import org.springframework.boot.autoconfigure.mongo.MongoConnectionDetails;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.autoconfigure.mongo.MongoReactiveAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoManagedTypes;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoDatabaseFactorySupport;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import com.mongodb.MongoClientSettings;
import com.mongodb.TransactionOptions;
import com.mongodb.client.MongoClient;
import com.yookue.commonplexus.springcondition.annotation.ConditionalOnAnyProperties;
import com.yookue.springstarter.multiplemongodb.property.ExtendedMongoProperties;


/**
 * Tertiary configuration for classic mongo
 *
 * @author David Hsing
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "spring.multiple-mongo", name = "enabled", havingValue = "true", matchIfMissing = true)
@ConditionalOnAnyProperties(value = {
    @ConditionalOnProperty(prefix = TertiaryMongoAutoConfiguration.PROPERTIES_PREFIX, name = "uri"),
    @ConditionalOnProperty(prefix = TertiaryMongoAutoConfiguration.PROPERTIES_PREFIX, name = "host")
})
@ConditionalOnClass(value = MongoClient.class)
@AutoConfigureAfter(value = SecondaryMongoAutoConfiguration.class)
@AutoConfigureBefore(value = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class, MongoReactiveAutoConfiguration.class, MongoReactiveDataAutoConfiguration.class})
public class TertiaryMongoAutoConfiguration {
    public static final String PROPERTIES_PREFIX = "spring.multiple-mongo.tertiary";    // $NON-NLS-1$
    public static final String MONGO_PROPERTIES = "tertiaryMongoProperties";    // $NON-NLS-1$
    public static final String CONNECTION_DETAILS = "tertiaryMongoConnectionDetails";    // $NON-NLS-1$
    public static final String SSL_BUNDLES = "tertiaryMongoSslBundles";    // $NON-NLS-1$
    public static final String SETTINGS_BUILDER_CUSTOMIZER = "tertiaryMongoClientSettingsBuilderCustomizer";    // $NON-NLS-1$
    public static final String CLIENT_FACTORY = "tertiaryMongoClientFactory";    // $NON-NLS-1$
    public static final String CLIENT_SETTINGS = "tertiaryMongoClientSettings";    // $NON-NLS-1$
    public static final String MONGO_CLIENT = "tertiaryMongoClient";    // $NON-NLS-1$
    public static final String DATABASE_FACTORY = "tertiaryMongoDatabaseFactory";    // $NON-NLS-1$
    public static final String TRANSACTION_MANAGER = "tertiaryMongoTransactionManager";    // $NON-NLS-1$
    public static final String TRANSACTION_OPTIONS = "tertiaryMongoTransactionOptions";    // $NON-NLS-1$
    public static final String CUSTOM_CONVERSIONS = "tertiaryMongoCustomConversions";    // $NON-NLS-1$
    public static final String MANAGED_TYPES = "tertiaryMongoManagedTypes";    // $NON-NLS-1$
    public static final String MAPPING_CONTEXT = "tertiaryMongoMappingContext";    // $NON-NLS-1$
    public static final String MAPPING_CONVERTER = "tertiaryMongoMappingConverter";    // $NON-NLS-1$
    public static final String MONGO_TEMPLATE = "tertiaryMongoTemplate";    // $NON-NLS-1$
    public static final String GRID_FS_TEMPLATE = "tertiaryMongoGridFsTemplate";    // $NON-NLS-1$

    @Bean(name = MONGO_PROPERTIES)
    @ConditionalOnMissingBean(name = MONGO_PROPERTIES)
    @ConfigurationProperties(prefix = PROPERTIES_PREFIX)
    public MongoProperties mongoProperties() {
        return new ExtendedMongoProperties();
    }

    @Bean(name = CONNECTION_DETAILS)
    @ConditionalOnBean(name = MONGO_PROPERTIES)
    @ConditionalOnMissingBean(name = CONNECTION_DETAILS)
    public MongoConnectionDetails mongoConnectionDetails(@Qualifier(value = MONGO_PROPERTIES) @Nonnull MongoProperties properties) {
        return MongoConfigurationUtils.mongoConnectionDetails(properties);
    }

    @Bean(name = SETTINGS_BUILDER_CUSTOMIZER)
    @ConditionalOnBean(name = MONGO_PROPERTIES)
    @ConditionalOnMissingBean(name = SETTINGS_BUILDER_CUSTOMIZER)
    public MongoClientSettingsBuilderCustomizer mongoClientSettingsBuilderCustomizer(@Qualifier(value = MONGO_PROPERTIES) @Nonnull MongoProperties properties,
        @Qualifier(value = CONNECTION_DETAILS) @Nonnull MongoConnectionDetails details,
        @Autowired(required = false) @Qualifier(value = SSL_BUNDLES) @Nonnull SslBundles bundles) {
        return MongoConfigurationUtils.mongoClientSettingsCustomizer(properties, details, bundles);
    }

    @Bean(name = CLIENT_FACTORY)
    @ConditionalOnBean(name = SETTINGS_BUILDER_CUSTOMIZER)
    @ConditionalOnMissingBean(name = CLIENT_FACTORY)
    public MongoClientFactory mongoClientFactory(@Qualifier(value = SETTINGS_BUILDER_CUSTOMIZER) @Nonnull MongoClientSettingsBuilderCustomizer customizer) {
        return new MongoClientFactory(Collections.singletonList(customizer));
    }

    @Bean(name = CLIENT_SETTINGS)
    @ConditionalOnBean(name = SETTINGS_BUILDER_CUSTOMIZER)
    @ConditionalOnMissingBean(name = CLIENT_SETTINGS)
    public MongoClientSettings mongoClientSettings(@Qualifier(value = SETTINGS_BUILDER_CUSTOMIZER) @Nonnull MongoClientSettingsBuilderCustomizer customizer) {
        return MongoConfigurationUtils.mongoClientSettings(customizer);
    }

    @Bean(name = MONGO_CLIENT, destroyMethod = "close")
    @ConditionalOnBean(name = {CLIENT_FACTORY, CLIENT_SETTINGS})
    @ConditionalOnMissingBean(name = MONGO_CLIENT)
    public MongoClient mongoClient(@Qualifier(value = CLIENT_FACTORY) @Nonnull MongoClientFactory factory,
        @Qualifier(value = CLIENT_SETTINGS) @Nonnull MongoClientSettings settings) {
        return factory.createMongoClient(settings);
    }

    @Bean(name = DATABASE_FACTORY)
    @ConditionalOnBean(name = {MONGO_CLIENT, MONGO_PROPERTIES})
    @ConditionalOnMissingBean(name = DATABASE_FACTORY)
    public MongoDatabaseFactorySupport<?> mongoDatabaseFactory(@Qualifier(value = MONGO_CLIENT) @Nonnull MongoClient client,
        @Qualifier(value = MONGO_PROPERTIES) @Nonnull MongoProperties properties,
        @Qualifier(value = CONNECTION_DETAILS) @Nonnull MongoConnectionDetails details) {
        return MongoDataConfigurationUtils.mongoDatabaseFactory(client, properties, details);
    }

    @Bean(name = TRANSACTION_MANAGER)
    @ConditionalOnBean(name = DATABASE_FACTORY)
    @ConditionalOnMissingBean(name = TRANSACTION_MANAGER)
    public MongoTransactionManager mongoTransactionManager(@Qualifier(value = DATABASE_FACTORY) @Nonnull MongoDatabaseFactory factory,
        @Autowired(required = false) @Qualifier(value = TRANSACTION_OPTIONS) @Nullable TransactionOptions options) {
        return new MongoTransactionManager(factory, options);
    }

    @Bean(name = CUSTOM_CONVERSIONS)
    @ConditionalOnMissingBean(name = CUSTOM_CONVERSIONS)
    public MongoCustomConversions mongoCustomConversions() {
        return MongoDataConfigurationUtils.mongoCustomConversions();
    }

    @Bean(name = MANAGED_TYPES)
    @ConditionalOnMissingBean(name = MANAGED_TYPES)
    public MongoManagedTypes mongoManagedTypes(@Nonnull ApplicationContext context) throws ClassNotFoundException {
        return MongoDataConfigurationUtils.mongoManagedTypes(context);
    }

    @Bean(name = MAPPING_CONTEXT)
    @ConditionalOnBean(name = {CUSTOM_CONVERSIONS, MONGO_PROPERTIES})
    @ConditionalOnMissingBean(name = MAPPING_CONTEXT)
    public MongoMappingContext mongoMappingContext(@Qualifier(value = MONGO_PROPERTIES) @Nonnull MongoProperties properties,
        @Qualifier(value = CUSTOM_CONVERSIONS) @Nonnull MongoCustomConversions conversions,
        @Qualifier(value = MANAGED_TYPES) @Nonnull MongoManagedTypes types) {
        return MongoDataConfigurationUtils.mongoMappingContext(properties, conversions, types);
    }

    @Bean(name = MAPPING_CONVERTER)
    @ConditionalOnBean(name = {DATABASE_FACTORY, MAPPING_CONTEXT, CUSTOM_CONVERSIONS, MONGO_PROPERTIES})
    @ConditionalOnMissingBean(name = MAPPING_CONVERTER)
    public MappingMongoConverter mongoMappingConverter(@Qualifier(value = DATABASE_FACTORY) @Nonnull MongoDatabaseFactory factory,
        @Qualifier(value = MAPPING_CONTEXT) @Nonnull MongoMappingContext context,
        @Qualifier(value = CUSTOM_CONVERSIONS) @Nonnull MongoCustomConversions conversions) {
        return MongoDataConfigurationUtils.mappingMongoConverter(factory, context, conversions);
    }

    @Bean(name = MONGO_TEMPLATE)
    @ConditionalOnBean(name = {DATABASE_FACTORY, MAPPING_CONVERTER})
    @ConditionalOnMissingBean(name = MONGO_TEMPLATE)
    public MongoTemplate mongoTemplate(@Qualifier(value = DATABASE_FACTORY) @Nonnull MongoDatabaseFactory factory,
        @Qualifier(value = MAPPING_CONVERTER) @Nonnull MongoConverter converter) {
        return new MongoTemplate(factory, converter);
    }

    @Bean(name = GRID_FS_TEMPLATE)
    @ConditionalOnBean(name = {DATABASE_FACTORY, MONGO_TEMPLATE, MONGO_PROPERTIES})
    @ConditionalOnMissingBean(name = GRID_FS_TEMPLATE)
    public GridFsTemplate mongoGridFsTemplate(@Qualifier(value = MONGO_PROPERTIES) @Nonnull MongoProperties properties,
        @Qualifier(value = DATABASE_FACTORY) @Nonnull MongoDatabaseFactory factory,
        @Qualifier(value = MONGO_TEMPLATE) @Nonnull MongoTemplate template,
        @Qualifier(value = CONNECTION_DETAILS) @Nonnull MongoConnectionDetails details) {
        return MongoDataConfigurationUtils.gridFsTemplate(properties, factory, template, details);
    }
}
