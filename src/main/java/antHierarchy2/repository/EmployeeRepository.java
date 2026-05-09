package antHierarchy2.repository;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import antHierarchy2.dto.OrgNodeDTO;
import antHierarchy2.model.Employee;
import antHierarchy2.resources.HelloResource;

public class EmployeeRepository extends GenericRepository<Employee, String> {
	private static final Logger logger = LogManager.getLogger(EmployeeRepository.class);

	public EmployeeRepository(EntityManagerFactory emf) {
		super(Employee.class, emf);
	}

	/**
	 * Managerial mode: find top-level employees (no manager)
	 */
	
	public List<OrgNodeDTO> findRootManagers() {
	    EntityManager em = emf.createEntityManager();
	    try {
	        // 1) Fetch root manager Employees (entity query)
	        String jpql =
	            "SELECT e " +
	            "FROM Employee e " +
	            "JOIN Company c ON e.companyCode = c.companyCode " +
	            "WHERE e.companyCode = :companyCode " +
	            "  AND e.title IN (:titles)";

	        List<Employee> employees = em.createQuery(jpql, Employee.class)
	            .setParameter("companyCode", "001")
	            .setParameter("titles", java.util.List.of("President"))
	            .getResultList();

	        // 2) Map to DTOs (shallow)
	        List<OrgNodeDTO> result = employees.stream()
	            .map(this::toOrgNode)
	            .collect(java.util.stream.Collectors.toList());

	        if (result.isEmpty()) {
	            return result;
	        }

	        // 3) Compute children counts in one grouped query
	        Set<String> managerIds = result.stream()
	            .map(OrgNodeDTO::getId)
	            .collect(java.util.stream.Collectors.toSet());

	        List<Object[]> rows = em.createQuery(
	                "SELECT m.id, COUNT(c) " +
	                "FROM Employee c " +
	                "JOIN c.manager m " +
	                "WHERE m.id IN :managerIds " +
	                "GROUP BY m.id",
	                Object[].class
	            )
	            .setParameter("managerIds", managerIds)
	            .getResultList();

	        Map<String, Long> childrenCount = new HashMap<>();
	        for (Object[] row : rows) {
	            childrenCount.put((String) row[0], (Long) row[1]);
	        }

	        // 4) Apply hasChildren flags + numberOfChildren
	        for (OrgNodeDTO node : result) {
	            long cnt = childrenCount.getOrDefault(node.getId(), 0L);
	            node.setNumberOfChildren((int) cnt);
	            node.setHasChildren(cnt > 0);
	            node.setChildrenLoaded(false); // keep your semantics
	        }

	        return result;
	    } finally {
	        em.close();
	    }
	}
	/**
	 * Managerial mode: find employees reporting to manager
	 */
public List<OrgNodeDTO> findByManagerId(String parentId) {
    logger.info("findByManagerId:" + parentId);
    EntityManager em = emf.createEntityManager();
    List<OrgNodeDTO> result = new ArrayList<>();

    try {
        List<Employee> employees = em.createQuery(
                "SELECT e " +
                "FROM Employee e " +
                "WHERE e.manager.id = :parentId",
                Employee.class
        )
        .setParameter("parentId", parentId)
        .getResultList();

        result = employees.stream()
                .map(this::toOrgNode)
                .collect(Collectors.toList());

        if (result != null && !result.isEmpty()) {
            boolean firstRun = true;
            List<String> parentPath = null;

            for (OrgNodeDTO orgNode : result) {
                String id = orgNode.getId();
                if (firstRun) {
                    parentPath = buildParentPath(id);
                    firstRun = false;
                }
                if (parentPath != null && !parentPath.isEmpty()) {
                    orgNode.setParentPath(parentPath);
                }
            }

            Set<String> managerIds = result.stream()
                    .map(OrgNodeDTO::getId)
                    .collect(Collectors.toSet());

            List<Object[]> rows = em.createQuery(HAS_CHILDREN_HQL, Object[].class)
                    .setParameter("managerIds", managerIds)
                    .getResultList();

            Map<String, Long> childrenCount = new HashMap<>();
            for (Object[] row : rows) {
                childrenCount.put((String) row[0], (Long) row[1]);
            }

            Employee parentEmployee = em.createQuery(
                    "SELECT e FROM Employee e WHERE e.id = :parentId",
                    Employee.class
            )
            .setParameter("parentId", parentId)
            .getSingleResult();

            OrgNodeDTO parent = null;
            if (parentEmployee != null) {
                parent = toOrgNode(parentEmployee);

                if (result != null && !result.isEmpty()) {
                    int numberOfChildren = result.size();
                    parent.setNumberOfChildren(numberOfChildren);
                    parent.setHasChildren(true);
                } else {
                    parent.setHasChildren(false);
                    parent.setNumberOfChildren(0);
                }
            }

            for (OrgNodeDTO node : result) {
                Long cnt = childrenCount.get(node.getId());
                if (cnt != null && cnt > 0) {
                    node.setHasChildren(true);
                    node.setNumberOfChildren(cnt.intValue());
                    node.setChildrenLoaded(false);
                } else {
                    node.setHasChildren(false);
                    node.setNumberOfChildren(0);
                }

               // node.setParentId(parentId);
                node.setManagerId(parentId);
              //  node.setParent(parent);
            }
        }

        return result;
    } finally {
        em.close();
    }
}
	public List<String> buildParentPath(String parentId) {
		logger.info("findByManagerId:" + parentId);
		EntityManager em = emf.createEntityManager();
		try {
			String sql = "WITH RECURSIVE parent_chain AS (" + "" + "        		    SELECT"
					+ "        		        e.id,\r\n" + "        		        e.name,\r\n"
					+ "        		        e.firstname,\r\n" + "        		        e.lastname,\r\n"
					+ "        		        e.title,\r\n" + "        		        e.manager_id,\r\n"
					+ "        		        e.company_code,\r\n" + "        		        e.company_name,\r\n"
					+ "        		        e.teudat_zehut,\r\n" + "        		        e.gender,\r\n"
					+ "        		        e.birthday,\r\n" + "        		        e.job_begin_date,\r\n"
					+ "        		        e.email,\r\n" + "        		        CASE\r\n"
					+ "        		            WHEN EXISTS (\r\n"
					+ "        		                SELECT 1 FROM employee c WHERE c.manager_id = e.id\r\n"
					+ "        		            ) THEN true ELSE false\r\n"
					+ "        		        END AS has_children,\r\n" + "        		        0 AS depth\r\n"
					+ "        		    FROM employee e\r\n" + "        		    WHERE e.id = :parentId"

					+ "        		    UNION ALL\r\n"

					+ "        		    SELECT\r\n" + "        		        m.id,\r\n"
					+ "        		        m.name,\r\n" + "        		        m.firstname,\r\n"
					+ "        		        m.lastname,\r\n" + "        		        m.title,\r\n"
					+ "        		        m.manager_id,\r\n" + "        		        m.company_code,\r\n"
					+ "        		        m.company_name,\r\n" + "        		        m.teudat_zehut,\r\n"
					+ "        		        m.gender,\r\n" + "        		        m.birthday,\r\n"
					+ "        		        m.job_begin_date,\r\n" + "        		        m.email,\r\n"
					+ "        		        CASE\r\n" + "        		            WHEN EXISTS (\r\n"
					+ "        		                SELECT 1 FROM employee c WHERE c.manager_id = m.id\r\n"
					+ "        		            ) THEN true ELSE false\r\n"
					+ "        		        END AS has_children,\r\n" + "        		        p.depth + 1\r\n"
					+ "        		    FROM employee m\r\n"
					+ "        		    JOIN parent_chain p ON m.id = p.manager_id\r\n" + "        		)\r\n"
					+ "        		SELECT  parent_chain.id,depth" + "        		FROM parent_chain"
					+ "  			where depth <> 0" + "        		ORDER BY depth DESC";

			@SuppressWarnings("unchecked")
			List<Object[]> results = em.createNativeQuery(sql).setParameter("parentId", parentId).getResultList();

			List<OrgNodeDTO> result = new ArrayList<>();
			List<String> parentPath = new ArrayList<>();

			// build path first (root → parent)
			for (Object[] r : results) {
				parentPath.add((String) r[0]); // id
			}

			// result.add(dto);

			return parentPath;

		} finally {
			em.close();
		}
	}

