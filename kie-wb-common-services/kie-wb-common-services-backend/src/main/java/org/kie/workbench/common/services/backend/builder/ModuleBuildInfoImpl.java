package org.kie.workbench.common.services.backend.builder;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.backend.cache.Cache;
import org.guvnor.common.services.backend.cache.LRUCache;
import org.guvnor.common.services.backend.file.FileDiscoveryService;
import org.guvnor.common.services.project.model.Module;
import org.kie.soup.project.datamodel.commons.util.MVELEvaluator;
import org.kie.workbench.common.services.backend.compiler.impl.utils.MavenRepos;
import org.kie.workbench.common.services.backend.compiler.impl.utils.MavenUtils;
import org.kie.workbench.common.services.backend.compiler.service.AFCompilerService;
import org.kie.workbench.common.services.datamodel.spi.DataModelExtension;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.services.shared.project.ProjectImportsService;
import org.kie.workbench.common.services.shared.whitelist.PackageNameWhiteListService;
import org.uberfire.io.IOService;

@ApplicationScoped
public class ModuleBuildInfoImpl implements ModuleBuildInfo {

    //this is always global today.
    // BUT when switching to workspaces.. this structure will change to Map<Workspace, LRUCache<Project, ModuleBuildData>>
    private final Cache<Module, ModuleBuildData> internalCache = new LRUCache<Module, ModuleBuildData>() {
    };
    private IOService ioService;
    private ProjectImportsService importsService;
    private PackageNameWhiteListService packageNameWhiteListService;
    private FileDiscoveryService fileDiscoveryService;
    private Instance<DataModelExtension> dataModelExtensionsProvider;
    private AFCompilerService compilerService;
    private MVELEvaluator evaluator;

    public ModuleBuildInfoImpl() {
        //CDI Proxy
    }

    @Inject
    public ModuleBuildInfoImpl(@Named("ioStrategy") final IOService ioService,
                               final ProjectImportsService importsService,
                               final PackageNameWhiteListService packageNameWhiteListService,
                               final FileDiscoveryService fileDiscoveryService,
                               final Instance<DataModelExtension> dataModelExtensionsProvider,
                               final AFCompilerService compilerService,
                               final MVELEvaluator evaluator) {
        this.ioService = ioService;
        this.importsService = importsService;
        this.packageNameWhiteListService = packageNameWhiteListService;
        this.fileDiscoveryService = fileDiscoveryService;
        this.dataModelExtensionsProvider = dataModelExtensionsProvider;
        this.compilerService = compilerService;
        this.evaluator = evaluator;
    }

    @Override
    public Optional<ModuleBuildData> getEntry(final Module module) {
        return Optional.ofNullable(internalCache.getEntry(module));
    }

    @Override
    public ModuleBuildData getOrCreateEntry(final Module module) {
        return getEntry(module).orElseGet(() -> {
            synchronized (module) {
                ModuleBuildData result = internalCache.getEntry(module);
                if (result == null) {
                    //the use of this maven repo needs to be improved for workspaces.. as Workspace will hold the maven repo
                    result = new ModuleBuildDataImpl(compilerService,
                                                     ioService,
                                                     importsService,
                                                     packageNameWhiteListService,
                                                     fileDiscoveryService,
                                                     dataModelExtensionsProvider,
                                                     evaluator,
                                                     (KieModule) module,
                                                     MavenUtils.getMavenRepoDir(MavenRepos.GLOBAL));
                    internalCache.setEntry(module, result);
                }
                return result;
            }
        });
    }
}