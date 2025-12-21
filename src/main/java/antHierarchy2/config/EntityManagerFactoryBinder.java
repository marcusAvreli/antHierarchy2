package antHierarchy2.config;

import org.glassfish.hk2.api.TypeLiteral;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import antHierarchy2.repository.BranchRepository;
import antHierarchy2.repository.CompanyRepository;
import antHierarchy2.repository.EmployeeRepository;
import antHierarchy2.repository.factory.BranchRepositoryFactory;
import antHierarchy2.repository.factory.CompanyRepositoryFactory;
import antHierarchy2.repository.factory.EmployeeRepositoryFactory;


import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * HK2 Binder to provide EntityManagerFactory as injectable dependency.
 */
public class EntityManagerFactoryBinder extends AbstractBinder {

    private final EntityManagerFactory emf;

    public EntityManagerFactoryBinder() {
        // Initialize once
        this.emf = Persistence.createEntityManagerFactory("myPU");
    }

    @Override
    protected void configure() {
        bind(emf).to(EntityManagerFactory.class);
        
        
        /*
        bindFactory(UserRolePermissionRepositoryFactory.class)
        .to(new TypeLiteral<GenericRepository<UserRolePermission,String>>() {});
        */
        bindFactory(CompanyRepositoryFactory.class)
        .to(CompanyRepository.class);
        
        bindFactory(BranchRepositoryFactory.class)
        .to(BranchRepository.class);
        
        bindFactory(EmployeeRepositoryFactory.class)
        .to(EmployeeRepository.class);
       
    
        
      
    }
}