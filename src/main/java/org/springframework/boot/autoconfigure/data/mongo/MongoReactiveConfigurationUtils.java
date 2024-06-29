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
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.SimpleReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.gridfs.ReactiveGridFsTemplate;
import com.mongodb.reactivestreams.client.MongoClient;


/**
 * Utilities for configuring reactive mongo
 *
 * @author David Hsing
 * @see org.springframework.boot.autoconfigure.mongo.MongoReactiveAutoConfiguration
 * @see org.springframework.boot.autoconfigure.data.mongo.MongoReactiveDataAutoConfiguration
 */
@SuppressWarnings({"unused", "BooleanMethodIsAlwaysInverted", "UnusedReturnValue"})
public abstract class MongoReactiveConfigurationUtils {
    @Nonnull
    public static SimpleReactiveMongoDatabaseFactory reactiveMongoDatabaseFactory(@Nonnull MongoClient mongoClient, @Nonnull MongoProperties properties) {
        return new MongoReactiveDataAutoConfiguration().reactiveMongoDatabaseFactory(properties, mongoClient);
    }

    @Nonnull
    public static ReactiveMongoTemplate reactiveMongoTemplate(@Nonnull ReactiveMongoDatabaseFactory factory, @Nonnull MongoConverter converter) {
        return new MongoReactiveDataAutoConfiguration().reactiveMongoTemplate(factory, converter);
    }

    @Nonnull
    public static ReactiveGridFsTemplate reactiveGridFsTemplate(@Nonnull ReactiveMongoDatabaseFactory databaseFactory, @Nonnull MappingMongoConverter converter, @Nonnull DataBufferFactory bufferFactory, @Nonnull MongoProperties properties) {
        return new MongoReactiveDataAutoConfiguration().reactiveGridFsTemplate(databaseFactory, converter, bufferFactory, properties);
    }
}
