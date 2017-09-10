/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.services.backend.builder.af;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Optional;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.guvnor.m2repo.backend.server.GuvnorM2Repository;
import org.kie.api.builder.KieModule;
import org.kie.scanner.KieModuleMetaData;
import org.kie.workbench.common.services.backend.compiler.impl.classloader.CompilerClassloaderUtils;
import org.kie.workbench.common.services.backend.compiler.impl.kie.KieCompilationResponse;
import org.kie.workbench.common.services.backend.compiler.impl.share.ClassloadersResourcesHolder;
import org.kie.workbench.common.services.backend.compiler.impl.share.CompilerMapsHolder;
import org.kie.workbench.common.services.backend.compiler.impl.utils.KieAFBuilderUtil;
import org.kie.workbench.common.services.backend.project.MapClassLoader;
import org.uberfire.java.nio.file.Path;

public class KieAfBuilderClassloaderUtil {

    /**
     * Thi smethod return the classloader with the .class founded in the target folder and the UrlClassloader with all .jsrs declared and transitives from poms
     */
    public static ClassLoader getProjectClassloader(Path nioPath,
                                                    CompilerMapsHolder compilerMapsHolder,
                                                    GuvnorM2Repository guvnorM2Repository, ClassloadersResourcesHolder classloadersResourcesHolder) {

        KieAFBuilder builder = KieAFBuilderUtil.getKieAFBuilder(nioPath,
                                                                compilerMapsHolder,
                                                                guvnorM2Repository);
        KieCompilationResponse res = builder.build();
        if (res.isSuccessful() && res.getKieModule().isPresent() && res.getWorkingDir().isPresent()) {
            ClassLoader projectClassLoader;
            final KieModule module = res.getKieModule().get();
            if (module instanceof InternalKieModule) {
                Optional<ClassLoader> opDependenciesClassLoader = classloadersResourcesHolder.getDependenciesClassloader(nioPath);
                ClassLoader dependenciesClassLoader;
                if (!opDependenciesClassLoader.isPresent()){
                    dependenciesClassLoader = new URLClassLoader(res.getProjectDependenciesAsURL().get().toArray(new URL[res.getProjectDependenciesAsURL().get().size()]));
                }else {
                    dependenciesClassLoader = opDependenciesClassLoader.get();
                }
                classloadersResourcesHolder.addDependenciesClassloader(nioPath, dependenciesClassLoader);

                /** The integration works with CompilerClassloaderUtils.getMapClasses
                 * This MapClassloader needs the .class from the target folders in a prj produced by the build, as a Map
                 * with a key like this "curriculumcourse/curriculumcourse/Curriculum.class" and the byte[] as a value */
                projectClassLoader = new MapClassLoader(CompilerClassloaderUtils.getMapClasses(res.getWorkingDir().get().toString()),
                                                        dependenciesClassLoader);
                classloadersResourcesHolder.addTargetClassloader(nioPath, projectClassLoader);
            } else {
                projectClassLoader = KieModuleMetaData.Factory.newKieModuleMetaData(module).getClassLoader();
                classloadersResourcesHolder.addTargetClassloader(nioPath, projectClassLoader);
            }
            return projectClassLoader;
        }
        return null;
    }
}
