package fr.koumare.comptease.service.impl;


import fr.koumare.comptease.model.ObligationFiscale;
import fr.koumare.comptease.model.User;
import fr.koumare.comptease.dao.ObligationFiscaleDao;

import java.time.Instant;

public class ObligationFiscaleServiceImpl {

    private final ObligationFiscaleDao dao = new ObligationFiscaleDao();

    public void enregistrerObligation(Instant dateEcheance, double montant, String typeImpot, User user) {
        ObligationFiscale obligation = new ObligationFiscale(dateEcheance, montant, typeImpot, user);
        dao.saveObligationFiscale(obligation);
    }


}
