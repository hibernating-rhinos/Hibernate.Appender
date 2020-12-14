//-----------------------------------------------------------------------
// <copyright file="DisposableMethodInvokingFactoryBean.java" company="Hibernating Rhinos LTD">
//     Copyright (c) Hibernating Rhinos LTD. All rights reserved.
// </copyright>
//-----------------------------------------------------------------------
package hibernatingrhinos.hibernate.profiler.spring;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.config.MethodInvokingFactoryBean;

import java.lang.reflect.Method;

public class DisposableMethodInvokingFactoryBean extends MethodInvokingFactoryBean implements DisposableBean {

    private String destroyMethod;
        
    /**
     * @return the destroyMethod
     */
    public String getDestroyMethod() {
        return this.destroyMethod;
    }

    /**
     * @param destroyMethod the destroyMethod to set
     */
    public void setDestroyMethod(String destroyMethod) {
        this.destroyMethod = destroyMethod;
    }

    /**
     * @see org.springframework.beans.factory.DisposableBean#destroy()
     */
    public void destroy() throws Exception {
        Class target = this.getTargetClass();
        Method destroy = target.getMethod(getDestroyMethod(), null);
        destroy.invoke(target, null);
    }

}
