package antHierarchy2.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrgNodeDTO {

	private String id;
	private String name;
	private String firstName;
	private String lastName;
	private String teudatZehut;
	private String gender;
	private Long birthday;
	private Long jobBeginDate;
	private String title;
	private String type; // "employee", "branch", "orgunit", "contract", "costcenter"
	private String companyCode;
	private String companyName;
	private String parentCompanyCode;
	private String parentCompanyName;
	private String branchId;
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
	private boolean childrenLoaded = false; // lazy load indicator
	private List<String> parentPath;
	private List<OrgNodeDTO> children;
	private List<String> childrenIds;
	private boolean hasChildren = false;
	private int numberOfChildren;
	private int  numberOfParents;
	public int getNumberOfChildren() {
		return numberOfChildren;
	}

	public String getDivisionName() {
		return divisionName;
	}

	public void setDivisionName(String divisionName) {
		this.divisionName = divisionName;
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

	public boolean isHasChildren() {
		return hasChildren;
	}

	public void setHasChildren(boolean hasChildren) {
		this.hasChildren = hasChildren;
	}

	public OrgNodeDTO() {
	}

	public OrgNodeDTO(String id, String name, String firstName, String lastName, String title, String type,
			String companyCode, String companyName,String teudatZehut, String gender, Long birthday, Long jobBeginDate, String parentCompanyCode, String parentCompanyName, String branchId,
			String managerId, String orgUnitCode, String costCenter, int contractCode, String email, String phoneNumber,
			int positionCode, String jobKey, String jobName, String image, boolean childrenLoaded,boolean hasChildren) {
		/*
		 * String jpql = "SELECT new antHierarchy2.dto.OrgNodeDTO(" + "  e.id, " +
		 * "  e.name, " + "  e.firstName, " + "  e.lastName, " + "  e.title, " +
		 * "  'employee', " + "  e.companyCode, " + "  e.companyName, " +
		 * "  e.branch.company.parent.companyCode, " +
		 * "  e.branch.company.parent.companyName, " +
		 * 
		 */
		this.id = id;
		this.name = name;
		this.firstName = firstName;
		this.lastName = lastName;
		this.title = title;
		this.type = type;
		this.companyCode = companyCode;
		this.companyName = companyName;
		
		//String teudatZehut, String gender, String birthday, String jobBeginDate,
		this.teudatZehut = teudatZehut;
		this.gender = gender;
		this.birthday = birthday;
		this.jobBeginDate = jobBeginDate;
		
		this.parentCompanyCode = parentCompanyCode;
		this.parentCompanyName = parentCompanyName;
		this.branchId = branchId;
		this.managerId = managerId;
		this.orgUnitCode = orgUnitCode;
		this.costCenter = costCenter;
		this.contractCode = contractCode;

		this.email = email;
		this.phoneNumber = phoneNumber;
		this.positionCode = positionCode;
		this.jobKey = jobKey;
		this.jobName = jobName;
		this.image = image;
		this.childrenLoaded = childrenLoaded;
		this.children = new ArrayList<>();
		this.childrenIds = new ArrayList<String>();
		this.hasChildren = hasChildren;

	}

	public List<String> getParentPath() {
		return parentPath;
	}

	public void setParentPath(List<String> parentPath) {
		this.parentPath = parentPath;
	}
/*
	public OrgNodeDTO(String id, String name, String firstName, String lastName, String title, String type,
			String companyCode, String companyName, String parentCompanyCode, String parentCompanyName, String branchId,
			String managerId, String orgUnitCode, String costCenter, String contractCode, String email,
			String phoneNumber, String positionCode, String jobKey, String jobName, String image,
			boolean childrenLoaded,boolean hasChildren) {
		this.id = id;
		this.name = name;
		this.firstName = firstName;
		this.lastName = lastName;
		this.title = title;
		this.type = type;
		this.companyCode = companyCode;
		this.companyName = companyName;
		this.parentCompanyCode = parentCompanyCode;
		this.parentCompanyName = parentCompanyName;
		this.branchId = branchId;
		this.managerId = managerId;
		this.orgUnitCode = orgUnitCode;
		this.costCenter = costCenter;
		// this.contractCode = contractCode;
		this.email = email;
		this.phoneNumber = phoneNumber;
		// this.positionCode = positionCode;
		this.jobKey = jobKey;
		this.jobName = jobName;
		this.image = image;
		this.childrenLoaded = childrenLoaded;
		this.children = new ArrayList<>();
		this.childrenIds = new ArrayList<String>();
		this.hasChildren = hasChildren;
	}
*/
	public List<String> getChildrenIds() {
		return childrenIds;
	}

	public void setChildrenIds(List<String> childrenIds) {
		this.childrenIds = childrenIds;
	}

	public List<OrgNodeDTO> getChildren() {
		return children;
	}

	public void setChildren(List<OrgNodeDTO> children) {
		this.children = children;
	}

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

	public boolean isChildrenLoaded() {
		return childrenLoaded;
	}

	public void setChildrenLoaded(boolean childrenLoaded) {
		this.childrenLoaded = childrenLoaded;
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

	// --- Getters & setters omitted for brevity ---
}