	/**
	 * OrgUnit mode: group employees by org unit
	 */
	public List<OrgNodeDTO> findByOrgUnit(String orgUnitCode) {
		EntityManager em = emf.createEntityManager();
		try {
			return em
					.createQuery(
							"SELECT new antHierarchy2.dto.OrgNodeDTO(e.id, e.name, e.title, 'orgunit', e.companyCode) "
									+ "FROM Employee e WHERE e.orgUnitCode = :orgUnitCode",
							OrgNodeDTO.class)
					.setParameter("orgUnitCode", orgUnitCode).getResultList();
		} finally {
			em.close();
		}
	}

	/**
	 * Contract mode: group employees by contract_code
	 */
	public List<OrgNodeDTO> findByContractCode(String contractCode) {
		EntityManager em = emf.createEntityManager();
		try {
			return em
					.createQuery(
							"SELECT new antHierarchy2.dto.OrgNodeDTO(e.id, e.name, e.title, 'contract', e.companyCode) "
									+ "FROM Employee e WHERE e.contractCode = :code",
							OrgNodeDTO.class)
					.setParameter("code", contractCode).getResultList();
		} finally {
			em.close();
		}
	}

	/**
	 * Cost center mode: group employees by cost center
	 */
	public List<OrgNodeDTO> findByCostCenter(String costCenter) {
		EntityManager em = emf.createEntityManager();
		try {
			return em.createQuery(
					"SELECT new antHierarchy2.dto.OrgNodeDTO(e.id, e.name, e.title, 'costcenter', e.companyCode) "
							+ "FROM Employee e WHERE e.costCenter = :cc",
					OrgNodeDTO.class).setParameter("cc", costCenter).getResultList();
		} finally {
			em.close();
		}
	}

	public List<OrgNodeDTO> searchEmployeesWithParents(String emailSearch) {
		EntityManager em = emf.createEntityManager();
		try {
			// 1️⃣ Find matching employees by email
			String jpql = "SELECT new antHierarchy2.dto.OrgNodeDTO("
					+ " e.id, e.name, e.firstName, e.lastName, e.title, "
					+ " 'employee', e.companyCode, e.companyName, " + " e.manager.companyCode, e.manager.companyName, "
					+ " e.manager.id, e.orgUnitCode, e.costCenter, e.contractCode, "
					+ " e.email, e.phoneNumber, e.position, e.jobKey, e.jobName, e.image, false) " + "FROM Employee e "
					+ "LEFT JOIN e.manager m " + "WHERE LOWER(e.email) LIKE :email";

			List<OrgNodeDTO> matches = em.createQuery(jpql, OrgNodeDTO.class)
					.setParameter("email", "%" + emailSearch.toLowerCase() + "%").getResultList();

			// 2️⃣ Collect all parent IDs recursively
			Set<String> parentIds = new HashSet<>();
			for (OrgNodeDTO node : matches) {
				String managerId = node.getManagerId();
				while (managerId != null && !parentIds.contains(managerId)) {
					parentIds.add(managerId);

					// fetch the manager's manager
					OrgNodeDTO manager = em.createQuery(
							"SELECT new antHierarchy2.dto.OrgNodeDTO("
									+ " m.id, m.name, m.firstName, m.lastName, m.title, "
									+ " 'employee', m.companyCode, m.companyName, "
									+ " m.manager.companyCode, m.manager.companyName, "
									+ " m.manager.id, m.orgUnitCode, m.costCenter, m.contractCode, "
									+ " m.email, m.phoneNumber, m.position, m.jobKey, m.jobName, m.image, false) "
									+ "FROM Employee m " + "LEFT JOIN m.manager mm " + "WHERE m.id = :id",
							OrgNodeDTO.class).setParameter("id", managerId).getSingleResult();

					if (manager != null) {
						managerId = manager.getManagerId();
						matches.add(manager);
					} else {
						managerId = null;
					}
				}
			}

			// 3️⃣ Remove duplicates
			Map<String, OrgNodeDTO> map = new LinkedHashMap<>();
			for (OrgNodeDTO node : matches) {
				map.put(node.getId(), node);
			}

			return new ArrayList<>(map.values());
		} finally {
			em.close();
		}
	}

