package com.travelinsurancemaster.model.dto.cms.page;

import org.hibernate.search.annotations.*;
import org.hibernate.search.annotations.Index;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Created by Chernov Artur on 18.10.15.
 */

@MappedSuperclass
public class BasePage extends BaseContent {

    @Field(index = Index.YES, analyze = Analyze.YES, store = Store.YES)
    @Column(nullable = false)
    @Length(max = 255)
    private String name;

    @Field(index = Index.YES, analyze = Analyze.YES, store = Store.YES)
    @Column(nullable = false)
    @Analyzer(definition = "customanalyzer")
    @Length(max = 255)
    private String caption;

    @Field(index = Index.YES, analyze = Analyze.YES, store = Store.YES)
    @Column(nullable = false)
    @Analyzer(definition = "customanalyzer")
    @Length(max = 1000)
    private String description;

    @ManyToOne
    @JoinColumn(name = "page_type_id")
    private PageType pageType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private PageStatus status;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public PageType getPageType() {
        return pageType;
    }

    public void setPageType(PageType pageType) {
        this.pageType = pageType;
    }

    public PageStatus getStatus() {
        return status;
    }

    public void setStatus(PageStatus status) {
        this.status = status;
    }
}
