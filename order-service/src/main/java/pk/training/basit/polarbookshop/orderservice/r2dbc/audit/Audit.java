package pk.training.basit.polarbookshop.orderservice.r2dbc.audit;

import org.springframework.data.annotation.*;

import java.time.Instant;

@AccessType(AccessType.Type.FIELD)
public class Audit {

    @CreatedBy
    private Long createdBy;

    @CreatedDate    // When the entity was created
    private Instant createdDate;

    @LastModifiedBy
    private Long lastModifiedBy;

    @LastModifiedDate   // When the entity was last modified
    private Instant lastModifiedDate;

    public Audit() {

    }

    public Audit(Long createdBy, Instant createdDate, Long lastModifiedBy, Instant lastModifiedDate) {
        this.createdBy = createdBy;
        this.createdDate = createdDate;
        this.lastModifiedBy = lastModifiedBy;
        this.lastModifiedDate = lastModifiedDate;
    }

    public Audit(Audit audit) {
        this.createdBy = audit.createdBy;
        this.createdDate = audit.createdDate;
        this.lastModifiedBy = audit.lastModifiedBy;
        this.lastModifiedDate = audit.lastModifiedDate;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public Long getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(Long lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Instant getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Instant lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
}
