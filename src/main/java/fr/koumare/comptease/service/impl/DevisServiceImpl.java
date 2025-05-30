//package fr.koumare.comptease.service.impl;
//import fr.koumare.comptease.dao.DevisDao;
//import fr.koumare.comptease.model.Devis;
//import fr.koumare.comptease.model.Invoice;
//import fr.koumare.comptease.model.enumarated.StatusDevis;
//import fr.koumare.comptease.model.enumarated.StatusInvoice;
//import fr.koumare.comptease.service.DevisService;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.time.Instant;
//import java.util.ArrayList;
//import java.util.List;
//
//public class DevisServiceImpl extends DocumentServiceImpl implements DevisService {
//
//    private static final Logger logger = LoggerFactory.getLogger(DevisServiceImpl.class);
//
//    private final DevisDao devisDao;
//
//
//    public DevisServiceImpl() {
//        super();
//        this.devisDao = new DevisDao();
//    }
//
//    @Override
//    public Devis createDevis(Devis devis) {
//        logger.info("Création d'un devis avec ID : {}", devis.getId());
//        if (devis.getArticles() != null) {
//            devis.calculatePrice();
//        }
//        Devis savedDevis = (Devis) createDocument(devis);
//        if (savedDevis == null) {
//            logger.error("Échec de la création du devis");
//            return null; // Retourner null si la sauvegarde a échoué
//        }
//        return savedDevis;
//    }
//
//     public Invoice createInvoiceFromDevis(Devis devis) {
//        logger.info("Création d'une facture à partir du devis ID : {}", devis.getId());
//        if (devis.getStatus() != StatusDevis.ACCEPTED) {
//            logger.warn("Le devis ID : {} n'est pas accepté, impossible de créer une facture", devis.getId());
//            throw new IllegalStateException("Le devis doit être accepté pour créer une facture");
//        }
//        if (devis.getInvoice() != null) {
//            logger.warn("Le devis ID : {} a déjà une facture associée", devis.getId());
//            throw new IllegalStateException("Une facture existe déjà pour ce devis");
//        }
//
//        /*Invoice facture = new Invoice(
//                devis.getPrice(),
//                devis.getDescription(),
//                Instant.now(),
//                StatusInvoice.UNPAID,
//                devis.getClient(),
//                devis,
//                devis.getArticles()
//        );*/
//
//        Invoice savedInvoice = (Invoice) createDocument(null);
//        if (savedInvoice == null) {
//            logger.error("Échec de la création de la facture à partir du devis ID : {}", devis.getId());
//            return null; //
//        }
//        devis.setInvoice(savedInvoice);
//        updateDocument(devis);
//        logger.debug("Facture créée avec succès à partir du devis ID : {}", devis.getId());
//        return savedInvoice;
//
//    }
//
//    @Override
//    public void updateDevisStatus(Long devisId) {
//        logger.info("Mise à jour du statut du devis ID : {}", devisId);
//        Devis devis = (Devis) findDocumentById(devisId);
//        if (devis == null) {
//            logger.warn("Devis non trouvé avec l'ID : {}", devisId);
//            throw new IllegalArgumentException("Devis non trouvé avec l'ID : " + devisId);
//        }
//        devis.setStatus(devis.getStatus() == StatusDevis.ACCEPTED ? StatusDevis.REJECTED : StatusDevis.ACCEPTED);
//        updateDocument(devis);
//        logger.debug("Statut du devis ID : {} mis à jour à : {}", devisId, devis.getStatus());
//    }
//    public List<Devis> getAllDevis() {
//        try {
//            List<Devis> devis = findAllDocumentsByType(Devis.class);
//            logger.info("Récupération de {} factures", devis.size());
//            return devis;
//        } catch (Exception e) {
//            logger.error("Erreur lors de la récupération des factures : {}", e.getMessage());
//            return new ArrayList<>();
//        }
//    }
//
//
//    @Override
//    public void deleteDevis(Long devisId) {
//        logger.info("Suppression du devis ID : {}", devisId);
//        Devis devis = (Devis) findDocumentById(devisId);
//        if (devis != null) {
//            deleteDocument(devis);
//            logger.debug("Devis ID : {} supprimé avec succès", devisId);
//        } else {
//            logger.warn("Devis non trouvé avec l'ID : {}", devisId);
//            throw new IllegalArgumentException("Devis non trouvé avec l'ID : " + devisId);
//        }
//    }
//}
