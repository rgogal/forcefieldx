//******************************************************************************
//
// File:    Constants.java
// Package: edu.rit.pj.cluster
// Unit:    Class edu.rit.pj.cluster.Constants
//
// This Java source file is copyright (C) 2006 by Alan Kaminsky. All rights
// reserved. For further information, contact the author, Alan Kaminsky, at
// ark@cs.rit.edu.
//
// This Java source file is part of the Parallel Java Library ("PJ"). PJ is free
// software; you can redistribute it and/or modify it under the terms of the GNU
// General Public License as published by the Free Software Foundation; either
// version 3 of the License, or (at your option) any later version.
//
// PJ is distributed in the hope that it will be useful, but WITHOUT ANY
// WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
// A PARTICULAR PURPOSE. See the GNU General Public License for more details.
//
// Linking this library statically or dynamically with other modules is making a
// combined work based on this library. Thus, the terms and conditions of the GNU
// General Public License cover the whole combination.
//
// As a special exception, the copyright holders of this library give you
// permission to link this library with independent modules to produce an
// executable, regardless of the license terms of these independent modules, and
// to copy and distribute the resulting executable under terms of your choice,
// provided that you also meet, for each linked independent module, the terms
// and conditions of the license of that module. An independent module is a module
// which is not derived from or based on this library. If you modify this library,
// you may extend this exception to your version of the library, but you are not
// obligated to do so. If you do not wish to do so, delete this exception
// statement from your version.
//
// A copy of the GNU General Public License is provided in the file gpl.txt. You
// may also obtain a copy of the GNU General Public License on the World Wide
// Web at http://www.gnu.org/licenses/gpl.html.
//
//******************************************************************************
package edu.rit.pj.cluster;

import static java.lang.Long.parseLong;

/**
 * Class Constants contains various constants used in the PJ cluster middleware.
 *
 * @author Alan Kaminsky
 * @version 19-Oct-2006
 */
public class Constants {

// Prevent construction.
    private Constants() {
    }

// Exported constants.
    /**
     * Host name referring to all network interfaces (<code>"0.0.0.0"</code>).
     */
    public static final String ALL_NETWORK_INTERFACES = "0.0.0.0";

    /**
     * The default port number to which the Job Scheduler listens for
     * connections from job frontend processes (20617).
     */
    public static final int PJ_PORT = 20617;

    /**
     * The default port number for the Job Scheduler's web interface (8080).
     */
    public static final int WEB_PORT = 8080;

    /**
     * The lease renewal interval (default is 60 seconds).
     */
    public static final long LEASE_RENEW_INTERVAL;

    /**
     * The lease expiration interval (default is 150 seconds).
     */
    public static final long LEASE_EXPIRE_INTERVAL;

    static {
        long renew;
        try {
            String leaseRenew = System.getProperty("pj.renew", "60000");
            renew = parseLong(leaseRenew);
        } catch (Exception e) {
            renew = 60000L;
        }
        LEASE_RENEW_INTERVAL = renew;

        long expire;
        try {
            String leaseExpire = System.getProperty("pj.expire", "150000");
            expire = parseLong(leaseExpire);
        } catch (Exception e) {
            expire = 150000L;
        }
        LEASE_EXPIRE_INTERVAL = expire;
    }

}
