package fr.koumare.comptease.dao;

import fr.koumare.comptease.model.ObligationFiscale;
import fr.koumare.comptease.utilis.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class ObligationFiscaleDao {

    public void saveObligationFiscale(ObligationFiscale obligationFiscale) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(obligationFiscale);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public List<ObligationFiscale> getAllObligationFiscale() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from ObligationFiscale", ObligationFiscale.class).list();
        }
    }

    public void updateObligationFiscale(ObligationFiscale obligationFiscale) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(obligationFiscale);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public void deleteObligationFiscale(Long ObligationFiscaleId) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            ObligationFiscale obligationFiscale = session.get(ObligationFiscale.class, ObligationFiscaleId);
            if (obligationFiscale != null) {
                session.delete(obligationFiscale);
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
