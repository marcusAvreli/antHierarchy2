package antHierarchy2.dto;

import com.fasterxml.jackson.databind.ObjectMapper;

import antHierarchy2.model.Employee;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import com.fasterxml.jackson.annotation.JsonInclude;


import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.ALWAYS)
public class OrgNodeDTO {

    private String id;
    private String name;
    private String firstName;
    private String lastName;
    private String fullName;

    private String teudatZehut;
    private String gender;
    private Long birthday;
    private Long jobBeginDate;
    private String title;

    /**
     * employee, branch, orgunit, contract, costcenter, manager
     */
    private String type;

    private String companyCode;
    private String companyName;
    private String parentCompanyCode;
    private String parentCompanyName;

    private String branchId;

    /**
     * parent id in tree
     */
    private String managerId;

    private String orgUnitCode;
    private String costCenter;
    private int contractCode;
    private String email;
    private String phoneNumber;
    private int positionCode;
    private String jobKey;
    private String jobName;
    private String image;
    private String divisionName;

    /**
     * true when real children were already loaded from REST
     */
    private boolean childrenLoaded = false;

    /**
     * ancestor ids from scope root to current node
     */
    private List<String> parentPath;

    /**
     * currently attached visible children in client response
     * in search mode this may contain only filtered children
     */
    //private List<OrgNodeDTO> children;

    /**
     * ids of currently visible children
     */
    private List<String> childrenIds;

    /**
     * true if node has real children in organization
     */
    private boolean hasChildren = false;

    /**
     * real number of immediate children in organization
     */
    private int numberOfChildren;

    /**
     * number of ancestors up to scope root / top
     */
    private int numberOfParents;

    private String positionPlansDesc;
    private String orgunitOrgehDesc;
    /**
     * true when this node is a manager node in the projected result.
     * In levelUp/levelDown manager projection, only managers are returned
     * for context levels.
     */
    private boolean isManager = false;

    /**
     * id of direct manager group.
     * In Option 2 this is the direct manager id of found node.
     */
    private String searchGroupId;

    /**
     * display name of direct manager group.
     */
    private String searchGroupName;

    /**
     * number of found nodes under this direct manager group.
     */
    private int searchGroupMatchCount;

    /**
     * true only for the direct manager node that represents this group.
     */
    private boolean searchGroupRoot = false;
    /* =========================================================
       Search / partial subtree / show-all flags
       ========================================================= */

    public boolean isManager() {
		return isManager;
	}

	public void setIsManager(boolean isManager) {
		this.isManager = isManager;
	}

	public String getSearchGroupId() {
		return searchGroupId;
	}

	public void setSearchGroupId(String searchGroupId) {
		this.searchGroupId = searchGroupId;
	}

	public String getSearchGroupName() {
		return searchGroupName;
	}

	public void setSearchGroupName(String searchGroupName) {
		this.searchGroupName = searchGroupName;
	}

	public int getSearchGroupMatchCount() {
		return searchGroupMatchCount;
	}

	public void setSearchGroupMatchCount(int searchGroupMatchCount) {
		this.searchGroupMatchCount = searchGroupMatchCount;
	}

	public boolean isSearchGroupRoot() {
		return searchGroupRoot;
	}

	public void setSearchGroupRoot(boolean searchGroupRoot) {
		this.searchGroupRoot = searchGroupRoot;
	}

	/**
     * true when this exact node matched the search term
     * for example fullName == "John Smith"
     */
    private boolean found = false;

    /**
     * true when this node did not match directly,
     * but has at least one descendant that matched
     */
    private boolean hasFoundDescendants = false;

    /**
     * true when this manager directly owns one or more found employees
     * example:
     * Dana Levi
     *   John Smith
     *   John Smith
     */
    private boolean hasDirectFoundChildren = false;

    /**
     * true when current visible subtree is partial,
     * meaning only matching descendant branches are shown
     */
    private boolean searchFiltered = false;

    /**
     * true when not all real immediate children are currently returned
     * and "show all nodes" / expand can load hidden siblings
     */
    private boolean showAllAvailable = false;

