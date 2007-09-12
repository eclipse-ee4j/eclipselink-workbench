/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.mappings.anyattribute.withgroupingelement;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.anyattribute.withoutgroupingelement.Root;
import org.eclipse.persistence.oxm.mappings.*;

public class AnyAttributeWithGroupingElementNSProject extends Project {
    public AnyAttributeWithGroupingElementNSProject() {
        this.addDescriptor(buildRootDescriptor());
        //XMLLogin login = new XMLLogin();
        //login.setPlatform(new DOMPlatform());
        //this.setLogin(login);
        //this.getLogin().setPlatform(new org.eclipse.persistence.oxm.platform.DOMPlatform());
    }

    public ClassDescriptor buildRootDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Root.class);
        descriptor.setDefaultRootElement("myns:root");

        XMLAnyAttributeMapping mapping = new XMLAnyAttributeMapping();
        mapping.setAttributeName("any");
        mapping.setGetMethodName("getAny");
        mapping.setSetMethodName("setAny");
        mapping.setXPath("myns:grouping-element");
        descriptor.addMapping(mapping);

        NamespaceResolver nr = new NamespaceResolver();
        nr.put("myns", "www.oracle.com/some-dir/some.xsd");
        //nr.put("", "www.oracle.com/some-other-dir/some.xsd");
        descriptor.setNamespaceResolver(nr);

        return descriptor;
    }
}