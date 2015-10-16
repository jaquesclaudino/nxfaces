package com.nexten.nxfaces.crud;

import com.nexten.nxfaces.model.EntityLazyDataModel;
import com.nexten.nxfaces.dao.AbstractDAO;
import com.nexten.nxfaces.dao.CriteriaGetter.OrderGetter;
import com.nexten.nxfaces.dao.CriteriaGetter.PredicateGetter;
import com.nexten.nxfaces.model.entity.Entity;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.model.DataModel;
import org.primefaces.context.RequestContext;
import org.primefaces.event.CloseEvent;

/**
 *
 * @author jaques
 * @param <T>
 */
public abstract class AbstractCRUD<T extends Entity> implements Serializable {
    
    private static final Logger LOG = Logger.getLogger(AbstractCRUD.class.getName());
    
    protected T entity;    
    private List<T> listAll;
    private DataModel<T> dataModel;
    protected boolean visible = false;
    private String globalFilter;
 
    public abstract AbstractDAO<T> getDAO();
    
    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public T getEntity() {
        if (entity == null) {
            entity = getDAO().createEntity();
        }
        return entity;
    }
    
    public void create() {
        edit(getDAO().createEntity());
    }
    
    public void save() {
        entity = getDAO().save(entity);
        listAll = null;
        visible = false;
    }
    
    public void delete(T entity) {
        getDAO().delete(entity);
        listAll = null;
    }
    
    public void edit(T entity) {
        this.entity = entity;
        this.visible = true;
    }
    
    public void cancel() {
        visible = false;
        try {
            RequestContext.getCurrentInstance().reset("form");
        } catch (Exception ex) {
            LOG.log(Level.WARNING, "Error on reset form", ex);
        }
    }
    
    public void clearList() {
        listAll = null;
        dataModel = null;
    }
    
    public List<T> getListAll() {
        if (listAll == null) {
            listAll = getDAO().findAll(getPredicateGetter(), getOrderGetter());
        }
        return listAll;
    }
    
    public DataModel<T> getDataModel() {        
        if (dataModel == null) {
            dataModel = new EntityLazyDataModel<>(getDAO(), getPredicateGetter(), getOrderGetter());
        }
        return dataModel;
    }

    protected PredicateGetter getPredicateGetter() {
        return null; //reimplementar nas subclasses. Prefira implementar o PredicateGetter dentro do DAO.
    }
    
    protected OrderGetter getOrderGetter() {
        return null; //reimplementar nas subclasses. Ex: return new String[] {"id"};
    }
    
    //chamado ao fechar o dialog:
    public void handleClose(CloseEvent event) {  
        visible = false; 
    }

    public String getGlobalFilter() {
        return globalFilter;
    }

    public void setGlobalFilter(String globalFilter) {
        this.globalFilter = globalFilter;
    }
    
}
