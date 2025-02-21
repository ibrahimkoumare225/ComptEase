package fr.koumare.comptease.dao;

import fr.koumare.comptease.utilis.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.management.Notification;
import java.util.List;

public class NotificationDao {

    public void saveNotification(Notification notification) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(notification);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public List<Notification> getAllNotifications() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Notification ", Notification.class).list();
        }
    }

    public void updateNotification(Notification notification) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(notification);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public void deleteNotification(Long notificationId) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Notification notification = session.get(Notification.class, notificationId);
            if (notification != null) {
                session.delete(notification);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }
}
