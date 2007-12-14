/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.oxm.record;

import org.eclipse.persistence.internal.oxm.ContainerValue;
import org.eclipse.persistence.internal.oxm.Reference;
import org.eclipse.persistence.internal.oxm.XPathFragment;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.oxm.record.UnmarshalRecord;
import org.eclipse.persistence.oxm.sequenced.SequencedObject;
import org.eclipse.persistence.oxm.sequenced.Setting;

/**
 * An implementation of UnmarshalContext for handling sequenced objects that
 * are mapped to XML. 
 */
public class SequencedUnmarshalContext implements UnmarshalContext {

    private static final String TEXT_XPATH = "text()";

    private Setting currentSetting;
    
    public void startElement(UnmarshalRecord unmarshalRecord) {
        Setting parentSetting = currentSetting;
        XPathFragment xPathFragment = unmarshalRecord.getXPathNode().getXPathFragment();
        if(null != xPathFragment) {
            currentSetting = new Setting(xPathFragment.getNamespaceURI(), xPathFragment.getLocalName());
        } else {
            currentSetting = new Setting();
        }
        int levelIndex = unmarshalRecord.getLevelIndex();
        if(0 == levelIndex) {
        } else if(1 == levelIndex) {
            SequencedObject sequencedObject = (SequencedObject) unmarshalRecord.getCurrentObject();
            sequencedObject.getSettings().add(currentSetting);
        } else {
            parentSetting.addChild(currentSetting);
        }
    }

    public void characters(UnmarshalRecord unmarshalRecord) {
        if(!TEXT_XPATH.equals(currentSetting.getName())) {
            Setting parentSetting = currentSetting;
            currentSetting = new Setting(null, TEXT_XPATH);
            if(null != parentSetting) {
                parentSetting.addChild(currentSetting);
            }
        }
    }

    public void endElement(UnmarshalRecord unmarshalRecord) {
        if(null == currentSetting) {
            return;
        }
        if(TEXT_XPATH.equals(currentSetting.getName())) {
            currentSetting = currentSetting.getParent().getParent();
        } else {
            currentSetting = currentSetting.getParent();
        }
    }

    public void setAttributeValue(UnmarshalRecord unmarshalRecord, Object value, DatabaseMapping mapping) {
        currentSetting.setMapping(mapping);
        currentSetting.setObject(unmarshalRecord.getCurrentObject());
        currentSetting.setValue(value);
    }

    public void addAttributeValue(UnmarshalRecord unmarshalRecord, ContainerValue containerValue, Object value) {
        currentSetting.setMapping(containerValue.getMapping());
        currentSetting.setObject(unmarshalRecord.getCurrentObject());
        currentSetting.addValue(value, true, unmarshalRecord.getContainerInstance(containerValue));
    }

    public void reference(Reference reference) {
        currentSetting.setObject(reference.getSourceObject());
        currentSetting.setMapping((DatabaseMapping) reference.getMapping());
        reference.setSetting(currentSetting);
    }

    public void unmappedContent(UnmarshalRecord unmarshalRecord) {
        Setting parentSetting = currentSetting.getParent();
        if(null == parentSetting) {
            ((SequencedObject)unmarshalRecord.getCurrentObject()).getSettings().remove(currentSetting);
        }
        currentSetting = parentSetting;
    }

}