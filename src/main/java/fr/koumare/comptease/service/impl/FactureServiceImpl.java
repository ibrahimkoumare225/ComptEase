package fr.koumare.comptease.service.impl;

import fr.koumare.comptease.dao.ClientDao;
import fr.koumare.comptease.dao.InvoiceDao;
import fr.koumare.comptease.model.*;
import fr.koumare.comptease.model.enumarated.TypeInvoice;
import fr.koumare.comptease.model.enumarated.StatusDevis;
import fr.koumare.comptease.model.enumarated.StatusInvoice;
import fr.koumare.comptease.service.FactureService;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.criteria.CriteriaBuilder.In;

public class FactureServiceImpl implements FactureService {

    private static final Logger logger = LoggerFactory.getLogger(FactureServiceImpl.class);
    private final InvoiceDao invoiceDao;
    private final ClientDao clientDao;

    public FactureServiceImpl() {
        this.invoiceDao = new InvoiceDao();
        this.clientDao = new ClientDao();
    }

//    public boolean addInvoice(Double prix, String description, Instant date, String status, Long clientId, List<Article> articles, String type, int quantity) {
//        logger.info("Création d'une facture avec paramètres : prix={}, description={}, date={}, status={}, clientId={}, type={}",
//                prix, description, date, status, clientId, type);
//
//        if (articles == null || articles.isEmpty()) {
//            logger.warn("La liste des articles est vide ou null");
//            return false;
//        }
//
//        ClientDao clientDao = new ClientDao();
//        Optional<fr.koumare.comptease.model.Client> clientOptional = clientDao.findById(clientId);
//        if (!clientOptional.isPresent()) {
//            logger.warn("Client avec ID {} non trouvé", clientId);
//            return false;
//        }
//
//        Invoice invoice = new Invoice(
//                0.0, // Prix initial, sera recalculé par calculatePrice
//                description,
//                date != null ? date : Instant.now(),
//                StatusInvoice.valueOf(status),
//                clientOptional.get(),
//                articles,
//                TypeInvoice.valueOf(type.toUpperCase())
//        );
//
//        invoice.calculatePrice();
//
//        try {
//            invoiceDao.saveFacture(invoice);
//            logger.debug("ID généré après sauvegarde : {}", invoice.getId());
//            logger.info("Facture créée avec succès : ID={}, prixTotal={}, description={}, date={}, status={}, clientId={}, type={}",
//                    invoice.getId(), invoice.getPrice(), description, date, status, clientId, type);
//            return true;
//        } catch (Exception e) {
//            logger.error("Échec de la création de la facture : {}", e.getMessage(), e);
//            return false;
//        }
//    }


    @Override
    public boolean addInvoice(String description, Instant date, String status, Long clientId,
                              java.util.List<Article> articles, String type, int quantity) {
        logger.info("Ajout d'une facture : description={}, date={}, status={}, clientId={}, articles.size={}, type={}, quantiteTotal={}",
                description, date, status, clientId, articles != null ? articles.size() : 0, type, quantity);

        // Vérification des champs obligatoires
        if (description == null || date == null || status == null || clientId == null || articles == null || type == null) {
            logger.warn("Informations facture incomplètes");
            return false;
        }

        // Vérifier si le type est valide
        try {
            TypeInvoice.valueOf(type);
        } catch (IllegalArgumentException e) {
            logger.error("Type de facture invalide : {}", type);
            return false;
        }

        // Vérifier si cette facture existe déjà pour ce client
        if (invoiceDao.invoiceExists(description, clientId)) {
            logger.warn("Facture déjà existante pour ce client");
            return false;
        }

        // Récupérer le client via ClientDao
        Client client = clientDao.findByIdQuery(clientId);
        if (client == null) {
            logger.warn("Client avec ID {} non trouvé", clientId);
            return false;
        }

        // Créer la facture
        Invoice invoice = new Invoice(
                0.0, // Prix initial, sera recalculé
                description,
                date,
                StatusInvoice.valueOf(status),
                client,
                new ArrayList<>(articles),
                TypeInvoice.valueOf(type),
                quantity
        );
        invoice.calculatePrice(); // Recalcule price et quantity

        // Enregistrer la facture
        try {
            invoiceDao.saveFacture(invoice);
            logger.info("Facture ajoutée avec succès : ID={}", invoice.getId());
            return true;
        } catch (Exception e) {
            logger.error("Échec de l'ajout de la facture : {}", e.getMessage(), e);
            return false;
        }
    }


