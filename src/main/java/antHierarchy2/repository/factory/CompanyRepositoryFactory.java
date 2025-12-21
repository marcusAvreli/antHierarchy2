package antHierarchy2.repository.factory;

import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;

import org.glassfish.hk2.api.Factory;

import antHierarchy2.model.Branch;
import antHierarchy2.model.Company;
import antHierarchy2.repository.CompanyRepository;
import antHierarchy2.repository.GenericRepository;


public class CompanyRepositoryFactory implements Factory<CompanyRepository> {
    private final EntityManagerFactory emf;

    @Inject
    public CompanyRepositoryFactory(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public CompanyRepository provide() {
        return new CompanyRepository(emf); // must have a constructor
    }

    @Override
    public void dispose(CompanyRepository instance) {}
}
