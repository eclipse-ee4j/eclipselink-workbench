#!/bin/sh
#
# Copyright (c) 2018, 2022 Oracle and/or its affiliates. All rights reserved.
#
# This program and the accompanying materials are made available under the
# terms of the Eclipse Public License v. 2.0 which is available at
# http://www.eclipse.org/legal/epl-2.0,
# or the Eclipse Distribution License v. 1.0 which is available at
# http://www.eclipse.org/org/documents/edl-v10.php.
#
# SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
#

. `dirname $0`/setenv.sh

# User may increase Java memory setting(s) if desired:
JVM_ARGS=-Xmx256m

# Please do not change any of the following lines:
CLASSPATH=`dirname $0`/../jlib/xercesImpl.jar:\
`dirname $0`/../jlib/xml-apis.jar:\
`dirname $0`/../jlib/jakarta.resource-api.jar:\
`dirname $0`/../jlib/eclipselink.jar:\
`dirname $0`/../jlib/elmwcore.jar:\
`dirname $0`/../jlib/eclipselinkmw.jar:\
`dirname $0`/../jlib/jakarta.persistence-api.jar:\
`dirname $0`/../config:\
${DRIVER_CLASSPATH}

WORKBENCH_ARGS=-open "$@"

${JAVA_HOME}/bin/java ${JVM_ARGS} -cp ${CLASSPATH} \
    org.eclipse.persistence.tools.workbench.Main ${WORKBENCH_ARGS}
