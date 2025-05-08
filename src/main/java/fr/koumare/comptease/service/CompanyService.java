package fr.koumare.comptease.service;

import fr.koumare.comptease.model.Company;
import fr.koumare.comptease.model.User;

public interface CompanyService {
    boolean saveCompany(Company company);
    Company findCompanyByUser(User user);

}