package fr.koumare.comptease.service;

import java.time.LocalDate;

import fr.koumare.comptease.model.Company;
import fr.koumare.comptease.model.User;

public interface CompanyService {
    boolean saveCompany(Company company);
    Company findCompanyByUser(User user);
    boolean updateCompany(String companyName, String legalForm, String taxRegime, String profession,
                   String salesNature, LocalDate creationDate, LocalDate closingDate, User user);
    Company getCompanyInformations();

}