package fr.koumare.comptease.service;

import fr.koumare.comptease.model.Devis;

import java.util.List;

public interface DevisService {

    public void addDevis(Devis devis) ;

    public List<Devis> getAllDevis() ;

    public void updateDevis(Devis devis);

    public void deleteDevis(Long id) ;
}
