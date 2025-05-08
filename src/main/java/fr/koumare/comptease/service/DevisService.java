package fr.koumare.comptease.service;

import fr.koumare.comptease.model.Devis;
import fr.koumare.comptease.model.enumarated.StatusDevis;

public interface DevisService extends DocumentService {
    void updateDevisStatus(Long invoiceId);

}