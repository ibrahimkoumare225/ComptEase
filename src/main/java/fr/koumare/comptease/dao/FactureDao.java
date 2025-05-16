/*package fr.koumare.comptease.dao;

import fr.koumare.comptease.model.Facture;
import fr.koumare.comptease.utilis.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class FactureDao {

    private static final Logger logger = LoggerFactory.getLogger(FactureDao.class);

    public void saveFacture(Facture facture) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(facture);
            transaction.commit();
            logger.info("Facture sauvegardée avec succès : ID {}", facture.getId());
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Erreur lors de la sauvegarde de la facture : {}", e.getMessage());
            e.printStackTrace();
        }
    }

    public List<Facture> getAllFactures() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Facture> invoiceList = session.createQuery("from Facture", Facture.class).list();
            logger.info("Récupération de {} factures", invoiceList.size());
            return invoiceList;
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des factures : {}", e.getMessage());
            e.printStackTrace();
            return new ArrayList<>(); // retourner une liste vide en cas d'erreur
        }
    }

    public void updateFacture(Facture facture) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(facture);
            transaction.commit();
            logger.info("Facture mise à jour avec succès : ID {}", facture.getId());
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Erreur lors de la mise à jour de la facture : {}", e.getMessage());
            e.printStackTrace();
        }
    }

    public void deleteFacture(Long invoiceId) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Facture facture = session.get(Facture.class, invoiceId);
            if (facture != null) {
                session.delete(facture);
                logger.info("Facture supprimée avec succès : ID {}", invoiceId);
            } else {
                logger.warn("Facture non trouvée pour suppression : ID {}", invoiceId);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Erreur lors de la suppression de la facture ID {} : {}", invoiceId, e.getMessage());
            e.printStackTrace();
        }
    }

    public Facture findFactureById(Long factureId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Facture facture = session.get(Facture.class, factureId);

            if (facture != null) {
                logger.info("Facture trouvée : ID {}", factureId);
            } else {
                logger.warn("Facture non trouvée : ID {}", factureId);
            }
            return facture;
        } catch (Exception e) {
            logger.error("Erreur lors de la recherche de la facture ID {} : {}", factureId, e.getMessage());
            e.printStackTrace();
            return null;
        }

        }
    }*/

