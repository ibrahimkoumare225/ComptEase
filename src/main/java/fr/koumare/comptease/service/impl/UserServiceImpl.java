package fr.koumare.comptease.service.impl;

import fr.koumare.comptease.dao.UserDao;
import fr.koumare.comptease.model.User;
import fr.koumare.comptease.service.UserService;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private UserDao userDao = new UserDao();

    @Override
    public boolean registerUser(String pseudo, String email, String password) {
        logger.info("Tentative d'inscription pour le pseudo : {}", pseudo);
        // Vérifier si l'utilisateur existe déjà
        if (userDao.findByPseudo(pseudo).isPresent()) {
            logger.warn("L'utilisateur existe déjà : {}", pseudo);
            return false;
        }

        // Hachage du mot de passe
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        logger.debug("Mot de passe haché pour {} : {}", pseudo, hashedPassword);

        // Création et sauvegarde de l'utilisateur
        User user = new User();
        user.setPseudo(pseudo);
        user.setEmail(email);
        user.setPassword(hashedPassword);
        userDao.saveUser(user);
        logger.info("Utilisateur enregistré avec succès : {}", pseudo);

        return true;
    }

    @Override
    public boolean authenticateUser(String pseudo, String password) {
        logger.info("Tentative d'authentification pour le pseudo : {}", pseudo);
        Optional<User> userOpt = userDao.findByPseudo(pseudo);
        if (!userOpt.isPresent()) {
            logger.warn("Utilisateur non trouvé pour le pseudo : {}", pseudo);
            return false;
        }

        User user = userOpt.get();
        boolean isPasswordCorrect = BCrypt.checkpw(password, user.getPassword());
        if (isPasswordCorrect) {
            logger.info("Authentification réussie pour : {}", pseudo);
        } else {
            logger.warn("Mot de passe incorrect pour : {}", pseudo);
        }
        return isPasswordCorrect;
    }

    @Override
    public Optional<User> findByPseudo(String pseudo) {
        logger.info("Recherche de l'utilisateur via service pour le pseudo : {}", pseudo);
        return userDao.findByPseudo(pseudo);
    }
}