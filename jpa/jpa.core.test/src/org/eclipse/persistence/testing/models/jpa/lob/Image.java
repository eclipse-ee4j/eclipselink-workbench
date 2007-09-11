/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.lob;

import java.io.Serializable;
import javax.persistence.*;

/**
 * Model class used to test Lobs and Lazy Basics.
 */
 
@Entity
@Table(name="CMP3_IMAGE")
@SecondaryTable(name="CMP3_CLIP")
@PrimaryKeyJoinColumn(name="ID", referencedColumnName="ID")
public class Image implements Serializable {
    private int id;
    private byte[] audio;
    private char[] commentary;
    private Byte[] picture;
    private String script;
    private SerializableNonEntity customAttribute1;
    private SerializableNonEntity customAttribute2;

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Lob
    @Column(table="CMP3_CLIP")
    @Basic(fetch=FetchType.LAZY)
    public byte[] getAudio(){
        return audio;
    }

    @Lob
    @Column(table="CMP3_CLIP")
    public char[] getCommentary()    {
        return commentary;
    }
  
    @Lob
    @Basic(fetch=FetchType.LAZY)
    public SerializableNonEntity getCustomAttribute1() {
        return customAttribute1;
    }
  

    public SerializableNonEntity getCustomAttribute2() {
        return customAttribute2;
    }

    @Id
    public int getId(){
        return id;
    }

    @Lob
    public Byte[] getPicture(){
        return picture;
    }

    @Lob
    public String getScript()    {
        return script;
    }

    public void setAudio(byte[] audio)    {
        this.audio = audio;
    }

    public void setCommentary(char[] commentary)    {
        this.commentary = commentary;
    }
  
    public void setCustomAttribute1(SerializableNonEntity customAttribute) {
        this.customAttribute1= customAttribute;
    }
  
    public void setCustomAttribute2(SerializableNonEntity customAttribute) {
        this.customAttribute2=customAttribute;
    }

    public void setId(int id)    {
        this.id = id;
    }

    public void setPicture(Byte[] picture)    {
        this.picture = picture;
    }

    public void setScript(String script)    {
        this.script = script;
    }
}