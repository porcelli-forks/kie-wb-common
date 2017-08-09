package org.kie.workbench.common.services.backend.compiler.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.jgit.api.Git;
import org.kie.workbench.common.services.backend.builder.af.AFBuilder;
import org.kie.workbench.common.services.backend.compiler.CompilerMapsHolder;
import org.uberfire.java.nio.fs.jgit.JGitFileSystem;

@ApplicationScoped
public class DefaultCompilerMapsHolder implements CompilerMapsHolder {

    private Map<JGitFileSystem, Git> gitMap ;
    private Map<String, AFBuilder> buildersMap ;

    public DefaultCompilerMapsHolder(){
        gitMap = new ConcurrentHashMap<>();
        buildersMap = new ConcurrentHashMap<>();
    }

    /** GIT */

    public Git getGit(JGitFileSystem key){
        return gitMap.get(key);
    }

    public void addGit(JGitFileSystem key, Git git){
        gitMap.putIfAbsent(key,git);
    }

    @Override
    public Git removeGit(JGitFileSystem key) {
        return gitMap.remove(key);
    }

    @Override
    public boolean containsGit(JGitFileSystem key) {
        return buildersMap.containsKey(key);
    }

    @Override
    public void clearGitMap() {
        gitMap.clear();
    }

    /** BUILDER*/

    @Override
    public AFBuilder getBuilder(String projectName) {
        return buildersMap.get(projectName);
    }

    @Override
    public void addBuilder(String projectName,
                           AFBuilder builder) {
        buildersMap.putIfAbsent(projectName, builder);
    }

    @Override
    public AFBuilder removeBuilder(String projectName) {
        return buildersMap.remove(projectName);
    }

    @Override
    public boolean containsBuilder(String projectName) {
        return buildersMap.containsKey(projectName);
    }

    @Override
    public void clearBuilderMap() {
        buildersMap.clear();
    }
}
