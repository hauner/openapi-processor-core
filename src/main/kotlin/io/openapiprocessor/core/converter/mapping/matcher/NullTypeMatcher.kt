/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping.matcher

import io.openapiprocessor.core.converter.mapping.NullTypeMapping

/**
 * [io.openapiprocessor.core.converter.mapping.MappingFinder] matcher for null type mapping.
 */
class NullTypeMatcher: (NullTypeMapping) -> Boolean {

    override fun invoke(mapping: NullTypeMapping): Boolean {
        return mapping.sourceTypeName == "null"
    }

}
