//-----------------------------------------------------------------------
// <copyright file="HibernateProfilerBeanDefinitionDecorator.java" company="Hibernating Rhinos LTD">
//     Copyright (c) Hibernating Rhinos LTD. All rights reserved.
// </copyright>
//-----------------------------------------------------------------------
package hibernatingrhinos.hibernate.profiler.spring;

import hibernatingrhinos.hibernate.profiler.appender.HibernateProfiler;
import hibernatingrhinos.hibernate.profiler.appender.HibernateProfilerTrace;
import hibernatingrhinos.hibernate.profiler.appender.JavaHelper;
import hibernatingrhinos.hibernate.profiler.event.ProfilerInitializeCollectionEventListener;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.BeanDefinitionDecorator;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.orm.hibernate3.AbstractSessionFactoryBean;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HibernateProfilerBeanDefinitionDecorator implements BeanDefinitionDecorator {

    public BeanDefinitionHolder decorate(Node source, BeanDefinitionHolder holder, ParserContext ctx) {
        HibernateProfilerSpringSettings settings = createSettings(source);
        registerSettings(settings, ctx);
        registerDataSourceProxying(settings, ctx);
        registerPostProcessor(ctx);
        registerHibernateProfiler(settings, ctx);
        registerListeners(holder);
        return holder;
    }

    private HibernateProfilerSpringSettings createSettings(Node node) {
        HibernateProfilerSpringSettings settings = new HibernateProfilerSpringSettings();
        settings.setInitOnStartup(getAttributeOrDefault(node, "init-on-startup", "true"));
        settings.setHost(getAttributeOrDefault(node, "host", HibernateProfiler.DefaultHost));
        settings.setPort(getAttributeOrDefault(node, "port", String.valueOf(HibernateProfiler.DefaultPort)));
        settings.setUseSpy(getAttributeOrDefault(node, "useSpy", String.valueOf(true)));
        return settings;
    }

    private void registerDataSourceProxying(HibernateProfilerSpringSettings settings, ParserContext ctx) {
        HibernateProfilerTrace.log("registering datasource proxying");

        BeanDefinitionBuilder datasourceProxyingBuilder = BeanDefinitionBuilder.rootBeanDefinition(DataSourceProxyingPostProcessor.class);
        ctx.getRegistry().registerBeanDefinition("hibernate-profiler-spring-datssource-proxying", datasourceProxyingBuilder.getBeanDefinition());
    }

    private void registerSettings(HibernateProfilerSpringSettings settings, ParserContext ctx) {
        HibernateProfilerTrace.log("registering profiler settings");

        BeanDefinitionBuilder settingsBuilder = BeanDefinitionBuilder.rootBeanDefinition(HibernateProfilerSpringSettings.class);
        settingsBuilder.addPropertyValue("initOnStartup", settings.getInitOnStartup());
        settingsBuilder.addPropertyValue("host", settings.getHost());
        settingsBuilder.addPropertyValue("port", settings.getPort());
        settingsBuilder.addPropertyValue("useSpy", settings.getUseSpy());
        ctx.getRegistry().registerBeanDefinition("hibernate-profiler-spring-settings", settingsBuilder.getBeanDefinition());
    }

    private void registerPostProcessor(ParserContext ctx) {
        BeanDefinitionBuilder factoryPostProcessor = BeanDefinitionBuilder.rootBeanDefinition(ProfilerBeanFactoryPostProcessor.class);
        ctx.getRegistry().registerBeanDefinition("hibernate-profiler-bean-post-processor", factoryPostProcessor.getBeanDefinition());
    }

    @SuppressWarnings("unchecked")
    private void registerHibernateProfiler(HibernateProfilerSpringSettings settings, ParserContext ctx) {
        HibernateProfilerTrace.log("registering profiler");

        if (ctx.getRegistry().containsBeanDefinition("hibernate-profiler-initializer"))
            return;

        if (Boolean.valueOf(settings.getInitOnStartup()) == false)
            return;

        BeanDefinitionBuilder initializer = BeanDefinitionBuilder.rootBeanDefinition(DisposableMethodInvokingFactoryBean.class);
        initializer.addPropertyValue("targetClass", HibernateProfiler.class);
        initializer.addPropertyValue("targetMethod", "initialize");
        initializer.addPropertyValue("destroyMethod", "stop");
        initializer.addPropertyValue("singleton", "true");

        List arguments = new ArrayList();
        arguments.add(settings.getHost());
        arguments.add(settings.getPort());

        initializer.addPropertyValue("arguments", arguments);
        initializer.setLazyInit(false);
        ctx.getRegistry().registerBeanDefinition("hibernate-profiler-initializer", initializer.getBeanDefinition());
    }

    @SuppressWarnings("unchecked")
    private void registerListeners(BeanDefinitionHolder holder) {
        HibernateProfilerTrace.log("registering listeners");

        BeanDefinition beanDefinition = holder.getBeanDefinition();
        if (!JavaHelper.IsAssignableFrom(AbstractSessionFactoryBean.class, beanDefinition.getBeanClassName()))
            return;

        MutablePropertyValues propertyValues = beanDefinition.getPropertyValues();
        PropertyValue propertyValue = propertyValues.getPropertyValue("eventListeners");

        if (propertyValue == null) {
            propertyValues.addPropertyValue("eventListeners", new HashMap() {{
                put("load-collection", new ProfilerInitializeCollectionEventListener());
            }});
        } else if (propertyValue.getValue() instanceof Map) {
            Map eventListeners = (Map)propertyValue.getValue();
            eventListeners.put("load-collection", new ProfilerInitializeCollectionEventListener());
        }
    }

    private String getAttributeOrDefault(Node node, String name, String defaultValue) {
        String item = null;
        if (node instanceof Element) {
            Element ele = (Element)node;
            if (ele.hasAttribute(name)) {
                item = ele.getAttribute(name);
            }
        }
        return item == null ? defaultValue : item;
    }

}