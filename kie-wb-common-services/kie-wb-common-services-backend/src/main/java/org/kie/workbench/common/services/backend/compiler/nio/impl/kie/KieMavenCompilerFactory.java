/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.services.backend.compiler.nio.impl.kie;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.kie.workbench.common.services.backend.compiler.configuration.Decorator;
import org.kie.workbench.common.services.backend.compiler.configuration.KieDecorator;
import org.kie.workbench.common.services.backend.compiler.nio.KieMavenCompiler;
import org.kie.workbench.common.services.backend.compiler.nio.decorators.kie.KieAfterDecorator;
import org.kie.workbench.common.services.backend.compiler.nio.decorators.kie.KieJGITCompilerBeforeDecorator;
import org.kie.workbench.common.services.backend.compiler.nio.decorators.kie.KieOutputLogAfterDecorator;

/***
 * Factory to create compilers with correct order of decorators to build Kie Projects
 * working with the kie takari plugin
 */
public class KieMavenCompilerFactory {

    private static Map<String, KieMavenCompiler> compilers = new ConcurrentHashMap<>();

    private KieMavenCompilerFactory() {
    }

    /**
     * Provides a Maven compiler decorated with a Decorator Behaviour
     */
    public static KieMavenCompiler getCompiler(KieDecorator decorator) {
        KieMavenCompiler compiler = compilers.get(decorator.name());
        if (compiler == null) {
            compiler = createAndAddNewCompiler(decorator);
        }
        return compiler;
    }

    private static KieMavenCompiler createAndAddNewCompiler(KieDecorator decorator) {

        KieMavenCompiler compiler;
        switch (decorator) {
            case NONE:
                compiler = new KieDefaultMavenCompiler();
                break;

            case KIE_AFTER:
                compiler = new KieAfterDecorator(new KieDefaultMavenCompiler());
                break;

            case KIE_AND_LOG_AFTER:
                compiler = new KieAfterDecorator(new KieOutputLogAfterDecorator(new KieDefaultMavenCompiler()));
                break;

            case JGIT_BEFORE:
                compiler = new KieJGITCompilerBeforeDecorator(new KieDefaultMavenCompiler());
                break;

            case JGIT_BEFORE_AND_LOG_AFTER:
                compiler = new KieJGITCompilerBeforeDecorator(new KieOutputLogAfterDecorator(new KieDefaultMavenCompiler()));
                break;

            case JGIT_BEFORE_AND_KIE_AFTER:
                compiler = new KieJGITCompilerBeforeDecorator(new KieAfterDecorator(new KieDefaultMavenCompiler()));
                break;

            case LOG_OUTPUT_AFTER:
                compiler = new KieOutputLogAfterDecorator(new KieDefaultMavenCompiler());
                break;

            case JGIT_BEFORE_AND_KIE_AND_LOG_AFTER:
                compiler = new KieJGITCompilerBeforeDecorator(new KieAfterDecorator(new KieOutputLogAfterDecorator(new KieDefaultMavenCompiler())));
                break;

            default:
                compiler = new KieDefaultMavenCompiler();
        }
        compilers.put(Decorator.NONE.name(),
                      compiler);
        return compiler;
    }

    /**
     * Delete the compilers creating a new data structure
     */
    public static void deleteCompilers() {
        compilers = new ConcurrentHashMap<>();
    }

    /**
     * Clear the internal data structure
     */
    public static void clearCompilers() {
        compilers.clear();
    }
}
