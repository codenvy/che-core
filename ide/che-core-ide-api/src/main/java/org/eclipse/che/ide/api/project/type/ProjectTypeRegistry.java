/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.api.project.type;

import org.eclipse.che.commons.annotation.Nullable;
import org.eclipse.che.api.project.shared.dto.ProjectTypeDto;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

/**
 * @author Vitaly Parfonov
 * @author Artem Zatsarynnyi
 */
public interface ProjectTypeRegistry {

    @Nullable
    ProjectTypeImpl getProjectType(@NotNull String id);

    Set<ProjectTypeImpl> getProjectTypes();

    void register(ProjectTypeDto projectType);

    void registerAll(List<ProjectTypeDto> projectTypesList);
}
