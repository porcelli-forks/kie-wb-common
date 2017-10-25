package org.kie.workbench.common.services.backend.builder.cache;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.backend.cache.Cache;
import org.guvnor.common.services.backend.cache.LRUCache;
import org.guvnor.common.services.backend.file.FileDiscoveryService;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.m2repo.backend.server.GuvnorM2Repository;
import org.kie.soup.project.datamodel.commons.util.MVELEvaluator;
import org.kie.workbench.common.services.backend.compiler.impl.utils.MavenRepos;
import org.kie.workbench.common.services.backend.compiler.impl.utils.MavenUtils;
import org.kie.workbench.common.services.backend.compiler.service.AFCompilerService;
import org.kie.workbench.common.services.datamodel.spi.DataModelExtension;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.services.shared.project.ProjectImportsService;
import org.kie.workbench.common.services.shared.whitelist.PackageNameWhiteListService;
import org.uberfire.io.IOService;

import static org.guvnor.m2repo.backend.server.repositories.ArtifactRepositoryService.GLOBAL_M2_REPO_NAME;

@ApplicationScoped
public class ModuleCache {

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private ProjectImportsService importsService;

    @Inject
    private PackageNameWhiteListService packageNameWhiteListService;

    @Inject
    private FileDiscoveryService fileDiscoveryService;

    @Inject
    private Instance<DataModelExtension> dataModelExtensionsProvider;

    @Inject
    private AFCompilerService compilerService;

    @Inject
    private MVELEvaluator evaluator;

    //this is always global today.
    // BUT when switching to workspaces.. this structure will change to Map<Workspace, LRUCache<Project, ProjectBuildData>>
    private final Cache<Module, ProjectBuildData> internalCache = new LRUCache<Module, ProjectBuildData>() {
    };

    public Optional<ProjectBuildData> getEntry(Module module) {
        return Optional.ofNullable(internalCache.getEntry(module));
    }

    public ProjectBuildData getOrCreateEntry(Module module) {
        return getEntry(module).orElseGet(() -> {
            //the use of this maven repo needs to be improved for workspaces.. as Workspace will hold the maven repo
            final ProjectBuildData value = new ProjectBuildDataImpl(compilerService,
                                                                    ioService,
                                                                    importsService,
                                                                    packageNameWhiteListService,
                                                                    fileDiscoveryService,
                                                                    dataModelExtensionsProvider,
                                                                    evaluator,
                                                                    (KieModule) module,
                                                                    MavenUtils.getMavenRepoDir(MavenRepos.GLOBAL));
            internalCache.setEntry(module, value);
            return value;
        });
    }
}
