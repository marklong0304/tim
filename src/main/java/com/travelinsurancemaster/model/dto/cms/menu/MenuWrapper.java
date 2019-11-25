package com.travelinsurancemaster.model.dto.cms.menu;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chernov Artur on 26.10.15.
 */
public class MenuWrapper implements Serializable {
    private static final long serialVersionUID = -4652889156461981865L;

    private String id;
    private String title;
    private String url;
    private String label;
    private List<MenuWrapper> children = new ArrayList<>();
    private Long pageId;
    // property from jqtree lib
    @JsonIgnore
    private boolean is_open;
    @JsonIgnore
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<MenuWrapper> getChildren() {
        return children;
    }

    public void setChildren(List<MenuWrapper> children) {
        this.children = children;
    }

    public Long getPageId() {
        return pageId;
    }

    public void setPageId(Long pageId) {
        this.pageId = pageId;
    }

    @JsonIgnore
    public boolean is_open() {
        return is_open;
    }

    @JsonIgnore
    public void setIs_open(boolean is_open) {
        this.is_open = is_open;
    }

    @JsonIgnore
    public String getName() {
        return name;
    }

    @JsonIgnore
    public void setName(String name) {
        this.name = name;
    }
}