	public List<OrgNodeDTO> searchEmployeesWithParentsSingleQuery(String emailSearch) {
		EntityManager em = emf.createEntityManager();
		try {
			String sql = "WITH RECURSIVE parent_path AS (" + "    SELECT " + "e.id," + " e.name," + " e.firstName,"
					+ " e.lastName," + " e.title," + " e.manager_id," + " e.company_Code," + " e.company_Name,"
					+ "  e.teudat_zehut, " + "  e.gender, " + "  e.birthday, " + "  e.job_begin_date, " + " e.email "
					+ "    FROM employee e " + "    WHERE LOWER(e.teudat_zehut) LIKE :teudatZehut " + "    UNION ALL "
					+ "    SELECT " + "m.id," + "m.name," + "m.firstName," + "m.lastName," + "m.title,"
					+ "m.manager_id," + "m.company_Code," + "m.company_Name," + "  m.teudat_zehut, " + "  m.gender, "
					+ "  m.birthday, " + "  m.job_begin_date, " + "m.email " + "    FROM employee m "
					+ "    INNER JOIN parent_path p ON m.id = p.manager_id" + ")" + "SELECT * FROM parent_path";

			@SuppressWarnings("unchecked")
			List<Object[]> results = em.createNativeQuery(sql).setParameter("teudatZehut", "%" + emailSearch + "%")
					.getResultList();

			List<OrgNodeDTO> nodes = new ArrayList<>();
			for (Object[] row : results) {
				OrgNodeDTO dto = new OrgNodeDTO();
				dto.setId((String) row[0]);
				dto.setName((String) row[1]);
				dto.setFirstName((String) row[2]);
				dto.setLastName((String) row[3]);
				dto.setTitle((String) row[4]);
				dto.setManagerId((String) row[5]);
				dto.setCompanyCode((String) row[6]);
				dto.setCompanyName((String) row[7]);
				dto.setTeudatZehut((String) row[8]);

				dto.setGender((String) row[9]);
				if (null != row[10]) {
					dto.setBirthday((Long) ((BigInteger) row[10]).longValue());
				}
				if (null != row[11]) {
					dto.setJobBeginDate((Long) ((BigInteger) row[11]).longValue());
				}

				dto.setEmail((String) row[12]);
				nodes.add(dto);
			}

			return nodes;
		} finally {
			em.close();
		}
	}

	public List<OrgNodeDTO> searchByRangeThreeLevels() {
		EntityManager em = emf.createEntityManager();
		List<OrgNodeDTO> nodes = new ArrayList<>();
		try {
			String sql = "WITH RECURSIVE\r\n" + "\r\n" + "\r\n" + "root AS (\r\n" + "    SELECT\r\n"
					+ "        e.id,\r\n" + "        e.name,\r\n" + "        e.firstName,\r\n"
					+ "        e.lastName,\r\n" + "        e.title,\r\n" + "        e.manager_id,\r\n"
					+ "        e.company_code,\r\n" + "        c.company_name,\r\n" + "  e.teudat_zehut, "
					+ "  e.gender, " + "  e.birthday, " + "  e.job_begin_date, "
					+ "        pc.company_code AS company_parent_id,\r\n"
					+ "        pc.company_name AS company_parent_name,\r\n" + "        e.email,\r\n"
					+ "        0 AS depth\r\n" + "    FROM employee e\r\n"
					+ "    LEFT JOIN company c ON c.company_code = e.company_code\r\n"
					+ "    LEFT JOIN company pc ON pc.id = c.parent_id\r\n"
					+ "    WHERE e.id = '1b9807e674fa4ae08ffb87ee83a5329d'             \r\n" + "),\r\n" + "\r\n"
					+ "-- move upward to managers (3 levels)\r\n" + "ancestors AS (\r\n" + "    SELECT * FROM root\r\n"
					+ "    UNION ALL\r\n" + "    SELECT\r\n" + "        m.id,\r\n" + "        m.name,\r\n"
					+ "        m.firstName,\r\n" + "        m.lastName,\r\n" + "        m.title,\r\n"
					+ "        m.manager_id,\r\n" + "        m.company_code,\r\n" + "        c.company_name,\r\n"
					+ "  m.teudat_zehut, " + "  m.gender, " + "  m.birthday, " + "  m.job_begin_date, "
					+ "        pc.company_code AS company_parent_id,\r\n"
					+ "        pc.company_name AS company_parent_name,\r\n" + "        m.email,\r\n"
					+ "        a.depth - 1\r\n" + "    FROM employee m\r\n"
					+ "    JOIN ancestors a ON m.id = a.manager_id\r\n"
					+ "    LEFT JOIN company c ON c.company_code = m.company_code\r\n"
					+ "    LEFT JOIN company pc ON pc.id = c.parent_id\r\n"
					+ "    WHERE a.depth > -3                     \r\n" + "),\r\n" + "\r\n"
					+ "-- move downward to subordinates (3 levels)\r\n" + "descendants AS (\r\n"
					+ "    SELECT * FROM root\r\n" + "    UNION ALL\r\n" + "    SELECT\r\n" + "        e2.id,\r\n"
					+ "        e2.name,\r\n" + "        e2.firstName,\r\n" + "        e2.lastName,\r\n"
					+ "        e2.title,\r\n" + "        e2.manager_id,\r\n" + "        e2.company_code,\r\n"
					+ "        c.company_name,\r\n" + "  e2.teudat_zehut, " + "  e2.gender, " + "  e2.birthday, "
					+ "  e2.job_begin_date, " + "        pc.company_code AS company_parent_id,\r\n"
					+ "        pc.company_name AS company_parent_name,\r\n" + "        e2.email,\r\n"
					+ "        d.depth + 1\r\n" + "    FROM employee e2\r\n"
					+ "    JOIN descendants d ON e2.manager_id = d.id\r\n"
					+ "    LEFT JOIN company c ON c.company_code = e2.company_code\r\n"
					+ "    LEFT JOIN company pc ON pc.id = c.parent_id\r\n"
					+ "    WHERE d.depth < 3                 \r\n" + "),\r\n" + "\r\n" + "combined AS (\r\n"
					+ "    SELECT * FROM ancestors\r\n" + "    UNION\r\n" + "    SELECT * FROM descendants\r\n"
					+ ")\r\n" + "\r\n" + "SELECT DISTINCT *\r\n" + "FROM combined\r\n"
					+ "WHERE depth BETWEEN -3 AND 3\r\n" + "ORDER BY depth, lastName, firstName;\r\n" + "";

			@SuppressWarnings("unchecked")
			List<Object[]> results = em.createNativeQuery(sql)
					// .setParameter("email", "%"+emailSearch+"%")
					.getResultList();

			for (Object[] row : results) {
				OrgNodeDTO dto = new OrgNodeDTO();
				dto.setId((String) row[0]);
				dto.setName((String) row[1]);
				dto.setFirstName((String) row[2]);
				dto.setLastName((String) row[3]);
				dto.setTitle((String) row[4]);
				dto.setManagerId((String) row[5]);
				dto.setCompanyCode((String) row[6]);

				dto.setCompanyName((String) row[7]);
				dto.setParentCompanyCode((String) row[8]);
				dto.setParentCompanyName((String) row[9]);
				dto.setEmail((String) row[10]);
				nodes.add(dto);
			}
			return nodes;
		} finally {
			em.close();
		}
	}

