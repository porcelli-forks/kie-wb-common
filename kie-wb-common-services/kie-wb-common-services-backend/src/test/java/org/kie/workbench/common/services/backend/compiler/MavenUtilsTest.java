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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.apache.commons.io.FilenameUtils;
import org.apache.maven.artifact.Artifact;
import org.drools.core.util.IoUtils;
import org.junit.Test;
import org.kie.workbench.common.services.backend.compiler.configuration.Compilers;
import org.kie.workbench.common.services.backend.compiler.impl.incrementalenabler.DefaultIncrementalCompilerEnabler;
import org.kie.workbench.common.services.backend.compiler.impl.utils.MavenUtils;
import org.uberfire.java.nio.file.Paths;

import static org.junit.Assert.*;

public class MavenUtilsTest {

    @Test
    public void presenceOfDepInThePrj() throws Exception {
        DefaultIncrementalCompilerEnabler enabler = new DefaultIncrementalCompilerEnabler(Compilers.JAVAC);
        List<String> pomList = new ArrayList<>();
        MavenUtils.searchPoms(Paths.get("src/test/projects/dummy_kie_multimodule_untouched/"),
                              pomList);
        assertTrue(pomList.size() == 3);
        List<Artifact> deps = MavenUtils.resolveDependenciesFromMultimodulePrj(pomList);
        assertTrue(deps.size() == 1);
        Artifact artifact = deps.get(0);
        assertTrue(artifact.getArtifactId().equals("kie-api"));
        assertTrue(artifact.getGroupId().equals("org.kie"));
        assertTrue(artifact.getVersion().equals("6.5.0.Final"));
        assertTrue(artifact.getType().equals("jar"));
        assertTrue(artifact.toString().equals("org.kie:kie-api:jar:6.5.0.Final"));
    }

    @Test
    public void testFilrecursive() throws Exception{

        String path = "/tmp/maven/2f0b13f7-d69b-4b6e-a75d-dbf5f575bf4d/examples-.kie-wb-playground/curriculumcourse/";

        List<String> items = IoUtils.recursiveListFile(new File(path), "", filterClasses());

        //List<String> cleaned = items.stream().map(item -> item.substring(item.lastIndexOf("target/classes/") + 15 ));
        String a = "target/classes/curriculumcourse/curriculumcourse/Curriculum.class";
        int ax = a.lastIndexOf("target/classes/");
        String aa = a.substring(ax+15);
        String b = "ann/miultimodule/target/classes/curriculumcourse/curriculumcourse/Curriculum.class";
        int bx = b.lastIndexOf("target/classes/");
        String bb = b.substring(bx+15);
        System.out.println(items.size());

    }

    public static Predicate<File> filterClasses() {
        return f -> f.toString().endsWith(".class") && !FilenameUtils.getName(f.toString()).startsWith(".");
    }
}
