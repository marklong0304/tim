package com.travelinsurancemaster.model.dto.cms.page;

import com.travelinsurancemaster.model.security.User;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by Chernov Artur on 18.10.15.
 */

@MappedSuperclass
public class BaseContent implements Serializable {
    private static final long serialVersionUID = -3622746426540987813L;

    @Id
    @GeneratedValue
    private Long id;

    @Field(index = Index.YES, analyze = Analyze.YES, store = Store.YES)
    @Basic(fetch = FetchType.LAZY)
    @Column(nullable = false, columnDefinition = "TEXT")
    @Analyzer(definition = "htmlalyzer")
    @Length(max = 100000)
    private String content;

    @DateTimeFormat(pattern = "MM/dd/yyyy")
    @Column(nullable = false)
    @CreationTimestamp
    private Date createDate;

    @DateTimeFormat(pattern = "MM/dd/yyyy")
    @Column(nullable = false)
    @UpdateTimestamp
    private Date modifiedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User author;

    @Version
    @Column(name = "opt_lock")
    private Long version;

    @Column(nullable = false)
    private boolean deleted = false;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
