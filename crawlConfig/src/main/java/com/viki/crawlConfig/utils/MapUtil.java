package com.viki.crawlConfig.utils;

import java.util.*;

public class MapUtil {

    public static boolean isAsc = true;

    /**
     * 如果是复杂对象，就按这些对象的值的多少排序
     * @param map
     * @return
     */
    public static<K,V> Map<K, V> sortMapByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(
                map.entrySet());
        Collections.sort(list,
                new Comparator<Map.Entry<K, V>>() {
                    public int compare(Map.Entry<K, V> o1,
                                       Map.Entry<K, V> o2) {
                        if(o1.getValue() instanceof Comparable){
                            return ((Comparable)o2.getValue()).compareTo((Comparable)o1.getValue());
                        }else if(o1.getValue() instanceof Collection){
                            return ((Collection)o2.getValue()).size() - ((Collection)o1.getValue()).size();
                        }else{
                            return 0;
                        }
                    }
                });

        Map<K, V> result = new LinkedHashMap<K, V>();
        for (Iterator<Map.Entry<K, V>> it = list.iterator(); it.hasNext();) {
            Map.Entry<K, V> entry = it.next();
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

}