	public List<OrgNodeDTO> searchByRange() {
		EntityManager em = emf.createEntityManager();
		List<OrgNodeDTO> nodes = new ArrayList<>();
		try {
			String sql = "\r\n" + "WITH RECURSIVE\r\n" + "\r\n" + "root_base AS (\r\n" + "    SELECT\r\n"
					+ "        e.id,\r\n" + "        e.name,\r\n" + "        e.firstName,\r\n"
					+ "        e.lastName,\r\n" + "        e.title,\r\n" + "        e.manager_id,\r\n"
					+ "        e.company_code,\r\n" + "        c.company_name AS company_name,\r\n"
					+ "  e.teudat_zehut, " + "  e.gender, " + "  e.birthday, " + "  e.job_begin_date, "
					+ "        CASE WHEN pc.company_code IS NOT NULL THEN pc.company_code ELSE '' END as company_parent_id ,\r\n"
					+ "        CASE WHEN pc.company_name IS NOT NULL THEN pc.company_name ELSE '' END as company_parent_name, \r\n"
					+ "        e.email,\r\n" + "        0 AS depth\r\n" + "    FROM employee e\r\n"
					+ "    LEFT JOIN company c ON c.company_code = e.company_code\r\n"
					+ "    LEFT JOIN company pc ON pc.id = c.parent_id\r\n"
					+ "    WHERE e.id = '64b6d7755a2a4cc1bd1e7a610c47b750'\r\n" + "),\r\n" + "\r\n"
					+ "ancestors AS (\r\n" + "    SELECT * FROM root_base\r\n" + "    UNION ALL\r\n" + "    SELECT\r\n"
					+ "        m.id,\r\n" + "        m.name,\r\n" + "        m.firstName,\r\n"
					+ "        m.lastName,\r\n" + "        m.title,\r\n" + "        m.manager_id,\r\n"
					+ "        m.company_code,\r\n" + "        c.company_name AS company_name,\r\n"
					+ "  m.teudat_zehut, " + "  m.gender, " + "  m.birthday, " + "  m.job_begin_date, "
					+ "       CASE WHEN pc.company_code IS NOT NULL THEN pc.company_code ELSE '' END as company_parent_id ,\r\n"
					+ "        CASE WHEN pc.company_name IS NOT NULL THEN pc.company_name ELSE '' END as company_parent_name, \r\n"
					+ "        m.email,\r\n" + "        a.depth - 1\r\n" + "    FROM employee m\r\n"
					+ "    JOIN ancestors a ON m.id = a.manager_id\r\n"
					+ "    LEFT JOIN company c ON c.company_code = m.company_code\r\n"
					+ "    LEFT JOIN company pc ON pc.id = c.parent_id\r\n" + "),\r\n" + "\r\n" + "descendants AS (\r\n"
					+ "    SELECT * FROM root_base\r\n" + "    UNION ALL\r\n" + "    SELECT\r\n"
					+ "        cemp.id,\r\n" + "        cemp.name,\r\n" + "        cemp.firstName,\r\n"
					+ "        cemp.lastName,\r\n" + "        cemp.title,\r\n" + "        cemp.manager_id,\r\n"
					+ "        cemp.company_code,\r\n" + "        c.company_name AS company_name,       \r\n"
					+ "  cemp.teudat_zehut, " + "  cemp.gender, " + "  cemp.birthday, " + " cemp.job_begin_date, "
					+ "        CASE WHEN pc.company_code IS NOT NULL THEN pc.company_code ELSE '' END as company_parent_id ,\r\n"
					+ "        CASE WHEN pc.company_name IS NOT NULL THEN pc.company_name ELSE '' END as company_parent_name, \r\n"
					+ "       \r\n" + "        cemp.email,\r\n" + "        d.depth + 1\r\n"
					+ "    FROM employee cemp\r\n" + "    JOIN descendants d ON cemp.manager_id = d.id\r\n"
					+ "    LEFT JOIN company c ON c.company_code = cemp.company_code\r\n"
					+ "    LEFT JOIN company pc ON pc.id = c.parent_id\r\n" + "    WHERE d.depth + 1 <= 1\r\n"
					+ "),\r\n" + "\r\n" + "combined AS (\r\n" + "    SELECT * FROM ancestors\r\n" + "    UNION\r\n"
					+ "    SELECT * FROM descendants\r\n" + ")\r\n" + "\r\n" + "SELECT DISTINCT *\r\n"
					+ "FROM combined\r\n" + "WHERE depth BETWEEN 0 AND 1\r\n" + "ORDER BY depth, id;\r\n" + "";
			@SuppressWarnings("unchecked")
			List<Object[]> results = em.createNativeQuery(sql)
					// .setParameter("email", "%"+emailSearch+"%")
					.getResultList();

			for (Object[] row : results) {
				OrgNodeDTO dto = new OrgNodeDTO();
				dto.setId((String) row[0]);
				dto.setName((String) row[1]);
				dto.setFirstName((String) row[2]);
				dto.setLastName((String) row[3]);
				dto.setTitle((String) row[4]);
				dto.setManagerId((String) row[5]);
				dto.setCompanyCode((String) row[6]);

				dto.setCompanyName((String) row[7]);
				dto.setParentCompanyCode((String) row[12]);
				dto.setParentCompanyName((String) row[13]);

				dto.setEmail((String) row[14]);
				nodes.add(dto);
			}
			return nodes;
		} finally {
			em.close();
		}
	}

	public OrgNodeDTO getByTeudatZehut(String teudatZehut) {
		EntityManager em = emf.createEntityManager();
		List<OrgNodeDTO> orgNodes = new ArrayList<OrgNodeDTO>();
		try {

			String sql = "select name" + ",firstName" + ",lastName" + ",manager_id" + ",teudat_zehut" + ",id "
					+ "from employee where teudat_zehut=:teudat_zehut";
			@SuppressWarnings("unchecked")
			Object[] results = (Object[]) em.createNativeQuery(sql).setParameter("teudat_zehut", teudatZehut)
					.getSingleResult();

			OrgNodeDTO dto = new OrgNodeDTO();
			dto.setName((String) results[0]);
			dto.setFirstName((String) results[1]);
			dto.setLastName((String) results[2]);
			dto.setManagerId((String) results[3]);
			dto.setTeudatZehut((String) results[4]);
			dto.setId((String) results[5]);
			orgNodes.add(dto);

			return dto;
		} finally {
			em.close();
		}
	}

	private String HQL_QUERY = "SELECT new antHierarchy2.dto.OrgNodeDTO(\r\n"

