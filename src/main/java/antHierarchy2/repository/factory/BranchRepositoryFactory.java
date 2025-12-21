package antHierarchy2.repository.factory;

import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;

import org.glassfish.hk2.api.Factory;

import antHierarchy2.model.Branch;
import antHierarchy2.repository.BranchRepository;
import antHierarchy2.repository.EmployeeRepository;
import antHierarchy2.repository.GenericRepository;


public class BranchRepositoryFactory  implements Factory<BranchRepository> {
    private final EntityManagerFactory emf;

    @Inject
    public BranchRepositoryFactory(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public BranchRepository provide() {
        return new BranchRepository(emf); // must have a constructor
    }

    @Override
    public void dispose(BranchRepository instance) {}
}


