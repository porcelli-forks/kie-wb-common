package org.kie.workbench.common.services.backend.builder;

import java.util.Optional;

import org.guvnor.common.services.project.model.Module;

public interface ModuleBuildInfo {

    Optional<ModuleBuildData> getEntry(Module module);

    ModuleBuildData getOrCreateEntry(Module module);
}