			+ "e.id,\r\n" + "e.name,\r\n" + "e.firstName,\r\n" + "e.lastName,\r\n" + "e.title,\r\n" + "'employee',\r\n"
			+ "e.companyCode,\r\n" + "c.companyName,\r\n" + "e.teudatZehut,\r\n" + "e.gender,\r\n" + "e.birthday,\r\n"
			+ "e.jobBeginDate,\r\n" + "pc.companyCode,\r\n" + "pc.companyName,\r\n" + "e.branch.id,\r\n"
			+ "e.manager.id,\r\n" + "e.orgUnitCode,\r\n" + "e.costCenter,\r\n" + "e.contractCode,\r\n" + "e.email,\r\n"
			+ "e.phoneNumber,\r\n" + "e.position,\r\n" + "e.jobKey,\r\n" + "e.jobName,\r\n" + "e.image,\r\n"
			+ "FALSE,\r\n" + "FALSE"

			+ ")\r\n" + "FROM Employee e\r\n" + "LEFT JOIN Company c  ON c.companyCode = e.companyCode\r\n"
			+ "LEFT JOIN Company pc ON pc.id = c.parent.id\r\n" + "WHERE e.manager.id IN :managerIds";

	private static final String HAS_CHILDREN_HQL = "SELECT e.manager.id, COUNT(e.id) " + "FROM Employee e "
			+ "WHERE e.manager.id IN :managerIds " + "GROUP BY e.manager.id";

	public List<OrgNodeDTO> loadEmployeesDownTreeHql(String rootId, int levels) {
		EntityManager em = emf.createEntityManager();
		Map<String, OrgNodeDTO> result = new LinkedHashMap<>();

		/*
		 * OrgNodeDTO rootNode = em.createQuery(
		 * "SELECT new antHierarchy2.dto.OrgNodeDTO(" + "e.id," + "e.name," +
		 * "e.firstName," + "e.lastName," + "e.title," + "'employee'," +
		 * "e.companyCode," + "c.companyName," + "e.teudatZehut," + "e.gender," +
		 * "e.birthday," + "e.jobBeginDate," + "pc.companyCode," + "pc.companyName," +
		 * "e.branch.id," + "e.manager.id," + "e.orgUnitCode," + "e.costCenter," +
		 * "e.contractCode," + "e.email," + "e.phoneNumber," + "e.position," +
		 * "e.jobKey," + "e.jobName," + "e.image," + "FALSE," + "FALSE" + ")" +
		 * "FROM Employee e " + "LEFT JOIN Company c ON c.companyCode = e.companyCode "
		 * + "LEFT JOIN Company pc ON pc.id = c.parent.id " + "WHERE e.id = :rootId",
		 * OrgNodeDTO.class) .setParameter("rootId", rootId) .getSingleResult();
		 */

		Employee rootEmployee = em
				.createQuery(
						"SELECT e FROM Employee e " + "LEFT JOIN Company c ON c.companyCode = e.companyCode "
								+ "LEFT JOIN Company pc ON pc.id = c.parent.id " + "WHERE e.id = :rootId",
						Employee.class)
				.setParameter("rootId", rootId).getSingleResult();
		OrgNodeDTO rootNode = toOrgNode(rootEmployee);
		// add root to result map
		result.put(rootNode.getId(), rootNode);

		logger.info("passed_root_node" + result);

		// current level managers
		Set<String> currentManagers = new HashSet<String>();
		currentManagers.add(rootId);

		// parent path tracking
		Map<String, List<String>> parentPaths = new HashMap<>();
		parentPaths.put(rootNode.getId(), List.of());
		parentPaths.put(rootId, List.of());

		for (int depth = 1; depth <= levels; depth++) {
			if (currentManagers.isEmpty())
				break;

			List<Employee> tempEmployees = em.createQuery(
					"SELECT e FROM Employee e " + "LEFT JOIN Company c ON c.companyCode = e.companyCode "
							+ "LEFT JOIN Company pc ON pc.id = c.parent.id " + "WHERE e.manager.id IN :managerIds",
					Employee.class).setParameter("managerIds", currentManagers).getResultList();
			List<OrgNodeDTO> level = new ArrayList<OrgNodeDTO>();
			for (Employee tempEmployee : tempEmployees) {
				OrgNodeDTO tempNode = toOrgNode(tempEmployee);

				level.add(tempNode);
			}
			logger.info("passed_iteration_node" + level);
			currentManagers = new HashSet<>();
			logger.info("checkPost_1");
			for (OrgNodeDTO node : level) {
				// ----- parent path -----
				logger.info("checkPost_2");
				List<String> parentPath = new ArrayList<>(parentPaths.get(node.getManagerId()));
				parentPath.add(node.getManagerId());
				logger.info("checkPost_3");
				node.setParentPath(parentPath);
				node.setNumberOfParents(parentPath.size());
				logger.info("checkPost_4");
				// ----- hierarchy flags -----
				node.setHasChildren(true); // assume true, verified later
				node.setChildrenLoaded(false);
				//node.setChildren(new ArrayList<>());
				node.setChildrenIds(new ArrayList<>());
				logger.info("checkPost_5");
				result.put(node.getId(), node);
				logger.info("checkPost_6");
				parentPaths.put(node.getId(), parentPath);
				logger.info("checkPost_7");
				currentManagers.add(node.getId());
				logger.info("checkPost_8");
			}
		}

		logger.info("checkPost_9");

		// ---------- post-processing ----------
		// count children & childrenIds
		Map<String, List<OrgNodeDTO>> groupedByManager = result.values().stream().filter(n -> n.getManagerId() != null)
				.collect(Collectors.groupingBy(OrgNodeDTO::getManagerId));
		logger.info("checkPost_10");
		for (OrgNodeDTO node : result.values()) {
			List<OrgNodeDTO> kids = groupedByManager.get(node.getId());
			if (kids != null) {
				node.setNumberOfChildren(kids.size());
				/*
				 * node.setChildrenIds( kids.stream().map(OrgNodeDTO::getId).toList() );
				 */
				kids.stream().map(OrgNodeDTO::getId).collect(Collectors.toList());
			} else {
				node.setHasChildren(false);
				node.setNumberOfChildren(0);
			}
		}
		logger.info("checkPost_11");
		Set<String> deepestNodeIds = new HashSet<>(currentManagers);
		deepestNodeIds.add(rootId);
		if (!deepestNodeIds.isEmpty()) {
			logger.info("checkPost_12");
			List<Object[]> rows = em.createQuery(HAS_CHILDREN_HQL, Object[].class)
					.setParameter("managerIds", deepestNodeIds).getResultList();

			// map: managerId -> real children count
			Map<String, Long> realChildrenCount = new HashMap<>();
			for (Object[] row : rows) {
				realChildrenCount.put((String) row[0], (Long) row[1]);
			}
			logger.info("checkPost_13");
			// apply to deepest nodes only
			for (String nodeId : deepestNodeIds) {
				OrgNodeDTO node = result.get(nodeId);
				if (node == null)
					continue;

				Long cnt = realChildrenCount.get(nodeId);
				if (cnt != null && cnt > 0) {
					node.setHasChildren(true);
					node.setNumberOfChildren(cnt.intValue());
					node.setChildrenLoaded(false); // important: children exist but not loaded
				} else {
					node.setHasChildren(false);
					node.setNumberOfChildren(0);
				}
			}
			logger.info("checkPost_14");
		}
		return new ArrayList<>(result.values());
	}
	public List<OrgNodeDTO> getDivisions() {
	    logger.info("getDivisions_started");

	    List<OrgNodeDTO> result = new ArrayList<>();
	    EntityManager em = emf.createEntityManager();

	    try {
	        // 1) manager is null -> single result
	        Employee rootEmployee = em.createQuery(
	                "SELECT e FROM Employee e WHERE e.manager IS NULL",
	                Employee.class
	            )
	            .getSingleResult();

	        result.add(toOrgNode(rootEmployee));

	        // 2) teudatZehut in (...) -> multiple results
	        List<String> teudatZehutList = Arrays.asList(
	            "802285098",
	            "964164628",
	            "674338900",
	            "569503055",
	            "402999213",
	            "771265378"
	        );

	        List<Employee> employees = em.createQuery(
	                "SELECT e FROM Employee e WHERE e.teudatZehut IN :ids",
	                Employee.class
	            )
	            .setParameter("ids", teudatZehutList)
	            .getResultList();

	        result.addAll(
	            employees.stream()
	                .map(this::toOrgNode)
	                .collect(Collectors.toList())
	        );

	        return result;
	    } finally {
	        em.close();
	    }
	}
	
