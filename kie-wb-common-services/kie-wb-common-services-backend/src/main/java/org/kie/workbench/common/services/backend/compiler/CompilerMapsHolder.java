package org.kie.workbench.common.services.backend.compiler;

import org.eclipse.jgit.api.Git;
import org.kie.workbench.common.services.backend.builder.af.AFBuilder;
import org.uberfire.java.nio.fs.jgit.JGitFileSystem;

/**
 * Holder of the maps used in the compiler
 * **/
public interface CompilerMapsHolder {

    Git getGit(JGitFileSystem key);

    void addGit(JGitFileSystem key, Git git);

    Git removeGit(JGitFileSystem key);

    boolean containsGit(JGitFileSystem key);

    void clearGitMap();

    AFBuilder getBuilder(String projectName);

    void addBuilder(String projectName, AFBuilder builder);

    AFBuilder removeBuilder(String projectName);

    boolean containsBuilder(String projectName);

    void clearBuilderMap();

}
