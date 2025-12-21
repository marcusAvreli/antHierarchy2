package antHierarchy2.model;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.ForeignKey;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "company")
public class Company extends BaseEntity {

    @Column(name = "company_code", length = 3, nullable = false, unique = true)
    private String companyCode;

    @Column(name = "company_name", length = 450, nullable = false)
    private String companyName;

    // --- Self-referencing parent company ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", foreignKey = @ForeignKey(name = "FK_company_parent"))
    private Company parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<Company> subsidiaries = new ArrayList<>();

    // --- Branch relationship ---
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<Branch> branches = new ArrayList<>();

    // --- Constructors ---
    public Company() {}

    public Company(String companyCode, String companyName) {
        this.companyCode = companyCode;
        this.companyName = companyName;
    }

    // --- Getters & Setters ---
    public String getCompanyCode() { return companyCode; }
    public void setCompanyCode(String companyCode) { this.companyCode = companyCode; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public Company getParent() { return parent; }
    public void setParent(Company parent) { this.parent = parent; }

    public List<Company> getSubsidiaries() { return subsidiaries; }
    public void setSubsidiaries(List<Company> subsidiaries) { this.subsidiaries = subsidiaries; }

    public List<Branch> getBranches() { return branches; }
    public void setBranches(List<Branch> branches) { this.branches = branches; }

    public void addSubsidiary(Company child) {
        subsidiaries.add(child);
        child.setParent(this);
    }
}
