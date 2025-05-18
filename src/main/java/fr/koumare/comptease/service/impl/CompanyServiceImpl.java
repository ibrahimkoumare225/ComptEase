package fr.koumare.comptease.service.impl;

import java.time.LocalDate;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import fr.koumare.comptease.dao.CompanyDao;
import fr.koumare.comptease.model.Company;
import fr.koumare.comptease.service.CompanyService;
import fr.koumare.comptease.model.User;

public class CompanyServiceImpl implements CompanyService {

    private static final Logger logger = LoggerFactory.getLogger(CompanyServiceImpl.class);
    private final CompanyDao companyDao;

    public CompanyServiceImpl() {
        this.companyDao = new CompanyDao();
    }

    @Override
    public boolean saveCompany(Company company) {
        if (company == null) {
            return false;
        }
        companyDao.saveCompany(company);
        if(!companyDao.companyExists(company.getId())) {
            logger.error("Erreur lors de la sauvegarde de l'entreprise : " + company.getCompanyName());
            return false;
        } 
        return true;
    }

    @Override
    public Company findCompanyByUser(User user) {
        if (user == null) {
            logger.error("L'utilisateur est null, impossible de trouver l'entreprise.");
            return null;
        }
        return companyDao.findCompanyByUser(user);
    }

    @Override
    public boolean updateCompany(String companyName, String legalForm, String taxRegime, String profession,
                   String salesNature, LocalDate creationDate, LocalDate closingDate, User user,
                   String siret, String rib, String address, String phone, String email,
                   Double capitalSocial, String tvaNumber) {
        Company company = companyDao.getCompanyInformations();

        if(company == null) {
            logger.warn("Aucune entreprise trouvée pour l'utilisateur : " + user.getPseudo());
            return false;
        }
        company.setCompanyName(companyName);
        company.setLegalForm(legalForm);
        company.setTaxRegime(taxRegime);
        company.setProfession(profession);
        company.setSalesNature(salesNature);
        company.setCreationDate(creationDate);
        company.setClosingDate(closingDate);
        company.setSiret(siret);
        company.setRib(rib);
        company.setAddress(address);
        company.setPhone(phone);
        company.setEmail(email);
        company.setCapitalSocial(capitalSocial);
        company.setTvaNumber(tvaNumber);
        
        companyDao.updateCompany(company);
        logger.info("Informations de l'entreprise mises à jour avec succès : " + company.getCompanyName());
        return true;
    }

    @Override
    public Company getCompanyInformations() {
        Company company = companyDao.getCompanyInformations();
        if(company == null) {
            logger.error("Erreur lors de la récupération des informations de l'entreprise.");
            return null;
        }
        logger.info("Informations de l'entreprise récupérées avec succès : " + company.getCompanyName());
        return company;
    }

    
}