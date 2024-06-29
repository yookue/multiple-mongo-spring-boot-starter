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
import javax.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.mongo.MongoConfigurationUtils;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoReactiveDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoClientFactory;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.autoconfigure.mongo.MongoPropertiesClientSettingsBuilderCustomizer;
import org.springframework.boot.autoconfigure.mongo.MongoReactiveAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.MongoDatabaseFactory;
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
 * Secondary configuration for classic mongo
 *
 * @author David Hsing
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "spring.multiple-mongo", name = "enabled", havingValue = "true", matchIfMissing = true)
@ConditionalOnAnyProperties(value = {
    @ConditionalOnProperty(prefix = SecondaryMongoAutoConfiguration.PROPERTIES_PREFIX, name = "uri"),
    @ConditionalOnProperty(prefix = SecondaryMongoAutoConfiguration.PROPERTIES_PREFIX, name = "host")
})
@ConditionalOnClass(value = MongoClient.class)
@AutoConfigureAfter(value = PrimaryMongoAutoConfiguration.class)
@AutoConfigureBefore(value = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class, MongoReactiveAutoConfiguration.class, MongoReactiveDataAutoConfiguration.class})
public class SecondaryMongoAutoConfiguration {
    public static final String PROPERTIES_PREFIX = "spring.multiple-mongo.secondary";    // $NON-NLS-1$
    public static final String MONGO_PROPERTIES = "secondaryMongoProperties";    // $NON-NLS-1$
    public static final String MONGO_PROPERTIES_CUSTOMIZER = "secondaryMongoPropertiesCustomizer";    // $NON-NLS-1$
    public static final String MONGO_CLIENT_FACTORY = "secondaryMongoClientFactory";    // $NON-NLS-1$
    public static final String MONGO_CLIENT_SETTINGS = "secondaryMongoClientSettings";    // $NON-NLS-1$
    public static final String MONGO_CLIENT = "secondaryMongoClient";    // $NON-NLS-1$
    public static final String MONGO_DATABASE_FACTORY = "secondaryMongoDatabaseFactory";    // $NON-NLS-1$
    public static final String TRANSACTION_MANAGER = "secondaryMongoTransactionManager";    // $NON-NLS-1$
    public static final String TRANSACTION_OPTIONS = "secondaryMongoTransactionOptions";    // $NON-NLS-1$
    public static final String MONGO_CUSTOM_CONVERSIONS = "secondaryMongoCustomConversions";    // $NON-NLS-1$
    public static final String MONGO_MAPPING_CONTEXT = "secondaryMongoMappingContext";    // $NON-NLS-1$
    public static final String MONGO_MAPPING_CONVERTER = "secondaryMongoMappingConverter";    // $NON-NLS-1$
    public static final String MONGO_TEMPLATE = "secondaryMongoTemplate";    // $NON-NLS-1$
    public static final String GRID_FS_TEMPLATE = "secondaryMongoGridFsTemplate";    // $NON-NLS-1$

    @Bean(name = MONGO_PROPERTIES)
    @ConditionalOnMissingBean(name = MONGO_PROPERTIES)
    @ConfigurationProperties(prefix = PROPERTIES_PREFIX)
    public MongoProperties mongoProperties() {
        return new ExtendedMongoProperties();
    }

    @Bean(name = MONGO_PROPERTIES_CUSTOMIZER)
    @ConditionalOnBean(name = MONGO_PROPERTIES)
    @ConditionalOnMissingBean(name = MONGO_PROPERTIES_CUSTOMIZER)
    public MongoPropertiesClientSettingsBuilderCustomizer mongoPropertiesCustomizer(@Qualifier(value = MONGO_PROPERTIES) @Nonnull MongoProperties properties, @Nonnull Environment environment) {
        return MongoConfigurationUtils.mongoPropertiesCustomizer(environment, properties);
    }

    @Bean(name = MONGO_CLIENT_FACTORY)
    @ConditionalOnBean(name = MONGO_PROPERTIES_CUSTOMIZER)
    @ConditionalOnMissingBean(name = MONGO_CLIENT_FACTORY)
    public MongoClientFactory mongoClientFactory(@Qualifier(value = MONGO_PROPERTIES_CUSTOMIZER) @Nonnull MongoPropertiesClientSettingsBuilderCustomizer customizer) {
        return new MongoClientFactory(Collections.singletonList(customizer));
    }

    @Bean(name = MONGO_CLIENT_SETTINGS)
    @ConditionalOnBean(name = MONGO_PROPERTIES_CUSTOMIZER)
    @ConditionalOnMissingBean(name = MONGO_CLIENT_SETTINGS)
    public MongoClientSettings mongoClientSettings(@Qualifier(value = MONGO_PROPERTIES_CUSTOMIZER) @Nonnull MongoPropertiesClientSettingsBuilderCustomizer customizer) {
        return MongoConfigurationUtils.mongoClientSettings(customizer);
    }

