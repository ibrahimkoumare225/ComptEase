package fr.koumare.comptease.service;

import fr.koumare.comptease.model.Document;

public interface DocumentService {
    Document createDocument(Document document);
    Document findDocumentById(Long id);
    void generatePDF(Document document);
}