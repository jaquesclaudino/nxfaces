package com.nexten.nxfaces.model;

import com.nexten.nxfaces.dao.AbstractDAO;
import com.nexten.nxfaces.dao.CriteriaGetter;
import com.nexten.nxfaces.dao.CriteriaGetter.OrderGetter;
import com.nexten.nxfaces.dao.CriteriaGetter.PredicateGetter;
import com.nexten.nxfaces.model.entity.Entity;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.ManagedType;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SelectableDataModel;
import org.primefaces.model.SortMeta;
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
    private final Map<String,T> rowDataCache = new HashMap<>();
    private String lastFilters;
    
    public EntityLazyDataModel(AbstractDAO<T> dao, PredicateGetter predicateGetter, OrderGetter orderGetter, List<String> globalFilterAttributeNames) {
        this.dao = dao;
        this.predicateGetter = predicateGetter;
        this.orderGetter = orderGetter;
        this.globalFilterAttributeNames = globalFilterAttributeNames != null ? globalFilterAttributeNames : getDefaultGlobalFilterAttributeNames();
        LOG.setLevel(Level.ALL); // NOSONAR
    }
        
    @Override
    public String getRowKey(T entity) {
        return Long.toString(entity.getId());
    }

    @Override
    public T getRowData(String rowKey) {
        T result = rowDataCache.get(rowKey);
        if (result == null) {
            LOG.log(Level.FINEST, "LazyFind: rowKey={0}", rowKey);
            result = dao.findById(Long.parseLong(rowKey));
            rowDataCache.put(rowKey, result);
        }
        return result;
    }
    
    @Override
    public int count(Map<String, FilterMeta> filterBy) {
        checkUpdateRowCount(filterBy);
        return getRowCount();
    }

    @Override
    public List<T> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        LOG.log(Level.FINEST, "LazyLoad: first={0}; pageSize={1}; sortBy={2}; filterBy={3}", new Object[] {first, pageSize, sortBy, filterBy});                
        checkUpdateRowCount(filterBy);
        return dao.findAll(getPredicateGetter(filterBy), getOrderGetter(sortBy), first, pageSize);
    }
    
    private void checkUpdateRowCount(Map<String, FilterMeta> filterBy) {
        if (!filterBy.toString().equals(lastFilters)) {
            LOG.log(Level.FINEST, "LazyCount: filterBy={0}", filterBy);
            setRowCount(dao.findCount(getPredicateGetter(filterBy)).intValue());
            lastFilters = filterBy.toString();
        }
    }
    
    protected PredicateGetter getPredicateGetter(final Map<String, FilterMeta> filterBy) {      
        return new PredicateGetter() {
            @Override
            public Predicate getPredicate(CriteriaQuery query, CriteriaBuilder builder, Root root) {
                CriteriaGetter criteria = dao.getCriteriaGetter();
                
                //predicados do filtro do dataTable:
                Predicate predicateFilter = null;  
                Predicate globalFilter = null;
                if (filterBy != null) {
                    for (Entry<String, FilterMeta> entry : filterBy.entrySet()) {
                        if (entry.getKey().equals(GLOBAL_FILTER)) {
                            for (String attributeName : globalFilterAttributeNames) {
                                Predicate p = createPredicate(builder, root, attributeName, entry.getValue().getFilterValue());
                                globalFilter = criteria.orNotNull(builder, globalFilter, p);
                            }
                        } else {
                            Predicate p = createPredicate(builder, root, entry.getKey(), entry.getValue().getFilterValue());
                            predicateFilter = criteria.andNotNull(builder, predicateFilter, p);
                        }
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
    
    protected OrderGetter getOrderGetter(Map<String, SortMeta> sortBy) {
        if (sortBy != null && !sortBy.isEmpty()) {
            return new OrderGetter() {
                @Override
                public List getListOrder(CriteriaQuery query, CriteriaBuilder builder, Root root) {
                    List<Order> result = new ArrayList<>();
                    for (Entry<String, SortMeta> entry : sortBy.entrySet()) {
                        Path path = dao.getCriteriaGetter().getAttribute(root, entry.getKey());
                        Order order;
                        if (SortOrder.DESCENDING.equals(entry.getValue().getOrder())) {
                            order = builder.desc(path);
                        } else {
                            order = builder.asc(path);
                        }
                        result.add(order);
                    }
                    return result;
                }
            };
        }
        return orderGetter;
    }
    
    private Predicate createPredicate(CriteriaBuilder builder, Root root, String attributeName, Object value) {       
        CriteriaGetter criteria = dao.getCriteriaGetter();
        
        //extract the path. e.g: user.company.name
        Path path = criteria.getAttribute(root, attributeName);

        Predicate predicate = null;
        
        if (path.getJavaType().equals(String.class)) {
            predicate = criteria.like(builder, path, (String) value);
        } else {
            if (value instanceof String) {
                value = tryParse(path.getJavaType(), (String) value);
            }
            if (value != null) {
                predicate = builder.equal(path, value);
            }
        }

        return predicate;
    }
    
    private Object tryParse(Class clazz, String value) {
        try {
            if (value != null && !value.isEmpty()) {
                if (clazz.isEnum()) {
                    for (Field f : clazz.getDeclaredFields()) {
                        if (f.isEnumConstant() && f.getName().toLowerCase().contains(value.toLowerCase())) {
                            return Enum.valueOf(clazz, f.getName());
                        }
                    }
                    
                } else if (clazz.equals(Integer.class) || clazz.equals(int.class)) {
                    return Integer.parseInt(value);

                } else if (clazz.equals(Long.class) || clazz.equals(long.class)) {
                    return Long.parseLong(value);

                } else if (clazz.equals(Double.class) || clazz.equals(double.class)) {
                    return Double.parseDouble(value);
                
                } else if (clazz.equals(Float.class) || clazz.equals(float.class)) {
                    return Float.parseFloat(value);
                }
            }
        } catch (NumberFormatException ex) {
        }
        return null;
    }

    private List<String> getDefaultGlobalFilterAttributeNames() {
        List<String> result = new ArrayList<>();
        ManagedType<T> managedType = dao.getEntityManager().getMetamodel().managedType(dao.getEntityClass());
        Set<Attribute<? super T, ?>> attributes = managedType.getAttributes();
        for (Attribute attribute : attributes) {
            Class type = attribute.getJavaType();
            if (type.isEnum() 
                    || type.equals(String.class) 
                    || type.equals(Integer.class) 
                    || type.equals(int.class)
                    || type.equals(Long.class) 
                    || type.equals(long.class)
                    || type.equals(Double.class) 
                    || type.equals(double.class)
                    || type.equals(Float.class) 
                    || type.equals(float.class)) {
                result.add(attribute.getName());
            }
        }
        LOG.log(Level.FINEST, "Default globalFilter {0}: {1}", new Object[]{dao.getEntityClass().getSimpleName(), result});
        return result;
    }
    
}
