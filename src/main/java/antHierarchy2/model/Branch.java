package antHierarchy2.model;

import javax.persistence.ForeignKey;

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


@Entity
@Table(name = "branch")
public class Branch extends BaseEntity {

    @Column(name = "branch_code", length = 10, nullable = false, unique = true)
    private String branchCode;

    @Column(name = "branch_name", length = 450, nullable = false)
    private String branchName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", foreignKey = @ForeignKey(name = "FK_branch_company"), nullable = false)
    private Company company;

    @OneToMany(mappedBy = "branch", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<Employee> employees = new ArrayList<>();

    public Branch() {}

    public Branch(String branchCode, String branchName, Company company) {
        this.branchCode = branchCode;
        this.branchName = branchName;
        this.company = company;
    }

    public String getBranchCode() { return branchCode; }
    public void setBranchCode(String branchCode) { this.branchCode = branchCode; }

    public String getBranchName() { return branchName; }
    public void setBranchName(String branchName) { this.branchName = branchName; }

    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }

    public List<Employee> getEmployees() { return employees; }
    public void setEmployees(List<Employee> employees) { this.employees = employees; }
}
