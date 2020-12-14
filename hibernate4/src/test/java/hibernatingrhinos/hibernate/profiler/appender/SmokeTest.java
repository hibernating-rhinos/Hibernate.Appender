//-----------------------------------------------------------------------
// <copyright file="SmokeTest.java" company="Hibernating Rhinos LTD">
//     Copyright (c) Hibernating Rhinos LTD. All rights reserved.
// </copyright>
//-----------------------------------------------------------------------
package hibernatingrhinos.hibernate.profiler.appender;

import hibernatingrhinos.hibernate.profiler.appender.entities.Child;
import hibernatingrhinos.hibernate.profiler.appender.entities.Parent;
import hibernatingrhinos.hibernate.profiler.appender.entities.Simple;
import hibernatingrhinos.hibernate.profiler.event.ProfilerInitializeCollectionEventListener;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.testing.cache.CachingRegionFactory;

/**
 *
 */
public class SmokeTest {

    public static void main(String[] args) throws Exception {
        SessionFactory factory = HibernateProfiler.configure(new Configuration()
            .addPackage("hibernatingrhinos.hibernate.profiler.appender.entities")
            .addAnnotatedClass(Simple.class)
            .addAnnotatedClass(Child.class)
            .addAnnotatedClass(Parent.class)
            .configure("hibernatingrhinos/hibernate/profiler/appender/entities/simple.cfg.xml")
            .setProperty("hibernate.jdbc.batch_size", "7")
            .setProperty("hibernate.cache.use_second_level_cache", "true")
            .setProperty("hibernate.cache.use_query_cache", "true")
            .setProperty("hibernate.cache.region.factory_class", CachingRegionFactory.class.getName()), false)
            .buildSessionFactory();
        if (factory instanceof SessionFactoryImplementor) {
            SessionFactoryImplementor sessionFactoryI = (SessionFactoryImplementor) factory;
            EventListenerRegistry eventListenerRegistry = sessionFactoryI.getServiceRegistry().getService(EventListenerRegistry.class);
            eventListenerRegistry.setListeners(EventType.INIT_COLLECTION, new ProfilerInitializeCollectionEventListener());
        }
/*
        final ServerSocket server = new ServerSocket(22897);
        Thread t = new Thread(new Runnable() {
            public void run() {
                while (true) {
                    try {
                        Socket clientSocket = server.accept();
                        BufferedInputStream is = new BufferedInputStream(clientSocket.getInputStream());

                        byte[] buffer = new byte[65936];
                        int bytesRead = 0;
                        while ((bytesRead = is.read(buffer)) != -1) {
                            String chunk = new String(buffer, 0, bytesRead);
                            System.out.print(chunk);
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        t.start();
*/
        HibernateProfiler.initialize(22897);

        int counter = 0;
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            Session session = factory.openSession();

            try {
               Parent parent = new Parent();
               parent.setId(counter++);
               List<Child> children = new ArrayList<Child>(10);
               for (int i = 0; i < 10; i++) {
                   Child child = new Child();
                   child.setId(counter++);
                   child.setParent(parent);
                   children.add(child);
               }
               parent.setChildren(children);
               session.persist(parent);
               Thread.sleep(1000);

                for (int i = 0; i < 25; i++) {
                    Simple simple = new Simple();
                    simple.setId(counter++);
                    session.persist(simple);
                }
                Simple s = (Simple) session.get(Simple.class, Integer.valueOf(1));
                session.flush();
                List results = session.createQuery("from Simple s").list();
                Query cacheableQuery = session.createQuery("from Simple s where s.id < 5");
                cacheableQuery.setCacheable(true);
                List lower1 = cacheableQuery.list();
                List lower2 = cacheableQuery.list();
                List family = session.createQuery("from Parent p where p.id < 10").list();
                System.err.println(family);
                System.err.println(((Parent) family.get(0)).getChildren().iterator().next());
            } finally {
                session.close();
            }
        }
    }
}
