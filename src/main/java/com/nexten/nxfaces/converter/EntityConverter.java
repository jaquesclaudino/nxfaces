package com.nexten.nxfaces.converter;

import com.nexten.nxfaces.model.entity.Entity;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 *
 * @author Jaques Claudino
 */
@Named
@ApplicationScoped
public class EntityConverter implements Converter {

    private static final Logger LOG = Logger.getLogger(EntityConverter.class.getName());
    
    @PersistenceContext(name = "entity-manager-nxfaces")
    private EntityManager em;
    
    @SuppressWarnings("unchecked")
    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value != null && !value.isEmpty()) {
            try {
                String key[] = value.split("\\|"); //ex: 1|User
                if (key.length != 2)
                    return null;
                
                Long id = Long.parseLong(key[0]);
                Class clazz = Class.forName(key[1]);
                return em.find(clazz, id);                
            } catch (ClassNotFoundException ex) {
                LOG.log(Level.SEVERE, "getAsObject error", ex);
            }
        }
        return null;
    }

    @Override
    public String getAsString(FacesContext ctx, UIComponent component, Object value) {
        if (value instanceof Entity) {
            Entity entity = (Entity) value;
            return entity.getId() + "|" + value.getClass().getName();
        }
        return null;
    }

}
