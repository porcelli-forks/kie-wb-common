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
import java.util.List;
import java.util.Optional;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.guvnor.m2repo.backend.server.GuvnorM2Repository;
import org.kie.api.builder.KieModule;
import org.kie.scanner.KieModuleMetaData;
import org.kie.scanner.KieModuleMetaDataImpl;
import org.kie.workbench.common.services.backend.compiler.impl.classloader.CompilerClassloaderUtils;
import org.kie.workbench.common.services.backend.compiler.impl.kie.KieCompilationResponse;
import org.kie.workbench.common.services.backend.compiler.impl.share.BuilderCache;
import org.kie.workbench.common.services.backend.compiler.impl.share.ClassLoadersResourcesHolder;
import org.kie.workbench.common.services.backend.compiler.impl.share.DependenciesCache;
import org.kie.workbench.common.services.backend.compiler.impl.share.GitCache;
import org.kie.workbench.common.services.backend.compiler.impl.share.KieModuleMetaDataCache;
import org.kie.workbench.common.services.backend.compiler.impl.utils.KieAFBuilderUtil;
import org.kie.workbench.common.services.backend.project.MapClassLoader;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.java.nio.file.Path;

public class KieAfBuilderClassloaderUtil {


    /**
     * This method return the classloader with the .class founded in the target folder and the UrlClassloader with all .jsrs declared and transitives from poms
     */
    public static Optional<MapClassLoader> getProjectClassloader(KieProject project,
                                                                 GitCache gitCache, BuilderCache builderCache,
                                                                 KieModuleMetaDataCache kieModuleMetaDataCache,
                                                                 DependenciesCache dependenciesCache,
                                                                 GuvnorM2Repository guvnorM2Repository,
                                                                 ClassLoadersResourcesHolder classloadersResourcesHolder,
                                                                 String indentity) {

        Path nioPath = Paths.convert(project.getRootPath());
        KieAFBuilder builder = KieAFBuilderUtil.getKieAFBuilder(project.getRootPath().toURI().toString(), nioPath, gitCache, builderCache,  guvnorM2Repository, indentity);

        KieCompilationResponse res = builder.build( !indentity.equals("system") , Boolean.FALSE);//Here the log is not required during the indexing startup

        if (res.isSuccessful() && res.getKieModule().isPresent() && res.getWorkingDir().isPresent()) {

            /* absolute path on the fs */
            Path workingDir = res.getWorkingDir().get();
            /* we collects all the thing produced in the target/classes folders */
            Optional<List<String>> artifactsFromTargets = CompilerClassloaderUtils.getStringFromTargets(workingDir);

            if (artifactsFromTargets.isPresent()) {
                classloadersResourcesHolder.addTargetProjectDependencies(workingDir, artifactsFromTargets.get());
            } else {
                Optional<List<String>> targetClassesOptional = CompilerClassloaderUtils.getStringsFromTargets(workingDir);
                if (targetClassesOptional.isPresent()) {
                    classloadersResourcesHolder.addTargetProjectDependencies(workingDir, targetClassesOptional.get());// check this add
                }
            }
            MapClassLoader projectClassLoader = null;
            final KieModule module = res.getKieModule().get();
            if (module instanceof InternalKieModule) {

                ClassLoader dependenciesClassLoader = addToHolderAndGetDependenciesClassloader(workingDir,
                        kieModuleMetaDataCache,
                        dependenciesCache,
                        classloadersResourcesHolder,
                        res);

                /** The integration works with CompilerClassloaderUtils.getMapClasses
                 * This MapClassloader needs the .class from the target folders in a prj produced by the build, as a Map
                 * with a key like this "curriculumcourse/curriculumcourse/Curriculum.class" and the byte[] as a value */
                projectClassLoader = new MapClassLoader(CompilerClassloaderUtils.getMapClasses(workingDir.toString()), dependenciesClassLoader);
                classloadersResourcesHolder.addTargetClassLoader(workingDir, projectClassLoader);
            }
            return Optional.ofNullable(projectClassLoader);
        }
        return Optional.empty();
    }

    private static ClassLoader addToHolderAndGetDependenciesClassloader(Path workingDir,
                                                                        KieModuleMetaDataCache kieModuleMetaDataCache,
                                                                        DependenciesCache dependenciesCache,
                                                                        ClassLoadersResourcesHolder classloadersResourcesHolder,
                                                                        KieCompilationResponse res) {

        Optional<ClassLoader> opDependenciesClassLoader = Optional.empty();
        if (res.getWorkingDir().isPresent()) {
            opDependenciesClassLoader = classloadersResourcesHolder.getDependenciesClassLoader(workingDir);
        }

        ClassLoader dependenciesClassLoader;
        if (!opDependenciesClassLoader.isPresent()) {
            dependenciesClassLoader = new URLClassLoader(res.getProjectDependenciesAsURL().get().toArray(new URL[res.getProjectDependenciesAsURL().get().size()]));
        } else {
            dependenciesClassLoader = opDependenciesClassLoader.get();
        }
        classloadersResourcesHolder.addDependenciesClassLoader(workingDir, dependenciesClassLoader);

        if (res.getProjectDependenciesRaw().isPresent()) {
            dependenciesCache.addDependenciesRaw(workingDir, res.getProjectDependenciesRaw().get());
        }
        if (res.getProjectDependenciesAsURI().isPresent()) {
            KieModuleMetaData kieModuleMetaData = new KieModuleMetaDataImpl((InternalKieModule) res.getKieModule().get(),
                    res.getProjectDependenciesAsURI().get());
            kieModuleMetaDataCache.addKieMetaData(workingDir, kieModuleMetaData);
        }
        return dependenciesClassLoader;
    }

}
