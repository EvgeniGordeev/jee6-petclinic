package example.jmx;

import java.lang.management.ManagementFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

/**
 * @Author Paul Bakker - paul.bakker@luminis.eu
 */
@Startup
@Singleton
public class JmxBeanExtension {
    @Inject
    @JMXBean
    Instance<Object> jmxBeans;
    private MBeanServer mBeanServer;

    @PostConstruct
    public void exportBean() throws Exception {
        mBeanServer = ManagementFactory.getPlatformMBeanServer();

        for (Object bean : jmxBeans) {
            String annotationValue = bean.getClass().getAnnotation(JMXBean.class).objectName();
            ObjectName objectName;
            if (annotationValue.equals("")) {
                objectName = new ObjectName(bean.getClass().getName() + ":type=" + bean.getClass().getName());
            } else {
                objectName = new ObjectName(annotationValue + ":type=" + bean.getClass().getName());
            }

            mBeanServer.registerMBean(bean, objectName);
            System.out.println("Registered " + objectName);
        }
    }

    @PreDestroy
    public void deexportBean() throws MalformedObjectNameException, MBeanRegistrationException, InstanceNotFoundException {
        mBeanServer = ManagementFactory.getPlatformMBeanServer();

        for (Object bean : jmxBeans) {
            String annotationValue = bean.getClass().getAnnotation(JMXBean.class).objectName();
            ObjectName objectName;
            if (annotationValue.equals("")) {
                objectName = new ObjectName(bean.getClass().getName() + ":type=" + bean.getClass().getName());
            } else {
                objectName = new ObjectName(annotationValue + ":type=" + bean.getClass().getName());
            }

            mBeanServer.unregisterMBean(objectName);
            System.out.println("Unregistered " + objectName);
        }
    }
}
