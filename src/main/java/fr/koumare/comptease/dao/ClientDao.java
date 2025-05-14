package fr.koumare.comptease.dao;

import fr.koumare.comptease.model.Client;
import fr.koumare.comptease.model.User;
import fr.koumare.comptease.model.CurrentUser;
import fr.koumare.comptease.model.Invoice;
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
        //recuperer l'utilisateur connecté
        User currentUser = CurrentUser.getCurrentUser();
        if (currentUser == null) {
            throw new IllegalStateException("Aucun utilisateur connecté. Impossible de récupérer les clients.");
        }
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Client WHERE user.id = :userId", Client.class)
                    .setParameter("userId", currentUser.getId())
                    .list();
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
            Optional<Client> cl = session.createQuery("FROM Client WHERE idc = :idc", Client.class)
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
            logger.info("Recherche du client avec le lastname : {}", nom+"  ;firstname: "+ prenom);
            Optional<Client> cl = session.createQuery("FROM Client WHERE lastName = :lastName and firstName = :firstName", Client.class)
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

    //chercher un client par un mot clé
    public List<Client> findByKeyword(String keyword) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Client WHERE lastName LIKE :keyword OR firstName LIKE :keyword OR contact LIKE :keyword OR adresse LIKE :keyword OR note LIKE :keyword", Client.class)
                    .setParameter("keyword", "%" + keyword + "%")
                    .list();
        } catch (Exception e) {
            logger.error("Erreur lors de la recherche du client avec le mot clé : {}", keyword, e);
            return null;
        }

    }

    //chercher un detail par un mot clé
    public List<Invoice> findByKeywordDetails(String keyword) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Invoice WHERE description LIKE :keyword", Invoice.class)
                    .setParameter("keyword", "%" + keyword + "%")
                    .list();
        } catch (Exception e) {
            logger.error("Erreur lors de la recherche du détail client avec le mot clé : {}", keyword, e);
            return null;
        }
    }

    //recuperer les details d'un client
    public List<Invoice> getClientDetails(Long clientId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            logger.info("Essaie recup c");
            return session.createQuery("FROM Invoice WHERE client.idc = :clientId", Invoice.class)
                    .setParameter("clientId", clientId)
                    .list();
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des détails du client avec l'Id : {}", clientId, e);
            return null;
        }
    }

    //trouver le client par id facture
    public Optional<Client> findUserByInvoiceId(Long invoiceId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("SELECT i.client FROM Invoice i WHERE i.id = :invoiceId", Client.class)
                    .setParameter("invoiceId", invoiceId)
                    .uniqueResultOptional();
        } catch (Exception e) {
            logger.error("Erreur lors de la recherche de l'utilisateur par l'ID de la facture : {}", invoiceId, e);
            return Optional.empty();
        }
    }
}
