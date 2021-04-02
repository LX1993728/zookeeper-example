package com.zoo.ninestar.config.beanAutowire;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;

public abstract class SpringBootBeanAutowiringSupport {
    private static final Log logger = LogFactory.getLog(SpringBeanAutowiringSupport.class);


    /**
     * This constructor performs injection on this instance,
     * based on the current web application context.
     * <p>Intended for use as a base class.
     * @see #processInjectionBasedOnCurrentContext
     */
    public SpringBootBeanAutowiringSupport() {
        processInjectionBasedOnCurrentContext(this);
    }


    /**
     * Process {@code @Autowired} injection for the given target object,
     * based on the current web application context.
     * <p>Intended for use as a delegate.
     * @param target the target object to process
     * @see ContextLoader#getCurrentWebApplicationContext()
     */
    public static void processInjectionBasedOnCurrentContext(Object target) {
        Assert.notNull(target, "Target object must not be null");
        WebApplicationContext cc = WebApplicationContextLocator.getCurrentWebApplicationContext();
        if (cc != null) {
            AutowiredAnnotationBeanPostProcessor bpp = new AutowiredAnnotationBeanPostProcessor();
            bpp.setBeanFactory(cc.getAutowireCapableBeanFactory());
            bpp.processInjection(target);
        }
        else {
            if (logger.isDebugEnabled()) {
                logger.debug("Current WebApplicationContext is not available for processing of " +
                        ClassUtils.getShortName(target.getClass()) + ": " +
                        "Make sure this class gets constructed in a Spring web application. Proceeding without injection.");
            }
        }
    }


    /**
     * Process {@code @Autowired} injection for the given target object,
     * based on the current root web application context as stored in the ServletContext.
     * <p>Intended for use as a delegate.
     * @param target the target object to process
     * @param servletContext the ServletContext to find the Spring web application context in
     * @see WebApplicationContextUtils#getWebApplicationContext(ServletContext)
     */
    public static void processInjectionBasedOnServletContext(Object target, ServletContext servletContext) {
        Assert.notNull(target, "Target object must not be null");
        WebApplicationContext cc =  WebApplicationContextLocator.getCurrentWebApplicationContext();
        AutowiredAnnotationBeanPostProcessor bpp = new AutowiredAnnotationBeanPostProcessor();
        bpp.setBeanFactory(cc.getAutowireCapableBeanFactory());
        bpp.processInjection(target);
    }
}
