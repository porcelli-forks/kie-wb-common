/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.services.datamodel.backend.server;

import java.net.URL;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;

import org.junit.Test;
import org.kie.soup.project.datamodel.oracle.ModuleDataModelOracle;
import org.kie.workbench.common.services.datamodel.backend.server.service.DataModelService;
import org.uberfire.backend.vfs.Path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.services.datamodel.backend.server.ModuleDataModelOracleTestUtils.assertContains;

/**
 * Tests for DataModelService
 */
public class ProjectDataModelExtendJavaTypeTest extends AbstractDataModelWeldTest {

    @Test
    public void testProjectExtendJavaTypeWithQualifiedDRLBeanName() throws Exception {
        final Bean dataModelServiceBean = (Bean) beanManager.getBeans(DataModelService.class).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext(dataModelServiceBean);
        final DataModelService dataModelService = (DataModelService) beanManager.getReference(dataModelServiceBean,
                                                                                              DataModelService.class,
                                                                                              cc);

        final URL packageUrl = this.getClass().getResource("/DataModelBackendExtendJavaTypeTest1");
        final org.uberfire.java.nio.file.Path nioPackagePath = fs.getPath(packageUrl.toURI());
        final Path packagePath = paths.convert(nioPackagePath);

        final ModuleDataModelOracle oracle = dataModelService.getModuleDataModel(packagePath);

        assertNotNull(oracle);

        assertEquals(2,
                     oracle.getModuleModelFields().size());
        assertContains("t4p1.Bean1",
                       oracle.getModuleModelFields().keySet());
        assertContains("java.lang.String",
                       oracle.getModuleModelFields().keySet());

        assertTrue(oracle.getModuleEventTypes().get("t4p1.Bean1"));
        assertFalse(oracle.getModuleEventTypes().get("java.lang.String"));
    }

    @Test
    public void testProjectExtendJavaTypeWithImport() throws Exception {
        final Bean dataModelServiceBean = (Bean) beanManager.getBeans(DataModelService.class).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext(dataModelServiceBean);
        final DataModelService dataModelService = (DataModelService) beanManager.getReference(dataModelServiceBean,
                                                                                              DataModelService.class,
                                                                                              cc);

        final URL packageUrl = this.getClass().getResource("/DataModelBackendExtendJavaTypeTest2");
        final org.uberfire.java.nio.file.Path nioPackagePath = fs.getPath(packageUrl.toURI());
        final Path packagePath = paths.convert(nioPackagePath);

        final ModuleDataModelOracle oracle = dataModelService.getModuleDataModel(packagePath);

        assertNotNull(oracle);

        assertEquals(2,
                     oracle.getModuleModelFields().size());
        assertContains("t5p1.Bean1",
                       oracle.getModuleModelFields().keySet());
        assertContains("java.lang.String",
                       oracle.getModuleModelFields().keySet());

        assertTrue(oracle.getModuleEventTypes().get("t5p1.Bean1"));
        assertFalse(oracle.getModuleEventTypes().get("java.lang.String"));
    }
}
