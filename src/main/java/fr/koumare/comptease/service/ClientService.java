package fr.koumare.comptease.service;

import fr.koumare.comptease.model.Client;

import java.util.List;

public interface ClientService {

    public boolean addClient(Client client) ;

    public List<Client> getAllClients() ;

    public boolean updateClient(Client client);

    public boolean deleteClient(Long id) ;
}