    /**
     * true when this node is currently represented as collapsed
     * search result node and not all intermediate subtree is expanded
     */
    private boolean partiallyLoaded = false;

    /**
     * true when this node should be visually highlighted in UI
     * usually same as found, but can stay independent
     */
    private boolean highlighted = false;

    /**
     * true when node belongs to current search result projection
     */
    private boolean inSearchResult = false;

    /**
     * optional: tells frontend that this node is ancestor/path node only
     * and was included to connect matches up to root
     */
    private boolean pathNode = false;

    /**
     * optional: tells frontend that node was loaded by regular expand
     * rather than initial search projection
     */
    private boolean loadedByExpand = false;

    /**
     * optional: tells frontend that node children were loaded
     * by "show all nodes" action
     */
    private boolean loadedByShowAll = false;

    /**
     * optional: true when this node is scope root of current search
     */
    private boolean scopeRoot = false;

    /**
     * how many found employees exist in subtree under this node
     * useful for badges and decisions in UI
     */
    private int foundCountInSubtree = 0;

    /**
     * how many found employees are immediate visible children of this node
     */
    private int foundCountInDirectChildren = 0;

    /**
     * optional frontend hint:
     * employee / manager / ancestor / scopeRoot
     */
    private String nodeRole;

    /**
     * optional frontend hint:
     * search / expand / showAll / initial
     */
    private String loadState;

    public OrgNodeDTO() {
    }

    /* =========================
       Getters and setters
       ========================= */

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getTeudatZehut() {
        return teudatZehut;
    }