	public List<OrgNodeDTO> getEmployeesUpToRoot(String rootId, String searchedValue, String field) {
		logger.info("getEmployeesUpToRoot repository /getEmployeesUpToRoot?scope={} searchedValue={} field={}", rootId, searchedValue,field);
		EntityManager em = emf.createEntityManager();
		Map<String, OrgNodeDTO> allNodes = new LinkedHashMap<>();

		try {

// ⚠ IMPORTANT: whitelist dynamic fields
			if (!List.of("costCenter", "id", "firstName", "lastName", "title", "email","name").contains(field)) {
				throw new IllegalArgumentException("Invalid search field");
			}

// Step 1: fetch starting employees
			List<Employee> starters = em
					.createQuery("SELECT e FROM Employee e WHERE e." + field + " = :val", Employee.class)
					.setParameter("val", searchedValue).getResultList();

// Step 2: collect employees + managers (scoped)
			for (Employee e : starters) {

				if (rootId != null && !isUnderRoot(e, rootId)) {
					continue; // skip employees outside subtree
				}

				addToFlatMapScoped(e, allNodes, rootId);
			}

// Step 3: build parentPath (respect scope boundary)
			for (OrgNodeDTO node : allNodes.values()) {
				List<String> path = new ArrayList<>();
				String parentId = node.getManagerId();

				while (parentId != null) {

					path.add(parentId);

					if (rootId != null && parentId.equals(rootId)) {
						break; // stop at scope boundary
					}

					OrgNodeDTO parentNode = allNodes.get(parentId);
					if (parentNode == null)
						break;

					parentId = parentNode.getManagerId();
				}

				Collections.reverse(path);
				node.setParentPath(path);
			}

// Step 4: children count
			if (!allNodes.isEmpty()) {

				List<Object[]> rows = em
						.createQuery("SELECT e.manager.id, COUNT(e) " + "FROM Employee e "
								+ "WHERE e.manager.id IN :ids " + "GROUP BY e.manager.id", Object[].class)
						.setParameter("ids", allNodes.keySet()).getResultList();

				Map<String, Long> childrenCount = new HashMap<>();
				for (Object[] row : rows) {
					childrenCount.put((String) row[0], (Long) row[1]);
				}

				for (OrgNodeDTO node : allNodes.values()) {
					Long cnt = childrenCount.get(node.getId());
					node.setHasChildren(cnt != null && cnt > 0);
					node.setNumberOfChildren(cnt != null ? cnt.intValue() : 0);
				}
			}

// Step 5: shallow parent object assignment (unchanged)
			for (OrgNodeDTO node : allNodes.values()) {
				String parentId = node.getManagerId();
				if (parentId != null) {
					OrgNodeDTO parent = allNodes.get(parentId);
					if (parent != null) {

						OrgNodeDTO shallow = new OrgNodeDTO();
						shallow.setId(parent.getId());
						shallow.setName(parent.getName());
						shallow.setFirstName(parent.getFirstName());
						shallow.setLastName(parent.getLastName());
						shallow.setTitle(parent.getTitle());
						shallow.setType(parent.getType());
						shallow.setCostCenter(parent.getCostCenter());

						//node.setParent(shallow);
					}
				}
			}

			return new ArrayList<>(allNodes.values());

		} finally {
			em.close();
		}
	}

	private OrgNodeDTO addToFlatMapScoped(Employee e, Map<String, OrgNodeDTO> map, String rootId) {

		if (e == null)
			return null;

		OrgNodeDTO node = map.get(e.getId());
		if (node != null)
			return node;

		node = toOrgNode(e);
		map.put(e.getId(), node);

// STOP climbing if we reached scope root
		if (rootId != null && e.getId().equals(rootId)) {
			return node;
		}

		Employee manager = e.getManager();
		if (manager != null) {
			OrgNodeDTO managerNode = addToFlatMapScoped(manager, map, rootId);
			//node.setParentId(managerNode.getId());
			node.setManagerId(managerNode.getId());
		}

		return node;
	}
	
	private boolean isUnderRoot(Employee employee, String rootId) {

	    Employee current = employee;

	    while (current != null) {
	        if (current.getId().equals(rootId)) {
	            return true;
	        }
	        current = current.getManager();
	    }

	    return false;
	}

	private OrgNodeDTO addToFlatMap(Employee e, Map<String, OrgNodeDTO> map) {
		if (e == null)
			return null;

		OrgNodeDTO node = map.get(e.getId());
		if (node != null)
			return node;

		node = toOrgNode(e);
		map.put(e.getId(), node);

		Employee manager = e.getManager();
		if (manager != null) {
			OrgNodeDTO managerNode = addToFlatMap(manager, map);
			//node.setParentId(managerNode.getId()); // only store parentId
			node.setManagerId(managerNode.getId());
			// parent object will be assigned later as shallow copy
		}

		return node;
	}

