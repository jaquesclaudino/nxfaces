package com.nexten.nxfaces.model.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

/**
 *
 * @author Jaques Claudino
 * Aug 27, 2015
 */
@MappedSuperclass
public abstract class IdentityIdEntity extends AbstractEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
}
