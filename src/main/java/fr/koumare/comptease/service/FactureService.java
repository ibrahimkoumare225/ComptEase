package fr.koumare.comptease.service;
import fr.koumare.comptease.model.Invoice;
import javafx.collections.ObservableList;
import fr.koumare.comptease.model.Article;
import java.util.List;
import java.time.Instant;


public interface FactureService  {
    boolean addInvoice(String description, Instant date, String status, Long clientId, List<Article> article, String type, int quantity);
//    void updateInvoiceStatus(Long invoiceId, String status);
    ObservableList<Invoice> getAllFactures();
    boolean updateArticle(Long id,String description, List<String> category, int quantite, Double price);
    boolean enregistrerArticle(Article article);
//    boolean deleteFactureById(Long id);
    ObservableList<Article>getAllArticles();



}