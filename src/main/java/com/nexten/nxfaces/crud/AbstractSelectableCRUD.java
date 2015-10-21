package com.nexten.nxfaces.crud;

import com.nexten.nxfaces.model.entity.Entity;
import java.util.logging.Logger;

/**
 *
 * @author jaques
 * @param <T>
 */
public abstract class AbstractSelectableCRUD<T extends Entity> extends AbstractCRUD<T> {
    
    private static final Logger LOG = Logger.getLogger(AbstractSelectableCRUD.class.getName());
    
    public interface SelectListener<T> {
        void onSelect(T selected);
    }
    
    private T selected;    
    private SelectListener<T> selectListener;
    
    @Override
    public void save() { 
        super.save();  
        clearList();
    }
    
    @Override
    public void clearList() {
        super.clearList();
        selected = null;
    }
    
    public void editSelected() {
        edit(selected);
    }
    
    public void deleteSelected() {
        delete(selected);
        clearList();
        editing = false;
    }
    
    public T getSelected() {
        return selected;
    }

    public void setSelected(T selected) {
        this.selected = selected;
        
        if (selected != null) {
            editSelected();
        }
        
        if (selectListener != null) {
            selectListener.onSelect(selected);
        }
    }

    public SelectListener<T> getSelectListener() {
        return selectListener;
    }

    public void setSelectListener(SelectListener<T> selectListener) {
        this.selectListener = selectListener;
    }
            
}
