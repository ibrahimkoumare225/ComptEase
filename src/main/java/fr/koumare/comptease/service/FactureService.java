package fr.koumare.comptease.service;
import fr.koumare.comptease.model.Invoice;
import javafx.collections.ObservableList;
import fr.koumare.comptease.model.Article;
import java.util.List;
import java.time.Instant;


public interface FactureService  {
    boolean addInvoice(String description, Instant date, String status, Long clientId, List<Article> article, String type, int quantity/*String descriptionArticle*/);
    boolean updateInvoice(Long id, String description, Instant date, String status, Long clientId, List<Article> article, String type, int quantity, String descriptionArticle);
//    void updateInvoiceStatus(Long invoiceId, String status);
    List<Invoice> getAllInvoices();
    boolean updateArticle(Long id,String description, List<String> category, int quantite, Double price);
    boolean enregistrerArticle(Article article);
    boolean deleteInvoice(Long invoicdId);

    //les fonctions d'ibrahim pour le dashboard
    Double getTotalOutgoingInvoices();
    Double getTotalUnpaidIncomingInvoices();
    Double getTotalPaidIncomingInvoices();
    int getQuantityById(Long invoiceId);



}