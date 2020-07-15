package com.nexten.nxfaces.model;

import com.nexten.nxfaces.dao.AbstractDAO;
import com.nexten.nxfaces.dao.CriteriaGetter;
import com.nexten.nxfaces.dao.CriteriaGetter.OrderGetter;
import com.nexten.nxfaces.dao.CriteriaGetter.PredicateGetter;
import com.nexten.nxfaces.model.entity.Entity;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.ManagedType;
import org.primefaces.model.FilterMeta;
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
    private String lastFilters;
    
    public EntityLazyDataModel(AbstractDAO<T> dao, PredicateGetter predicateGetter, OrderGetter orderGetter, List<String> globalFilterAttributeNames) {
        this.dao = dao;
        this.predicateGetter = predicateGetter;
        this.orderGetter = orderGetter;
        this.globalFilterAttributeNames = globalFilterAttributeNames != null ? globalFilterAttributeNames : getDefaultGlobalFilterAttributeNames();
        LOG.setLevel(Level.ALL); // NOSONAR
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
    public List<T> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, FilterMeta> filters) {        
        LOG.log(Level.FINEST, "LazyLoad: first={0}; pageSize={1}; sortField={2}; sortOrder={3}; filters={4}", new Object[] {first, pageSize, sortField, sortOrder, filters});
                
        if (!filters.toString().equals(lastFilters)) {
            setRowCount(dao.findCount(getPredicateGetter(filters)).intValue());
            lastFilters = filters.toString();
        }        
        return dao.findAll(getPredicateGetter(filters), getOrderGetter(sortField, sortOrder), first, pageSize);
    }
    
    private PredicateGetter getPredicateGetter(final Map<String, FilterMeta> filters) {      
        return new PredicateGetter() {
            @Override
            public Predicate getPredicate(CriteriaQuery query, CriteriaBuilder builder, Root root) {
                CriteriaGetter criteria = dao.getCriteriaGetter();
                
                //predicados do filtro do dataTable:
                Predicate predicateFilter = null;  
                Predicate globalFilter = null;
                if (filters != null) {
                    for (Entry<String, FilterMeta> entry : filters.entrySet()) {
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
    
    private OrderGetter getOrderGetter(String sortField, SortOrder sortOrder) {
        if (sortField != null && !sortField.isEmpty()) {
            return new OrderGetter() {
                @Override
                public List getListOrder(CriteriaQuery query, CriteriaBuilder builder, Root root) {
                    Path path = dao.getCriteriaGetter().getAttribute(root, sortField);
                    Order order;
                    if (SortOrder.DESCENDING.equals(sortOrder)) {
                        order = builder.desc(path);
                    } else {
                        order = builder.asc(path);
                    }
                    return Arrays.asList(order);
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
        LOG.log(Level.FINE, "Default globalFilter {0}: {1}", new Object[]{dao.getEntityClass().getSimpleName(), result});
        return result;
    }
    
}
