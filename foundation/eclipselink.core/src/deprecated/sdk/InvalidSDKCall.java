/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package deprecated.sdk;

import org.eclipse.persistence.internal.databaseaccess.*;
import org.eclipse.persistence.internal.sessions.AbstractRecord;

/**
 * The <code>InvalidSDKCall</code> class "implements"
 * the <code>SDKCall</code> interface by throwing an exception whenever
 * it is sent the <code>#execute(Record, Accessor)</code> message.
 * Instances of <code>SDKDescriptor</code>
 * use this transaction to initialize their standard queries.
 * This is because the
 * generated <code>SDKException</code>s are easier to debug than
 * the <code>NullPointerException</code>s that will be generated by the default TopLink
 * queries (which typically use a <code>ExpressionQueryMechanism</code>).
 * <p>
 * <b>Notes</b><ul>
 * <li> Only one instance of this class is needed, so the Singleton pattern is used.
 * </ul>
 * @see SDKDescriptor
 *
 * @author Big Country
 * @since TOPLink/Java 3.0
 * @deprecated since OracleAS TopLink 10<i>g</i> (10.1.3).  This class is replaced by
 *         {@link org.eclipse.persistence.eis}
 */
class InvalidSDKCall extends AbstractSDKCall {

    /** Singleton */
    private static InvalidSDKCall INSTANCE;

    /**
     * Default constructor.
     * Private - use <code>#getInstance()</code>
     */
    private InvalidSDKCall() {
        super();
    }

    /**
     * Throw an exception.
     * @see SDKCall#execute(org.eclipse.persistence.sessions.Record, org.eclipse.persistence.internal.databaseaccess.Accessor)
     */
    public Object execute(AbstractRecord translationRow, Accessor accessor) throws SDKDataStoreException {
        throw SDKDataStoreException.invalidCall(this.getQuery());
    }

    /**
     * Return the default singleton instance of the call.
     */
    public static InvalidSDKCall getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new InvalidSDKCall();
        }
        return INSTANCE;
    }
}