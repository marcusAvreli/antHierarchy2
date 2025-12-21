package antHierarchy2.repository;

import javax.persistence.EntityManagerFactory;

import antHierarchy2.model.Company;


public class CompanyRepository  extends GenericRepository<Company, String> {

    public CompanyRepository(EntityManagerFactory emf) {
        super(Company.class, emf);
    }

    // Add custom queries here if needed
}