	/////PROJECTION_SEARCH_START
	
	public List<OrgNodeDTO> searchProjectedTreeByFullName(String divisionRootId, String fullNameTerm) {

	    if (divisionRootId == null || divisionRootId.trim().isEmpty()) {
	        return Collections.emptyList();
	    }

	    String normalizedTerm = normalize(fullNameTerm);
	    if (normalizedTerm.isEmpty()) {
	        return Collections.emptyList();
	    }

	    EntityManager em = emf.createEntityManager();

	    try {
	        Employee divisionRoot = em.find(Employee.class, divisionRootId);
	        if (divisionRoot == null) {
	            return Collections.emptyList();
	        }

	        // =========================================================
	        // 1️⃣ MATCHES
	        // =========================================================
	        List<Employee> matchedEmployees = em.createQuery(
	                "SELECT e FROM Employee e " +
	                "WHERE LOWER(CONCAT(e.firstName, ' ', e.lastName)) LIKE :term",
	                Employee.class
	        )
	        .setParameter("term", "%" + normalizedTerm + "%")
	        .getResultList();

	        if (matchedEmployees.isEmpty()) {
	            OrgNodeDTO rootDto = toOrgNode(divisionRoot);
	            markScopeRoot(rootDto);
	            return Collections.singletonList(rootDto);
	        }

	        Map<String, Employee> employeeById = new LinkedHashMap<>();
	        Map<String, String> parentMap = new HashMap<>();

	        for (Employee e : matchedEmployees) {
	            employeeById.put(e.getId(), e);
	        }

	     // =========================================================
	     // 2️⃣ BATCH UPWARD TRAVERSAL (FIXED)
	     // =========================================================
	     Set<String> frontier = matchedEmployees.stream()
	             .map(Employee::getId)
	             .collect(Collectors.toSet());

	     Set<String> processed = new HashSet<>();

	     while (!frontier.isEmpty()) {

	         // remove already processed
	         Set<String> batch = frontier.stream()
	                 .filter(id -> !processed.contains(id))
	                 .collect(Collectors.toSet());

	         if (batch.isEmpty()) break;

	         processed.addAll(batch);

	         List<Object[]> rows = em.createQuery(
	                 "SELECT child.id, parent.id " +
	                 "FROM Employee child " +
	                 "JOIN child.manager parent " +
	                 "WHERE child.id IN :ids",
	                 Object[].class
	         )
	         .setParameter("ids", batch)
	         .getResultList();

	         Set<String> nextFrontier = new HashSet<>();

	         for (Object[] row : rows) {
	             String childId = (String) row[0];
	             String parentId = (String) row[1];

	             if (parentId == null) continue;

	             // ✅ ALWAYS record relation
	             parentMap.put(childId, parentId);

	             // ✅ ALWAYS ensure parent is loaded
	             if (!employeeById.containsKey(parentId)) {
	                 Employee parent = em.find(Employee.class, parentId);
	                 if (parent != null) {
	                     employeeById.put(parentId, parent);
	                 }
	             }

	             // ✅ ALWAYS continue climbing
	             if (!processed.contains(parentId)) {
	                 nextFrontier.add(parentId);
	             }
	         }

	         frontier = nextFrontier;
	     }

	        // =========================================================
	        // 3️⃣ FILTER ONLY PATHS THAT REACH ROOT
	        // =========================================================
	        Map<String, OrgNodeDTO> dtoById = new LinkedHashMap<>();

	        for (Employee e : employeeById.values()) {
	            List<String> parentPath = buildParentPath(e.getId(), divisionRootId, parentMap);

	            if (parentPath.isEmpty() && !divisionRootId.equals(e.getId())) {
	                //continue;
	            	 // allow nodes that still have a parent in chain
	                if (!parentMap.containsKey(e.getId())) {
	                    continue;
	                }
	            }

	            OrgNodeDTO dto = toOrgNode(e);

	            dto.setParentPath(parentPath);
	            dto.setNumberOfParents(parentPath.size());

	            dto.setInSearchResult(true);
	            dto.setPathNode(true);

	            dtoById.put(dto.getId(), dto);
	        }
	     // =========================================================
	     // 🔥 GUARANTEE ROOT PRESENCE (CRITICAL FIX)
	     // =========================================================
	        logger.info("divisionRootId:"+divisionRootId);
	        if (!dtoById.containsKey(divisionRootId)) {
	            OrgNodeDTO rootDto = toOrgNode(divisionRoot);

	            rootDto.setParentPath(Collections.emptyList());
	            rootDto.setNumberOfParents(0);

	            rootDto.setInSearchResult(true);
	            rootDto.setPathNode(true);
	            rootDto.setScopeRoot(true);

	            dtoById.put(rootDto.getId(), rootDto);
	        }
	     // =========================================================
	     // 🔥 ENSURE ROOT CONNECTION (CRITICAL FIX)
	     // =========================================================
	     Set<String> rootChildrenIds = parentMap.entrySet().stream()
	             .filter(e -> divisionRootId.equals(e.getValue()))
	             .map(Map.Entry::getKey)
	             .collect(Collectors.toSet());

	     for (String childId : rootChildrenIds) {
	         if (!dtoById.containsKey(childId)) continue;

	         OrgNodeDTO child = dtoById.get(childId);

	         // 🔥 FORCE correct linkage
	         child.setManagerId(divisionRootId);
	     }
	        // =========================================================
	        // 4️⃣ MARK MATCHES
	        // =========================================================
	        Set<String> matchedIds = matchedEmployees.stream()
	                .map(Employee::getId)
	                .collect(Collectors.toSet());

	        for (String id : matchedIds) {
	            OrgNodeDTO dto = dtoById.get(id);
	            if (dto != null) {
	                dto.setFound(true);
	                dto.setHighlighted(true);
	            }
	        }

	        // =========================================================
	        // 5️⃣ CHILD COUNT (ONLY METADATA)
	        // =========================================================
	        Map<String, Long> counts = getRealChildCounts(em, dtoById.keySet());

	        for (OrgNodeDTO dto : dtoById.values()) {
	            int count = counts.getOrDefault(dto.getId(), 0L).intValue();

	            dto.setHasChildren(count > 0);
	            dto.setNumberOfChildren(count);

	            dto.setChildrenLoaded(false);
	            dto.setSearchFiltered(false);
	            dto.setShowAllAvailable(false);
	            dto.setPartiallyLoaded(false);

	            if (divisionRootId.equals(dto.getId())) {
	                markScopeRoot(dto);
	               // dto.setManagerId(null);
	            }
	        }
	     // =========================================================
	     // 🔥 FORCE SINGLE ROOT (FINAL GUARANTEE FOR d3)
	     // =========================================================
	     OrgNodeDTO rootDto = dtoById.get(divisionRootId);
	     if (rootDto != null) {
	    	 
	    	 logger.info("FORCE SINGLE ROOT");
	         rootDto.setManagerId(null);
	         rootDto.setParentPath(Collections.emptyList());
	         rootDto.setNumberOfParents(0);
	         rootDto.setScopeRoot(true);
	     }
	        return new ArrayList<>(dtoById.values());

	    } finally {
	        em.close();
	    }
	}
	private List<String> buildParentPath(
	        String employeeId,
	        String divisionRootId,
	        Map<String, String> parentMap
	) {
	    LinkedList<String> path = new LinkedList<>();

	    String current = parentMap.get(employeeId);

	    while (current != null) {

	        if (divisionRootId.equals(current)) {
	            return path; // valid path
	        }

	        path.addFirst(current);
	        current = parentMap.get(current);
	    }

	    return Collections.emptyList(); // not in division
	}
	private List<String> getPathToDivisionRoot(
		    String employeeId,
		    String divisionRootId,
		    Map<String, String> parentIdByEmployeeId
		) {
		    if (employeeId == null || divisionRootId == null) {
		        return Collections.emptyList();
		    }

		    LinkedList<String> path = new LinkedList<>();
		    Set<String> visited = new HashSet<>();

		    String currentId = employeeId;

		    while (currentId != null && visited.add(currentId)) {
		        path.addFirst(currentId);

		        if (divisionRootId.equals(currentId)) {
		            return path;
		        }

		        currentId = parentIdByEmployeeId.get(currentId);
		    }

		    return Collections.emptyList();
		}

