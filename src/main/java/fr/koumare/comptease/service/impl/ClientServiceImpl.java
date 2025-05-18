package fr.koumare.comptease.service.impl;

import fr.koumare.comptease.dao.ClientDao;
import fr.koumare.comptease.model.Client;
import fr.koumare.comptease.model.Invoice;
import fr.koumare.comptease.service.ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ClientServiceImpl implements ClientService {

    private static final Logger logger = LoggerFactory.getLogger(ClientServiceImpl.class);

    private final ClientDao clientDao;

    public ClientServiceImpl() {
        this.clientDao = new ClientDao();
    }

    @Override
    public List<Client> getAllClients() {
        List<Client> clients = clientDao.getAllClients();
        logger.info("Retrieved {} clients via getAllClients", clients.size());
        return clients;
    }

    @Override
    public boolean updateClient(Long id, String nom, String prenom, String adresse, String contact, Double solde, String note) {
        if (nom == null || prenom == null || adresse == null) {
            logger.warn("Informations client incomplete");
            return false;
        }
        Optional<Client> clientOptional = clientDao.findById(id);
        if (clientOptional.isPresent()) {
            Client client = clientOptional.get();
            client.setLastName(nom);
            client.setFirstName(prenom);
            client.setContact(contact);
            client.setAdresse(adresse);
            client.setSolde(solde);
            client.setNote(note);
            clientDao.updateClient(client);
            logger.info("Client modifié : {} {} {}", nom, prenom, note);
            return true;
        } else {
            logger.warn("Client non trouvé avec l'ID : {}", id);
            return false;
        }
    }

    @Override
    public boolean deleteClient(Long clientId) {
        clientDao.deleteClient(clientId);
        if (clientDao.findById(clientId).isPresent()) {
            logger.warn("Client non supprimé avec l'ID : {}", clientId);
            return false;
        }
        logger.info("Client supprimé avec l'ID : {}", clientId);
        return true;
    }

    @Override
    public boolean addClient(String nom, String prenom, String adresse, String contact, Long idUser, Double solde, String note) {
        logger.info("Ajout d'un client : {} {} {} {} {} {}", nom, prenom, adresse, contact, idUser, solde);
        if (clientDao.clientExists(nom, prenom)) {
            logger.warn("Client déjà existant : {} {}", nom, prenom);
            return false;
        }
        if (nom == null || prenom == null || adresse == null) {
            logger.warn("Informations client incomplete");
            return false;
        }
        Client client = new Client();
        client.setLastName(nom);
        client.setFirstName(prenom);
        client.setAdresse(adresse);
        client.setContact(contact);
        client.setId_user(1L);
        client.setSolde(solde != null ? solde : 0.0);
        client.setNote(note);

        clientDao.saveClient(client);
        logger.info("Client ajouté : {} {}", nom, prenom);
        return true;
    }

    @Override
    public Optional<Client> findById(Long id) {
        logger.info("Recherche du client par l'ID : {}", id);
        return clientDao.findById(id);
    }

    @Override
    public Optional<Client> findByNames(String nom, String prenom) {
        logger.info("Recherche du client par le nom+prenom : {} {}", nom, prenom);
        return clientDao.findByNames(nom, prenom);
    }

    @Override
    public List<Client> findByKeyword(String keyword) {
        logger.info("Recherche du client par le mot clé : {}", keyword);
        return clientDao.findByKeyword(keyword);
    }

    @Override
    public List<Invoice> getClientDetails(Long idClient) {
        logger.info("Recherche des détails du client : {}", idClient);
        return clientDao.getClientDetails(idClient);
    }

    @Override
    public List<Invoice> findByKeywordDetails(String keyword) {
        logger.info("Recherche des détails du client par le mot clé : {}", keyword);
        return clientDao.findByKeywordDetails(keyword);
    }

    @Override
    public Optional<Client> findClientByInvoiceId(Long invoiceId) {
        logger.info("Recherche du client par l'ID de la facture : {}", invoiceId);
        return clientDao.findClientByInvoiceId(invoiceId);
    }

    @Override
    public boolean updateClientBalance(Long clientId) {
        logger.info("Mise à jour du solde du client : {}", clientId);
        Optional<Client> clientOptional = clientDao.findById(clientId);
        if (clientOptional.isPresent()) {
            Client client = clientOptional.get();
            double totalInvoices = clientDao.getClientInvoiceSum(clientId);
            logger.info("Somme des factures pour le client ID {} : {}", clientId, totalInvoices);
            client.setSolde(totalInvoices);
            clientDao.updateClient(client);
            logger.info("Solde du client mis à jour : {}", client.getSolde());
            return true;
        } else {
            logger.warn("Client non trouvé avec l'ID : {}", clientId);
            return false;
        }
    }

    @Override
    public Double getArticlePrice(Long idInvoice) {
        logger.info("Recherche du prix de l'article de la facture : {}", idInvoice);
        Optional<Invoice> invoiceOptional = clientDao.findInvoiceById(idInvoice);
        if (invoiceOptional.isPresent()) {
            Invoice invoice = invoiceOptional.get();
            Double totalPrice = invoice.getPrice();
            int quantite = invoice.getQuantity();
            Double unitPrice = totalPrice != null && quantite > 0 ? totalPrice / quantite : 0.0;
            logger.info("Prix total des articles : {}, Quantité : {}, Prix unitaire : {}", totalPrice, quantite, unitPrice);
            return unitPrice;
        } else {
            logger.warn("Facture non trouvée avec l'ID : {}", idInvoice);
            return 0.0;
        }
    }

    @Override
    public boolean modifDescriptionFacture(Long idInvoice, String description) {
        logger.info("Modification de la description de la facture : {}", idInvoice);
        Optional<Invoice> invoiceOptional = clientDao.findInvoiceById(idInvoice);
        if (invoiceOptional.isPresent()) {
            clientDao.updateInvoiceDescription(idInvoice, description);
            logger.info("Description de la facture mise à jour : {}", description);
            return true;
        } else {
            logger.warn("Facture non trouvée avec l'ID : {}", idInvoice);
            return false;
        }
    }

    @Override
    public void drawInDashboard() {
        logger.info("Affichage du graphique tableau de bord");
        Map<String, Integer> achatParClients = new HashMap<>();
        Map<String, Double> soldeParClients = new HashMap<>();
        List<Client> clientsWithMostInvoices = clientDao.getClientsWithMostInvoices();
        List<Client> clientsWithHighestBalance = clientDao.getClientsWithHighestBalance();
        if (clientsWithMostInvoices != null) {
            for (Client client : clientsWithMostInvoices) {
                achatParClients.put(client.getFirstName() + " " + client.getLastName(), clientDao.getClientInvoiceCount(client.getIdc()));
            }
        }
        if (clientsWithHighestBalance != null) {
            for (Client client : clientsWithHighestBalance) {
                soldeParClients.put(client.getFirstName() + " " + client.getLastName(), client.getSolde());
            }
        }
        logger.info("Clients avec le plus de factures : {}, Soldes par clients : {}", achatParClients, soldeParClients);
    }

    @Override
    public List<Client> getClientsWithHighestBalance() {
        List<Client> clients = clientDao.getClientsWithHighestBalance();
        logger.info("Retrieved {} clients with highest balance via service", clients.size());
        return clients;
    }

    @Override
    public List<Client> getClientsWithHighestBalanceByMonth(int year, int month) {
        List<Client> clients = clientDao.getClientsWithHighestBalanceByMonth(year, month);
        logger.info("Retrieved {} clients with highest balance for year {} and month {} via service", clients.size(), year, month);
        return clients;
    }

    @Override
    public Double getClientInvoiceSumByMonth(Long clientId, int year, int month) {
        logger.info("Calculating invoice sum for client ID {} in year {} and month {}", clientId, year, month);
        List<Invoice> invoices = clientDao.getClientDetails(clientId);
        if (invoices == null || invoices.isEmpty()) {
            logger.warn("No invoices found for client ID {} in year {} and month {}", clientId, year, month);
            return 0.0;
        }
        double sum = invoices.stream()
                .filter(invoice -> invoice.getDate() != null &&
                        invoice.getDate().atZone(ZoneId.systemDefault()).getYear() == year &&
                        invoice.getDate().atZone(ZoneId.systemDefault()).getMonthValue() == month &&
                        invoice.getPrice() != null)
                .mapToDouble(Invoice::getPrice)
                .sum();
        logger.info("Invoice sum for client ID {} in year {} and month {}: {}", clientId, year, month, sum);
        return sum;
    }

    @Override
    public boolean addClient(String nom, String prenom, String adresse, String contact, Long idUser, Double solde, String note, String siret, String rib) {
        logger.info("fonction addClient :{}", nom + " " + prenom + " " + adresse + " " + contact + " " + idUser + " " + solde);
        //verification si ce client existe deja
        if(clientDao.clientExists(nom, prenom)) {
            logger.warn("Client déjà existant");
            return false;
        }
        //verification des champs
        if(nom == null || prenom == null || adresse == null) {
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
        client.setId_user(1L);
        client.setSolde(solde);
        client.setNote(note);
        client.setSiret(siret);
        client.setRib(rib);

        clientDao.saveClient(client);
        logger.info("Client ajouté : {} {}", nom, prenom);
        return true;
    }
}