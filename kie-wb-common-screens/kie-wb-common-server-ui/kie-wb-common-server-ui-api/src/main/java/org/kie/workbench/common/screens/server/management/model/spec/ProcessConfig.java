package org.kie.workbench.common.screens.server.management.model.spec;

public interface ProcessConfig extends Config {

    String getRuntimeStrategy();

    String getKBase();

    String getKSession();

    String getMergeMode();

}
