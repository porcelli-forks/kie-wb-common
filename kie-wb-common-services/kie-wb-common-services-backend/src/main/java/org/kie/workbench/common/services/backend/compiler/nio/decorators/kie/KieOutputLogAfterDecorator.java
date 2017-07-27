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
package org.kie.workbench.common.services.backend.compiler.nio.decorators.kie;

import java.util.Collections;

import org.kie.workbench.common.services.backend.compiler.CompilationResponse;
import org.kie.workbench.common.services.backend.compiler.KieCompilationResponse;
import org.kie.workbench.common.services.backend.compiler.impl.DefaultKieCompilationResponse;
import org.kie.workbench.common.services.backend.compiler.impl.LogUtils;
import org.kie.workbench.common.services.backend.compiler.nio.CompilationRequest;
import org.kie.workbench.common.services.backend.compiler.nio.KieMavenCompiler;

public class KieOutputLogAfterDecorator implements KieCompilerDecorator {

    private KieMavenCompiler compiler;

    public KieOutputLogAfterDecorator(KieMavenCompiler compiler) {
        this.compiler = compiler;
    }

    @Override
    public KieCompilationResponse compileSync(CompilationRequest req) {
        CompilationResponse res = compiler.compileSync(req);

        if (res.isSuccessful()) {
            return getResponse(Boolean.TRUE,
                               req);
        } else {
            return getResponse(Boolean.FALSE,
                               req);
        }
    }

    private DefaultKieCompilationResponse getResponse(Boolean result,
                                                      CompilationRequest req) {
        if (req.getKieCliRequest().isLogRequested()) {
            return new DefaultKieCompilationResponse(result,
                                                     LogUtils.getOutput(req.getInfo().getPrjPath().toAbsolutePath().toString(),
                                                                        req.getKieCliRequest().getRequestUUID()));
        } else {
            return new DefaultKieCompilationResponse(result,
                                                     Collections.emptyList());
        }
    }
}
