/*
 * Copyright 2020
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
package de.tudarmstadt.ukp.inception.workload.dynamic.extension;

import de.tudarmstadt.ukp.inception.workload.dynamic.config.DynamicWorkloadManagerAutoConfiguration;
import de.tudarmstadt.ukp.inception.workload.extension.WorkloadExtension;

/**
 * <p>
 * This class is exposed as a Spring Component via
 * {@link DynamicWorkloadManagerAutoConfiguration#dynamicWorkloadExtension}
 * </p>
 */
public class DynamicWorkloadExtension implements WorkloadExtension
{
    public static final String EXTENSION_ID = "Dynamic_Workload";

    @Override
    public String getId()
    {
        return EXTENSION_ID;
    }

}
