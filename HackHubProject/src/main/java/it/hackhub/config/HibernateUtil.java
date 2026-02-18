package it.hackhub.config;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
    private static volatile SessionFactory sessionFactory;

    private HibernateUtil() {}  // Private constructor

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            synchronized (HibernateUtil.class) {
                if (sessionFactory == null) {
                    sessionFactory = buildSessionFactory();
                }
            }
        }
        return sessionFactory;
    }

    private static SessionFactory buildSessionFactory() {
        try {
            Configuration config = new Configuration()
                    .configure("hibernate.cfg.xml");  // Carica da classpath
            return config.buildSessionFactory();
        } catch (Exception e) {
            System.err.println("❌ Errore inizializzazione Hibernate: " + e.getMessage());
            throw new ExceptionInInitializerError(e);
        }
    }

    public static Session getSession() {
        return getSessionFactory().openSession();
    }

    public static void shutdown() {
        SessionFactory factory = sessionFactory;
        sessionFactory = null;
        if (factory != null && factory.isOpen()) {
            factory.close();
        }
    }
}
