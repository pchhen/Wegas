/*
 * Wegas
 * http://wegas.albasim.ch
 *
 * Copyright (c) 2013, 2014, 2015 School of Business and Engineering Vaud, Comem
 * Licensed under the MIT License
 */
package com.wegas.core.persistence;

import java.util.*;

/**
 * @author Cyril Junod <cyril.junod at gmail.com>
 */
public class ListUtils {

    /**
     * Convert a list of object to a map. The key is based on a unique
     * identifier.<br/> Example:
     * <pre>{@code
     *   ListUtils.ListKeyToMap<Long, Transition> converter = new ListUtils.ListKeyToMap<Long, Transition>() {
     *       public Long getKey(Transition item) {
     *           return item.getId(); //Assume an Id exists
     *       }
     *   }; Map<Long, Transition> transitionMap =
     * ListUtils.listAsMap(transitionList, converter); }</pre>
     *
     * @param <K>  The key class
     * @param <V>  The value class
     * @param list The List to convert
     * @param key  ListKeyToMap object
     * @return Map
     */
    public static <K, V> Map<K, V> listAsMap(Collection<V> list, ListKeyToMap<K, V> key) {
        Map<K, V> map = new HashMap<>();
        for (V item : list) {
            map.put(key.getKey(item), item);
        }
        return map;
    }

    /**
     * Simple interface to retrieve a key from an object.
     *
     * @param <K> the key class
     * @param <V> the object class
     */
    public interface ListKeyToMap<K, V> {

        /**
         * retrieve a key from an object
         *
         * @param item
         * @return the key
         */
        K getKey(V item);
    }

    /**
     * This function takes two lists and merge them. This does not preserve any order.
     * <br/> Assumptions:<br/>
     * - An element from the new list is new if it has no <code>ID</code> or if
     * it's <code>ID</code> is missing in the old list<br/>
     * - 2 Abstract entities with the same <code>ID</code> have to be
     * merged<br/>
     * - An element from the old list has to be removed if its <code>ID</code>
     * is missing in the new list
     *
     * @param <E>     extends (@see AbstractEntity) the element type
     * @param oldList The list containing old elements
     * @param newList The list containing new elements
     * @return A merged list
     */
    public static <E extends AbstractEntity> List<E> mergeLists(List<E> oldList, List<E> newList) {
        List<E> newElements = new ArrayList<>();
        for (Iterator<E> it = newList.iterator(); it.hasNext(); ) {                 //remove AbstractEntities without id and store them
            E element = it.next();
            if (element.getId() == null) {
                newElements.add(element);
                it.remove();
            }
        }
        ListUtils.ListKeyToMap<Long, E> converter = new ListUtils.ListKeyToMap<Long, E>() {
            @Override
            public Long getKey(E item) {
                return item.getId();
            }
        };
        Map<Long, E> elementMap = ListUtils.listAsMap(newList, converter);      //Create a map with newList based on Ids
        for (Iterator<E> it = oldList.iterator(); it.hasNext(); ) {
            E element = it.next();
            if (elementMap.containsKey(element.getId())) {                      //old element still exists
                element.merge(elementMap.get(element.getId()));                 //Then merge them
                elementMap.remove(element.getId());                             //remove element from map
            } else {
                it.remove();                                                    //else remove that old element
            }
        }
        for (Iterator<E> it = elementMap.values().iterator(); it.hasNext(); ) {  //Process remaining elements
            try {
                E element = it.next();
                E newElement = (E) element.getClass().newInstance();
                newElement.merge(element);
                newElements.add(newElement);
            } catch (InstantiationException | IllegalAccessException ex) {
            }
        }
        oldList.addAll(newElements);                                            //Add all new elements
        return oldList;
    }

    /**
     * This function takes two lists and replace the content of the first one with the content from the second one.<br/>
     * Merging elements with same <code>id</code> and preserving second list's order.
     *
     * @param <E>     extends (@see AbstractEntity) the element type
     * @param oldList The list containing old elements
     * @param newList The list containing new elements
     * @return A merged list
     */
    public static <E extends AbstractEntity> List<E> mergeReplace(List<E> oldList, List<E> newList) {
        ListUtils.ListKeyToMap<Long, E> converter = new ListUtils.ListKeyToMap<Long, E>() {
            @Override
            public Long getKey(E item) {
                return item.getId();
            }
        };
        Map<Long, E> elementMap = ListUtils.listAsMap(oldList, converter);      //Create a map with oldList based on Ids
        oldList.clear();
        for (Iterator<E> it = newList.iterator(); it.hasNext(); ) {
            E element = it.next();
            if (elementMap.containsKey(element.getId())) {                      //old element still exists
                elementMap.get(element.getId()).merge(element);                 //Then merge them
                oldList.add(elementMap.get(element.getId()));
            } else {
                try {
                    E newElement = (E) element.getClass().newInstance();
                    newElement.merge(element);
                    oldList.add(newElement);
                } catch (InstantiationException | IllegalAccessException ex) {
                }
            }
        }
        return oldList;
    }

    /**
     * make the old list content the same as newlist one
     * only references are copied, elements are not merged ever
     * 
     * @param <E>     extends (@see AbstractEntity) the element type
     * @param oldList The list containing old elements
     * @param newList The list containing new elements
     * @return A merged list
     */
    public static <E extends AbstractEntity> List<E> updateList(List<E> oldList, List<E> newList) {

        ListUtils.ListKeyToMap<Long, E> converter = new ListUtils.ListKeyToMap<Long, E>() {
            @Override
            public Long getKey(E item) {
                return item.getId();
            }
        };
        Map<Long, E> elementMap = ListUtils.listAsMap(newList, converter);      //Create a map with new list ids

        for (Iterator<E> it = oldList.iterator(); it.hasNext(); ) {
            E element = it.next();
            if (elementMap.containsKey(element.getId())) {                      //old element still exists
                elementMap.remove(element.getId());                             //remove element from map
            } else {
                it.remove();                                                    //else remove that old element
            }
        }
        oldList.addAll(elementMap.values());                                    //Add all new elements
        return oldList;
    }
}
