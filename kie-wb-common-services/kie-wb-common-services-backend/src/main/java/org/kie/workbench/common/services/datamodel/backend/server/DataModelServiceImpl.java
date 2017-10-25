/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.services.datamodel.backend.server;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.guvnor.common.services.backend.exceptions.ExceptionUtilities;
import org.guvnor.common.services.project.model.Package;
import org.kie.soup.project.datamodel.commons.oracle.ModuleDataModelOracleImpl;
import org.kie.soup.project.datamodel.commons.oracle.PackageDataModelOracleImpl;
import org.kie.soup.project.datamodel.oracle.ModuleDataModelOracle;
import org.kie.soup.project.datamodel.oracle.PackageDataModelOracle;
import org.kie.workbench.common.services.backend.builder.cache.ModuleCache;
import org.kie.workbench.common.services.datamodel.backend.server.service.DataModelService;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.uberfire.backend.vfs.Path;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

@ApplicationScoped
public class DataModelServiceImpl
        implements DataModelService {

    private static final ModuleDataModelOracleImpl EMPTY_MODULE_MODEL = new ModuleDataModelOracleImpl();
    private static final PackageDataModelOracle EMPTY_PKG_MODEL = new PackageDataModelOracleImpl();

    private ModuleCache moduleCache;

    private KieModuleService moduleService;

    public DataModelServiceImpl() {
    }

    @Inject
    public DataModelServiceImpl(final ModuleCache moduleCache,
                                final KieModuleService moduleService) {
        this.moduleCache = moduleCache;
        this.moduleService = moduleService;
    }

    @Override
    public PackageDataModelOracle getDataModel(final Path resourcePath) {
        try {
            final Optional<KieModule> project = resolveModule(checkNotNull("resourcePath", resourcePath));
            final Optional<Package> pkg = resolvePackage(resourcePath);

            //Resource was not within a Project structure
            if (!project.isPresent()) {
                return EMPTY_PKG_MODEL;
            }

            return moduleCache.getOrCreateEntry(project.get()).getPackageDataModelOracle(pkg.get());
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }

    @Override
    public ModuleDataModelOracle getModuleDataModel(final Path resourcePath) {
        try {
            final Optional<KieModule> module = resolveModule(checkNotNull("resourcePath", resourcePath));

            //Resource was not within a Project structure
            if (!module.isPresent()) {
                return EMPTY_MODULE_MODEL;
            }
            //Retrieve (or build) oracle
            return moduleCache.getOrCreateEntry(module.get()).getModuleDataModelOracle();
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }

    private Optional<KieModule> resolveModule(final Path resourcePath) {
        return Optional.ofNullable(moduleService.resolveModule(resourcePath));
    }

    private Optional<Package> resolvePackage(final Path resourcePath) {
        return Optional.ofNullable(moduleService.resolvePackage(resourcePath));
    }
}
