/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.model

/**
 * OpenAPI documentation properties.
 *
 * a property is null if it is not set or not allowed at the element.
 */
class Documentation(

    /**
     * plain text `summary`
     */
    val summary: String? = null,

    /**
     * plain text (or common mark)
     */
    val description: String? = null

)
