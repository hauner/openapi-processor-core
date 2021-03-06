/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.openapiprocessor.core.model.datatypes.*
import io.openapiprocessor.core.support.datatypes.ObjectDataType

class BeanValidationFactorySpec: StringSpec({
    isolationMode = IsolationMode.InstancePerTest

    "applies @Valid to 'array' with object items" {
        val validation = BeanValidationFactory()

        val dataType = ArrayDataType(
            ObjectDataType("Foo", "pkg", linkedMapOf("foo" to StringDataType())
        ))
        val info = validation.validate(dataType)

        info.typeName shouldBe "Foo[]"
        info.imports shouldBe listOf(BeanValidation.VALID.import)
        info.annotations shouldBe listOf(BeanValidation.VALID.annotation)
    }

    "does not apply @Valid to 'array' with simple items" {
        val validation = BeanValidationFactory()

        val dataType = ArrayDataType(StringDataType())
        val info = validation.validate(dataType)

        info.typeName shouldBe "String[]"
        info.imports shouldBe listOf()
        info.annotations shouldBe emptyList()
    }

    "applies @Valid to mapped collection with object items" {
        val validation = BeanValidationFactory()

        val dataType = MappedCollectionDataType("List", "pkg",
            ObjectDataType("Foo", "pkg", linkedMapOf("foo" to StringDataType())
        ))
        val info = validation.validate(dataType)

        info.typeName shouldBe "List<@Valid Foo>"
        info.imports shouldBe listOf(BeanValidation.VALID.import)
        info.annotations shouldBe emptyList()
    }

    "does not apply @Valid to mapped collection with simple items" {
        val validation = BeanValidationFactory()

        val dataType = MappedCollectionDataType("List", "pkg", StringDataType())
        val info = validation.validate(dataType)

        info.typeName shouldBe "List<String>"
        info.imports shouldBe listOf()
        info.annotations shouldBe emptyList()
    }

    "applies @Pattern to String" {
        val validation = BeanValidationFactory()

        val dataType = StringDataType(DataTypeConstraints(pattern = "regex"))
        val info = validation.validate(dataType)

        info.typeName shouldBe "String"
        info.imports shouldBe listOf(BeanValidation.PATTERN.import)
        info.annotations shouldBe listOf("""${BeanValidation.PATTERN.annotation}(regexp = "regex")""")
    }

    "applies @Pattern to String with escaping" {
        val validation = BeanValidationFactory()

        val dataType = StringDataType(DataTypeConstraints(pattern = """\.\\"""))
        val info = validation.validate(dataType)

        info.typeName shouldBe "String"
        info.imports shouldBe listOf(BeanValidation.PATTERN.import)
        info.annotations shouldBe listOf("""${BeanValidation.PATTERN.annotation}(regexp = "\\.\\\\")""")
    }

})
