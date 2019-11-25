package com.travelinsurancemaster.model.dto.cms.attachment;

import com.travelinsurancemaster.model.security.User;
import org.apache.commons.io.FileUtils;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by Chernov Artur on 29.07.15.
 */

@Entity
public class FileAttachment implements Serializable {
    private static final long serialVersionUID = -5829722123107441508L;

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String fileUid;

    @DateTimeFormat(pattern = "MM/dd/yyyy")
    @Column(nullable = false)
    private Date createDate;

    @DateTimeFormat(pattern = "MM/dd/yyyy")
    @Column(nullable = false)
    private Date modifiedDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User author;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(nullable = false)
    private byte[] content;

    @Column(nullable = false)
    @NotEmpty
    private String name;

    @Column
    private String fileName;

    @Column(nullable = false)
    private String mimeType;

    @Column(nullable = false)
    private long size;

    @Column
    private boolean deleted;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileUid() {
        return fileUid;
    }

    public void setFileUid(String fileUid) {
        this.fileUid = fileUid;
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

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public String getPrettySize() {
        return FileUtils.byteCountToDisplaySize(size);
    }
}
