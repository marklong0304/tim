package com.travelinsurancemaster.model.dto.json;

import com.travelinsurancemaster.model.dto.BasicEntity;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author Artur Chernov
 */
public abstract class BasicEntityDTO<T extends BasicEntity> implements Serializable, EntityMapper<T> {
    private static final long serialVersionUID = 1L;

    private Long id;
    //todo: implement later
    //private Date deletedDate;

    public BasicEntityDTO() {
    }

    public BasicEntityDTO(T basicEntity) {
        this.id = basicEntity.getId() == 0 ? null : basicEntity.getId();

    }

    protected T fillEntityObject(T entity) {
        if (id != null) {
            entity.setId(id);
            if (entity.getId() < -1) {
                entity.setId(0L);
            }
        }
        return entity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        @SuppressWarnings("rawtypes")
        BasicEntityDTO that = (BasicEntityDTO) o;
        if (this.getId() == null || that.getId() == null)
            return false;
        if (getId() == 0L || that.getId() == 0L)
            return false;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
