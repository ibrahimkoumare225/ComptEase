package fr.koumare.comptease.service;

import fr.koumare.comptease.model.Client;

import java.util.List;

public interface ClientService {

    public void addClient(Client client) ;

    public List<Client> getAllClients() ;

    public void updateClient(Client client);

    public void deleteClient(Long id) ;
}
