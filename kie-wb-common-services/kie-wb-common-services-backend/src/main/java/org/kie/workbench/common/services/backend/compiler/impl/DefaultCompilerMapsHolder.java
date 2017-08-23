package org.kie.workbench.common.services.backend.compiler.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.enterprise.context.ApplicationScoped;

import org.eclipse.jgit.api.Git;
import org.kie.workbench.common.services.backend.builder.af.AFBuilder;
import org.kie.workbench.common.services.backend.builder.af.KieAFBuilder;
import org.kie.workbench.common.services.backend.compiler.CompilerMapsHolder;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.fs.jgit.JGitFileSystem;

@ApplicationScoped
public class DefaultCompilerMapsHolder implements CompilerMapsHolder {

    private Map<JGitFileSystem, Git> gitMap;
    private Map<Path, KieAFBuilder> buildersMap;

    public DefaultCompilerMapsHolder() {
        gitMap = new ConcurrentHashMap<>();
        buildersMap = new ConcurrentHashMap<>();
    }

    /**
     * GIT
     */

    public Git getGit(JGitFileSystem key) {
        return gitMap.get(key);
    }

    public void addGit(JGitFileSystem key, Git git) { gitMap.putIfAbsent(key, git); }

    public Git removeGit(JGitFileSystem key) {
        return gitMap.remove(key);
    }

    public boolean containsGit(JGitFileSystem key) {
        return gitMap.containsKey(key);
    }

    public void clearGitMap() {
        gitMap.clear();
    }

    /**
     * BUILDER
     */

    public KieAFBuilder getBuilder(Path projectRootPath) {
        return buildersMap.get(projectRootPath);
    }

    public void addBuilder(final Path projectRootPath, final KieAFBuilder builder) {
        buildersMap.putIfAbsent(projectRootPath, builder);
    }

    public KieAFBuilder removeBuilder(Path projectRootPath) {
        return buildersMap.remove(projectRootPath);
    }

    public boolean containsBuilder(Path projectRootPath) {
        return buildersMap.containsKey(projectRootPath);
    }

    public void clearBuilderMap() {
        buildersMap.clear();
    }

}