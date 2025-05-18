package fr.koumare.comptease.dao;

import fr.koumare.comptease.model.Client;
import fr.koumare.comptease.model.Invoice;
import fr.koumare.comptease.utilis.HibernateUtil;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ClientDao {
    private static final Logger logger = LoggerFactory.getLogger(ClientDao.class);

    public void saveClient(Client client) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            logger.info("Saving client: {}", client.getFirstName());
            transaction = session.beginTransaction();
            session.save(client);
            transaction.commit();
            logger.info("Client saved: {}", client.getFirstName());
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Error saving client: {}", client.getFirstName(), e);
            throw e;
        }
    }

    public List<Client> getAllClients() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Client> clients = session.createQuery("FROM Client", Client.class).list();
            logger.info("Retrieved {} clients", clients.size());
            return clients;
        } catch (Exception e) {
            logger.error("Error retrieving all clients", e);
            return Collections.emptyList();
        }
    }

    public void updateClient(Client client) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(client);
            transaction.commit();
            logger.info("Client updated: {}", client.getFirstName());
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Error updating client: {}", client.getFirstName(), e);
            throw e;
        }
    }

    public void deleteClient(Long clientId) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Client client = session.get(Client.class, clientId);
            if (client != null) {
                session.delete(client);
                logger.info("Client deleted: ID={}", clientId);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Error deleting client: ID={}", clientId, e);
            throw e;
        }
    }

    public boolean clientExists(String firstName, String lastName) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Client> clients = session.createQuery("FROM Client WHERE firstName = :firstName AND lastName = :lastName", Client.class)
                    .setParameter("firstName", firstName)
                    .setParameter("lastName", lastName)
                    .list();
            boolean exists = !clients.isEmpty();
            logger.info("Client exists check: {} {} -> {}", firstName, lastName, exists);
            return exists;
        } catch (Exception e) {
            logger.error("Error checking client existence: {} {}", firstName, lastName, e);
            return false;
        }
    }

    public Optional<Client> findById(Long clientId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            logger.info("Finding client by ID: {}", clientId);
            Optional<Client> client = session.createQuery("FROM Client WHERE idc = :idc", Client.class)
                    .setParameter("idc", clientId)
                    .uniqueResultOptional();
            if (client.isPresent()) {
                logger.info("Client found: {}", client.get().getFirstName());
            } else {
                logger.warn("No client found for ID: {}", clientId);
            }
            return client;
        } catch (Exception e) {
            logger.error("Error finding client by ID: {}", clientId, e);
            return Optional.empty();
        }
    }

    public Optional<Client> findByNames(String nom, String prenom) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            logger.info("Finding client by name: {} {}", nom, prenom);
            Optional<Client> client = session.createQuery("FROM Client WHERE lastName = :lastName AND firstName = :firstName", Client.class)
                    .setParameter("lastName", nom)
                    .setParameter("firstName", prenom)
                    .uniqueResultOptional();
            if (client.isPresent()) {
                logger.info("Client found: {}", client.get().getFirstName());
            } else {
                logger.warn("No client found for name: {} {}", nom, prenom);
            }
            return client;
        } catch (Exception e) {
            logger.error("Error finding client by name: {} {}", nom, prenom, e);
            return Optional.empty();
        }
    }

    public List<Client> findByKeyword(String keyword) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Client> clients = session.createQuery("FROM Client WHERE lastName LIKE :keyword OR firstName LIKE :keyword OR contact LIKE :keyword OR adresse LIKE :keyword OR note LIKE :keyword", Client.class)
                    .setParameter("keyword", "%" + keyword + "%")
                    .list();
            logger.info("Found {} clients for keyword: {}", clients.size(), keyword);
            return clients;
        } catch (Exception e) {
            logger.error("Error finding clients by keyword: {}", keyword, e);
            return Collections.emptyList();
        }
    }

    public List<Invoice> findByKeywordDetails(String keyword) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Invoice> invoices = session.createQuery("FROM Invoice WHERE description LIKE :keyword", Invoice.class)
                    .setParameter("keyword", "%" + keyword + "%")
                    .list();
            logger.info("Found {} invoices for keyword: {}", invoices.size(), keyword);
            return invoices;
        } catch (Exception e) {
            logger.error("Error finding invoice details by keyword: {}", keyword, e);
            return Collections.emptyList();
        }
    }

    public List<Invoice> getClientDetails(Long clientId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            logger.info("Retrieving details for client ID: {}", clientId);
            List<Invoice> invoices = session.createQuery("FROM Invoice WHERE client.idc = :clientId", Invoice.class)
                    .setParameter("clientId", clientId)
                    .list();
            logger.info("Found {} invoices for client ID: {}", invoices.size(), clientId);
            return invoices;
        } catch (Exception e) {
            logger.error("Error retrieving client details for ID: {}", clientId, e);
            return Collections.emptyList();
        }
    }

    public Optional<Invoice> findInvoiceById(Long invoiceId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Optional<Invoice> invoice = session.createQuery("FROM Invoice WHERE id = :invoiceId", Invoice.class)
                    .setParameter("invoiceId", invoiceId)
                    .uniqueResultOptional();
            logger.info("Invoice {} for ID: {}", invoice.isPresent() ? "found" : "not found", invoiceId);
            return invoice;
        } catch (Exception e) {
            logger.error("Error finding invoice by ID: {}", invoiceId, e);
            return Optional.empty();
        }
    }

    public Double getClientInvoiceSum(Long clientId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Double sum = session.createQuery("SELECT SUM(i.price) FROM Invoice i WHERE i.client.idc = :clientId", Double.class)
                    .setParameter("clientId", clientId)
                    .uniqueResult();
            logger.info("Invoice sum for client ID {}: {}", clientId, sum != null ? sum : 0.0);
            return sum != null ? sum : 0.0;
        } catch (Exception e) {
            logger.error("Error retrieving invoice sum for client ID: {}", clientId, e);
            return 0.0;
        }
    }

    public Double getClientInvoiceSumByMonth(Long clientId, int year, int month) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Double sum = session.createQuery(
                            "SELECT SUM(i.price) FROM Invoice i WHERE i.client.idc = :clientId " +
                                    "AND YEAR(i.date) = :year AND MONTH(i.date) = :month", Double.class)
                    .setParameter("clientId", clientId)
                    .setParameter("year", year)
                    .setParameter("month", month)
                    .uniqueResult();
            logger.info("Invoice sum for client ID {} in year {} month {}: {}", clientId, year, month, sum != null ? sum : 0.0);
            return sum != null ? sum : 0.0;
        } catch (Exception e) {
            logger.error("Error retrieving invoice sum for client ID {} in year {} month {}", clientId, year, month, e);
            return 0.0;
        }
    }

    public int getClientInvoiceCount(Long clientId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Long count = session.createQuery("SELECT COUNT(i.id) FROM Invoice i WHERE i.client.idc = :clientId", Long.class)
                    .setParameter("clientId", clientId)
                    .uniqueResult();
            logger.info("Invoice count for client ID {}: {}", clientId, count);
            return count != null ? count.intValue() : 0;
        } catch (Exception e) {
            logger.error("Error retrieving invoice count for client ID: {}", clientId, e);
            return 0;
        }
    }

    public Optional<Client> findClientByInvoiceId(Long invoiceId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Optional<Client> client = session.createQuery("SELECT i.client FROM Invoice i WHERE i.id = :invoiceId", Client.class)
                    .setParameter("invoiceId", invoiceId)
                    .uniqueResultOptional();
            logger.info("Client {} for invoice ID: {}", client.isPresent() ? "found" : "not found", invoiceId);
            return client;
        } catch (Exception e) {
            logger.error("Error finding client by invoice ID: {}", invoiceId, e);
            return Optional.empty();
        }
    }

    public void updateInvoiceDescription(Long invoiceId, String newDescription) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Invoice invoice = session.get(Invoice.class, invoiceId);
            if (invoice != null) {
                invoice.setDescription(newDescription);
                session.update(invoice);
                transaction.commit();
                logger.info("Invoice description updated for ID: {}", invoiceId);
            } else {
                logger.warn("Invoice not found for ID: {}", invoiceId);
            }
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Error updating invoice description for ID: {}", invoiceId, e);
            throw e;
        }
    }

    public List<Client> getClientsWithMostInvoices() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Client> clients = session.createQuery("SELECT c FROM Client c JOIN c.invoices i GROUP BY c.idc ORDER BY COUNT(i.id) DESC", Client.class)
                    .setMaxResults(3)
                    .list();
            logger.info("Retrieved {} clients with most invoices", clients.size());
            return clients;
        } catch (Exception e) {
            logger.error("Error retrieving clients with most invoices", e);
            return Collections.emptyList();
        }
    }

    public List<Client> getClientsWithHighestBalance() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Client> clients = session.createQuery("FROM Client WHERE solde IS NOT NULL ORDER BY solde DESC", Client.class)
                    .setMaxResults(3)
                    .list();
            logger.info("Retrieved {} clients with highest balance", clients.size());
            return clients;
        } catch (Exception e) {
            logger.error("Error retrieving clients with highest balance", e);
            return Collections.emptyList();
        }
    }

    public List<Client> getClientsWithHighestBalanceByMonth(int year, int month) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Client> clients = session.createQuery(
                            "SELECT c FROM Client c JOIN c.invoices i " +
                                    "WHERE YEAR(i.date) = :year AND MONTH(i.date) = :month " +
                                    "GROUP BY c.idc ORDER BY SUM(i.price) DESC", Client.class)
                    .setParameter("year", year)
                    .setParameter("month", month)
                    .setMaxResults(3)
                    .list();
            logger.info("Retrieved {} clients with highest balance for year {} and month {}", clients.size(), year, month);
            return clients;
        } catch (Exception e) {
            logger.error("Error retrieving clients with highest balance for year {} and month {}", year, month, e);
            return Collections.emptyList();
        }
    }

    public Client findByIdQuery(Long clientId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Client> query = session.createQuery("FROM Client WHERE idc = :clientId", Client.class);
            query.setParameter("clientId", clientId);
            Client client = query.uniqueResult();
            if (client == null) {
                logger.warn("Client with ID {} not found", clientId);
                return null;
            }
            logger.info("Client found with ID {}: {}", clientId, client.getFirstName());
            return client;
        } catch (Exception e) {
            logger.error("Error retrieving client: {}", e.getMessage());
            return null;
        }
    }
}