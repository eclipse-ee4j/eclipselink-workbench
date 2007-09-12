/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa;

import java.lang.instrument.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;


/**
 * This agent is intended to be run prior to start up on a CMP3 JavaSE application.
 * It gets the globalInstrumentation and makes it available to TopLink's initialization code.
 * There are two kinds of initialization.  Normally, initialization occurs through reflective
 * creation and invokation of TopLink JavaSECMPInitializer.
 * It is possible to run it with the "main" argument to the agent in which case it will simply
 * try to set the globalInstrumentation field on the JavaSECMPInitializer.  This type of initialization
 * is useful when debugging, but imposes some restrictions on the user.  One such restriction is
 * that no domain classes that use lazy loading may be references in any way other than reflective in the application
 */
public class JavaSECMPInitializerAgent {
    public static void premain(String agentArgs, Instrumentation instr) throws Exception {
        // Reflection allows:
        //  JavaSECMPInitializerAgent to be the *ONLY* class is the jar file specified in -javaagent;
        //  Loading JavaSECMPInitializer class using SystemClassLoader.
        if ((agentArgs != null) && agentArgs.equals("main")) {
            initializeFromMain(instr);
        } else {
            initializeFromAgent(instr);
        }
    }
    
    public static void initializeFromAgent(Instrumentation instr) throws Exception {
            Class cls = Class.forName("org.eclipse.persistence.internal.jpa.JavaSECMPInitializer");
            Method method = cls.getDeclaredMethod("initializeFromAgent", new Class[] { Instrumentation.class });
            method.invoke(null, new Object[] { instr });
    }
    
    public static void initializeFromMain(Instrumentation instr) throws Exception {
            Class cls = Class.forName("org.eclipse.persistence.internal.jpa.JavaSECMPInitializer");
            Field field = cls.getField("globalInstrumentation");
            field.set(null, instr);        
    }
}