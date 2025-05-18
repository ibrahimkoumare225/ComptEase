package fr.koumare.comptease.service;

import fr.koumare.comptease.model.User;

import java.util.Optional;
import java.util.List;

public interface UserService {

    public boolean registerUser(String pseudo, String email, String password) ;
    public boolean authenticateUser(String pseudo, String password);
    Optional<User> findByPseudo(String pseudo);
    List<User> getAllUsers();
    boolean updateUser(String pseudo, String email);
    boolean updateUserPassword(List<User> user, String password);
    User getCurrentUser();
    void setCurrentUser(User user);

}
