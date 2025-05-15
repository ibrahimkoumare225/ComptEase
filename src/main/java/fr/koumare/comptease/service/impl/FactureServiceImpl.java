package fr.koumare.comptease.service.impl;

import fr.koumare.comptease.dao.ClientDao;
import fr.koumare.comptease.dao.InvoiceDao;
import fr.koumare.comptease.model.Article;
import fr.koumare.comptease.model.Devis;
import fr.koumare.comptease.model.Document;
import fr.koumare.comptease.model.Invoice;
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

public class FactureServiceImpl extends DocumentServiceImpl implements FactureService {

    private static final Logger logger = LoggerFactory.getLogger(FactureServiceImpl.class);
    private final InvoiceDao invoiceDao;

    public FactureServiceImpl() {
        this.invoiceDao = new InvoiceDao();
    }

    @Override
    public boolean createInvoice(Double prix, String description, Instant date, String status, Long clientId, List<Article> article, String type, int quantity) {
        logger.info("Création d'une facture avec ID : {}");
        if (article.isEmpty()) {
            return false;
        }
        Double prixTotal=invoiceDao.calculatePrice(article.getFirst(), quantity);
        Invoice invoice = new Invoice(
                prixTotal,
                description,
                Instant.now(),
                StatusInvoice.valueOf(status),
                new ClientDao().findById(clientId).orElse(null),
                article,
                TypeInvoice.valueOf(type.toUpperCase()),
                quantity
        );
        invoiceDao.saveFacture(invoice);
        /*Invoice savedInvoice = (Invoice) super.createDocument(invoice);
        if (savedInvoice == null) {
            logger.error("Échec de la création de la facture");
            return null;
        }*/
        logger.info("Facture créée avec succès : ID {}", invoice.getId()+" ,"+prixTotal+" ,"+description+" ,"+date+" ,"+status+" ,"+clientId+" , ,"+type+" ,"+quantity);
        return true;
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

    @Override
    public void updateInvoiceStatus(Long invoiceId, String status) {
        logger.info("Mise à jour du statut de la facture ID : {} avec statut : {}", invoiceId, status);
        Invoice invoice = (Invoice) findDocumentById(invoiceId);
        if (invoice != null) {
            invoice.setStatus(StatusInvoice.valueOf(status.toUpperCase()));
            updateDocument(invoice);
            logger.debug("Statut de la facture ID : {} mis à jour à : {}", invoiceId, status);
        } else {
            logger.warn("Facture non trouvée avec l'ID : {}", invoiceId);
            throw new IllegalArgumentException("Facture non trouvée avec l'ID : " + invoiceId);
        }
    }
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


    @Override
    public void generatePDF(Document document) {
        super.generatePDF(document);
    }

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
        invoiceDao.saveArticle(article);
        logger.info("Article enregistré avec succès : {}", article);
        return true;
    }

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