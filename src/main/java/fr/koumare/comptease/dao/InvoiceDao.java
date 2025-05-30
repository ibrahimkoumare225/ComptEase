package fr.koumare.comptease.dao;
import fr.koumare.comptease.model.Article;
import fr.koumare.comptease.model.Invoice;
import fr.koumare.comptease.utilis.HibernateUtil;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;

import org.hibernate.Session;
import org.hibernate.Transaction;

import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class InvoiceDao {

    private static final Logger logger = LoggerFactory.getLogger(InvoiceDao.class);

    public void saveFacture(Invoice invoice) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            logger.info("Début de la sauvegarde de la facture : description={}", invoice.getDescription());
            transaction = session.beginTransaction();
            session.save(invoice);
            transaction.commit();
            logger.info("Facture sauvegardée avec succès : ID {}", invoice.getId());
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
                logger.error("Rollback effectué pour la facture : description={}", invoice.getDescription());
            }
            logger.error("Erreur lors de la sauvegarde de la facture : {}", e.getMessage(), e);
            throw new RuntimeException("Échec de la sauvegarde de la facture", e);
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
            logger.error("Erreur lors de la mise à jour de la facture : {}", e.getMessage(), e);
            throw new RuntimeException("Échec de la mise à jour de la facture", e);
        }
    }

    public boolean invoiceExists(String description, Long clientId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Invoice> query = session.createQuery(
                    "FROM Invoice i WHERE i.description = :description AND i.client.idc = :clientId", Invoice.class);
            query.setParameter("description", description);
            query.setParameter("clientId", clientId);
            return query.uniqueResult() != null;
        } catch (Exception e) {
            logger.error("Erreur lors de la vérification de l'existence de la facture : {}", e.getMessage());
            return false;
        }
    }


    public List<Invoice> getAllFactures() {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Query<Invoice> query = session.createQuery("SELECT DISTINCT i FROM Invoice i LEFT JOIN FETCH i.articles a LEFT JOIN FETCH i.client c" ,Invoice.class);
            List<Invoice> invoices = query.list();
            transaction.commit();
            logger.info("Factures récupérées avec succès : {}", invoices.size());
            return invoices;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Erreur lors de la récupération des factures : {}", e.getMessage(), e);
            throw new RuntimeException("Échec de la récupération des factures", e);
        }
    }
    //la sommes des impayees des entrees
    public Double getTotalUnpaidIncomingInvoices() {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Query<Double> query = session.createQuery(
                    "SELECT SUM(i.price) FROM Invoice i WHERE i.type = 'INCOMING' AND i.status = 'UNPAID'", Double.class);
            Double total = query.uniqueResult();
            transaction.commit();
            logger.info("Total des factures impayées entrantes calculé : {}", total != null ? total : 0.0);
            return total != null ? total : 0.0;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Erreur lors du calcul du total des factures impayées entrantes : {}", e.getMessage(), e);
            throw new RuntimeException("Échec du calcul du total des factures impayées entrantes", e);
        }
    }
    //la somme des payes des entrees
    public Double getTotalPaidIncomingInvoices() {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Query<Double> query = session.createQuery(
                    "SELECT SUM(i.price) FROM Invoice i WHERE i.type = 'INCOMING' AND i.status = 'PAID'", Double.class);
            Double total = query.uniqueResult();
            transaction.commit();
            logger.info("Total des factures payées entrantes calculé : {}", total != null ? total : 0.0);
            return total != null ? total : 0.0;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Erreur lors du calcul du total des factures payées entrantes : {}", e.getMessage(), e);
            throw new RuntimeException("Échec du calcul du total des factures payées entrantes", e);
        }
    }

    //la sommes des depenses
    public Double getTotalOutgoingInvoices() {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Query<Double> query = session.createQuery(
                    "SELECT SUM(i.price) FROM Invoice i WHERE i.type = 'OUTGOING'", Double.class);
            Double total = query.uniqueResult();
            transaction.commit();
            logger.info("Total des factures sortantes calculé : {}", total != null ? total : 0.0);
            return total != null ? total : 0.0;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Erreur lors du calcul du total des factures sortantes : {}", e.getMessage(), e);
            throw new RuntimeException("Échec du calcul du total des factures sortantes", e);
        }
    }


