package com.travelinsurancemaster.model.dto.cms.menu;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.travelinsurancemaster.model.dto.cms.page.Page;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chernov Artur on 23.07.15.
 */

@Entity
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class MenuItem extends AbstractMenu implements Serializable {
    private static final long serialVersionUID = -7250162382094534917L;

    @Id
    @GeneratedValue
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "menu_id")
    private Menu menu;

    @Column(nullable = false)
    private String title;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "parent_menu_item_id")
    private MenuItem parentMenuItem;

    @Column
    private String url;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "page_id")
    private Page page;

    @JsonIgnore
    @Column(nullable = false)
    private int level;

    @JsonIgnore
    @Column(nullable = false)
    private int sortOrder;

    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JsonIgnore
    @OneToMany(mappedBy = "parentMenuItem", fetch = FetchType.EAGER)
    @OrderBy("sortOrder")
    private List<MenuItem> childMenuItems = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public MenuItem getParentMenuItem() {
        return parentMenuItem;
    }

    public void setParentMenuItem(MenuItem parentMenuItem) {
        this.parentMenuItem = parentMenuItem;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public List<MenuItem> getChildMenuItems() {
        return childMenuItems;
    }

    public void setChildMenuItems(List<MenuItem> childMenuItems) {
        this.childMenuItems = childMenuItems;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }



}
