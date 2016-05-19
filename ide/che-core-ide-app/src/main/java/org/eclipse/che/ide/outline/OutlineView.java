/*******************************************************************************
 * Copyright (c) 2012-2016 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.outline;

import org.eclipse.che.ide.api.texteditor.outline.CodeBlock;
import org.eclipse.che.ide.ui.tree.Tree.Listener;
import com.google.gwt.user.client.ui.IsWidget;

public interface OutlineView extends IsWidget {
    void renderTree();

    void rootChanged(CodeBlock newRoot);

    void setTreeEventHandler(Listener<CodeBlock> listener);

    void selectAndExpand(CodeBlock block);
}