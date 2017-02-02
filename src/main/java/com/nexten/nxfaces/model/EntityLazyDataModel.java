package com.nexten.nxfaces.model;

import com.nexten.nxfaces.dao.AbstractDAO;
import com.nexten.nxfaces.dao.CriteriaGetter;
import com.nexten.nxfaces.dao.CriteriaGetter.OrderGetter;
import com.nexten.nxfaces.dao.CriteriaGetter.PredicateGetter;
import com.nexten.nxfaces.model.entity.Entity;
import java.io.Serializable;
import java.util.Map;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SelectableDataModel;
import org.primefaces.model.SortOrder;

/**
 *
 * @author jaques
 * @param <T>
 */
public class EntityLazyDataModel<T extends Entity> extends LazyDataModel<T> implements SelectableDataModel<T>, Serializable {
    
    private static final Logger LOG = Logger.getLogger(EntityLazyDataModel.class.getName());
    private static final String GLOBAL_FILTER = "globalFilter";
    
    private final AbstractDAO<T> dao;
    private final PredicateGetter predicateGetter;
    private final OrderGetter orderGetter;
    private final List<String> globalFilterAttributeNames;
    
    public EntityLazyDataModel(AbstractDAO<T> dao, PredicateGetter predicateGetter, OrderGetter orderGetter,
            List<String> globalFilterAttributeNames) {
        this.dao = dao;
        this.predicateGetter = predicateGetter;
        this.orderGetter = orderGetter;
        this.globalFilterAttributeNames = globalFilterAttributeNames;
        this.setRowCount(dao.findCount(predicateGetter).intValue());
        LOG.setLevel(Level.ALL);
    }
        
    @Override
    public Object getRowKey(T entity) {
        return entity.getId();
    }

    @Override
    public T getRowData(String rowKey) {
        return dao.findById(Long.parseLong(rowKey));
    }       
    
    @Override  
    public List<T> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String,Object> filters) {        
        LOG.log(Level.FINEST, "first={0} | pageSize={1} | sortField={2} | sortOrder={3} | filters={4}", new Object[] {first, pageSize, sortField, sortOrder, filters});
                
        return dao.findAll(getPredicateGetter(filters), orderGetter, first, pageSize);
    }
    
    private PredicateGetter getPredicateGetter(final Map<String,Object> filters) {      
        return new PredicateGetter() {
            @Override
            public Predicate getPredicate(CriteriaQuery query, CriteriaBuilder builder, Root root) {
                CriteriaGetter criteria = dao.getCriteriaGetter();
                
                //predicados do filtro do dataTable:
                Predicate predicateFilter = null;  
                Predicate globalFilter = null;
                for (Entry<String,Object> entry : filters.entrySet()) {
                    if (entry.getKey().equals(GLOBAL_FILTER)) {
                        for (String attributeName : globalFilterAttributeNames) {
                            Predicate p = createPredicate(builder, root, attributeName, entry.getValue());                            
                            globalFilter = criteria.orNotNull(builder, globalFilter, p);
                        }
                    } else {
                        Predicate p = createPredicate(builder, root, entry.getKey(), entry.getValue());
                        predicateFilter = criteria.andNotNull(builder, predicateFilter, p);
                    }
                }                
                predicateFilter = criteria.andNotNull(builder, predicateFilter, globalFilter);
                               
                Predicate predicateCustom = null;
                if (predicateGetter != null) {
                    predicateCustom = predicateGetter.getPredicate(query, builder, root);
                }
                
                return criteria.andNotNull(builder, predicateFilter, predicateCustom);
            }
        };
    }
    
    private Predicate createPredicate(CriteriaBuilder builder, Root root, String attributeName, Object value) {
        CriteriaGetter criteria = dao.getCriteriaGetter();
        
        //extract the path. e.g: user.company.name
        Path path = criteria.getAttribute(root, attributeName);

        Predicate predicate;
        if (value instanceof String) {
            predicate = criteria.like(builder, path, (String) value);
        } else {
            predicate = builder.equal(path, value);
        }

        return predicate;
    }
    
}
