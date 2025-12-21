package antHierarchy2.resources;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



import antHierarchy2.dto.OrgNodeDTO;
import antHierarchy2.repository.BranchRepository;
import antHierarchy2.repository.CompanyRepository;
import antHierarchy2.repository.EmployeeRepository;
import antHierarchy2.util.api.ApiResponse;
import antHierarchy2.util.api.StatusCode;



//http://localhost:8080/pictureGalleryHibernate/api/hello?mode=managerial&parentId=35a1e7327b90492cb7b63cf1a9eb3698
//http://localhost:8080/pictureGalleryHibernate/api/hello?mode=managerial&parentId=4ae58b0f06404df2bf04e8bd9391b4e5
@Path("hello")
public class HelloResource {

	   private static final Logger logger = LogManager.getLogger(HelloResource.class);
	    @Inject
	    private EntityManagerFactory emf;
	   @Inject
	    private CompanyRepository companyRepo;  // ✅ updated
	   
	   @Inject
	    private BranchRepository branchRepo;  // ✅ updated
	   
	   @Inject
	    private EmployeeRepository employeeRepo;  // ✅ updated
	    @Context
	    private javax.servlet.ServletContext servletContext;

	 

	    /**
	     * Supports:
	     *   /api/org?mode=managerial
	     *   /api/org?mode=branch
	     *   /api/org?mode=orgunit
	     *   /api/org?mode=contract
	     *   /api/org?mode=costcenter
	     *   Optional: &parentId=xxxx
	     */
	    @GET
	    @Produces(MediaType.APPLICATION_JSON)
	    public ApiResponse<List<OrgNodeDTO>> getOrganizationTree(
	            @QueryParam("mode") @DefaultValue("managerial") String mode,
	            @QueryParam("parentId") String parentId
	    ) {
	        logger.info("GET /api/org?mode={} parentId={}", mode, parentId);

	        try {
	            List<OrgNodeDTO> result;

	            switch (mode.toLowerCase()) {
	                case "branch":
	                    result = parentId == null ? branchRepo.findRootBranches()
	                                              : branchRepo.findChildren(parentId);
	                    break;
	                case "orgunit":
	                    result = employeeRepo.findByOrgUnit(parentId);
	                    break;
	                case "contract":
	                    result = employeeRepo.findByContractCode(parentId);
	                    break;
	                case "costcenter":
	                    result = employeeRepo.findByCostCenter(parentId);
	                    break;
	                default:
	                    result = parentId == null ? employeeRepo.findRootManagers()
	                                              : employeeRepo.findByManagerId(parentId);
	            }

	            /*ObjectMapper mapper = new ObjectMapper();
	            return Response.ok(mapper.writeValueAsString(result)).build();
	            */
	            logger.info("result"+result);
	            return ApiResponse.success(result);


	        } catch (Exception e) {
	            logger.error("Error retrieving organization structure", e);

	            // Custom error code and message
	            StatusCode errorCode = new StatusCode();
	            errorCode.setErrorCode(1001L); 
	            errorCode.setErrorMessage("Failed to load organization structure");

	            return ApiResponse.error(
	                    Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
	                    Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase(),
	                    errorCode
	            );
	        }
	    }
	    