    @Bean(name = MONGO_CLIENT, destroyMethod = "close")
    @ConditionalOnBean(name = {MONGO_CLIENT_FACTORY, MONGO_CLIENT_SETTINGS})
    @ConditionalOnMissingBean(name = MONGO_CLIENT)
    public MongoClient mongoClient(@Qualifier(value = MONGO_CLIENT_FACTORY) @Nonnull MongoClientFactory factory, @Qualifier(value = MONGO_CLIENT_SETTINGS) @Nonnull MongoClientSettings settings) {
        return factory.createMongoClient(settings);
    }

    @Bean(name = MONGO_DATABASE_FACTORY)
    @ConditionalOnBean(name = {MONGO_CLIENT, MONGO_PROPERTIES})
    @ConditionalOnMissingBean(name = MONGO_DATABASE_FACTORY)
    public MongoDatabaseFactorySupport<?> mongoDatabaseFactory(@Qualifier(value = MONGO_CLIENT) @Nonnull MongoClient mongoClient, @Qualifier(value = MONGO_PROPERTIES) @Nonnull MongoProperties properties) {
        return MongoConfigurationUtils.mongoDatabaseFactory(mongoClient, properties);
    }

    @Bean(name = TRANSACTION_MANAGER)
    @ConditionalOnBean(name = MONGO_DATABASE_FACTORY)
    @ConditionalOnMissingBean(name = TRANSACTION_MANAGER)
    public MongoTransactionManager mongoTransactionManager(@Qualifier(value = MONGO_DATABASE_FACTORY) @Nonnull MongoDatabaseFactory factory, @Autowired(required = false) @Qualifier(value = TRANSACTION_OPTIONS) @Nullable TransactionOptions options) {
        return new MongoTransactionManager(factory, options);
    }

    @Bean(name = MONGO_CUSTOM_CONVERSIONS)
    @ConditionalOnMissingBean(name = MONGO_CUSTOM_CONVERSIONS)
    public MongoCustomConversions mongoCustomConversions() {
        return MongoConfigurationUtils.mongoCustomConversions();
    }

    @Bean(name = MONGO_MAPPING_CONTEXT)
    @ConditionalOnBean(name = {MONGO_CUSTOM_CONVERSIONS, MONGO_PROPERTIES})
    @ConditionalOnMissingBean(name = MONGO_MAPPING_CONTEXT)
    public MongoMappingContext mongoMappingContext(@Nonnull ApplicationContext applicationContext, @Qualifier(value = MONGO_CUSTOM_CONVERSIONS) @Nonnull MongoCustomConversions conversions, @Qualifier(value = MONGO_PROPERTIES) @Nonnull MongoProperties properties) throws ClassNotFoundException {
        return MongoConfigurationUtils.mongoMappingContext(applicationContext, conversions, properties);
    }

    @Bean(name = MONGO_MAPPING_CONVERTER)
    @ConditionalOnBean(name = {MONGO_DATABASE_FACTORY, MONGO_MAPPING_CONTEXT, MONGO_CUSTOM_CONVERSIONS, MONGO_PROPERTIES})
    @ConditionalOnMissingBean(name = MONGO_MAPPING_CONVERTER)
    public MappingMongoConverter mongoMappingConverter(@Qualifier(value = MONGO_DATABASE_FACTORY) @Nonnull MongoDatabaseFactory factory, @Qualifier(value = MONGO_MAPPING_CONTEXT) @Nonnull MongoMappingContext context, @Qualifier(value = MONGO_CUSTOM_CONVERSIONS) @Nonnull MongoCustomConversions conversions, @Qualifier(value = MONGO_PROPERTIES) @Nonnull MongoProperties properties) {
        return MongoConfigurationUtils.mappingMongoConverter(factory, context, conversions, properties);
    }

    @Bean(name = MONGO_TEMPLATE)
    @ConditionalOnBean(name = {MONGO_DATABASE_FACTORY, MONGO_MAPPING_CONVERTER})
    @ConditionalOnMissingBean(name = MONGO_TEMPLATE)
    public MongoTemplate mongoTemplate(@Qualifier(value = MONGO_DATABASE_FACTORY) @Nonnull MongoDatabaseFactory factory, @Qualifier(value = MONGO_MAPPING_CONVERTER) @Nonnull MongoConverter converter) {
        return new MongoTemplate(factory, converter);
    }

    @Bean(name = GRID_FS_TEMPLATE)
    @ConditionalOnBean(name = {MONGO_DATABASE_FACTORY, MONGO_TEMPLATE, MONGO_PROPERTIES})
    @ConditionalOnMissingBean(name = GRID_FS_TEMPLATE)
    public GridFsTemplate mongoGridFsTemplate(@Qualifier(value = MONGO_DATABASE_FACTORY) @Nonnull MongoDatabaseFactory factory, @Qualifier(value = MONGO_TEMPLATE) @Nonnull MongoTemplate mongoTemplate, @Qualifier(value = MONGO_PROPERTIES) @Nonnull MongoProperties properties) {
        return MongoConfigurationUtils.gridFsTemplate(factory, mongoTemplate, properties);
    }
}
