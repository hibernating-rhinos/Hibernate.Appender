//-----------------------------------------------------------------------
// <copyright file="GuidHelper.java" company="Hibernating Rhinos LTD">
//     Copyright (c) Hibernating Rhinos LTD. All rights reserved.
// </copyright>
//-----------------------------------------------------------------------
package hibernatingrhinos.hibernate.profiler.appender;

import java.util.UUID;

public class GuidHelper {

    public static String getGuid() {
        //Use this for now for Java 5, but we can use fall back to Hibernate's UUID generator for Java 1.4
        return UUID.randomUUID().toString();
    }
    
}
