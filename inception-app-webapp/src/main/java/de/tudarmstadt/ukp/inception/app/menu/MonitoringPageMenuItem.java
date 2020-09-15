/*
 * Copyright 2018
 * Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universität Darmstadt
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.tudarmstadt.ukp.inception.app.menu;

import static de.tudarmstadt.ukp.inception.workload.monitoring.extension.StaticWorkloadExtension.EXTENSION_ID;

import org.apache.wicket.Page;
import org.apache.wicket.Session;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import de.tudarmstadt.ukp.clarin.webanno.api.ProjectService;
import de.tudarmstadt.ukp.clarin.webanno.api.WebAnnoConst;
import de.tudarmstadt.ukp.clarin.webanno.model.Project;
import de.tudarmstadt.ukp.clarin.webanno.security.UserDao;
import de.tudarmstadt.ukp.clarin.webanno.security.model.User;
import de.tudarmstadt.ukp.clarin.webanno.ui.core.menu.MenuItem;
import de.tudarmstadt.ukp.clarin.webanno.ui.monitoring.page.MonitoringPage;
import de.tudarmstadt.ukp.inception.ui.core.session.SessionMetaData;
import de.tudarmstadt.ukp.inception.workload.dynamic.extension.DynamicWorkloadExtension;
import de.tudarmstadt.ukp.inception.workload.model.WorkloadManagementService;

@Order(300)
@Component
public class MonitoringPageMenuItem implements MenuItem
{
    private final ApplicationContext applicationContext;
    private final UserDao userRepo;
    private final ProjectService projectService;
    private final WorkloadManagementService workloadManagementService;

    @Autowired
    public MonitoringPageMenuItem(ApplicationContext aApplicationContextUserDao, UserDao aUserRepo,
        ProjectService aProjectService, WorkloadManagementService aWorkloadManagementService)
    {
        applicationContext = aApplicationContextUserDao;
        userRepo = aUserRepo;
        projectService = aProjectService;
        workloadManagementService = aWorkloadManagementService;
    }

    @Override
    public String getPath()
    {
        return "/monitoring";
    }
    
    @Override
    public String getIcon()
    {
        return "images/attribution.png";
    }
    
    @Override
    public String getLabel()
    {
        return "Monitoring";
    }
    
    /**
     * Only admins and project managers can see this page
     */
    @Override
    public boolean applies()
    {
        Project sessionProject = Session.get().getMetaData(SessionMetaData.CURRENT_PROJECT);
        if (sessionProject == null) {
            return false;
        }

        //Check if for currently in the DB set workload strategy the bean is used,
        //if not use the static workload manager.
        //Other strategies simply get a new case.
        try {
            switch (workloadManagementService.
                getOrCreateWorkloadManagerConfiguration(sessionProject).getExtensionPointID()) {
            case "Dynamic workload":
                applicationContext.getBean(DynamicWorkloadExtension.class);
                break;
            }
        } catch (NoSuchBeanDefinitionException e) {
            workloadManagementService.setWorkloadManagerConfiguration(EXTENSION_ID,sessionProject);
        }

        // The project object stored in the session is detached from the persistence context and
        // cannot be used immediately in DB interactions. Fetch a fresh copy from the DB.

        // Visible if the current user is a curator or project admin
        User user = userRepo.getCurrentUser();

        return (projectService.isCurator(sessionProject, user)
            || projectService.isProjectAdmin(sessionProject, user))
            && WebAnnoConst.PROJECT_TYPE_ANNOTATION.equals(sessionProject.getMode())
            && EXTENSION_ID.equals(workloadManagementService.
            getOrCreateWorkloadManagerConfiguration(sessionProject).
            getExtensionPointID());
    }
    
    @Override
    public Class<? extends Page> getPageClass()
    {
        return MonitoringPage.class;
    }
}
