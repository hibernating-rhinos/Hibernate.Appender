//-----------------------------------------------------------------------
// <copyright file="JavaHelper.java" company="Hibernating Rhinos LTD">
//     Copyright (c) Hibernating Rhinos LTD. All rights reserved.
// </copyright>
//-----------------------------------------------------------------------
package hibernatingrhinos.hibernate.profiler.appender;

public class JavaHelper {

    public static boolean isJava6() {
        String javaVersion = System.getProperty("java.version");
        Float version = Float.valueOf(javaVersion.substring(0,3));
        return version >= (float)1.6;
    }

    public static boolean IsAssignableFrom(Class assignable, String className) {
        Class clazz = null;
        try {
            clazz = Class.forName(className);
        } catch (Exception e) {
            // intentionally ignore
        }

        return clazz != null && assignable.isAssignableFrom(clazz);
    }
}
