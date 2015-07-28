/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.social.hp.client.userpage;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.FieldSet;
import org.gwtbootstrap3.client.ui.base.HasPlaceholder;
import org.uberfire.mvp.ParameterizedCommand;

public class SearchWidget extends Composite {

    @UiField
    FieldSet fieldset;

    interface Mybinder
            extends
            UiBinder<Widget, SearchWidget> {

    }

    private static Mybinder uiBinder = GWT.create( Mybinder.class );

    protected void init(final List<String> users,
                        final ParameterizedCommand<String> onSelect,
                        final String suggestText) {
        initWidget( uiBinder.createAndBindUi( this ) );
        SuggestBox suggestBox = new SuggestBox( new MultiWordSuggestOracle() {{
            addAll( users );
        }} );
        suggestBox.getElement().setAttribute( HasPlaceholder.PLACEHOLDER, suggestText );
        suggestBox.addSelectionHandler( new SelectionHandler<SuggestOracle.Suggestion>() {
            @Override
            public void onSelection( SelectionEvent<SuggestOracle.Suggestion> event ) {
                onSelect.execute( event.getSelectedItem().getReplacementString() );
            }
        } );
        fieldset.add( suggestBox );
    }
}