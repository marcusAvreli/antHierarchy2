package antHierarchy2.repository;

import javax.persistence.*;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import antHierarchy2.model.BaseEntity;



public class GenericRepository<T extends BaseEntity, ID> {

    private static final Logger logger = LogManager.getLogger(GenericRepository.class);

    private final Class<T> entityClass;
    public EntityManagerFactory emf;

    public GenericRepository(Class<T> entityClass, EntityManagerFactory emf) {
        this.entityClass = entityClass;
        this.emf = emf;
    }

    public T findById(ID id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(entityClass, id);
        } finally {
            em.close();
        }
    }

    public List<T> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT e FROM " + entityClass.getSimpleName() + " e", entityClass)
                     .getResultList();
        } finally {
            em.close();
        }
    }

    public void save(T entity) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(entity);
         //   em.merge(entity);
            em.flush();
         //   em.refresh(entity);
            tx.commit();
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public void delete(ID id) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            T entity = em.getReference(entityClass, id);
            em.remove(entity);
            tx.commit();
        } catch (EntityNotFoundException e) {
            if (tx.isActive()) tx.rollback();
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}