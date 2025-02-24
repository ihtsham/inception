/*
 * Licensed to the Technische Universität Darmstadt under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The Technische Universität Darmstadt 
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.
 *  
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.tudarmstadt.ukp.inception.diam.editor.actions;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.request.Request;
import org.springframework.core.annotation.Order;

import de.tudarmstadt.ukp.clarin.webanno.api.annotation.page.AnnotationPageBase;
import de.tudarmstadt.ukp.clarin.webanno.support.uima.ICasUtil;
import de.tudarmstadt.ukp.inception.diam.editor.config.DiamAutoConfig;
import de.tudarmstadt.ukp.inception.diam.model.ajax.DefaultAjaxResponse;
import de.tudarmstadt.ukp.inception.rendering.editorstate.AnnotatorState;
import de.tudarmstadt.ukp.inception.rendering.vmodel.VID;
import de.tudarmstadt.ukp.inception.schema.AnnotationSchemaService;
import de.tudarmstadt.ukp.inception.schema.adapter.TypeAdapter;

/**
 * <p>
 * This class is exposed as a Spring Component via {@link DiamAutoConfig#deleteAnnotationHandler}.
 * </p>
 */
@Order(EditorAjaxRequestHandler.PRIO_ANNOTATION_HANDLER)
public class DeleteAnnotationHandler
    extends EditorAjaxRequestHandlerBase
{
    public static final String COMMAND = "deleteAnnotation";

    private final AnnotationSchemaService schemaService;

    public DeleteAnnotationHandler(AnnotationSchemaService aSchemaService)
    {
        schemaService = aSchemaService;
    }

    @Override
    public String getCommand()
    {
        return COMMAND;
    }

    @Override
    public boolean accepts(Request aRequest)
    {
        return super.accepts(aRequest) && !getAnnotatorState().isSlotArmed();
    }

    @Override
    public DefaultAjaxResponse handle(AjaxRequestTarget aTarget, Request aRequest)
    {
        try {
            AnnotationPageBase page = getPage();

            VID vid = VID.parseOptional(
                    aRequest.getRequestParameters().getParameterValue(PARAM_ID).toOptionalString());

            if (vid.isNotSet() || vid.isSynthetic()) {
                return new DefaultAjaxResponse(getAction(aRequest));
            }

            CAS cas = page.getEditorCas();
            AnnotatorState state = page.getModelObject();

            AnnotationFS fs = ICasUtil.selectAnnotationByAddr(cas, vid.getId());

            TypeAdapter adapter = schemaService.findAdapter(state.getProject(), fs);
            state.getSelection().set(adapter.select(vid, fs));

            page.getAnnotationActionHandler().actionSelect(aTarget);
            page.getAnnotationActionHandler().actionDelete(aTarget);

            return new DefaultAjaxResponse(getAction(aRequest));
        }
        catch (Exception e) {
            return handleError("Unable to delete annotation", e);
        }
    }
}
