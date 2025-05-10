package fr.koumare.comptease.service.impl;
import fr.koumare.comptease.dao.ClientDao;
import fr.koumare.comptease.model.Client;
import fr.koumare.comptease.service.ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

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
    public boolean updateClient(Client client) {
        if(client.getFirstName()== null || client.getLastName() == null || client.getAdresse() == null) {
            logger.warn("Informations client incomplete");
            return false;
        }
        clientDao.updateClient(client);
        logger.info("Client modifié");
        return true;
    }

    @Override
    public boolean deleteClient(Long clientId) {
        clientDao.deleteClient(clientId);
        logger.info("Client supprimé");
        return true;
    }
    
    @Override
    public boolean addClient(Client client) {
        boolean clientExists = clientDao.clientExists(client);
        if(clientExists) {
            logger.warn("Le client existe déjà : {}", client);
            return false;
        }
        if(client.getFirstName()== null || client.getLastName() == null || client.getAdresse() == null || client.getSolde() == null) {
            logger.warn("Informations client incomplete");
            return false;            
        }
        //Enregistrement du client dans la base de données
        clientDao.saveClient(client);
        logger.info("Client enregistré : {}", client);
        return true;
        
    }

}
