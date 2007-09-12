/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.descriptor.rootelement.identifiedbynamespace;

import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.sessions.Project;

import org.eclipse.persistence.testing.oxm.descriptor.rootelement.EmailAddress;
import org.eclipse.persistence.testing.oxm.descriptor.rootelement.MailingAddress;

public class RootElementIdentifiedByNamespaceProject extends Project {

  private final static String EMAIL_PREFIX = "email";
  private final static String EMAIL_NAMESPACE = "www.oracle.com/some-dir/email.xsd";
  private final static String MAILING_PREFIX = "mailing";
  private final static String MAILING_NAMESPACE = "www.oracle.com/some-dir/mailing.xsd";

  public RootElementIdentifiedByNamespaceProject() {
    super();
    addDescriptor(buildEmailAddressDescriptor());
    addDescriptor(buildMailingAddressDescriptor());
  }

  private XMLDescriptor buildEmailAddressDescriptor() {
    XMLDescriptor descriptor = new XMLDescriptor();
    descriptor.setJavaClass(EmailAddress.class);
    descriptor.setDefaultRootElement(EMAIL_PREFIX + ":address");

    NamespaceResolver namespaceResolver = new NamespaceResolver();
    namespaceResolver.put(EMAIL_PREFIX, EMAIL_NAMESPACE);
    descriptor.setNamespaceResolver(namespaceResolver);

    return descriptor;
  }

  private XMLDescriptor buildMailingAddressDescriptor() {
    XMLDescriptor descriptor = new XMLDescriptor();
    descriptor.setJavaClass(MailingAddress.class);
    descriptor.setDefaultRootElement(MAILING_PREFIX + ":address");

    NamespaceResolver namespaceResolver = new NamespaceResolver();
    namespaceResolver.put(MAILING_PREFIX, MAILING_NAMESPACE);
    descriptor.setNamespaceResolver(namespaceResolver);

    return descriptor;    
  }

}