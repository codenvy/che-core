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
package org.eclipse.che.ide.api.project.tree.generic;

import org.eclipse.che.api.project.gwt.client.ProjectServiceClient;
import org.eclipse.che.api.project.shared.dto.ItemReference;
import org.eclipse.che.ide.api.event.ItemEvent;
import org.eclipse.che.ide.api.event.UpdateTreeNodeChildrenEvent;
import org.eclipse.che.ide.api.project.tree.AbstractTreeNode;
import org.eclipse.che.ide.api.project.tree.TreeNode;
import org.eclipse.che.ide.api.project.tree.TreeStructure;
import org.eclipse.che.ide.rest.AsyncRequestCallback;
import org.eclipse.che.ide.rest.DtoUnmarshallerFactory;
import org.eclipse.che.ide.rest.Unmarshallable;
import org.eclipse.che.ide.util.loging.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;

import javax.annotation.Nonnull;

/**
 * Abstract base class for all tree nodes that represent an {@link ItemReference}.
 * There are exactly two kinds of {@link ItemNode}: {@link FileNode}, {@link FolderNode}.
 *
 * @author Artem Zatsarynnyy
 * @see FileNode
 * @see FolderNode
 */
public abstract class ItemNode extends AbstractTreeNode<ItemReference> implements StorableNode<ItemReference>, UpdateTreeNodeDataIterable {
    protected ProjectServiceClient   projectServiceClient;
    protected DtoUnmarshallerFactory dtoUnmarshallerFactory;

    /**
     * Creates new node.
     *
     * @param parent
     *         parent node
     * @param data
     *         an object this node encapsulates
     * @param treeStructure
     *         {@link org.eclipse.che.ide.api.project.tree.TreeStructure} which this node belongs
     * @param eventBus
     *         {@link EventBus}
     * @param projectServiceClient
     *         {@link ProjectServiceClient}
     * @param dtoUnmarshallerFactory
     *         {@link DtoUnmarshallerFactory}
     */
    public ItemNode(TreeNode<?> parent,
                    ItemReference data,
                    TreeStructure treeStructure,
                    EventBus eventBus,
                    ProjectServiceClient projectServiceClient,
                    DtoUnmarshallerFactory dtoUnmarshallerFactory) {
        super(parent, data, treeStructure, eventBus);
        this.projectServiceClient = projectServiceClient;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
    }

    /** {@inheritDoc} */
    @Nonnull
    @Override
    public String getId() {
        return getData().getName();
    }

    /** {@inheritDoc} */
    @Nonnull
    @Override
    public String getDisplayName() {
        return getData().getName();
    }

    /** {@inheritDoc} */
    @Override
    public void refreshChildren(AsyncCallback<TreeNode<?>> callback) {
    }

    /** {@inheritDoc} */
    @Nonnull
    @Override
    public String getName() {
        return getData().getName();
    }

    /** {@inheritDoc} */
    @Nonnull
    @Override
    public String getPath() {
        return getData().getPath();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isRenamable() {
        return true;
    }

    /** Rename appropriate {@link ItemReference} using Codenvy Project API. */
    @Override
    public void rename(final String newName, final RenameCallback callback) {
        projectServiceClient.rename(getPath(), newName, null, new AsyncRequestCallback<Void>() {
            @Override
            protected void onSuccess(final Void result) {
                // parent node should be StorableNode instance
                final String parentPath = ((StorableNode) getParent()).getPath();
                Unmarshallable<ItemReference> unmarshaller = dtoUnmarshallerFactory.newUnmarshaller(ItemReference.class);

                // update inner ItemReference object
                projectServiceClient.getItem(parentPath + "/" + newName, new AsyncRequestCallback<ItemReference>(unmarshaller) {
                    @Override
                    protected void onSuccess(ItemReference itemReference) {
                        setData(itemReference);

                        AsyncCallback<Void> asyncCallback = new AsyncCallback<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                ItemNode.super.rename(newName, callback);
                            }

                            @Override
                            public void onFailure(Throwable throwable) {
                                Log.info(getClass(), "Error update children");
                            }
                        };

                        eventBus.fireEvent(new UpdateTreeNodeChildrenEvent(ItemNode.this, asyncCallback));
                    }

                    @Override
                    protected void onFailure(Throwable exception) {
                        callback.onFailure(exception);
                    }
                });
            }

            @Override
            protected void onFailure(Throwable exception) {
                callback.onFailure(exception);
            }
        });
    }

    /** {@inheritDoc} */
    public void updateData(final AsyncCallback<Void> asyncCallback, String updatedParentNodePath) {
        String path = solveNewPath(updatedParentNodePath, getPath());
        Log.error(getClass(), path);

        Unmarshallable<ItemReference> unmarshaller = dtoUnmarshallerFactory.newUnmarshaller(ItemReference.class);
        projectServiceClient.getItem(path, new AsyncRequestCallback<ItemReference>(unmarshaller) {
            @Override
            protected void onSuccess(ItemReference result) {
                setData(result);
                asyncCallback.onSuccess(null);
            }

            @Override
            protected void onFailure(Throwable exception) {
                asyncCallback.onFailure(exception);
            }
        });
    }

    /**
     * This method uses for calculation new path of node.
     * Simple formula: newPath = updatedParentPath + "/" + itemName doesn't work for package node, so we use this method
     * @param nodeRenamedPath path of renamed node
     * @param currentPath current node path
     * @return item path. This path we will use for getting new ItemReference of node
     */
    private String solveNewPath(String nodeRenamedPath, String currentPath) {
        String prefixPath = nodeRenamedPath.substring(0, nodeRenamedPath.lastIndexOf("/") + 1);
        currentPath = currentPath.replace(prefixPath, "");
        currentPath = currentPath.substring(currentPath.indexOf("/"), currentPath.length());
        nodeRenamedPath = nodeRenamedPath.substring(nodeRenamedPath.lastIndexOf("/") + 1, nodeRenamedPath.length());

        return prefixPath + nodeRenamedPath + currentPath;
    }



    /** {@inheritDoc} */
    @Override
    public boolean isDeletable() {
        return true;
    }

    /** Delete appropriate {@link ItemReference} using Codenvy Project API. */
    @Override
    public void delete(final DeleteCallback callback) {
        projectServiceClient.delete(getPath(), new AsyncRequestCallback<Void>() {
            @Override
            protected void onSuccess(Void result) {
                ItemNode.super.delete(new DeleteCallback() {
                    @Override
                    public void onDeleted() {
                        callback.onDeleted();
                    }

                    @Override
                    public void onFailure(Throwable exception) {
                        callback.onFailure(exception);
                    }
                });
                eventBus.fireEvent(new ItemEvent(ItemNode.this, ItemEvent.ItemOperation.DELETED));
            }

            @Override
            protected void onFailure(Throwable exception) {
                callback.onFailure(exception);
            }
        });
    }
}
