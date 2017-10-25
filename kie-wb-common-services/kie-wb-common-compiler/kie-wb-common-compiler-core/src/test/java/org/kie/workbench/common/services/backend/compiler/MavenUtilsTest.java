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

package org.kie.workbench.common.services.backend.compiler;

import java.util.List;

import org.apache.maven.artifact.Artifact;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;
import org.kie.workbench.common.services.backend.compiler.impl.utils.MavenUtils;
import org.uberfire.java.nio.file.Paths;

public class MavenUtilsTest {

    @Test
    public void presenceOfDepInThePrj() throws Exception {
        List<String> pomList = MavenUtils.searchPoms(Paths.get("src/test/projects/dummy_kie_multimodule_untouched/"));
        assertThat(pomList).hasSize(3);
        List<Artifact> deps = MavenUtils.resolveDependenciesFromMultimodulePrj(pomList);
        assertThat(deps).hasSize(1);
        Artifact artifact = deps.get(0);
        assertThat(artifact.getArtifactId()).isEqualTo("kie-api");
        assertThat(artifact.getGroupId()).isEqualTo("org.kie");
        assertThat(artifact.getVersion()).isEqualTo("6.5.0.Final");
        assertThat(artifact.getType()).isEqualTo("jar");
        assertThat(artifact.toString()).isEqualTo("org.kie:kie-api:jar:6.5.0.Final");
    }
}
