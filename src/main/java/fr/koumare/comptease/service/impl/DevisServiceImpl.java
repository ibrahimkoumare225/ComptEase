package fr.koumare.comptease.service.impl;
import fr.koumare.comptease.model.Devis;
import fr.koumare.comptease.model.Invoice;
import fr.koumare.comptease.model.enumarated.StatusInvoice;
import fr.koumare.comptease.service.DevisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;

public class DevisServiceImpl extends DocumentServiceImpl implements DevisService {

    private static final Logger logger = LoggerFactory.getLogger(DevisServiceImpl.class);

    public DevisServiceImpl() {
        super();
    }

    @Override
    public Devis createDevis(Devis devis) {
        logger.info("Création d'un devis avec ID : {}", devis.getId());
        return (Devis) createDocument(devis);
    }

    public Invoice createInvoiceFromDevis(Devis devis) {
        logger.info("Création d'une facture à partir du devis ID : {}", devis.getId());
        if (!devis.isValid()) {
            logger.warn("Le devis ID : {} n'est pas valide, impossible de créer une facture", devis.getId());
            throw new IllegalStateException("Le devis doit être valide pour créer une facture");
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
                devis
        );
        invoice.setArticles(devis.getArticles()); // les articles du devis
        createDocument(invoice); // sauvegarde la facture
        devis.setInvoice(invoice); // Associer la facture au devis
        updateDocument(devis); // mettre à jour le devis
        logger.debug("Facture créée avec succès à partir du devis ID : {}", devis.getId());
        return invoice;

    }

    @Override
    public void updateDevisStatus(Long devisId) {
        logger.info("Mise à jour du statut du devis ID : {}", devisId);
        Devis devis = (Devis) findDocumentById(devisId);
        if (devis != null) {
            devis.setValid(!devis.isValid()); // À ajuster selon vos besoins (par exemple, passer un paramètre)
            updateDocument(devis);
            logger.debug("Statut du devis ID : {} mis à jour à : {}", devisId, devis.isValid());
        } else {
            logger.warn("Devis non trouvé avec l'ID : {}", devisId);
            throw new IllegalArgumentException("Devis non trouvé avec l'ID : " + devisId);
        }
    }
}