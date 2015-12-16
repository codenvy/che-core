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
package org.eclipse.che.ide.debug;

import org.eclipse.che.ide.api.project.tree.VirtualFile;

/**
 * Immutable object represents a breakpoint. It isn't designed to be preserved.
 * {@link org.eclipse.che.ide.debug.dto.BreakpointDto} should be used then.
 *
 * @author Evgen Vidolob
 * @author Anatoliy Bazko
 */
public class Breakpoint {
    protected int         lineNumber;
    protected VirtualFile file;
    private   Type        type;
    private   String      message;
    private   String      path;

    /**
     * Breakpoint becomes active if is added to a JVM, otherwise it is just a user mark.
     */
    private boolean active;

    public Breakpoint(Type type, int lineNumber, String path, VirtualFile file, boolean active) {
        this(type, lineNumber, path, file, null, active);
    }

    public Breakpoint(Type type, int lineNumber, String path, VirtualFile file, String message, boolean active) {
        this.type = type;
        this.lineNumber = lineNumber;
        this.path = path;
        this.message = message;
        this.file = file;
        this.active = active;
    }

    /**
     * Getter for {@link #active}
     */
    public boolean isActive() {
        return active;
    }

    /** @return the type */
    public Type getType() {
        return type;
    }

    /** @return the lineNumber */
    public int getLineNumber() {
        return lineNumber;
    }

    /** @return the message */
    public String getMessage() {
        return message;
    }

    /** @return file path */
    public String getPath() {
        return path;
    }

    /**
     * Returns the file with which this breakpoint is associated.
     *
     * @return file with which this breakpoint is associated
     */
    public VirtualFile getFile() {
        return file;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Breakpoint [lineNumber=").append(lineNumber)
               .append(", type=").append(type)
               .append(", active=").append(active)
               .append(", message=").append(message)
               .append(", path=").append(path)
               .append("]");
        return builder.toString();
    }

    public enum Type {
        BREAKPOINT, DISABLED, CONDITIONAL, CURRENT
    }
}