//-----------------------------------------------------------------------
// <copyright file="SmokeTest.java" company="Hibernating Rhinos LTD">
//     Copyright (c) Hibernating Rhinos LTD. All rights reserved.
// </copyright>
//-----------------------------------------------------------------------
package hibernatingrhinos.hibernate.profiler.appender;

import hibernatingrhinos.hibernate.profiler.appender.entities.Simple;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;

/**
 *
 */
public class SmokeTest {

    public static void main(String[] args) throws Exception {
        SessionFactory factory = HibernateProfiler.configure(new AnnotationConfiguration()
            .addPackage("hibernatingrhinos.hibernate.profiler.appender.entities")
            .addAnnotatedClass(Simple.class)
            .configure("hibernatingrhinos/hibernate/profiler/appender/entities/simple.cfg.xml")
            .setProperty("hibernate.jdbc.batch_size", "5"), false)
            .buildSessionFactory();

        /*final ServerSocket server = new ServerSocket(22897);
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
        t.start();*/

        HibernateProfiler.initialize(22897);

        int counter = 0;
        while (true) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            Session session = factory.openSession();
            try {
                for (int i = 0; i < 25; i++) {
                    Simple simple = new Simple();
                    simple.setId(counter++);
                    session.persist(simple);
                }
                session.flush();
                List resultsAll = session.createQuery("from Simple s").list();
                List resultsPart = session.createQuery("from Simple s where s.id < :id").setParameter("id", 1).list();
            } finally {
                session.close();
            }
        }
    }

}
