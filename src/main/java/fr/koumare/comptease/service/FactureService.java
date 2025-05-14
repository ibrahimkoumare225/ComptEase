package fr.koumare.comptease.service;
import fr.koumare.comptease.model.Invoice;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import fr.koumare.comptease.model.Article;
import java.util.List;
import java.time.Instant;


public interface FactureService extends DocumentService {
    boolean createInvoice(Double prix, String description, Instant date, String status, Long clientId, List<Article> article, String type, int quantity);
    void updateInvoiceStatus(Long invoiceId, String status);
    List<Invoice> getAllFactures();
    ObservableList<Article>getAllArticles();



}