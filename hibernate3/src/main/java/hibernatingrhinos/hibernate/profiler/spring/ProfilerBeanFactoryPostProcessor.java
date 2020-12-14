//-----------------------------------------------------------------------
// <copyright file="ProfilerBeanFactoryPostProcessor.java" company="Hibernating Rhinos LTD">
//     Copyright (c) Hibernating Rhinos LTD. All rights reserved.
// </copyright>
//-----------------------------------------------------------------------
package hibernatingrhinos.hibernate.profiler.spring;

import hibernatingrhinos.hibernate.profiler.appender.HibernateProfilerTrace;
import hibernatingrhinos.hibernate.profiler.appender.JavaHelper;
import hibernatingrhinos.hibernate.profiler.appender.ProfilerConfiguration;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.support.ManagedProperties;
import org.springframework.core.Ordered;
import org.springframework.orm.hibernate3.AbstractSessionFactoryBean;

public class ProfilerBeanFactoryPostProcessor implements BeanFactoryPostProcessor, Ordered {

    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        HibernateProfilerTrace.log("Post processing session factories");

        HibernateProfilerSpringSettings settings = (HibernateProfilerSpringSettings)configurableListableBeanFactory.getBean("hibernate-profiler-spring-settings");

        for (String name : configurableListableBeanFactory.getBeanDefinitionNames()) {
            System.out.println(name);
            BeanDefinition beanDef = configurableListableBeanFactory.getBeanDefinition(name);
            overrideHibernateSettings(settings, beanDef);
        }
    }

    private void overrideHibernateSettings(HibernateProfilerSpringSettings settings, BeanDefinition beanDefinition) {
        HibernateProfilerTrace.log("Evaluating " + beanDefinition.getBeanClassName());

        if (!JavaHelper.IsAssignableFrom(AbstractSessionFactoryBean.class, beanDefinition.getBeanClassName()))
            return;

        HibernateProfilerTrace.log("Changing properties for bean: " + beanDefinition.getResourceDescription() + " - " + beanDefinition.getBeanClassName());

        registerProperties(settings, beanDefinition);
    }

    public static void registerProperties(HibernateProfilerSpringSettings settings, BeanDefinition beanDefinition) {
        MutablePropertyValues propertyValues = beanDefinition.getPropertyValues();
        PropertyValue propertyValue = propertyValues.getPropertyValue("hibernateProperties");

        Object value = propertyValue.getValue();
        ProfilerConfiguration configuration = new ProfilerConfiguration(Boolean.valueOf(settings.getUseSpy()));
        if (value instanceof ManagedProperties) {
            ManagedProperties properties = (ManagedProperties)value;
            configuration.configure(properties);
            value = properties;
        } else if (value instanceof TypedStringValue) {
            TypedStringValue stringValue = (TypedStringValue)value;
            String string = stringValue.getValue();

            String newProperties = configuration.configure(string);
            stringValue.setValue(newProperties);
            value = newProperties;
        } else if (value instanceof String) {
            value = configuration.configure(((String)value));
        }
        propertyValues.addPropertyValue("hibernateProperties", value);
        HibernateProfilerTrace.log("Registering hibernate properties: " + propertyValues);
    }

    public int getOrder() {
        return Integer.MAX_VALUE;
    }
}