//    public void updateFacture(Invoice invoice) {
//        Transaction transaction = null;
//        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
//            transaction = session.beginTransaction();
//            session.update(invoice);
//            transaction.commit();
//            logger.info("Facture mise à jour avec succès : ID {}", invoice.getId());
//        } catch (Exception e) {
//            if (transaction != null) {
//                transaction.rollback();
//            }
//            logger.error("Erreur lors de la mise à jour de la facture : {}", e.getMessage());
//            e.printStackTrace();
//        }
//    }
//
//    public void deleteFacture(Long invoiceId) {
//        Transaction transaction = null;
//        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
//            transaction = session.beginTransaction();
//            Invoice invoice = session.get(Invoice.class, invoiceId);
//            if (invoice != null) {
//                session.delete(invoice);
//                logger.info("Facture supprimée avec succès : ID {}", invoiceId);
//            } else {
//                logger.warn("Facture non trouvée pour suppression : ID {}", invoiceId);
//            }
//            transaction.commit();
//        } catch (Exception e) {
//            if (transaction != null) {
//                transaction.rollback();
//            }
//            logger.error("Erreur lors de la suppression de la facture ID {} : {}", invoiceId, e.getMessage());
//            e.printStackTrace();
//        }
//    }
//
//    public Invoice findFactureById(Long invoiceId) {
//        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
//            Invoice facture = session.get(Invoice.class, invoiceId);
//
//            if (facture != null) {
//                logger.info("Facture trouvée : ID {}", invoiceId);
//            } else {
//                logger.warn("Facture non trouvée : ID {}", invoiceId);
//            }
//            return facture;
//        } catch (Exception e) {
//            logger.error("Erreur lors de la recherche de la facture ID {} : {}", invoiceId, e.getMessage());
//            e.printStackTrace();
//            return null;
//        }
//
//    }

    /*public Double calculatePrice(Article articles, int quantity) {
    
        logger.info("Calcul du prix de la facture");
        if (articles == null) {
            logger.warn("Aucun article fourni pour le calcul du prix");
            return 0.0;
        }
        Double totalPrice = articles.getPrice() * quantity;
        return totalPrice;
        
    }*/

    //enregistrer un article
    public void saveArticle(Article article) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(article);
            transaction.commit();
            logger.info("Article sauvegardé avec succès : ID {}", article.getId());
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Erreur lors de la sauvegarde de l'article : {}", e.getMessage(), e);
            throw new RuntimeException("Échec de la sauvegarde de l'article", e);
        }
    }

    //modifier un article
    public void updateArticle(Article article) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(article);
            transaction.commit();
            logger.info("Article mis à jour avec succès : ID {}", article.getId());
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Erreur lors de la mise à jour de l'article : {}", e.getMessage());
            e.printStackTrace();
        }
    }

//    public Article findArticleById(Long id) {
//        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
//            Article article = session.get(Article.class, id);
//            if (article == null) {
//                logger.warn("Article non trouvé pour ID : {}", id);
//            }
//            return article;
//        } catch (Exception e) {
//            logger.error("Erreur lors de la recherche de l'article ID {} : {}", id, e.getMessage(), e);
//            throw new RuntimeException("Échec de la recherche de l'article", e);
//        }
//    }

    //recuperer un article par son id
    public Optional<Article> getArticleById(Long articleId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Optional<Article> article = session.createQuery("FROM Article WHERE id = :id", Article.class)
                    .setParameter("id", articleId)
                    .uniqueResultOptional();
            return article;
        } catch (Exception e) {
            logger.error("Erreur lors de la recherche de l'article ID {} : {}", articleId, e.getMessage());
            e.printStackTrace();
            return null;
        }
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
    public void deleteInvoice(Long invoiceId) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Invoice invoice = session.get(Invoice.class, invoiceId);
            if (invoice != null) {
                session.delete(invoice);
                logger.info("Facture supprimée avec succès : ID {}", invoiceId);
            } else {
                logger.warn("Facture non trouvée pour suppression : ID {}", invoiceId);
                throw new RuntimeException("Facture non trouvée");
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Erreur lors de la suppression de la facture ID {} : {}", invoiceId, e.getMessage(), e);
            throw new RuntimeException("Échec de la suppression de la facture", e);
        }
    }

    

    //recuperer la quantite de la facture par son id
    public int getQuantityById(Long invoiceId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Invoice invoice = session.get(Invoice.class, invoiceId);
            if (invoice != null) {
                return invoice.getQuantity();
            } else {
                logger.warn("Facture non trouvée pour ID : {}", invoiceId);
                return 0;
            }
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération de la quantité de la facture ID {} : {}", invoiceId, e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }


}