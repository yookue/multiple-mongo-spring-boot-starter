/*
 * Copyright (c) 2020 Unikue Ltd. All rights reserved.
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

package cn.unikue.springstarter.multiplemongodb.config;


import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoReactiveDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoReactiveAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.stereotype.Repository;
import com.mongodb.client.MongoClient;
import cn.unikue.commonplexus.springcondition.annotation.ConditionalOnAllBooleanProperties;
import cn.unikue.commonplexus.springcondition.annotation.ConditionalOnAnnotation;


/**
 * Secondary configuration for mongo repository
 *
 * @author David Hsing
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnAllBooleanProperties(value = {
    @ConditionalOnBooleanProperty(prefix = "spring.multiple-mongo", name = "enabled", matchIfMissing = true),
    @ConditionalOnBooleanProperty(prefix = SecondaryMongoAutoConfiguration.PROPERTIES_PREFIX, name = "repository-enabled", matchIfMissing = true)
})
@ConditionalOnClass(value = MongoClient.class)
@ConditionalOnBean(name = SecondaryMongoAutoConfiguration.MONGO_CLIENT)
@ConditionalOnAnnotation(includeFilter = Repository.class, basePackage = SecondaryMongoRepositoryConfiguration.REPOSITORY_PACKAGE)
@AutoConfigureAfter(value = SecondaryMongoAutoConfiguration.class)
@AutoConfigureBefore(value = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class, MongoReactiveAutoConfiguration.class, MongoReactiveDataAutoConfiguration.class})
@EnableMongoRepositories(basePackages = SecondaryMongoRepositoryConfiguration.REPOSITORY_PACKAGE, mongoTemplateRef = SecondaryMongoAutoConfiguration.MONGO_TEMPLATE)
public class SecondaryMongoRepositoryConfiguration {
    public static final String REPOSITORY_PACKAGE = "**.repository.secondary.mongo";    // $NON-NLS-1$
}
