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

package org.kie.workbench.common.screens.server.management.client.remote;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import org.gwtbootstrap3.client.ui.Anchor;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Heading;
import org.gwtbootstrap3.client.ui.constants.HeadingSize;
import org.gwtbootstrap3.client.ui.html.Div;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import static org.uberfire.commons.validation.PortablePreconditions.*;

@Dependent
@Templated
public class RemoteView extends Composite
        implements RemotePresenter.View {

    private RemotePresenter presenter;

    @Inject
    @DataField("card-container")
    Div cardContainer;

    @Inject
    @DataField
    Button refresh;

    @DataField("server-name")
    Heading serverName = new Heading( HeadingSize.H3 );

    @Inject
    @DataField
    Anchor url;

    @Override
    public void init( final RemotePresenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void setServerName( final String serverName ) {
        this.serverName.setText( serverName );
    }

    @Override
    public void addCard( final IsWidget widget ) {
        cardContainer.add( checkNotNull( "widget", widget ) );
    }

    @Override
    public void setServerURL( final String url ) {
        this.url.setHref( url );
        this.url.setText( url );
    }

    @Override
    public void clear() {
        cardContainer.clear();
    }

    @EventHandler("refresh")
    public void onRefresh( final ClickEvent event ) {
        presenter.refresh();
    }
}