    public void setTeudatZehut(String teudatZehut) {
        this.teudatZehut = teudatZehut;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Long getBirthday() {
        return birthday;
    }

    public void setBirthday(Long birthday) {
        this.birthday = birthday;
    }

    public Long getJobBeginDate() {
        return jobBeginDate;
    }

    public void setJobBeginDate(Long jobBeginDate) {
        this.jobBeginDate = jobBeginDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getParentCompanyCode() {
        return parentCompanyCode;
    }

    public void setParentCompanyCode(String parentCompanyCode) {
        this.parentCompanyCode = parentCompanyCode;
    }

    public String getParentCompanyName() {
        return parentCompanyName;
    }

    public void setParentCompanyName(String parentCompanyName) {
        this.parentCompanyName = parentCompanyName;
    }

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public String getManagerId() {
        return managerId;
    }

    public void setManagerId(String managerId) {
        this.managerId = managerId;
    }

    public String getOrgUnitCode() {
        return orgUnitCode;
    }

    public void setOrgUnitCode(String orgUnitCode) {
        this.orgUnitCode = orgUnitCode;
    }

    public String getCostCenter() {
        return costCenter;
    }

    public void setCostCenter(String costCenter) {
        this.costCenter = costCenter;
    }

    public int getContractCode() {
        return contractCode;
    }

    public void setContractCode(int contractCode) {
        this.contractCode = contractCode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getPositionCode() {
        return positionCode;
    }

    public void setPositionCode(int positionCode) {
        this.positionCode = positionCode;
    }

    public String getJobKey() {
        return jobKey;
    }

    public void setJobKey(String jobKey) {
        this.jobKey = jobKey;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDivisionName() {
        return divisionName;
    }

    public void setDivisionName(String divisionName) {
        this.divisionName = divisionName;
    }

    public boolean isChildrenLoaded() {
        return childrenLoaded;
    }

    public void setChildrenLoaded(boolean childrenLoaded) {
        this.childrenLoaded = childrenLoaded;
    }

    public List<String> getParentPath() {
        return parentPath;
    }

    public void setParentPath(List<String> parentPath) {
        this.parentPath = parentPath;
    }
/*
    public List<OrgNodeDTO> getChildren() {
        return children;
    }

    public void setChildren(List<OrgNodeDTO> children) {
        this.children = children;
    }
*/
    public List<String> getChildrenIds() {
        return childrenIds;
    }

    public void setChildrenIds(List<String> childrenIds) {
        this.childrenIds = childrenIds;
    }

    public boolean isHasChildren() {
        return hasChildren;
    }

    public void setHasChildren(boolean hasChildren) {
        this.hasChildren = hasChildren;
    }

    public int getNumberOfChildren() {
        return numberOfChildren;
    }

    public void setNumberOfChildren(int numberOfChildren) {
        this.numberOfChildren = numberOfChildren;
    }

    public int getNumberOfParents() {
        return numberOfParents;
    }

    public void setNumberOfParents(int numberOfParents) {
        this.numberOfParents = numberOfParents;
    }

    public String getPositionPlansDesc() {
        return positionPlansDesc;
    }

    public void setPositionPlansDesc(String positionPlansDesc) {
        this.positionPlansDesc = positionPlansDesc;
    }

    public String getOrgunitOrgehDesc() {
        return orgunitOrgehDesc;
    }

    public void setOrgunitOrgehDesc(String orgunitOrgehDesc) {
        this.orgunitOrgehDesc = orgunitOrgehDesc;
    }

    public boolean isFound() {
        return found;
    }

    public void setFound(boolean found) {
        this.found = found;
    }

    public boolean isHasFoundDescendants() {
        return hasFoundDescendants;
    }

    public void setHasFoundDescendants(boolean hasFoundDescendants) {
        this.hasFoundDescendants = hasFoundDescendants;
    }

    public boolean isHasDirectFoundChildren() {
        return hasDirectFoundChildren;
    }

    public void setHasDirectFoundChildren(boolean hasDirectFoundChildren) {
        this.hasDirectFoundChildren = hasDirectFoundChildren;
    }

    public boolean isSearchFiltered() {
        return searchFiltered;
    }

    public void setSearchFiltered(boolean searchFiltered) {
        this.searchFiltered = searchFiltered;
    }

    public boolean isShowAllAvailable() {
        return showAllAvailable;
    }

    public void setShowAllAvailable(boolean showAllAvailable) {
        this.showAllAvailable = showAllAvailable;
    }

    public boolean isPartiallyLoaded() {
        return partiallyLoaded;
    }

    public void setPartiallyLoaded(boolean partiallyLoaded) {
        this.partiallyLoaded = partiallyLoaded;
    }

    public boolean isHighlighted() {
        return highlighted;
    }

    public void setHighlighted(boolean highlighted) {
        this.highlighted = highlighted;
    }

    public boolean isInSearchResult() {
        return inSearchResult;
    }

    public void setInSearchResult(boolean inSearchResult) {
        this.inSearchResult = inSearchResult;
    }

    public boolean isPathNode() {
        return pathNode;
    }

    public void setPathNode(boolean pathNode) {
        this.pathNode = pathNode;
    }

    public boolean isLoadedByExpand() {
        return loadedByExpand;
    }

    public void setLoadedByExpand(boolean loadedByExpand) {
        this.loadedByExpand = loadedByExpand;
    }

    public boolean isLoadedByShowAll() {
        return loadedByShowAll;
    }

    public void setLoadedByShowAll(boolean loadedByShowAll) {
        this.loadedByShowAll = loadedByShowAll;
    }

    public boolean isScopeRoot() {
        return scopeRoot;
    }

    public void setScopeRoot(boolean scopeRoot) {
        this.scopeRoot = scopeRoot;
    }

    public int getFoundCountInSubtree() {
        return foundCountInSubtree;
    }

    public void setFoundCountInSubtree(int foundCountInSubtree) {
        this.foundCountInSubtree = foundCountInSubtree;
    }

    public int getFoundCountInDirectChildren() {
        return foundCountInDirectChildren;
    }

    public void setFoundCountInDirectChildren(int foundCountInDirectChildren) {
        this.foundCountInDirectChildren = foundCountInDirectChildren;
    }

    public String getNodeRole() {
        return nodeRole;
    }

    public void setNodeRole(String nodeRole) {
        this.nodeRole = nodeRole;
    }

    public String getLoadState() {
        return loadState;
    }

    public void setLoadState(String loadState) {
        this.loadState = loadState;
    }
}