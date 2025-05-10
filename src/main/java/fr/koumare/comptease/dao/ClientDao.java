package fr.koumare.comptease.dao;

import fr.koumare.comptease.model.Client;
import fr.koumare.comptease.utilis.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class ClientDao {

    public void saveClient(Client client) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(client);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }


    public List<Client> getAllClients() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Client", Client.class).list();
        }
    }

    public void updateClient(Client client) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(client);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public void deleteClient(Long clientId) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Client client = session.get(Client.class, clientId);
            if (client != null) {
                session.delete(client);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    //verification de l'existence du client
    public boolean clientExists(Client client) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Client> clients = session.createQuery("from Client where firstName = :firstName and lastName = :lastName", Client.class)
                    .setParameter("firstName", client.getFirstName())
                    .setParameter("lastName", client.getLastName())
                    .list();
            return !clients.isEmpty();
        }
    }

    //recuperation du client par son id
    public Client getClientById(Long clientId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Client.class, clientId);
        }
    }
}
