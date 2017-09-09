package org.kie.workbench.common.services.backend.compiler.impl.utils;

import java.util.Optional;
import java.util.UUID;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.jgit.api.Git;
import org.guvnor.m2repo.backend.server.GuvnorM2Repository;
import org.guvnor.m2repo.backend.server.repositories.ArtifactRepositoryService;
import org.kie.workbench.common.services.backend.builder.af.KieAFBuilder;
import org.kie.workbench.common.services.backend.builder.af.impl.DefaultKieAFBuilder;
import org.kie.workbench.common.services.backend.compiler.impl.share.CompilerMapsHolder;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.fs.jgit.JGitFileSystem;

@ApplicationScoped
public class BuilderUtils {

    @Inject
    private CompilerMapsHolder compilerMapsHolder;
    @Inject
    private GuvnorM2Repository guvnorM2Repository;

    public Optional<KieAFBuilder> getBuilder(Path nioPath) {
        KieAFBuilder builder = compilerMapsHolder.getBuilder(nioPath);
        if (builder == null) {
            if (nioPath.getFileSystem() instanceof JGitFileSystem) {
                Git repo = JGitUtils.tempClone((JGitFileSystem) nioPath.getFileSystem(),
                                               UUID.randomUUID().toString());
                compilerMapsHolder.addGit((JGitFileSystem) nioPath.getFileSystem(),
                                          repo);
                builder = new DefaultKieAFBuilder(nioPath,
                                                  MavenUtils.getMavenRepoDir(guvnorM2Repository.getM2RepositoryDir(ArtifactRepositoryService.GLOBAL_M2_REPO_NAME)),
                                                  compilerMapsHolder);
            }
        }
        return Optional.ofNullable(builder);
    }
}
