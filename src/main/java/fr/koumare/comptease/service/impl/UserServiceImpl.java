package fr.koumare.comptease.service.impl;

import fr.koumare.comptease.dao.UserDao;
import fr.koumare.comptease.model.User;
import fr.koumare.comptease.service.UserService;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.List;

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

    @Override
    public List<User> getAllUsers() {
        logger.info("Récupération de tous les utilisateurs");
        return userDao.getAllUsers();
    }

    @Override
    public boolean updateUser(String pseudo, String email) {
        logger.info("Mise à jour de l'utilisateur : {}", pseudo);
        Optional<User> userOpt = userDao.findByPseudo(pseudo);
        Optional<User> userOptMail = userDao.findByEmail(email);
        if (!userOpt.isPresent() && !userOptMail.isPresent()) {
            logger.warn("Utilisateur pas trouvé pour la mise à jour : {}");
            return false;
        }
        User user = userOpt.get();
        user.setEmail(email);
        user.setPseudo(pseudo);
        userDao.updateUser(user);
        logger.info("Utilisateur mis à jour avec succès : {}", pseudo);
        return true;
    }

    @Override
    public boolean updateUserPassword(List<User> user, String password) {
        logger.info("Mise à jour du mot de passe pour l'utilisateur : {}");
        Optional<User> userOpt = userDao.findByPseudo(user.get(0).getPseudo());
        Optional<User> userOptMail = userDao.findByEmail(user.get(0).getEmail());
        if (!userOpt.isPresent() && !userOptMail.isPresent()) {
            logger.warn("Utilisateur pas trouvé pour la mise à jour du mot de passe : {}");
            return false;
        }
        logger.info("Utilisateur trouvé pour la mise à jour du mot de passe : {}", user.get(0).getPseudo());
        User userToUpdate = userOpt.get();
        // Hachage du nouveau mot de passe
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        userToUpdate.setPassword(hashedPassword);
        userToUpdate.setPseudo(user.get(0).getPseudo());
        userToUpdate.setEmail(user.get(0).getEmail());
        userDao.updateUser(userToUpdate);
        logger.info("Mot de passe mis à jour avec succès pour : {}", user.get(0).getPseudo());
        return true;
    }
}