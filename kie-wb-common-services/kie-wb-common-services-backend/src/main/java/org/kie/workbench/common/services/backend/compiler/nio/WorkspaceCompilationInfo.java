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
package org.kie.workbench.common.services.backend.compiler.nio;

import java.util.Optional;

import org.uberfire.java.nio.file.Path;

/***
 * Holds informations shared with Kie compilers and the Compilation Request/Response
 */
public class WorkspaceCompilationInfo {

    protected Path prjPath;
    protected Path enhancedMainPomFile;
    protected Boolean kiePluginPresent = Boolean.FALSE;

    public WorkspaceCompilationInfo(Path prjPath) {
        this.prjPath = prjPath;
    }

    public Boolean lateAdditionEnhancedMainPomFile(Path enhancedPom) {
        if (enhancedMainPomFile == null && enhancedPom != null) {
            this.enhancedMainPomFile = enhancedPom;
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public Boolean lateAdditionKiePluginPresent(Boolean present) {
        if ((kiePluginPresent == null && present != null)) {
            this.kiePluginPresent = present;
            return Boolean.TRUE;
        }
        if (present != null) {
            kiePluginPresent = kiePluginPresent | present;
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public Boolean isKiePluginPresent() {
        return kiePluginPresent;
    }

    public Path getPrjPath() {
        return prjPath;
    }

    public Optional<Path> getEnhancedMainPomFile() {
        return Optional.ofNullable(enhancedMainPomFile);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("WorkspaceCompilationInfo{");
        sb.append("prjPath:{").append(prjPath);
        sb.append("}, enhancedMainPomFile:{").append(enhancedMainPomFile);
        sb.append('}');
        return sb.toString();
    }
}
