package fr.koumare.comptease.service;

import fr.koumare.comptease.model.User;

public interface UserService {

    public boolean registerUser(String pseudo, String email, String password) ;
    public boolean authenticateUser(String pseudo, String password);

}
