/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor;

import java.util.Iterator;

import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.MWXmlNode;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.SchemaChange;
import org.eclipse.persistence.tools.workbench.utility.iterators.NullIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.SingleElementIterator;

import org.eclipse.persistence.descriptors.ClassDescriptor;

/**
 * Null representation of the descriptor inheritance policy.  This class
 * should be used only as a placeholder for persistence and runtime conversion
 * purposes.  This class is essentially read-only.  Any calls to the implemented
 * setter methods will invoke an <code>UnsupportedOperationException</code>.
 *
 * @version 10.1.3
 */
public final class MWNullInheritancePolicy
    extends MWAbstractDescriptorPolicy
    implements MWInheritancePolicy, MWXmlNode
{
    public MWNullInheritancePolicy(MWDescriptor parent) {
        super(parent);
    }

    @Override
    public MWDescriptor getParentDescriptor() {
        return null;
    }

    @Override
    public Iterator candidateParentDescriptors() {
        return NullIterator.instance();
    }

    @Override
    public MWDescriptor getRootDescriptor() {
        return getOwningDescriptor();
    }

    @Override
    public Iterator descriptorLineage() {
        // a lineage always includes the starting descriptor
        return new SingleElementIterator(this.getOwningDescriptor());
    }

    @Override
    public Iterator childDescriptors() {
        return NullIterator.instance();
    }

    @Override
    public Iterator descendentDescriptors() {
        return NullIterator.instance();
    }

    @Override
    public boolean isRoot() {
        return false;
    }

    @Override
    public MWClassIndicatorPolicy getClassIndicatorPolicy() {
        return new MWNullClassIndicatorPolicy(this);
    }

    @Override
    public void buildClassIndicatorValues() {
        //do nothing
    }

    @Override
    public void descriptorInheritanceChanged() {
        // no op
    }

    @Override
    public void parentDescriptorMorphedToAggregate() {
        //do nothing, null policy
    }

    @Override
    public void automap() {
        //do nothing, null policy
    }

    @Override
    public void adjustRuntimeDescriptor(ClassDescriptor runtimeDescriptor) {
        // Do Nothing.  Null Policy.
    }


    // **************** MWXmlInheritancePolicy implementation *****************

    /** @see MWXmlNode#resolveXpaths() */
    @Override
    public void resolveXpaths() {
        // Do nothing.  Null policy.
    }

    /** @see MWXmlNode#schemaChanged(SchemaChange) */
    @Override
    public void schemaChanged(SchemaChange change) {
        // Do nothing.  Null policy.
    }


    // **************** MWClassIndicatorPolicy.Parent implementation **********

    @Override
    public MWMappingDescriptor getContainingDescriptor() {
        return this.getOwningDescriptor();
    }
}