   /*  public Invoice createInvoiceFromDevis(Devis devis) {
        logger.info("Création d'une facture à partir du devis ID : {}", devis.getId());
        if (devis.getStatus() != StatusDevis.ACCEPTED) {
            logger.warn("Le devis ID : {} n'est pas accepté, impossible de créer une facture", devis.getId());
            throw new IllegalStateException("Le devis doit être accepté pour créer une facture");
        }
        if (devis.getInvoice() != null) {
            logger.warn("Le devis ID : {} a déjà une facture associée", devis.getId());
            throw new IllegalStateException("Une facture existe déjà pour ce devis");
        }

        Invoice invoice = new Invoice(
                devis.getPrice(),
                devis.getDescription(),
                Instant.now(),
                StatusInvoice.UNPAID,
                devis.getClient(),
                devis,
                devis.getArticles()
        );

        Invoice savedInvoice = (Invoice) super.createDocument(invoice);
        if (savedInvoice == null) {
            logger.error("Échec de la création de la facture à partir du devis ID : {}", devis.getId());
            return null;
        }
        devis.setInvoice(savedInvoice);
        super.updateDocument(devis);
        logger.debug("Facture créée avec succès à partir du devis ID : {}", devis.getId());
        return savedInvoice;
    }*/

//    @Override
//    public void updateInvoiceStatus(Long invoiceId, String status) {
//        logger.info("Mise à jour du statut de la facture ID : {} avec statut : {}", invoiceId, status);
//        Invoice invoice = (Invoice) findDocumentById(invoiceId);
//        if (invoice != null) {
//            invoice.setStatus(StatusInvoice.valueOf(status.toUpperCase()));
//            updateDocument(invoice);
//            logger.debug("Statut de la facture ID : {} mis à jour à : {}", invoiceId, status);
//        } else {
//            logger.warn("Facture non trouvée avec l'ID : {}", invoiceId);
//            throw new IllegalArgumentException("Facture non trouvée avec l'ID : " + invoiceId);
//        }
//    }
    @Override
    public ObservableList<Invoice> getAllFactures() {
        logger.info("Récupération de toutes les factures");
        ObservableList<Invoice> invoices = invoiceDao.getAllFactures();
        logger.info("Récupération de {} factures", invoices.size());
        if (invoices.isEmpty()) {
            logger.warn("Aucune facture trouvée");
        } else {
            logger.info("Factures récupérées avec succès");
        }
        return invoices;
    }


//    @Override
//    public void generatePDF(Document document) {
//        super.generatePDF(document);
//    }

    @Override
    public boolean updateArticle(Long id, String description, List<String> category, int quantite, Double price) {
        logger.info("Mise à jour de l'article : {}", description);
        Optional<Article> optionalArticle = invoiceDao.getArticleById(id);
       if(price<=0){
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

//    @Override
//    public boolean deleteFactureById(Long invoiceId) {
//        logger.info("Tentative de suppression de la facture avec ID : {}", invoiceId);
//        try {
//            invoiceDao.deleteFactureById(invoiceId);
//            logger.info("Facture supprimée avec succès : ID={}", invoiceId);
//            return true;
//        } catch (Exception e) {
//            logger.error("Échec de la suppression de la facture ID {} : {}", invoiceId, e.getMessage(), e);
//            return false;
//        }
//    }
//

    @Override
    public ObservableList<Article> getAllArticles(){
        logger.info("Récupération de tous les articles");
        ObservableList<Article> articles = invoiceDao.getAllArticles();
        logger.info("Récupération de {} articles", articles.size());
        if(articles.isEmpty()){
            logger.warn("Aucun article trouvé");
        }
        else{
            logger.info("Articles récupérés avec succès");
        }
        return articles;
    }
}