//-----------------------------------------------------------------------
// <copyright file="HibernateProfilerNamespaceHandler.java" company="Hibernating Rhinos LTD">
//     Copyright (c) Hibernating Rhinos LTD. All rights reserved.
// </copyright>
//-----------------------------------------------------------------------
package hibernatingrhinos.hibernate.profiler.spring;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class HibernateProfilerNamespaceHandler extends NamespaceHandlerSupport {

    /**
     * @see org.springframework.beans.factory.xml.NamespaceHandler#init()
     */
    public void init() {
        super.registerBeanDefinitionDecorator("profiler", new HibernateProfilerBeanDefinitionDecorator());        
    }

}
