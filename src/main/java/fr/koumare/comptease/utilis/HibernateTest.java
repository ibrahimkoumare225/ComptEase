package fr.koumare.comptease.utilis;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import fr.koumare.comptease.model.Client;

public class HibernateTest {
    public static void main(String[] args) {
        try {
            // Ouvre la session Hibernate
            SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
            Session session = sessionFactory.openSession();

            Transaction transaction = session.beginTransaction();

            // Test de création d'un client
            Client client = new Client();
            client.setFirstName("Test Client");
            session.save(client);

            transaction.commit();
            session.close();
            System.out.println("✅ Données insérées avec succès !");
        } catch (Exception e) {
            System.out.println("❌ Erreur lors de l'insertion des données !");
            e.printStackTrace();
        }
    }
}
