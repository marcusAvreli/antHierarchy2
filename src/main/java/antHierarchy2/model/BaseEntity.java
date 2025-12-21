package antHierarchy2.model;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import org.hibernate.annotations.GenericGenerator;

@MappedSuperclass
public abstract class BaseEntity {

    @Id
    @GeneratedValue(generator = "uuid2NoDash")
    @GenericGenerator(
        name = "uuid2NoDash",
        strategy = "antHierarchy2.util.UUIDNoDashGenerator"
    )
    private String id;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    
    @Column(name = "created", nullable = false)
    private long created;

    @Column(name = "modified", nullable = true)
    private Long modified; // nullable now

    // --- Getters and Setters ---
 

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public Long getModified() {
        return modified;
    }

    public void setModified(Long modified) {
        this.modified = modified;
    }

    // --- Lifecycle hooks for automatic timestamps ---
    @PrePersist
    protected void onCreate() {
        this.created = System.currentTimeMillis();
        // Do NOT set modified here; it will remain null until updated
    }

    @PreUpdate
    protected void onUpdate() {
        this.modified = System.currentTimeMillis();
    }

}
