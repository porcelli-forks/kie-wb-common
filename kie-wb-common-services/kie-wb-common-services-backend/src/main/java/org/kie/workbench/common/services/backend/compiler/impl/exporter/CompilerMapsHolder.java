package org.kie.workbench.common.services.backend.compiler.impl.exporter;

import org.eclipse.jgit.api.Git;
import org.kie.workbench.common.services.backend.builder.af.KieAFBuilder;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.fs.jgit.JGitFileSystem;

/**
 * Holder of the maps used in the compiler
 * **/
public interface CompilerMapsHolder {

    //BUILDER

    Git getGit(JGitFileSystem key);

    void addGit(JGitFileSystem key, Git git);

    Git removeGit(JGitFileSystem key);

    boolean containsGit(JGitFileSystem key);

    void clearGitMap();

    //BUILDER

    KieAFBuilder getBuilder(Path projectRootPath);

    void addBuilder(Path projectRootPath, KieAFBuilder builder);

    KieAFBuilder removeBuilder(Path projectRootPath);

    boolean containsBuilder(Path projectRootPath);

    void clearBuilderMap();

}
