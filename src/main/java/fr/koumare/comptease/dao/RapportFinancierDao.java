package fr.koumare.comptease.dao;

import fr.koumare.comptease.model.RapportFinancier;
import fr.koumare.comptease.utilis.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class RapportFinancierDao {

    public void saveRapportFinancier(RapportFinancier rapportFinancier) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(rapportFinancier);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public List<RapportFinancier> getAllRapportFinanciers() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from RapportFinancier", RapportFinancier.class).list();
        }
    }

    public void updateRapportFinancier(RapportFinancier rapportFinancier) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(rapportFinancier);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public void deleteRapportFinancier(Long rapportFinancierId) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            RapportFinancier rapportFinancier = session.get(RapportFinancier.class, rapportFinancierId);
            if (rapportFinancier != null) {
                session.delete(rapportFinancier);
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
