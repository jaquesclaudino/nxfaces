package com.nexten.nxfaces.dao;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Selection;

/**
 *
 * @author jaques
 * @param <T>
 */
public class CriteriaGetter<T> {
    
    public enum DateFilterType { BEGIN_OR_END, BEGIN, END }
    
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
    
    public SelectionGetter getSelectionEntityCountDistinct() {
        return new SelectionGetter() {
            @Override
            public Selection getSelection(CriteriaQuery query, CriteriaBuilder builder, Root root) {
                return builder.countDistinct(root);
            }
        };
    }
    
    public SelectionGetter getSelectionEntityAttribute(String attributeName, boolean distinct) {
        return new SelectionGetter() {
            @Override
            public Selection getSelection(CriteriaQuery query, CriteriaBuilder builder, Root root) {
                query.distinct(distinct);
                return root.get(attributeName);
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
        if (queryString != null && !queryString.trim().isEmpty()) {                
            return builder.like(builder.lower(expression), "%" + queryString.toLowerCase() + "%");
        } else {
            return null;
        }
    }

    public Predicate betweenDate(CriteriaBuilder builder, Root root, String attributeNameBegin, Date begin, String attributeNameEnd, Date end, DateFilterType dateFilterType) {
        switch (dateFilterType) {
            case BEGIN: return builder.between(root.get(attributeNameBegin), begin, end);
            case END: return builder.between(root.get(attributeNameEnd), begin, end);
            default: //BEGIN_OR_END:
                return builder.or( 
                    builder.between(root.get(attributeNameBegin), begin, end),
                    builder.between(root.get(attributeNameEnd), begin, end)
                );
        }  
    }
    
    //---------- order
    
    public OrderGetter<T> getOrderAttributeAsc(final String... attributeNames) {
        return new OrderGetter<T>() {
            @Override
            public List<Order> getListOrder(CriteriaQuery<T> query, CriteriaBuilder builder, Root<T> root) {
                return Arrays.stream(attributeNames)
                        .map(attributeName -> builder.asc(root.get(attributeName)))
                        .collect(Collectors.toList());
            }
        };
    }
    
    public OrderGetter<T> getOrderAttributeDesc(final String... attributeNames) {
        return new OrderGetter<T>() {
            @Override
            public List<Order> getListOrder(CriteriaQuery<T> query, CriteriaBuilder builder, Root<T> root) {
                return Arrays.stream(attributeNames)
                        .map(attributeName -> builder.desc(root.get(attributeName)))
                        .collect(Collectors.toList());
            }
        };
    }
    
    //---------- path
    
    public Path getAttribute(Root root, String attributeName) {
        if (attributeName.contains(".")) { //ex: company.name
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
