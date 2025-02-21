package fr.koumare.comptease.utilis;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateConfigTest {
    public static void main(String[] args) {
        try {
            // Charge hibernate.cfg.xml
            SessionFactory sessionFactory = new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
            System.out.println("✅ Hibernate est bien configuré !");
        } catch (Exception e) {
            System.out.println("❌ Erreur lors de la configuration Hibernate !");
            e.printStackTrace();
        }
    }
}
