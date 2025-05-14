package fr.koumare.comptease.service.impl;

import fr.koumare.comptease.dao.DevisDao;
import fr.koumare.comptease.dao.InvoiceDao;
import fr.koumare.comptease.model.Devis;
import fr.koumare.comptease.model.Document;
import fr.koumare.comptease.model.Invoice;
import fr.koumare.comptease.service.DocumentService;
import fr.koumare.comptease.utilis.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public abstract class DocumentServiceImpl implements DocumentService {

    private static final Logger logger = LoggerFactory.getLogger(DocumentServiceImpl.class);
    private final DevisDao devisDao = new DevisDao();
    private final InvoiceDao invoiceDao = new InvoiceDao();

    @Override
    public Document createDocument(Document document) {
        if (document == null) {
            logger.warn("Tentative de création d'un document null");
            throw new IllegalArgumentException("Le document ne peut pas être null");
        }
        logger.info("Création d'un document avec ID initial : {}", document.getId());

        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            if (document instanceof Invoice) {
                logger.debug("Le document est une facture");
                Invoice invoice = (Invoice) document;
                //invoiceDao.saveFacture(invoice);
                session.refresh(invoice); // Mettre à jour l'objet avec l'ID généré
                if (invoice.getId() == null) {
                    logger.error("Échec de la sauvegarde de la facture : ID non généré");
                    throw new IllegalStateException("L'ID de la facture n'a pas été généré après sauvegarde");
                }
                logger.info("Facture sauvegardée avec succès : ID {}", invoice.getId());
                transaction.commit();
                return invoice;
            } else if (document instanceof Devis) {
                logger.debug("Le document est un devis");
                Devis devis = (Devis) document;
                devisDao.saveDevis(devis);
                session.refresh(devis);
                if (devis.getId() == null) {
                    logger.error("Échec de la sauvegarde du devis : ID non généré");
                    throw new IllegalStateException("L'ID du devis n'a pas été généré après sauvegarde");
                }
                logger.info("Devis sauvegardé avec succès : ID {}", devis.getId());
                transaction.commit();
                return devis;
            } else {
                logger.error("Type de document non pris en charge : {}", document.getClass().getName());
                throw new IllegalArgumentException("Type de document non pris en charge");
            }
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Erreur lors de la création du document : {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Document findDocumentById(Long id) {
        if (id == null) {
            logger.warn("Tentative de recherche d'un document avec un ID null");
            throw new IllegalArgumentException("L'ID ne peut pas être null");
        }
        logger.info("Recherche du document avec ID : {}", id);
        Invoice invoice = invoiceDao.findFactureById(id);
        if (invoice != null) {
            logger.debug("Facture trouvée avec ID : {}", id);
            return invoice;
        }

        Devis devis = devisDao.findDevisById(id);
        if (devis != null) {
            logger.debug("Devis trouvé avec ID : {}", id);
            return devis;
        }

        logger.warn("Aucun document trouvé avec l'ID : {}", id);
        return null;
    }

    @Override
    public void deleteDocumentById(Long id) {
        if (id == null) {
            logger.warn("Tentative de suppression d'un document avec un ID null");
            throw new IllegalArgumentException("L'ID ne peut pas être null");
        }
        logger.info("Suppression du document avec ID : {}", id);
        Document document = findDocumentById(id);
        if (document == null) {
            logger.warn("Document avec ID : {} non trouvé pour suppression", id);
            return;
        }
        if (document instanceof Invoice) {
            invoiceDao.deleteFacture(id);
            logger.debug("Facture avec ID : {} supprimée", id);
        } else if (document instanceof Devis) {
            devisDao.deleteDevis(id);
            logger.debug("Devis avec ID : {} supprimé", id);
        }
    }

    public void deleteDocument(Object document) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.delete(document);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Erreur lors de la suppression du document : {}", e.getMessage());
            throw new IllegalStateException("Erreur lors de la suppression du document", e);
        }
    }

    public void updateDocument(Document document) {
        if (document == null) {
            logger.warn("Tentative de mise à jour d'un document null");
            throw new IllegalArgumentException("Le document ne peut pas être null");
        }
        logger.info("Mise à jour du document avec ID : {}", document.getId());
        if (document instanceof Invoice) {
            invoiceDao.updateFacture((Invoice) document);
            logger.debug("Facture avec ID : {} mise à jour", document.getId());
        } else if (document instanceof Devis) {
            devisDao.updateDevis((Devis) document);
            logger.debug("Devis avec ID : {} mis à jour", document.getId());
        }
    }

    protected <T> List<T> findAllDocumentsByType(Class<T> type) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<T> query = session.createQuery("FROM " + type.getName(), type);
            return query.list();
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des entités de type {} : {}", type.getSimpleName(), e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public void generatePDF(Document document) {
        // Implémentation non nécessaire pour l'instant
    }
}