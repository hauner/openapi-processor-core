/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.support.datatypes

import io.openapiprocessor.core.model.datatypes.DataType
import io.openapiprocessor.core.model.datatypes.MappedCollectionDataType

/**
 * OpenAPI type 'array' maps to Collection<>.
 */
class CollectionDataType(
    item: DataType
): MappedCollectionDataType(
    Collection::class.java.simpleName,
    Collection::class.java.packageName,
    item)
