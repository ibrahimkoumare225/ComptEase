package fr.koumare.comptease.service;

import fr.koumare.comptease.model.Client;
import fr.koumare.comptease.model.Invoice;

import java.util.List;
import java.util.Optional;


public interface ClientService {

    public boolean addClient(String nom, String prenom, String adresse, String contact, Long idUser,Double solde, String note) ;

    public List<Client> getAllClients() ;

    public boolean updateClient(Long id,String nom, String prenom, String adresse, String contact,Double solde, String note) ;

    public boolean deleteClient(Long id) ;

    Optional<Client> findById(Long id);

    Optional<Client> findByNames(String nom, String prenom);

    List<Client> findByKeyword(String keyword);

    List<Invoice> getClientDetails(Long idClient);

    List<Invoice> findByKeywordDetails(String keyword);

    Optional<Client> findUserByInvoiceId(Long invoiceId);

    boolean updateClientBalance(Long clientId);
}
