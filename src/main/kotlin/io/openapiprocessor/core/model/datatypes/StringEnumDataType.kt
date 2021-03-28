/*
 * Copyright 2019 the original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.openapiprocessor.core.model.datatypes

/**
 * OpenAPI type 'string' with enum constraint maps to enum class.
 *
 * @author Martin Hauner
 */
class StringEnumDataType(

    private val type: String,
    private val pkg: String,
    val values: List<String> = emptyList(),
    override val constraints: DataTypeConstraints? = null,
    override val deprecated: Boolean = false

): DataTypeBase() {

    override fun getName(): String {
        return type
    }

    override fun getPackageName(): String {
        return pkg
    }

}


