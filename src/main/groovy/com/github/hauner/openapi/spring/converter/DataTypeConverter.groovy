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

package com.github.hauner.openapi.spring.converter

import com.github.hauner.openapi.spring.converter.mapping.AmbiguousTypeMappingException
import com.github.hauner.openapi.spring.converter.mapping.TargetType
import com.github.hauner.openapi.spring.converter.mapping.TypeMapping
import com.github.hauner.openapi.spring.converter.schema.ArraySchemaType
import com.github.hauner.openapi.spring.converter.schema.ObjectSchemaType
import com.github.hauner.openapi.spring.converter.schema.SchemaInfo
import com.github.hauner.openapi.spring.model.DataTypes
import com.github.hauner.openapi.spring.model.datatypes.ArrayDataType
import com.github.hauner.openapi.spring.model.datatypes.BooleanDataType
import com.github.hauner.openapi.spring.model.datatypes.CollectionDataType
import com.github.hauner.openapi.spring.model.datatypes.ListDataType
import com.github.hauner.openapi.spring.model.datatypes.LocalDateDataType
import com.github.hauner.openapi.spring.model.datatypes.MapDataType
import com.github.hauner.openapi.spring.model.datatypes.MappedDataType
import com.github.hauner.openapi.spring.model.datatypes.ObjectDataType
import com.github.hauner.openapi.spring.model.datatypes.DataType
import com.github.hauner.openapi.spring.model.datatypes.DoubleDataType
import com.github.hauner.openapi.spring.model.datatypes.FloatDataType
import com.github.hauner.openapi.spring.model.datatypes.IntegerDataType
import com.github.hauner.openapi.spring.model.datatypes.LongDataType
import com.github.hauner.openapi.spring.model.datatypes.NoneDataType
import com.github.hauner.openapi.spring.model.datatypes.OffsetDateTimeDataType
import com.github.hauner.openapi.spring.model.datatypes.SetDataType
import com.github.hauner.openapi.spring.model.datatypes.StringDataType

/**
 * Converter to map OpenAPI schemas to Java data types.
 *
 * @author Martin Hauner
 */
class DataTypeConverter {

    private ApiOptions options
    private DataTypeMapper mapper

    DataTypeConverter(ApiOptions options) {
        this.options = options
        this.mapper = new DataTypeMapper(options.typeMappings)
    }

    DataType none() {
        new NoneDataType()
    }

    /**
     * converts an open api type (i.e. a {@code Schema}) to a java data type including nested types.
     * Stores named objects in {@code dataTypes} for re-use. {@code dataTypeInfo} provides the type
     * name used to add it to the list of data types.
     *
     * @param dataTypeInfo the open api type with context information
     * @param dataTypes known object types
     * @return the resulting java data type
     */
    DataType convert (SchemaInfo dataTypeInfo, DataTypes dataTypes) {

        if (dataTypeInfo.isArray ()) {
            createArrayDataType (dataTypeInfo, dataTypes)

        } else if (dataTypeInfo.isRefObject ()) {
            def datatype = dataTypes.findRef (dataTypeInfo.ref)
            if (datatype) {
                return datatype
            }

            def refTypeInfo = dataTypeInfo.buildForRef ()
            convert (refTypeInfo, dataTypes)

        } else if (dataTypeInfo.isObject ()) {
            createObjectDataType (dataTypeInfo, dataTypes)

        } else {
            createSimpleDataType (dataTypeInfo)
        }
    }

    private DataType createArrayDataType (SchemaInfo schemaInfo, DataTypes dataTypes) {
        SchemaInfo itemSchemaInfo = schemaInfo.buildForItem ()
        DataType item = convert (itemSchemaInfo, dataTypes)

        def arrayType
        TargetType targetType = mapper.getMappedDataType (schemaInfo, 'array', new ArraySchemaType(schemaInfo))
        switch (targetType?.typeName) {
            case Collection.name:
                arrayType = new CollectionDataType (item: item)
                break
            case List.name:
                arrayType = new ListDataType (item: item)
                break
            case Set.name:
                arrayType = new SetDataType (item: item)
                break
            default:
                arrayType = new ArrayDataType (item: item)
        }

        arrayType
    }

    private DataType createObjectDataType (SchemaInfo schemaInfo, DataTypes dataTypes) {
        def objectType

        TargetType targetType = mapper.getMappedDataType (schemaInfo, 'object', new ObjectSchemaType(schemaInfo))
        if (targetType) {
            objectType = new MappedDataType (
                type: targetType.name,
                pkg: targetType.pkg,
                genericTypes: targetType.genericNames
            )

            dataTypes.add (schemaInfo.name, objectType)
            return objectType
        }

        switch (schemaInfo.getXJavaType ()) {
            case Map.name:
                objectType = new MapDataType ()
                dataTypes.add (schemaInfo.name, objectType)
                break

            default:
                objectType = new ObjectDataType (
                    type: schemaInfo.name,
                    pkg: [options.packageName, 'model'].join ('.')
                )

                schemaInfo.eachProperty { String propName, SchemaInfo propDataTypeInfo ->
                    def propType = convert (propDataTypeInfo, dataTypes)
                    objectType.addObjectProperty (propName, propType)
                }

                dataTypes.add (objectType)
        }

        objectType
    }

    private DataType createSimpleDataType (SchemaInfo schemaInfo) {

        TargetType targetType = getSimpleDataType (schemaInfo)
        if (targetType) {
            def simpleType = new MappedDataType (
                type: targetType.name,
                pkg: targetType.pkg,
                genericTypes: targetType.genericNames
            )
            return simpleType
        }

        def typeFormat = schemaInfo.type
        if (schemaInfo.format) {
            typeFormat += '/' + schemaInfo.format
        }

        def simpleType
        switch (typeFormat) {
            case 'integer':
            case 'integer/int32':
                simpleType = new IntegerDataType ()
                break
            case 'integer/int64':
                simpleType = new LongDataType ()
                break
            case 'number':
            case 'number/float':
                simpleType = new FloatDataType ()
                break
            case 'number/double':
                simpleType = new DoubleDataType ()
                break
            case 'boolean':
                simpleType = new BooleanDataType ()
                break
            case 'string':
                simpleType = new StringDataType ()
                break
            case 'string/date':
                simpleType = new LocalDateDataType ()
                break
            case 'string/date-time':
                simpleType = new OffsetDateTimeDataType ()
                break
            default:
                throw new UnknownDataTypeException(schemaInfo.type, schemaInfo.format)
        }

        simpleType
    }

    private TargetType getSimpleDataType (SchemaInfo schemaInfo) {
        if (options.typeMappings) {

            // check global mapping
            List<TypeMapping> mappings = getTypeMappings ()
            List<TypeMapping> matches = mappings.findAll {
                it.sourceTypeName == schemaInfo.type && it.sourceTypeFormat == schemaInfo.format
            }

            // no mapping, use default
            if (matches.isEmpty ()) {
                return null
            }

            if (matches.size () != 1) {
                 throw new AmbiguousTypeMappingException (matches)
            }

            def match = matches.first ()
            return new TargetType (
                typeName: match.targetTypeName,
                genericNames: match.genericTypeNames ?: [])
        }

        null
    }

    private List<TypeMapping> getTypeMappings () {
        options.typeMappings.findResults {
            it instanceof TypeMapping ? it : null
        }
    }

}
