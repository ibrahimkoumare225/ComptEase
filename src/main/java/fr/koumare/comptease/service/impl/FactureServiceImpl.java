package fr.koumare.comptease.service.impl;

import fr.koumare.comptease.dao.ClientDao;
import fr.koumare.comptease.dao.InvoiceDao;
import fr.koumare.comptease.model.*;
import fr.koumare.comptease.model.enumarated.TypeInvoice;
import fr.koumare.comptease.model.enumarated.StatusInvoice;
import fr.koumare.comptease.service.FactureService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FactureServiceImpl implements FactureService {

    private static final Logger logger = LoggerFactory.getLogger(FactureServiceImpl.class);
    private final InvoiceDao invoiceDao;
    private final ClientDao clientDao;

    public FactureServiceImpl() {
        this.invoiceDao = new InvoiceDao();
        this.clientDao = new ClientDao();
    }

    @Override
    public boolean addInvoice(String description, Instant date, String status, Long clientId,
                              java.util.List<Article> articles, String type, int quantity) {
        logger.info("Ajout d'une facture : description={}, date={}, status={}, clientId={}, articles.size={}, type={}, quantiteTotal={}",
                description, date, status, clientId, articles != null ? articles.size() : 0, type, quantity);

        // Vérification des champs obligatoires
        if (description == null || date == null || status == null || articles == null || type == null /*descriptionArticle == null*/) {
            logger.warn("Informations facture incomplètes");
            return false;
        }

        try {
            TypeInvoice.valueOf(type);
        } catch (IllegalArgumentException e) {
            logger.error("Type de facture invalide : {}", type);
            return false;
        }

        try {
            TypeInvoice.valueOf(type);
            if (!type.equals("OUTGOING") && clientId == null) {
                logger.warn("ClientId requis pour une facture de type {}", type);
                return false;
            }
        } catch (IllegalArgumentException e) {
            logger.error("Type de facture invalide : {}", type);
            return false;
        }

        if (invoiceDao.invoiceExists(description, clientId)) {
            logger.warn("Facture déjà existante pour ce client");
            return false;
        }

        Client client = clientDao.findByIdQuery(clientId);
        if (clientId != null) {
            client = clientDao.findByIdQuery(clientId);
            if (client == null) {
                logger.warn("Client avec ID {} non trouvé", clientId);
                return false;
            }
        }

        Invoice invoice = new Invoice(
                0.0,
                description,
                date,
                StatusInvoice.valueOf(status),
                client,
                new ArrayList<>(articles),
                TypeInvoice.valueOf(type),
                quantity
        );
        invoice.calculatePrice();

        try {
            invoiceDao.saveFacture(invoice);
            logger.info("Facture ajoutée avec succès : ID={}", invoice.getId());
            return true;
        } catch (Exception e) {
            logger.error("Échec de l'ajout de la facture : {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean updateInvoice(Long id, String description, Instant date, String status, Long clientId,
                                 List<Article> articles, String type, int quantity, String descriptionArticle) {
        logger.info("Mise à jour de la facture : ID={}, description={}, date={}, status={}, clientId={}, articles.size={}, type={}, quantiteTotal={}",
                id, description, date, status, clientId, articles != null ? articles.size() : 0, type, quantity);

        // Vérification des champs obligatoires
        if (description == null || date == null || status == null || articles == null || type == null || descriptionArticle == null) {
            logger.warn("Informations facture incomplètes");
            return false;
        }

        try {
            TypeInvoice.valueOf(type);
            if (!type.equals("OUTGOING") && clientId == null) {
                logger.warn("ClientId requis pour une facture de type {}", type);
                return false;
            }
        } catch (IllegalArgumentException e) {
            logger.error("Type de facture invalide : {}", type);
            return false;
        }

        Client client = clientDao.findByIdQuery(clientId);
        if (clientId != null) {
            client = clientDao.findByIdQuery(clientId);
            if (client == null) {
                logger.warn("Client avec ID {} non trouvé", clientId);
                return false;
            }
        }

        Invoice invoice = new Invoice(
                0.0,
                description,
                date,
                StatusInvoice.valueOf(status),
                client,
                new ArrayList<>(articles),
                TypeInvoice.valueOf(type),
                quantity
                /*descriptionArticle*/
        );
        invoice.setId(id);
        invoice.calculatePrice();

        try {
            invoiceDao.updateFacture(invoice);
            
            logger.info("Facture mise à jour avec succès : ID={}", invoice.getId());
            return true;
        } catch (Exception e) {
            logger.error("Échec de la mise à jour de la facture : {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public Double getTotalUnpaidIncomingInvoices() {
        try {
            Double total = invoiceDao.getTotalUnpaidIncomingInvoices();
            logger.info("Total des factures impayées entrantes récupéré via le service : {}", total);
            return total;
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération du total des factures impayées entrantes : {}", e.getMessage(), e);
            throw new RuntimeException("Échec de la récupération du total des factures impayées entrantes", e);
        }
    }

    @Override
    public Double getTotalPaidIncomingInvoices() {
        try {
            Double total = invoiceDao.getTotalPaidIncomingInvoices();
            logger.info("Total des factures payées entrantes récupéré via le service : {}", total);
            return total;
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération du total des factures payées entrantes : {}", e.getMessage(), e);
            throw new RuntimeException("Échec de la récupération du total des factures payées entrantes", e);
        }
    }

    @Override
    public Double getTotalOutgoingInvoices() {
        try {
            Double total = invoiceDao.getTotalOutgoingInvoices();
            logger.info("Total de toutes les factures sortantes récupéré via le service : {}", total);
            return total;
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération du total des factures sortantes : {}", e.getMessage(), e);
            throw new RuntimeException("Échec de la récupération du total des factures sortantes", e);
        }
    }

    @Override
    public List<Invoice> getAllInvoices() {
        try {
            List<Invoice> invoices = invoiceDao.getAllFactures();
            logger.info("Factures récupérées via le service : {}", invoices.size());
            return invoices;
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des factures via le service : {}", e.getMessage(), e);
            throw new RuntimeException("Échec de la récupération des factures", e);
        }
    }

    @Override
    public boolean deleteInvoice(Long invoiceId) {
        try {
            logger.info("Tentative de suppression de la facture avec ID : {}", invoiceId);
            invoiceDao.deleteInvoice(invoiceId);
            clientDao.getClientInvoiceSum(invoiceId);
            logger.info("Facture supprimée avec succès : ID={}", invoiceId);
            return true;
        } catch (Exception e) {
            logger.error("Échec de la suppression de la facture ID {} : {}", invoiceId, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean updateArticle(Long id, String description, List<String> category, int quantite, Double price) {
        logger.info("Mise à jour de l'article : {}", description);
        Optional<Article> optionalArticle = invoiceDao.getArticleById(id);
        if (price <= 0) {
            logger.warn("Le prix de l'article doit être supérieur à 0");
            return false;
        }
        if (optionalArticle.isPresent()) {
            Article article = optionalArticle.get();
            article.setDescription(description);
            article.setCategory(category);
            article.setQuantite(quantite);
            article.setPrice(price);
            invoiceDao.updateArticle(article);
            logger.info("Article mis à jour avec succès : {}", article);
        } else {
            logger.warn("Aucun article trouvé avec l'ID : {}", id);
            return false;
        }
        return true;
    }

    @Override
    public boolean enregistrerArticle(Article article) {
        logger.info("Enregistrement de l'article : {}", article);
        if (article == null) {
            logger.warn("L'article est nul, échec de l'enregistrement");
            return false;
        }
        try {
            invoiceDao.saveArticle(article);
            logger.info("Article enregistré avec succès : ID={}", article.getId());
            return true;
        } catch (Exception e) {
            logger.error("Échec de l'enregistrement de l'article : {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public int getQuantityById(Long invoiceId) {
        logger.info("Récupération de la quantité pour la facture ID : {}", invoiceId);
        try {
            return invoiceDao.getQuantityById(invoiceId);
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération de la quantité pour la facture ID {} : {}", invoiceId, e.getMessage(), e);
            throw new RuntimeException("Échec de la récupération de la quantité pour la facture", e);
        }
    }
}