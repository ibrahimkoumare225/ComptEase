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
            return session.createQuery("from Client where id_user=3", Client.class).list();
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
            return session.createQuery("FROM Client WHERE lastName LIKE :keyword OR firstName LIKE :keyword OR contact LIKE :keyword OR adresse LIKE :keyword", Client.class)
                    .setParameter("keyword", "%" + keyword + "%")
                    .list();
        } catch (Exception e) {
            logger.error("Erreur lors de la recherche du client avec le mot clé : {}", keyword, e);
            return null;
        }

    }

    //tri croissant des clients par nom
    public List<Client> sortByName() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Client ORDER BY lastName", Client.class).list();
        } catch (Exception e) {
            logger.error("Erreur lors du tri des clients par nom", e);
            return null;
        }
    }
    //tri decroissant des clients par nom
    public List<Client> sortByNameDesc() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Client ORDER BY lastName DESC", Client.class).list();
        } catch (Exception e) {
            logger.error("Erreur lors du tri des clients par nom", e);
            return null;
        }
    }

    //tri croissant des client par prenom
    public List<Client> sortByFirstName() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Client ORDER BY firstName", Client.class).list();
        } catch (Exception e) {
            logger.error("Erreur lors du tri des clients par prenom", e);
            return null;
        }
    }

    //tri decroissant des client par prenom
    public List<Client> sortByFirstNameDesc() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Client ORDER BY firstName DESC", Client.class).list();
        } catch (Exception e) {
            logger.error("Erreur lors du tri des clients par prenom", e);
            return null;
        }
    }

    //tri croissant des client par id
    public List<Client> sortById() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Client ORDER BY idc", Client.class).list();
        } catch (Exception e) {
            logger.error("Erreur lors du tri des clients par id", e);
            return null;
        }
    }

    //tri decroissant des client par id
    public List<Client> sortByIdDesc() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Client ORDER BY idc DESC", Client.class).list();
        } catch (Exception e) {
            logger.error("Erreur lors du tri des clients par id", e);
            return null;
        }
    }
}
