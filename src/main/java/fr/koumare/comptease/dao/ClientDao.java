package fr.koumare.comptease.dao;

import fr.koumare.comptease.model.Client;
import fr.koumare.comptease.model.User;
import fr.koumare.comptease.utilis.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientDao {
    private static final Logger logger = LoggerFactory.getLogger(ClientDao.class);

    public void saveClient(Client client) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            logger.info("la");
            transaction = session.beginTransaction();
            session.save(client);
            transaction.commit();
            logger.info("Client sauvegardé : {}", client.getFirstName());
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
    public boolean clientExists(String firstName, String lastName) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Client> clients = session.createQuery("from Client where firstName = :firstName and lastName = :lastName", Client.class)
                    .setParameter("firstName", firstName)
                    .setParameter("lastName", lastName)
                    .list();
            return !clients.isEmpty();
        }
        
    }

    //recuperation du client par son id
    public Optional<Client> findById(Long clientId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
          logger.info("Recherche du client avec l'Id : {}", clientId);
            Optional<Client> cl = session.createQuery("FROM client WHERE idc = :idc", Client.class)
                    .setParameter("idc", clientId)
                    .uniqueResultOptional();
            if (cl.isPresent()) {
                logger.info("Client trouvé : {}", cl.get().getFirstName());
            } else {
                logger.warn("Aucun client trouvé pour l'Id : {}", clientId);
            }
            return cl;
        } catch (Exception e) {
           logger.error("Erreur lors de la recherche du client avec l'Id : {}", clientId, e);
            return Optional.empty();
        }
    }

    //recuperation du client par son nom
    public Optional<Client> findByNames(String nom, String prenom) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            logger.info("Recherche du client avec le nom : {}", nom);
            Optional<Client> cl = session.createQuery("FROM client WHERE lastName = :lastName and firstName = :firstName", Client.class)
                    .setParameter("lastName", nom)
                    .setParameter("firstName", prenom)
                    .uniqueResultOptional();
            if (cl.isPresent()) {
                logger.info("Client trouvé : {}", cl.get().getFirstName());
            } else {
                logger.warn("Aucun client trouvé pour le nom : {}", nom);
            }
            return cl;
        } catch (Exception e) {
            logger.error("Erreur lors de la recherche du client avec le nom : {}", nom, e);
            return Optional.empty();
        }
    }
}