		private Map<String, Long> getRealChildCounts(EntityManager em, Collection<String> managerIds) {
		    if (managerIds == null || managerIds.isEmpty()) {
		        return Collections.emptyMap();
		    }

		    return em.createQuery(
		            "SELECT e.manager.id, COUNT(e) " +
		            "FROM Employee e " +
		            "WHERE e.manager.id IN :ids " +
		            "GROUP BY e.manager.id",
		            Object[].class
		        )
		        .setParameter("ids", managerIds)
		        .getResultStream()
		        .collect(Collectors.toMap(
		            row -> (String) row[0],
		            row -> (Long) row[1],
		            (a, b) -> a,
		            LinkedHashMap::new
		        ));
		}

		private int countChildren(EntityManager em, String managerId) {
		    if (managerId == null || managerId.trim().isEmpty()) {
		        return 0;
		    }

		    Long count = em.createQuery(
		            "SELECT COUNT(e) FROM Employee e WHERE e.manager.id = :managerId",
		            Long.class
		        )
		        .setParameter("managerId", managerId)
		        .getSingleResult();

		    return count == null ? 0 : count.intValue();
		}

		private void markScopeRoot(OrgNodeDTO dto) {
		    if (dto == null) {
		        return;
		    }

		    dto.setScopeRoot(true);
		    dto.setPathNode(true);
		    dto.setInSearchResult(true);
		    /*
		    dto.setManagerId(null);
		    dto.setParentPath(Collections.emptyList());
		    dto.setNumberOfParents(0);
		    */
		}

		private List<OrgNodeDTO> buildChildrenList(
		    List<String> childIds,
		    Map<String, OrgNodeDTO> dtoById
		) {
		    if (childIds == null || childIds.isEmpty()) {
		        return Collections.emptyList();
		    }

		    List<OrgNodeDTO> result = new ArrayList<>();

		    for (String childId : childIds) {
		        OrgNodeDTO child = dtoById.get(childId);
		        if (child != null) {
		            result.add(child);
		        }
		    }

		    return result;
		}

		private void computeFoundCountsBottomUp(
		    String rootId,
		    Map<String, List<String>> childrenIdsByParentId,
		    Map<String, OrgNodeDTO> dtoById
		) {
		    postOrderCount(rootId, childrenIdsByParentId, dtoById, new HashSet<>());
		}

		private int postOrderCount(
		    String nodeId,
		    Map<String, List<String>> childrenIdsByParentId,
		    Map<String, OrgNodeDTO> dtoById,
		    Set<String> visiting
		) {
		    if (nodeId == null || !dtoById.containsKey(nodeId)) {
		        return 0;
		    }

		    if (!visiting.add(nodeId)) {
		        return 0;
		    }

		    OrgNodeDTO dto = dtoById.get(nodeId);
		    int count = dto.isFound() ? 1 : 0;

		    for (String childId : childrenIdsByParentId.getOrDefault(nodeId, Collections.emptyList())) {
		        count += postOrderCount(childId, childrenIdsByParentId, dtoById, visiting);
		    }

		    dto.setFoundCountInSubtree(count);
		    visiting.remove(nodeId);

		    return count;
		}

	
	
	


		
		private int countChildren(String managerId) {
		    if (managerId == null || managerId.trim().isEmpty()) {
		        return 0;
		    }

		    EntityManager em = emf.createEntityManager();
		    try {
		        Long count = em.createQuery(
		                "SELECT COUNT(e) FROM Employee e WHERE e.manager.id = :managerId",
		                Long.class
		            )
		            .setParameter("managerId", managerId)
		            .getSingleResult();

		        return count == null ? 0 : count.intValue();
		    } finally {
		        em.close();
		    }
		}
		private String normalize(String value) {
		    return safe(value).trim().toLowerCase();
		}

		private String safe(String value) {
		    return value == null ? "" : value;
		}
	///PROJECTION_SEARCH_END
	
	
	
	
	
	
	
	
	
	
	
	private OrgNodeDTO toOrgNode(Employee e) {
		OrgNodeDTO dto = new OrgNodeDTO();

		dto.setId(e.getId());
		dto.setName(e.getName());
		dto.setFirstName(e.getFirstName());
		dto.setLastName(e.getLastName());
		dto.setFullName(e.getName());
		dto.setCostCenter(e.getCostCenter());
		dto.setPositionPlansDesc("positionPlansTest "+e.getFirstName());
		dto.setOrgunitOrgehDesc("orgunitOrgehDesc "+ e.getLastName());
		dto.setEmail(e.getEmail());
		dto.setPhoneNumber(e.getPhoneNumber());
		dto.setTitle(e.getTitle());
		dto.setGender(e.getGender());
		Employee manager = e.getManager();
		if (null != manager) {
			dto.setManagerId(manager.getId());
		}else {
			dto.setManagerId(null);
		}
		dto.setType("managerial");
		dto.setHasChildren(false);
		dto.setNumberOfChildren(0);
		dto.setChildrenLoaded(false);

		return dto;
	}

}