package antHierarchy2.repository.factory;

import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;

import org.glassfish.hk2.api.Factory;

import antHierarchy2.model.Company;
import antHierarchy2.model.Employee;
import antHierarchy2.repository.CompanyRepository;
import antHierarchy2.repository.EmployeeRepository;
import antHierarchy2.repository.GenericRepository;

public class EmployeeRepositoryFactory  implements Factory<EmployeeRepository> {
    private final EntityManagerFactory emf;

    @Inject
    public EmployeeRepositoryFactory(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public EmployeeRepository provide() {
        return new EmployeeRepository(emf); // must have a constructor
    }

    @Override
    public void dispose(EmployeeRepository instance) {}
}


