package fr.koumare.comptease.service.impl;

import fr.koumare.comptease.dao.UserDao;
import fr.koumare.comptease.model.User;
import fr.koumare.comptease.service.UserService;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;

public class UserServiceImpl implements UserService {

    private UserDao userDAO = new UserDao();
    public boolean registerUser(String pseudo,String email, String password) {
        // Vérifier si l'utilisateur existe déjà
        if (userDAO.findByPseudo(pseudo).isPresent()) {
            System.out.println("L'utilisateur existe déjà !");
            return false;
        }

        // Hachage du mot de passe
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        // Création et sauvegarde de l'utilisateur
        User user = new User();
        user.setPseudo(pseudo);
        user.setEmail(email);
        user.setPassword(hashedPassword);
        userDAO.saveUser(user);

        return true;
    }

    public boolean authenticateUser(String pseudo, String password) {
        Optional<User> userOpt = userDAO.findByPseudo(pseudo);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            return BCrypt.checkpw(password, user.getPassword());
        }
        return false;
    }
}

