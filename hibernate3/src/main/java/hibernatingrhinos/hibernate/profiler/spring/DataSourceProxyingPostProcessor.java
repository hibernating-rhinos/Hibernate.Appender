//-----------------------------------------------------------------------
// <copyright file="ProfilerBeanFactoryPostProcessor.java" company="Hibernating Rhinos LTD">
//     Copyright (c) Hibernating Rhinos LTD. All rights reserved.
// </copyright>
//-----------------------------------------------------------------------
package hibernatingrhinos.hibernate.profiler.spring;

import java.lang.reflect.Field;

import javax.sql.DataSource;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.orm.hibernate3.LocalSessionFactoryBean;
import org.springframework.orm.hibernate3.annotation.AnnotationSessionFactoryBean;
import org.springframework.util.ReflectionUtils;

/**
 * Replacing data sources in {@link LocalSessionFactoryBean}.
 */
public class DataSourceProxyingPostProcessor implements BeanPostProcessor, BeanFactoryPostProcessor {

    private boolean useSpy = false;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName)
            throws BeansException {
        if (! useSpy) {
            return bean;
        }
        if (bean instanceof LocalSessionFactoryBean) {
            AnnotationSessionFactoryBean ab;
            LocalSessionFactoryBean hibernate = (LocalSessionFactoryBean) bean;
            try {
                Class clz = hibernate.getClass();
                while (clz != null) {
                  Field[] flds = clz.getDeclaredFields();
                  for (Field field : clz.getDeclaredFields()) {
                      if ("dataSource".equals(field.getName())) {
                          ReflectionUtils.makeAccessible(field);
                          DataSource dataSource = (DataSource) field.get(hibernate);
                          if (dataSource != null) {
                              hibernate.setDataSource(new ConnectionWrappingDataSource(dataSource));
                          }
                      }
                  }
                  clz = clz.getSuperclass();
                }
            } catch (Exception e) {
                e.printStackTrace();
                //
            }
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName)
            throws BeansException {
        return bean;
    }

    @Override
    public void postProcessBeanFactory(
            ConfigurableListableBeanFactory beanFactory) throws BeansException {
        HibernateProfilerSpringSettings settings = (HibernateProfilerSpringSettings) beanFactory.getBean("hibernate-profiler-spring-settings");
        useSpy = Boolean.valueOf(settings.getUseSpy());
    }

}
