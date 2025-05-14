package fr.koumare.comptease.dao;

import fr.koumare.comptease.model.Article;
import fr.koumare.comptease.model.Invoice;
import fr.koumare.comptease.utilis.HibernateUtil;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;

import org.hibernate.Session;
import org.hibernate.Transaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class InvoiceDao {

    private static final Logger logger = LoggerFactory.getLogger(InvoiceDao.class);

    public void saveFacture(Invoice invoice) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(invoice);
            transaction.commit();
            logger.info("Facture sauvegardée avec succès : ID {}", invoice.getId());
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Erreur lors de la sauvegarde de la facture : {}", e.getMessage());
            e.printStackTrace();
        }
    }

    public List<Invoice> getAllFactures() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Invoice> invoiceList = session.createQuery("from Invoice", Invoice.class).list();
            logger.info("Récupération de {} factures", invoiceList.size());
            return invoiceList;
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des factures : {}", e.getMessage());
            e.printStackTrace();
            return new ArrayList<>(); // retourner une liste vide en cas d'erreur
        }
    }

    public void updateFacture(Invoice invoice) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(invoice);
            transaction.commit();
            logger.info("Facture mise à jour avec succès : ID {}", invoice.getId());
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Erreur lors de la mise à jour de la facture : {}", e.getMessage());
            e.printStackTrace();
        }
    }

    public void deleteFacture(Long invoiceId) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Invoice invoice = session.get(Invoice.class, invoiceId);
            if (invoice != null) {
                session.delete(invoice);
                logger.info("Facture supprimée avec succès : ID {}", invoiceId);
            } else {
                logger.warn("Facture non trouvée pour suppression : ID {}", invoiceId);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Erreur lors de la suppression de la facture ID {} : {}", invoiceId, e.getMessage());
            e.printStackTrace();
        }
    }

    public Invoice findFactureById(Long invoiceId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Invoice facture = session.get(Invoice.class, invoiceId);

            if (facture != null) {
                logger.info("Facture trouvée : ID {}", invoiceId);
            } else {
                logger.warn("Facture non trouvée : ID {}", invoiceId);
            }
            return facture;
        } catch (Exception e) {
            logger.error("Erreur lors de la recherche de la facture ID {} : {}", invoiceId, e.getMessage());
            e.printStackTrace();
            return null;
        }

    }

    public Double calculatePrice(Article articles, int quantity) {
    
        logger.info("Calcul du prix de la facture");
        if (articles == null) {
            logger.warn("Aucun article fourni pour le calcul du prix");
            return 0.0;
        }
        Double totalPrice = articles.getPrice() * quantity;
        return totalPrice;
        
    }


    //recuperer la liste des articles
    public ObservableList<Article> getAllArticles() {
        logger.info("Récupération de tous les articles");
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Article> articles = session.createQuery("from Article", Article.class).list();
            if(articles.isEmpty()|| articles == null){
                logger.warn("Aucun article trouvé");
            } else {
                logger.info("Récupération de {} articles", articles.size());
            }
            return FXCollections.observableArrayList(articles);
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des articles : {}", e.getMessage());
            e.printStackTrace();
            return null; // retourner null en cas d'erreur
        }
    }
}