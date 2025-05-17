
package fr.koumare.comptease.dao;

import fr.koumare.comptease.model.Devis;
import fr.koumare.comptease.utilis.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class DevisDao {

    private static final Logger logger = LoggerFactory.getLogger(DevisDao.class);

    public void saveDevis(Devis devis) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(devis);
            transaction.commit();
            logger.info("Devis sauvegardé avec succès : ID {}", devis.getId());
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Erreur lors de la sauvegarde du devis : {}", e.getMessage());
            e.printStackTrace();
        }
    }

    public List<Devis> getAllDevis() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Devis> devisList = session.createQuery("from Devis", Devis.class).list();
            logger.info("Récupération de {} devis", devisList.size());
            return devisList;
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des devis : {}", e.getMessage());
            e.printStackTrace();
            return new ArrayList<>(); // Retourner une liste vide en cas d'erreur
        }
    }

    public void updateDevis(Devis devis) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(devis);
            transaction.commit();
            logger.info("Devis mis à jour avec succès : ID {}", devis.getId());
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Erreur lors de la mise à jour du devis : {}", e.getMessage());
            e.printStackTrace();
        }
    }

    public void deleteDevis(Long devisId) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Devis devis = session.get(Devis.class, devisId);
            if (devis != null) {
                session.delete(devis);
                logger.info("Devis supprimé avec succès : ID {}", devisId);
            } else {
                logger.warn("Devis non trouvé pour suppression : ID {}", devisId);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Erreur lors de la suppression du devis ID {} : {}", devisId, e.getMessage());
            e.printStackTrace();
        }
    }

    public Devis findDevisById(Long devisId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Devis devis = session.get(Devis.class, devisId);
            if (devis != null) {
                logger.info("Devis trouvé : ID {}", devisId);
            } else {
                logger.warn("Devis non trouvé : ID {}", devisId);
            }
            return devis;
        } catch (Exception e) {
            logger.error("Erreur lors de la recherche du devis ID {} : {}", devisId, e.getMessage());
            e.printStackTrace();
            return null; // Retourner null en cas d'erreur
        }
    }
}