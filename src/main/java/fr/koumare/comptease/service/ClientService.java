package fr.koumare.comptease.service;

import fr.koumare.comptease.model.Client;
import fr.koumare.comptease.model.Invoice;
import fr.koumare.comptease.model.User;

import java.util.List;
import java.util.Optional;


public interface ClientService {

    public boolean addClient(String nom, String prenom, String adresse, String contact, Long idUser,Long solde) ;

    public List<Client> getAllClients() ;

    public boolean updateClient(Long id,String nom, String prenom, String adresse, String contact,Long solde) ;

    public boolean deleteClient(Long id) ;

    Optional<Client> findById(Long id);

    Optional<Client> findByNames(String nom, String prenom);

    List<Client> findByKeyword(String keyword);

    List<Client> sortByName();

    List<Client> sortByNameDesc();

    List<Client> sortByFirstName();

    List<Client> sortByFirstNameDesc();

    List<Client> sortById();

    List<Client> sortByIdDesc();

    List<Invoice>  getClientDetails(Long idClient);
}
