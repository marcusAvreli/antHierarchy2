package antHierarchy2.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.ForeignKey;

@Entity
@Table(name = "employee")
public class Employee extends BaseEntity {

	@Column(name = "teudat_zehut", length = 9, unique = true, nullable = false)
	private String teudatZehut;

	@Column(length = 20, nullable = false)
	private String gender;  // "male" / "female"
	
	
    @Column(name = "birthday", nullable = false)
    private Long birthday;  // epoch millis

    @Column(name = "job_begin_date", nullable = false)
    private Long jobBeginDate; // epoch millis
    
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
	@Column(length = 450, nullable = false)
    private String firstName;

    @Column(length = 450, nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String name; // full name (derived from first + last)

    @Column(length = 20)
    private String phoneNumber;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(length = 512)
    private String image; // path or URL

    // --- Self-referencing manager relationship ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "manager_id",
        foreignKey = @ForeignKey(name = "FK_employee_manager"),
        nullable = true
    )
    private Employee manager;

    @OneToMany(mappedBy = "manager", cascade = CascadeType.PERSIST)
    private List<Employee> reports = new ArrayList<>();

    // --- Organizational fields ---
    @Column(name = "org_unit_code", length = 10, nullable = false)
    private String orgUnitCode;

    @Column(name = "position_code", length = 8, nullable = false)
    private int position;

    @Column(name = "cost_center", length = 10, nullable = false)
    private String costCenter;

    @Column(name = "contract_code", nullable = false)
    private int contractCode;

    @Column(name = "job_key", length = 10, nullable = false)
    private String jobKey;

    @Column(name = "job_name", length = 200, nullable = false)
    private String jobName;

    // --- Company Fields ---
    @Column(name = "company_code", length = 3, nullable = false)
    private String companyCode;

    @Column(name = "company_name", length = 450, nullable = false)
    private String companyName;
    
/*
    @Column(name = "branch_id", length = 255, nullable = true)
	private String branchId;
    
    public String getBranchId() {
		return branchId;
	}
	public void setBranchId(String branchId) {
		this.branchId = branchId;
	}
	*/
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
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", foreignKey = @ForeignKey(name = "FK_employee_branch"))
    private Branch branch;

    public Branch getBranch() { return branch; }
    public void setBranch(Branch branch) { this.branch = branch; }
    
    // --- Constructors ---
    public Employee() {}

    public Employee(String firstName, String lastName, String title, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.name = firstName + " " + lastName;
        this.title = title;
        this.email = email;
    }

    // --- Getters and Setters ---
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
        this.name = firstName + " " + this.lastName;
    }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) {
        this.lastName = lastName;
        this.name = this.firstName + " " + lastName;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public Employee getManager() { return manager; }
    public void setManager(Employee manager) { this.manager = manager; }

    public List<Employee> getReports() { return reports; }
    public void setReports(List<Employee> reports) { this.reports = reports; }

    public void addReport(Employee report) {
        this.reports.add(report);
        report.setManager(this);
    }

    public String getOrgUnitCode() { return orgUnitCode; }
    public void setOrgUnitCode(String orgUnitCode) { this.orgUnitCode = orgUnitCode; }

    public int getPosition() { return position; }
    public void setPosition(int position) { this.position = position; }

    public String getCostCenter() { return costCenter; }
    public void setCostCenter(String costCenter) { this.costCenter = costCenter; }

    public int getContractCode() { return contractCode; }
    public void setContractCode(int contractCode) { this.contractCode = contractCode; }

    public String getJobKey() { return jobKey; }
    public void setJobKey(String jobKey) { this.jobKey = jobKey; }

    public String getJobName() { return jobName; }
    public void setJobName(String jobName) { this.jobName = jobName; }


}
