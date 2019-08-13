/*
 * Copyright 2019 https://github.com/hauner/openapi-generatr-spring
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

package com.github.hauner.openapi.spring.writer

import com.github.hauner.openapi.spring.model.Endpoint
import com.github.hauner.openapi.spring.model.Interface


class InterfaceWriter {
    HeaderWriter headerWriter
    MethodWriter methodWriter

    void write (Writer target, Interface itf) {
        headerWriter.write (target)
        target.write ("package ${itf.packageName};\n\n")
        target.write ("\n")

        Set<String> imports = collectImports (itf.endpoints)
        imports.each {
            target.write ("import ${it};\n")
        }
        target.write ("\n")

        target.write ("interface ${itf.interfaceName} {\n\n")

        itf.endpoints.each {
            methodWriter.write(target, it)
            target.write ("\n")
        }

        target.write ("}\n")
    }

    Set<String> collectImports(List<Endpoint> endpoints) {
        Set<String> imports = []

        imports.add ('org.springframework.http.ResponseEntity')

        endpoints.each {
            imports.add (it.method.classNameWithPackage)
        }

        imports
    }
}