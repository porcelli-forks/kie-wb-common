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
package org.kie.workbench.common.services.backend.compiler.impl.share;

import java.util.List;
import java.util.Optional;

import org.uberfire.java.nio.file.Path;

/***
 * Contract to manage Resources URI in terms of pom dependencies (included transient)
 * and .class and resources (.drl and similar) present in the target folders and List of deps raw, instead of rebuild to read the same resources from a previous build
 */
public interface ClassLoadersResourcesHolder {

    void addTargetClassLoader(Path project,
                              ClassLoader classLoader);

    void addDependenciesClassLoader(Path project,
                                    ClassLoader classLoader);

    Optional<ClassLoader> getTargetClassLoader(Path project);

    Optional<ClassLoader> getDependenciesClassLoader(Path project);

    boolean containsPomDependencies(Path projectRootPath);

    void clearClassloaderResourcesMap();

    Boolean removeProjectDeps(Path projectRootPath);

    //POM deps

    void addPomDependencies(Path projectRootPath,
                            List<String> uri);

    List<String> getPomDependencies(Path projectRootPath);

    void replacePomDependencies(Path projectRootPath,
                                List<String> uris);

    //Target deps

    void addTargetProjectDependencies(Path projectRootPath,
                                      List<String> uri);

    List<String> getTargetsProjectDependencies(Path projectRootPath);

    List<String> getTargetsProjectDependenciesFiltered(Path projectRootPath, String packageName);

    void replaceTargetDependencies(Path projectRootPath,
                                   List<String> uris);
}