	    @GET
	    @Path("/search")
	    @Produces(MediaType.APPLICATION_JSON)
	    public ApiResponse search(
	            @QueryParam("mode") @DefaultValue("managerial") String mode,
	            @QueryParam("searchTerm") String searchTerm
	    ) {
	        logger.info("search_method_was_called /api/org?mode={} searchTerm={}", mode, searchTerm);
	        if (searchTerm == null || searchTerm.trim().isEmpty()) {
	            return ApiResponse.success(Collections.<OrgNodeDTO>emptyList());
	        }
	        
	        searchTerm = searchTerm.toLowerCase();
	        List<OrgNodeDTO> allNodes = employeeRepo.searchEmployeesWithParentsSingleQuery(searchTerm); // fetch all nodes (or at least all relevant)

	     // Step 1: Create lookup map
	        logger.info("allNodes:"+allNodes);
	  
	        
	        Map<String, OrgNodeDTO> nodeMap = new HashMap<>();
	        for (OrgNodeDTO node : allNodes) {
	            node.setChildrenIds(new ArrayList<>()); // new field: IDs only
	            nodeMap.put(node.getId(), node);
	        }

	        // Step 2: Compute parentPath (root → parent → ... → node)
	        for (OrgNodeDTO node : allNodes) {
	            List<String> path = new ArrayList<>();
	            String currentManagerId = node.getManagerId();
	            while (currentManagerId != null) {
	                OrgNodeDTO manager = nodeMap.get(currentManagerId);
	                if (manager == null) break;
	                path.add(0, manager.getId());
	                currentManagerId = manager.getManagerId();
	            }
	            node.setParentPath(path);
	        }

	        // Step 3: Build shallow child ID references (no deep embedding)
	        for (OrgNodeDTO node : allNodes) {
	            String managerId = node.getManagerId();
	            if (managerId != null) {
	                OrgNodeDTO parent = nodeMap.get(managerId);
	                if (parent != null) {
	                    parent.getChildrenIds().add(node.getId()); // IDs only
	                    parent.setChildrenLoaded(true);
	                }
	            }
	        }

	        // Step 4: Remove any existing object children to keep output flat
	        for (OrgNodeDTO node : allNodes) {
	            node.setChildren(null); // ensure we don't have recursive references
	        }

	        // Step 4: Return a flat list — no recursive nesting
	        return ApiResponse.success(new ArrayList<>(nodeMap.values()));
	    }
	    
	    
	    @GET
	    @Path("/searchByRange")
	    @Produces(MediaType.APPLICATION_JSON)
	    public ApiResponse searchByRange(
	          
	    ) {
	    	  logger.info("searchByRange_called");// /api/org?mode={} searchTerm={}", mode, searchTerm);
		        /*if (searchTerm == null || searchTerm.trim().isEmpty()) {
		            return ApiResponse.success(Collections.<OrgNodeDTO>emptyList());
		        }
		        
		        searchTerm = searchTerm.toLowerCase();
		        */
		        List<OrgNodeDTO> allNodes = employeeRepo.searchByRange(); // fetch all nodes (or at least all relevant)

		     // Step 1: Create lookup map
		        logger.info("allNodes:"+allNodes);
		  
		        
		        Map<String, OrgNodeDTO> nodeMap = new HashMap<>();
		        for (OrgNodeDTO node : allNodes) {
		            node.setChildrenIds(new ArrayList<>()); // new field: IDs only
		            nodeMap.put(node.getId(), node);
		        }

		        // Step 2: Compute parentPath (root → parent → ... → node)
		        for (OrgNodeDTO node : allNodes) {
		            List<String> path = new ArrayList<>();
		            String currentManagerId = node.getManagerId();
		            while (currentManagerId != null) {
		                OrgNodeDTO manager = nodeMap.get(currentManagerId);
		                if (manager == null) break;
		                path.add(0, manager.getId());
		                currentManagerId = manager.getManagerId();
		            }
		            node.setParentPath(path);
		        }

		        // Step 3: Build shallow child ID references (no deep embedding)
		        for (OrgNodeDTO node : allNodes) {
		            String managerId = node.getManagerId();
		            if (managerId != null) {
		                OrgNodeDTO parent = nodeMap.get(managerId);
		                if (parent != null) {
		                    parent.getChildrenIds().add(node.getId()); // IDs only
		                    parent.setChildrenLoaded(true);
		                }
		            }
		        }

		        // Step 4: Remove any existing object children to keep output flat
		        for (OrgNodeDTO node : allNodes) {
		            node.setChildren(null); // ensure we don't have recursive references
		        }

		        // Step 4: Return a flat list — no recursive nesting
		        return ApiResponse.success(new ArrayList<>(nodeMap.values()));
	    }
	    
	    @GET
	    @Path("/searchByRangeThreeLevels")
	    @Produces(MediaType.APPLICATION_JSON)
	    public ApiResponse searchByRangeThreeLevels( ) {
	    	  logger.info("searchByRange_called");
	    	  
	    	  List<OrgNodeDTO> allNodes = employeeRepo.searchByRangeThreeLevels(); // fetch all nodes (or at least all relevant)

			     // Step 1: Create lookup map
			        logger.info("allNodes:"+allNodes);
			  
			        
			        Map<String, OrgNodeDTO> nodeMap = new HashMap<>();
			        for (OrgNodeDTO node : allNodes) {
			            node.setChildrenIds(new ArrayList<>()); // new field: IDs only
			            nodeMap.put(node.getId(), node);
			        }

			        // Step 2: Compute parentPath (root → parent → ... → node)
			        for (OrgNodeDTO node : allNodes) {
			            List<String> path = new ArrayList<>();
			            String currentManagerId = node.getManagerId();
			            while (currentManagerId != null) {
			                OrgNodeDTO manager = nodeMap.get(currentManagerId);
			                if (manager == null) break;
			                path.add(0, manager.getId());
			                currentManagerId = manager.getManagerId();
			            }
			            node.setParentPath(path);
			        }

			        // Step 3: Build shallow child ID references (no deep embedding)
			        for (OrgNodeDTO node : allNodes) {
			            String managerId = node.getManagerId();
			            if (managerId != null) {
			                OrgNodeDTO parent = nodeMap.get(managerId);
			                if (parent != null) {
			                    parent.getChildrenIds().add(node.getId()); // IDs only
			                    parent.setChildrenLoaded(true);
			                }
			            }
			        }

			        // Step 4: Remove any existing object children to keep output flat
			        for (OrgNodeDTO node : allNodes) {
			            node.setChildren(null); // ensure we don't have recursive references
			        }

			        // Step 4: Return a flat list — no recursive nesting
			        return ApiResponse.success(new ArrayList<>(nodeMap.values()));
	    }
	    		
	    
	   
}