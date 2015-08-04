/*
 * Copyright 2015 JBoss Inc
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

package org.kie.workbench.common.screens.server.management.client.util;

import com.google.gwt.dom.client.Element;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.constants.Styles;
import org.kie.workbench.common.screens.server.management.client.resources.ContainerResources;
import org.kie.workbench.common.screens.server.management.model.ContainerStatus;

/**
 * TODO: update me
 */
public final class ContainerStatusUtil {

    public static void setupStatus( final Element status,
                                    final ContainerStatus state ) {
        status.removeAttribute( "class" );
        switch ( state ) {
            case STARTED:
                status.setTitle( "Started" );
                status.addClassName( IconType.PLAY_CIRCLE_O.getCssName() );
                status.addClassName( ContainerResources.INSTANCE.CSS().green() );
                break;
            case STOPPED:
                status.setTitle( "Stopped" );
                status.addClassName( IconType.POWER_OFF.getCssName() );
                status.addClassName( ContainerResources.INSTANCE.CSS().orange() );
                break;
            case LOADING:
                status.setTitle( "Loading" );
                status.addClassName( IconType.REFRESH.getCssName() );
                status.addClassName( Styles.ICON_SPIN );
                break;
            case ERROR:
                status.setTitle( "Error" );
                status.addClassName( IconType.EXCLAMATION_CIRCLE.getCssName() );
                status.addClassName( ContainerResources.INSTANCE.CSS().red() );
                break;
            default:
                status.setTitle( "" );
                break;
        }
    }
}
