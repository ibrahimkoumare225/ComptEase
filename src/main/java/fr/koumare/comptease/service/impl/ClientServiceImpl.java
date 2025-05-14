package fr.koumare.comptease.service.impl;
import fr.koumare.comptease.dao.ClientDao;
import fr.koumare.comptease.model.Client;
import fr.koumare.comptease.model.Invoice;
import fr.koumare.comptease.model.User;
import fr.koumare.comptease.model.CurrentUser;
import fr.koumare.comptease.service.ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class ClientServiceImpl implements ClientService {

    private static final Logger logger = LoggerFactory.getLogger(ClientServiceImpl.class);

    private final ClientDao clientDao;

    public ClientServiceImpl() {
        this.clientDao = new ClientDao();
    }


    @Override
    public List<Client> getAllClients() {
        return clientDao.getAllClients();
    }

    @Override
    public boolean updateClient(Long id, String nom, String prenom, String adresse, String contact,Long solde, String note) {
        if(nom == null || prenom == null || adresse == null ) {
            logger.warn("Informations client incomplete");
            return false;
        }
        //modifier le client dans la base de données
        //chercher le client dans la base de données
        Optional<Client> clientOptional = clientDao.findById(id);
        if(clientOptional.isPresent()) {
            Client client = clientOptional.get();
            client.setLastName(nom);
            client.setFirstName(prenom);
            client.setContact(contact);
            client.setAdresse(adresse);
            client.setSolde(solde);
            client.setNote(note);
            clientDao.updateClient(client);
            logger.info("Client modifié : {} {}", nom, prenom, note);
            return true;
        } else {
            logger.warn("Client non trouvé");
            return false;
        }
    }

    @Override
    public boolean deleteClient(Long clientId) {
        clientDao.deleteClient(clientId);
        if(clientDao.findById(clientId).isPresent()) {
            logger.warn("Client non supprimé");
            return false;
        }
        logger.info("Client supprimé");
        return true;
    }
    
    @Override
    public boolean addClient(String nom, String prenom, String adresse, String contact, Long idUser,Long solde, String note) {
        logger.info("fonction addClient :{}", nom+" "+ prenom+" "+ adresse+" "+ contact+" "+ idUser+" "+ solde);
        //verification si ce client existe deja
        if(clientDao.clientExists(nom,prenom)) {
            logger.warn("Client déjà existant");
            return false;
        }
        //verification des champs
        if(nom == null || prenom == null || adresse == null ) {
            logger.warn("Informations client incomplete");
            return false;
        }
        logger.info("Ajout d'un client addC");
        //Enregistrement du client dans la base de données
        Client client = new Client();
        client.setLastName(nom);
        client.setFirstName(prenom);
        client.setAdresse(adresse);
        client.setContact(contact);
        client.setId_user(CurrentUser.getCurrentUser().getId());
        client.setSolde(solde);
        client.setNote(note);
        clientDao.saveClient(client);
        logger.info("Client ajouté : {} {}", nom, prenom);
        return true;
        
    }

    @Override
    public Optional<Client> findById(Long id) {
        logger.info("Recherche du client via service par l'ID : {}", id);
        return clientDao.findById(id);
    }

    @Override
    public Optional<Client> findByNames(String nom, String prenom) {
        logger.info("Recherche du client via service par le nom+prenom : {} {}", nom, prenom);
        return clientDao.findByNames(nom, prenom);
    }

    @Override
    public List<Client> findByKeyword(String keyword) {
        logger.info("Recherche du client via service par le mot clé : {}", keyword);
        return clientDao.findByKeyword(keyword);
    }
        
    @Override
    public List<Invoice> getClientDetails(Long idClient) {
        logger.info("Recherche des details du client : {}", idClient);
        return clientDao.getClientDetails(idClient);
    }

    @Override
    public List<Invoice> findByKeywordDetails(String keyword) {
        logger.info("Recherche des details du client via service par le mot clé : {}", keyword);
        return clientDao.findByKeywordDetails(keyword);
    }

    @Override
    public Optional<Client> findUserByInvoiceId(Long invoiceId) {
        logger.info("Recherche du client par l'ID de la facture : {}", invoiceId);
        return clientDao.findUserByInvoiceId(invoiceId);
    }
}
