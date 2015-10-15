package com.nexten.nxfaces.model.entity;

import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author Jaques Claudino
 * Out 15, 2015
 */
public abstract class AbstractEntity implements Entity, Serializable {
        
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + Objects.hashCode(getId());
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AbstractEntity other = (AbstractEntity) obj;
        if (!Objects.equals(this.getId(), other.getId())) {
            return false;
        }
        return true;
    }    
    
}
