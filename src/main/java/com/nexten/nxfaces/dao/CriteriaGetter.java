package com.nexten.nxfaces.dao;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

/**
 *
 * @author jaques
 * @param <T>
 */
public class CriteriaGetter<T> {
    
    public interface SelectionGetter<T> {
        Selection getSelection(CriteriaQuery<T> query, CriteriaBuilder builder, Root<T> root);
    }
    
    public interface PredicateGetter<T> {
        Predicate getPredicate(CriteriaQuery<T> query, CriteriaBuilder builder, Root<T> root);
    }
    
    public interface OrderGetter<T> {
        List<Order> getListOrder(CriteriaQuery<T> query, CriteriaBuilder builder, Root<T> root);
    }
    
    //---------- selection
    
    public SelectionGetter getSelectionEntity() {
        return new SelectionGetter() {
            @Override
            public Selection getSelection(CriteriaQuery query, CriteriaBuilder builder, Root root) {
                return root;
            }
        };
    }
    
    public SelectionGetter getSelectionEntityCount() {
        return new SelectionGetter() {
            @Override
            public Selection getSelection(CriteriaQuery query, CriteriaBuilder builder, Root root) {
                return builder.count(root);
            }
        };
    }
    
    //---------- predicate
    
    public PredicateGetter getPredicateAttributeEqual(final String attributeName, final Object value) {
        return new PredicateGetter() {
            @Override
            public Predicate getPredicate(CriteriaQuery query, CriteriaBuilder builder, Root root) {
                return builder.equal(root.get(attributeName), value);
            }
        };
    }
    
    public PredicateGetter getPredicateAttributeLike(final String attributeName, final String queryString) {
        return new PredicateGetter() {
            @Override
            public Predicate getPredicate(CriteriaQuery query, CriteriaBuilder builder, Root root) {
                return like(builder, root.get(attributeName), queryString.toLowerCase());
            }
        };
    }
        
    public Predicate orNotNull(CriteriaBuilder builder, Predicate predicate1, Predicate predicate2) {
        if (predicate1 != null && predicate2 != null) {
            return builder.or(predicate1, predicate2);
        } else if (predicate1 != null) {
            return predicate1;
        } else if (predicate2 != null) {
            return predicate2;
        }
        return null;
    }
    
    public Predicate andNotNull(CriteriaBuilder builder, Predicate predicate1, Predicate predicate2) {
        if (predicate1 != null && predicate2 != null) {
            return builder.and(predicate1, predicate2);
        } else if (predicate1 != null) {
            return predicate1;
        } else if (predicate2 != null) {
            return predicate2;
        }
        return null;
    }
    
    public Predicate like(CriteriaBuilder builder, Expression expression, String queryString) {
        if (queryString != null && !queryString.isEmpty()) {                
            return builder.like(builder.lower(expression), "%" + queryString.toLowerCase() + "%");
        } else {
            return null;
        }
    }
    
    /**
     *
     * @param filtroData
     *   0 = inicio OU fim
     *   1 = inicio
     *   2 = fim
     */
    public Predicate betweenData(CriteriaBuilder builder, Root root, String atributeNameInicio, Date inicio, String attributeNameFim, Date fim, int filtroData) {
        switch (filtroData) {
            case 1: return builder.between(root.get(atributeNameInicio), inicio, fim);
            case 2: return builder.between(root.get(attributeNameFim), inicio, fim);
            default:
                return builder.or( //inicio OU fim
                    builder.between(root.get(atributeNameInicio), inicio, fim),
                    builder.between(root.get(attributeNameFim), inicio, fim)
                );
        }  
    }
    
    //---------- order
    
    public OrderGetter getOrderAttributeAsc(final String attributeName) {
        return new OrderGetter() {
            @Override
            public List getListOrder(CriteriaQuery query, CriteriaBuilder builder, Root root) {          
                return Arrays.asList(builder.asc(root.get(attributeName)));
            }
        };
    }
    
    public OrderGetter getOrderAttributeDesc(final String attributeName) {
        return new OrderGetter() {
            @Override
            public List getListOrder(CriteriaQuery query, CriteriaBuilder builder, Root root) {          
                return Arrays.asList(builder.desc(root.get(attributeName)));
            }
        };
    }
    
    //---------- path
    
    public Path getAttribute(Root root, String attributeName) {
        if (attributeName.contains(".")) { //ex: empresa.nome
            Path path = root;
            for (String name : attributeName.split("\\.")) {
                path = path.get(name);
            }
            return path;
        } else {
            return root.get(attributeName);
        }
    }
    
}
