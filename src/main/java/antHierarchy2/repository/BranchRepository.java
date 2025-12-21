package antHierarchy2.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import antHierarchy2.dto.OrgNodeDTO;
import antHierarchy2.model.Branch;


public class BranchRepository  extends GenericRepository<Branch, String> {

    public BranchRepository(EntityManagerFactory emf) {
        super(Branch.class, emf);
    }

    // Add custom queries here if needed
    

    /**
     * Finds all branches belonging to a specific company by its ID.
     *
     * @param companyId The ID of the company whose branches to fetch.
     * @return List of Branch entities belonging to that company.
     */
    public List<OrgNodeDTO> findRootBranches() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                "SELECT new antHierarchy2.dto.OrgNodeDTO(b.id, b.name, b.address, 'branch', b.company.code) " +
                "FROM Branch b WHERE b.parent IS NULL", OrgNodeDTO.class)
                .getResultList();
        } finally {
            em.close();
        }
    }

    public List<OrgNodeDTO> findChildren(String parentId) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                "SELECT new antHierarchy2.dto.OrgNodeDTO(b.id, b.name, b.address, 'branch', b.company.code) " +
                "FROM Branch b WHERE b.parent.id = :pid", OrgNodeDTO.class)
                .setParameter("pid", parentId)
                .getResultList();
        } finally {
            em.close();
        }
    }

    public List<OrgNodeDTO> findByCompanyId(String companyId) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                "SELECT new antHierarchy2.dto.OrgNodeDTO(b.id, b.name, b.address, 'branch', b.company.code) " +
                "FROM Branch b WHERE b.company.id = :cid", OrgNodeDTO.class)
                .setParameter("cid", companyId)
                .getResultList();
        } finally {
            em.close();
        }
    }
}

