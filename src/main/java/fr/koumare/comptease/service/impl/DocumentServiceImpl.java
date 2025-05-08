package fr.koumare.comptease.service.impl;

import fr.koumare.comptease.model.Document;
import fr.koumare.comptease.service.DocumentService;

public abstract class DocumentServiceImpl implements DocumentService {

    @Override
    public Document createDocument(Document document) {

        return document;
    }

    @Override
    public Document findDocumentById(Long id) {

        return null;
    }
    @Override
    public void deleteDocumentById(Long id){

    }
    public void updateDocument(Document document){

    }

    @Override
    public void generatePDF(Document document) {

        //System.out.println("Génération PDF pour le document : " + document.getId());
    }
}