package com.travelinsurancemaster.model.dto.json;

import com.travelinsurancemaster.model.dto.Group;
import org.apache.commons.collections4.CollectionUtils;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * @author Artur Chernov
 */
public class GroupDTO extends BasicEntityDTO<Group> implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private Set<CategoryDTO> categories = new TreeSet<>();

    public GroupDTO(Group from) {
        super(from);
        this.name = from.getName();
        if (Hibernate.isInitialized(from.getCategoryList()) && CollectionUtils.isNotEmpty(from.getCategoryList())) {
            this.categories = new TreeSet<>(from.getCategoryList().stream().map(CategoryDTO::new).collect(Collectors.toSet()));
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<CategoryDTO> getCategories() {
        return categories;
    }

    public void setCategories(Set<CategoryDTO> categories) {
        this.categories = categories;
    }

    @Override
    public Group toEntityObject() {
        return null;
    }
}
