package com.travelinsurancemaster.model.dto.cms.menu;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class Menu extends AbstractMenu implements Serializable {
    private static final long serialVersionUID = 6111309058159416172L;

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true)
    private String title;

    @JsonIgnore
    @Column
    @Enumerated(EnumType.STRING)
    private MenuType menuType = MenuType.USER_MENU;

    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JsonIgnore
    @OneToMany(mappedBy = "menu",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    @OrderBy("sortOrder")
    private List<MenuItem> menuItems = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<MenuItem> getMenuItems() {
        return menuItems;
    }

    public void setMenuItems(List<MenuItem> menuItems) {
        this.menuItems = menuItems;
    }

    public MenuType getMenuType() {
        return menuType;
    }

    public void setMenuType(MenuType menuType) {
        this.menuType = menuType;
    }

    public static enum MenuType {
        SYSTEM_MENU, USER_MENU
    }


}
