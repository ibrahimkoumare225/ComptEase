package fr.koumare.comptease.dao;

import fr.koumare.comptease.model.User;
import fr.koumare.comptease.utilis.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.List;

public class UserDao {

    private static final Logger logger = LoggerFactory.getLogger(UserDao.class);

    public void saveUser(User user) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(user);
            transaction.commit();
            logger.info("Utilisateur sauvegardé : {}", user.getPseudo());
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Erreur lors de la sauvegarde de l'utilisateur : {}", user.getPseudo(), e);
            e.printStackTrace();
        }
    }

    public Optional<User> findByPseudo(String pseudo) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            logger.info("Recherche de l'utilisateur avec pseudo : {}", pseudo);
            Optional<User> user = session.createQuery("FROM User WHERE pseudo = :pseudo", User.class)
                    .setParameter("pseudo", pseudo)
                    .uniqueResultOptional();
            if (user.isPresent()) {
                logger.info("Utilisateur trouvé : {}", user.get().getPseudo()+" "+user.get().getId());
            } else {
                logger.warn("Aucun utilisateur trouvé pour le pseudo : {}", pseudo);
            }
            return user;
        } catch (Exception e) {
            logger.error("Erreur lors de la recherche de l'utilisateur avec pseudo : {}", pseudo, e);
            return Optional.empty();
        }
    }

    public Optional<User> findByEmail(String email) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM User WHERE email = :email", User.class)
                    .setParameter("email", email)
                    .uniqueResultOptional();
        }
    }

    public boolean authenticateUser(String pseudo, String password) {
        Optional<User> user = findByPseudo(pseudo);
        return user.isPresent() && BCrypt.checkpw(password, user.get().getPassword());
    }

    public void updateUser(User user) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(user);
            transaction.commit();
            logger.info("Utilisateur mis à jour : {}", user.getPseudo());
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Erreur lors de la mise à jour de l'utilisateur : {}", user.getPseudo(), e);
            e.printStackTrace();
        }
    }

    public void deleteUser(Long userId) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            User user = session.get(User.class, userId);
            if (user != null) {
                session.delete(user);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }


    public User getUserById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(User.class, id);
        }
    }

    public List<User> getAllUsers() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM User", User.class).list();
        }
    }

}

