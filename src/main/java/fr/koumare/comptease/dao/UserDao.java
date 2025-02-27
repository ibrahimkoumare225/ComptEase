package fr.koumare.comptease.dao;

import fr.koumare.comptease.model.User;
import fr.koumare.comptease.utilis.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.Optional;

public class UserDao {

    public void saveUser(User user) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(user);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }
    public Optional<User> findByPseudo(String pseudo) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            System.out.println("Session opened");
            Optional<User> user = session.createQuery("FROM User WHERE pseudo = :pseudo", User.class)
                    .setParameter("pseudo", pseudo)
                    .uniqueResultOptional();
            System.out.println("User found: " + user.orElse(null));
            return user;
        }

    }
    public void updateUser(User user) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(user);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }


}
