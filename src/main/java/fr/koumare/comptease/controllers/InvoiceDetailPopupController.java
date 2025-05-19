package fr.koumare.comptease.controllers;

import fr.koumare.comptease.model.Article;
import fr.koumare.comptease.model.Client;
import fr.koumare.comptease.model.Invoice;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.property.ReadOnlyObjectWrapper;
import fr.koumare.comptease.model.Company;
import fr.koumare.comptease.service.impl.CompanyServiceImpl;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.FileOutputStream;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class InvoiceDetailPopupController {
    @FXML private Label invoiceIdLabel;
    @FXML private Label invoiceDateLabel;
    @FXML private Label clientLabel;
    @FXML private Label statusLabel;
    @FXML private Label typeLabel;
    @FXML private Label descriptionLabel;
    @FXML private TableView<Article> articlesTable;
    @FXML private TableColumn<Article, String> descCol;
    @FXML private TableColumn<Article, Integer> qtyCol;
    @FXML private TableColumn<Article, Double> unitCol;
    @FXML private TableColumn<Article, Double> totalCol;
    @FXML private Label totalLabel;
    @FXML private Label companyNameLabel;
    @FXML private Label companyAddressLabel;
    @FXML private Label companySiretLabel;
    @FXML private Label companyRibLabel;
    @FXML private Label companyTvaLabel;
    @FXML private Label companyPhoneLabel;
    @FXML private Label companyEmailLabel;
    @FXML private Label companyCapitalLabel;
    @FXML private Label clientNameLabel;
    @FXML private Label clientAddressLabel;
    @FXML private Label clientSiretLabel;
    @FXML private Label clientRibLabel;
    @FXML private Label clientEmailLabel;
    @FXML private Label clientPhoneLabel;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private Invoice currentInvoice;
    ObservableList<Article> articles;

    public void setInvoice(Invoice invoice) {
        this.currentInvoice = invoice;
        invoiceIdLabel.setText(String.valueOf(invoice.getId()));
        invoiceDateLabel.setText(invoice.getDate() != null ? dateFormatter.format(invoice.getDate().atZone(ZoneId.systemDefault())) : "");
        Client client = invoice.getClient();
        clientLabel.setText(client != null ? client.getFirstName() + " " + client.getLastName() : "-");
        statusLabel.setText(invoice.getStatus() != null ? invoice.getStatus().toString() : "");
        typeLabel.setText(invoice.getType() != null ? invoice.getType().toString() : "");
        descriptionLabel.setText(invoice.getDescription());


        articles = FXCollections.observableArrayList();
        if (invoice.getArticles() != null && !invoice.getArticles().isEmpty()) {
            articles.addAll(invoice.getArticles());
        } else {
            // pas d'artcile un message par defaut
            Article defaultArticle = new Article();
            defaultArticle.setDescription("Aucun article associé");
            defaultArticle.setQuantite(0);
            defaultArticle.setPrice(0.0);
            articles.add(defaultArticle);
        }

        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantite"));
        unitCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        totalCol.setCellValueFactory(cellData -> {
            Article article = cellData.getValue();
            double total = article.getPrice() * article.getQuantite();
            return new ReadOnlyObjectWrapper<>(total);
        });
        articlesTable.setItems(articles);
        double total = articles.stream().mapToDouble(a -> a.getPrice() * a.getQuantite()).sum();
        totalLabel.setText(String.format("%.2f €", total));


        CompanyServiceImpl companyService = new CompanyServiceImpl();
        Company company = companyService.getCompanyInformations();
        if (company != null) {
            companyNameLabel.setText(company.getCompanyName() != null ? company.getCompanyName() : "");
            companyAddressLabel.setText(company.getAddress() != null ? company.getAddress() : "");
            companySiretLabel.setText(company.getSiret() != null ? "SIRET : " + company.getSiret() : "");
            companyRibLabel.setText(company.getRib() != null ? "RIB : " + company.getRib() : "");
            companyTvaLabel.setText(company.getTvaNumber() != null ? "TVA : " + company.getTvaNumber() : "");
            companyPhoneLabel.setText(company.getPhone() != null ? "Tél : " + company.getPhone() : "");
            companyEmailLabel.setText(company.getEmail() != null ? "Email : " + company.getEmail() : "");
            companyCapitalLabel.setText(company.getCapitalSocial() != null ? "Capital : " + company.getCapitalSocial() + " €" : "");
        } else {
            companyNameLabel.setText("");
            companyAddressLabel.setText("");
            companySiretLabel.setText("");
            companyRibLabel.setText("");
            companyTvaLabel.setText("");
            companyPhoneLabel.setText("");
            companyEmailLabel.setText("");
            companyCapitalLabel.setText("");
        }

        if (client != null) {
            clientNameLabel.setText(client.getFirstName() + " " + client.getLastName());
            clientAddressLabel.setText(client.getAdresse() != null ? client.getAdresse() : "");
            clientSiretLabel.setText(client.getSiret() != null ? "SIRET : " + client.getSiret() : "");
            clientRibLabel.setText(client.getRib() != null ? "RIB : " + client.getRib() : "");
            clientEmailLabel.setText(client.getContact() != null ? "Email : " + client.getContact() : "");
            clientPhoneLabel.setText("");
        } else {
            clientNameLabel.setText("");
            clientAddressLabel.setText("");
            clientSiretLabel.setText("");
            clientRibLabel.setText("");
            clientEmailLabel.setText("");
            clientPhoneLabel.setText("");
        }
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) totalLabel.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleExportPDF() {
        if (currentInvoice == null) return;
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer la facture en PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers PDF", "*.pdf"));
        fileChooser.setInitialFileName("facture_" + currentInvoice.getId() + ".pdf");
        File file = fileChooser.showSaveDialog(totalLabel.getScene().getWindow());
        if (file == null) return;
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();

            document.add(new Paragraph("FACTURE", com.itextpdf.text.FontFactory.getFont(com.itextpdf.text.FontFactory.HELVETICA_BOLD, 22)));
            document.add(new Paragraph(" "));

            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(100);
            PdfPCell cellEntreprise = new PdfPCell();
            cellEntreprise.setBorder(0);
            cellEntreprise.addElement(new Paragraph(companyNameLabel.getText()));
            cellEntreprise.addElement(new Paragraph(companyAddressLabel.getText()));
            cellEntreprise.addElement(new Paragraph(companySiretLabel.getText()));
            cellEntreprise.addElement(new Paragraph(companyRibLabel.getText()));
            cellEntreprise.addElement(new Paragraph(companyTvaLabel.getText()));
            cellEntreprise.addElement(new Paragraph(companyPhoneLabel.getText()));
            cellEntreprise.addElement(new Paragraph(companyEmailLabel.getText()));
            cellEntreprise.addElement(new Paragraph(companyCapitalLabel.getText()));
            PdfPCell cellClient = new PdfPCell();
            cellClient.setBorder(0);
            cellClient.addElement(new Paragraph(clientNameLabel.getText()));
            cellClient.addElement(new Paragraph(clientAddressLabel.getText()));
            cellClient.addElement(new Paragraph(clientSiretLabel.getText()));
            cellClient.addElement(new Paragraph(clientRibLabel.getText()));
            cellClient.addElement(new Paragraph(clientEmailLabel.getText()));
            cellClient.addElement(new Paragraph(clientPhoneLabel.getText()));
            infoTable.addCell(cellEntreprise);
            infoTable.addCell(cellClient);
            document.add(infoTable);
            document.add(new Paragraph(" "));
            // les facture de la facture
            document.add(new Paragraph("Numéro : " + invoiceIdLabel.getText()));
            document.add(new Paragraph("Date : " + invoiceDateLabel.getText()));
            document.add(new Paragraph("Statut : " + statusLabel.getText()));
            document.add(new Paragraph("Type : " + typeLabel.getText()));
            document.add(new Paragraph("Description : " + descriptionLabel.getText()));
            document.add(new Paragraph(" "));
            // les articles
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.addCell("Description");
            table.addCell("Quantité");
            table.addCell("Prix unitaire");
            table.addCell("Total");
            for (Article article : currentInvoice.getArticles()) {
                table.addCell(article.getDescription());
                table.addCell(String.valueOf(article.getQuantite()));
                table.addCell(String.format("%.2f €", article.getPrice()));
                table.addCell(String.format("%.2f €", article.getPrice() * article.getQuantite()));
            }
            document.add(table);
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Total général : " + totalLabel.getText(), com.itextpdf.text.FontFactory.getFont(com.itextpdf.text.FontFactory.HELVETICA_BOLD, 14)));
            document.close();
            showAlert("Export PDF", "La facture a été exportée avec succès !");
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de l'export PDF : " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}