package org.kie.workbench.common.screens.server.management.model.spec;

public interface RuleConfig extends Config {

    Long getPollInterval();

    ScannerStatus getScannerStatus();

}
