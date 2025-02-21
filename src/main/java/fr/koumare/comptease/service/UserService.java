package fr.koumare.comptease.service;

import fr.koumare.comptease.model.User;

import java.util.List;

public interface UserService {

    public void addUser(User user) ;

    public List<User> getAllUser() ;

    public void updateUser(User user);

    public void deleteUser(Long id) ;
}
