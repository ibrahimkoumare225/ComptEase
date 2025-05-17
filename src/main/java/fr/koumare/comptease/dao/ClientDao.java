package fr.koumare.comptease.dao;

import fr.koumare.comptease.model.Client;
import fr.koumare.comptease.model.Invoice;
import fr.koumare.comptease.model.Article;
import fr.koumare.comptease.utilis.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/*import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;*/

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
            return session.createQuery("FROM Client", Client.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
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

    //recuperer une facture par son id
    public Optional<Invoice> findInvoiceById(Long invoiceId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Invoice WHERE id = :invoiceId", Invoice.class)
                    .setParameter("invoiceId", invoiceId)
                    .uniqueResultOptional();
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération de la facture avec l'Id : {}", invoiceId, e);
            return Optional.empty();
        }
    }
    //recuperer la somme des factures d'un client pour le mettre dans le solde
    public Double getClientInvoiceSum(Long clientId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Double result=session.createQuery("SELECT SUM(i.price) FROM Invoice i WHERE i.client.idc = :clientId", Double.class)
                    .setParameter("clientId", clientId)
                    .uniqueResult();
            return result != null ? result : 0.0;
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération de la somme des factures pour le client avec l'Id : {}", clientId, e);
            return 0.0;
        }
    }
    //recuperer le nombre de factucres d'un client
    public int getClientInvoiceCount(Long clientId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("SELECT COUNT(i.id) FROM Invoice i WHERE i.client.idc = :clientId", Integer.class)
                    .setParameter("clientId", clientId)
                    .uniqueResult();
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération du nombre de factures pour le client avec l'Id : {}", clientId, e);
            return 0;
        }
    }
    //trouver le client par id facture
    public Optional<Client> findClientByInvoiceId(Long invoiceId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("SELECT i.client FROM Invoice i WHERE i.id = :invoiceId", Client.class)
                    .setParameter("invoiceId", invoiceId)
                    .uniqueResultOptional();
        } catch (Exception e) {
            logger.error("Erreur lors de la recherche de l'utilisateur par l'ID de la facture : {}", invoiceId, e);
            return Optional.empty();
        }
    }

    //envoyer un mail au client
//    public void sendEmailToClient(Client client, String article) {
//        // Implémentez la logique d'envoi d'email ici
//        // Vous pouvez utiliser JavaMail ou une autre bibliothèque pour envoyer des emails
//        logger.info("Envoi d'un email au client : {}", client.getFirstName());
//        String to = client.getContact();
//        // Exemple : récupération du nom de la société (à adapter selon votre logique)
//        String companyName = ""; // Valeur par défaut
//        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
//            Object result = session.createQuery("SELECT company_name FROM Company")
//                    .setMaxResults(1)
//                    .uniqueResult();
//            if (result != null) {
//                companyName = result.toString();
//                String subject = "Notification de " + companyName;
//                String body = "Bonjour " + client.getFirstName() + ",\n\n" +
//                        "Ceci est une notification de "+companyName+" concernant votre facture de "+article+"."+
//                        " Nous vous rappelons que cette facture est toujours impayée.\n" +
//                        "Merci de la régler dans les plus brefs délais.\n\n" +
//                        "Cordialement,\n" +
//                        "L'équipe de Comptease";
//
//            }
//        } catch (Exception e) {
//            logger.error("Erreur lors de la récupération du nom de la société", e);
//        }
//
//        // Ici, ajoutez la logique d'envoi d'email avec 'to', 'subject' et 'body'
//    }


    //modifier description de la facture
    public void updateInvoiceDescription(Long invoiceId, String newDescription) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Invoice invoice = session.get(Invoice.class, invoiceId);
            if (invoice != null) {
                invoice.setDescription(newDescription);
                session.update(invoice);
                transaction.commit();
            }
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();

        }
    }
    //POUUR LE DASHBOARD
    //recuperer la liste des 3 clients avec le plus de factures
    public List<Client> getClientsWithMostInvoices() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("SELECT c FROM Client c JOIN c.invoices i GROUP BY c.idc ORDER BY COUNT(i.id) DESC", Client.class)
                    .setMaxResults(3)
                    .list();
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des 3 clients avec le plus de factures", e);
            return null;
        }
    }

    //recuperer la liste des 3 clients avec le plus de solde
    public List<Client> getClientsWithHighestBalance() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Client ORDER BY solde DESC", Client.class)
                    .setMaxResults(3)
                    .list();
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des 3 clients avec le plus de solde", e);
            return null;
        }
    }

    public Client findByIdQuery(Long clientId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Client> query = session.createQuery("FROM Client WHERE idc = :clientId", Client.class);
            query.setParameter("clientId", clientId);
            Client client = query.uniqueResult();
            if (client == null) {
                logger.warn("Client avec ID {} non trouvé", clientId);
                return null;
            }
            logger.info("Client trouvé avec ID {} : {}", clientId, client.getFirstName());
            return client;
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération du client : {}", e.getMessage());
            return null;
        }
    }



}