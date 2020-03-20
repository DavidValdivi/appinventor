// -*- mode: java; c-basic-offset: 2; -*-
// Copyright 2017-2020  MIT, All rights reserve
// Released under the Apache License, Version 2.0
// http://www.apache.org/licenses/LICENSE-2.0

package com.google.appinventor.components.runtime.util;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Range;
import com.google.common.collect.TreeRangeSet;

/**
 * Java implementation of string utility methods for use in Scheme calls.
 * Used for ease of reasoning about the solution or to address issues
 * with Kawa (e.g. memory problems).
 * See runtime.scm
 */
public class JavaStringUtils {
  /**
   * Auxiliary class for replaceAllMappings that defines
   * the mapping application order for a given List of keys.
   * Default option is to do nothing with the order, which represents
   * the dictionary order.
   */
  private static class MappingOrder {
    /**
     * Changes the order of the specified key list
     * @param keys  List of keys
     * @param text  Text in which to search for keys
     */
    public void changeOrder(List<String> keys, String text) {
      // Default option: Do nothing (dictionary order)
    }
  }

  /**
   * Changes the key order in descending order of key length.
   */
  private static class MappingLongestStringFirstOrder extends MappingOrder {
    @Override
    public void changeOrder(List<String> keys, String text) {
      Collections.sort(keys, new Comparator<String>() {
        @Override
        public int compare(String s, String t1) {
          // Sort in descending order of string length
          return Integer.compare(t1.length(), s.length());
        }
      });
    }
  }

  /**
   * Changes the key order based on the earliest occurrences in the original
   * text string.
   *
   * TODO: If we do re-implement earliest occurrence at any time, it should
   * TODO: probably take the regex order (so that the keys are replaced in
   * TODO: order of actual occurrence variably) rather than the current
   * TODO: static order implemented here.
   */
  private static class MappingEarliestOccurrenceFirstOrder extends MappingOrder {
    @Override
    public void changeOrder(List<String> keys, String text) {
      // Construct a map for first index of occurrence for String
      final Map<String, Integer> occurrenceIndices = new HashMap<>();

      // TODO: Can we optimize the O(mn) loop with m = length of text,
      // TODO: n = number of keys?
      for (String key : keys) {
        int firstIndex = text.indexOf(key);

        // No first index; Key should gain less priority than
        // other occurrences (this value can be arbitrary)
        if (firstIndex == -1) {
          firstIndex = text.length() + occurrenceIndices.size();
        }

        // Map key to first index of occurrence
        occurrenceIndices.put(key, firstIndex);
      }

      Collections.sort(keys, new Comparator<String>() {
        @Override
        public int compare(String s, String t1) {
          // Sort in ascending order by first index in String
          int id1 = occurrenceIndices.get(s);
          int id2 = occurrenceIndices.get(t1);

          if (id1 == id2) {
            // Use longer string instead if indices equal
            return Integer.compare(t1.length(), s.length());
          } else {
            // Take smaller index first
            return Integer.compare(id1, id2);
          }
        }
      });
    }
  }

  public static final String LOG_TAG_JOIN_STRINGS = "JavaJoinListOfStrings";
  private static final boolean DEBUG = false;

  /**
   * Since mapping orders do not have state, we initialize
   * fixed final MappingOrders to use for replaceAllMappings.
   */
  private static final MappingOrder mappingOrderDictionary;
  private static final MappingOrder mappingOrderLongestStringFirst;
  private static final MappingOrder mappingOrderEarliestOccurrence;

  static {
    mappingOrderDictionary = new MappingOrder();
    mappingOrderLongestStringFirst = new MappingLongestStringFirstOrder();
    mappingOrderEarliestOccurrence = new MappingEarliestOccurrenceFirstOrder();
  }


  /**
   * Java implementation of join-strings since the Kawa version appears to run of space.
   * See runtime.scm
   *
   * The elements in listOString are Kawa strings, but these are
   * not necessarily Java Strings.   They might be FStrings.   So we
   * accept a list of Objects and use toString to do a conversion.
   *
   * Implements the following operation
   *
   * (define join-strings (strings separator)
   *    (JavaJoinListOfStrings:joinStrings strings separator))
   *
   * I'm writing this in Java, rather than using Kawa in runtime.scm
   * because Kawa seems to blow out memory (or stack?) on small-memory systems
   * and large lists.
   *
   * @author halabelson@google.com (Hal Abelson)
   */
  public static String joinStrings(List<Object> listOfStrings, String separator) {
    // We would use String.join, but that is Java 8
    if (DEBUG) {
      Log.i(LOG_TAG_JOIN_STRINGS, "calling joinStrings");
    }
    return join(listOfStrings, separator);
  }

  private static String join(List<Object> list, String separator)
  {
    StringBuilder sb = new StringBuilder();
    boolean first = true;
    for (Object item : list)
    {
      if (first)
        first = false;
      else
        sb.append(separator);
      sb.append(item.toString());
    }
    return sb.toString();
  }

  /**
   * Replaces the specified text string with the specified mappings in
   * dictionary element order.
   *
   * @see #replaceAllMappings(String, Map, MappingOrder)
   * @see MappingOrder
   * @param text      Text to apply mappings to
   * @param mappings  Map containing mappings
   * @return Text with the mappings applied
   */
  public static String replaceAllMappingsDictionaryOrder(String text, Map<Object, Object> mappings) {
    return replaceAllMappings(text, mappings, mappingOrderDictionary);
  }

  /**
   * Replaces the specified text string with the specified mappings in
   * longest string first order.
   *
   * @see #replaceAllMappings(String, Map, MappingOrder)
   * @see MappingLongestStringFirstOrder
   * @param text      Text to apply mappings to
   * @param mappings  Map containing mappings
   * @return Text with the mappings applied
   */
  public static String replaceAllMappingsLongestStringOrder(String text, Map<Object, Object> mappings) {
    return replaceAllMappings(text, mappings, mappingOrderLongestStringFirst);
  }

  /**
   * Replaces the specified text string with the specified mappings in
   * earliest occurrence first order.
   *
   * @see #replaceAllMappings(String, Map, MappingOrder)
   * @see MappingEarliestOccurrenceFirstOrder
   * @param text      Text to apply mappings to
   * @param mappings  Map containing mappings
   * @return Text with the mappings applied
   */
  public static String replaceAllMappingsEarliestOccurrenceOrder(String text, Map<Object, Object> mappings) {
    return replaceAllMappings(text, mappings, mappingOrderEarliestOccurrence);
  }

  /**
   * Replaces the specified text string with the specified mappings,
   * which is a map containing Key Value pairs where the key is the
   * substring to replace, and the value is the value to replace the substring with.
   * An order parameter is specified to indicate the order of mapping applications.
   *
   * @param text     Text to apply mappings to
   * @param mappings Map containing mappings
   * @param order    Order to use for replacing mappings
   * @return Text with the mappings applied
   */
  public static String replaceAllMappings(String text, Map<Object, Object> mappings, MappingOrder order) {
    // Iterate over all the mappings
    Iterator<Map.Entry<Object, Object>> it = mappings.entrySet().iterator();

    // Construct a new map for <String, String> mappings in order to support
    // look-ups for non-pure String values (e.g. numbers)
    Map<String, String> stringMappings = new HashMap<>();

    // Construct a new List to store the Map's keys
    List<String> keys = new ArrayList<>();

    while (it.hasNext()) {
      Map.Entry<Object, Object> current = it.next();

      // Get Key & Value as strings
      // This is needed to convert any non-String values to a String,
      // e.g. numbers to String literals
      String key = current.getKey().toString();
      String value = current.getValue().toString();

      // Add key only if it was not added before (to reduce potential
      // redundancy on potentially duplicate keys)
      if (!stringMappings.containsKey(key)) {
        keys.add(key);
      }

      // Update map
      stringMappings.put(key, value);
    }

    // Change the order of the keys based on the given Order object
    // TODO: If we have stream support, we can sort the stringMappings map
    // TODO: directly provided we initialize it to a LinkedHashMap.
    // TODO: It could save some memory in the long run.
    order.changeOrder(keys, text);

    // Apply mappings owith re-ordered keys and mappings
    return applyMappings(text, stringMappings, keys);
  }

  /**
   * Auxiliary function to apply the mappings provided in the form of a map
   * to the given text string. A supplementary keys list is provided to specify
   * key order (the first element in the list is the first key to replace,
   * the second is the second and so on).
   *
   * The method applies mappings by making use of a range set that keeps track of
   * which indices have been replaced to avoid conflicts. Each key is traversed
   * one by one, and the ranges of replacement are updated, provided that the
   * range is not already overlapped/enclosed by other ranges. Finally, once
   * we have all the ranges, the ranges are replaced in order of descending
   * end point of the range so as to keep the previous range end points invariable
   * (if we replace the largest end point, and there are no overlaps, no range
   * will ever hit the start point of the last range, therefore all indices
   * of the previous range remain unaffected)
   *
   * TODO: By optimizing the way strings are replaced (reducing substrings
   * TODO: to use on smaller strings), we can achieve better runtime complexity
   * TODO: on this end.
   *
   * @param text      Text to apply mappings to
   * @param mappings  Mappings in the form {String -> String}
   * @param keys      List of keys to replace (all values must exist in the mappings map)
   * @return  String with the mappings applied
   */
  private static String applyMappings(String text, Map<String, String> mappings, List<String> keys) {
    // Create a set of ranges to keep track of which index ranges in the
    // original text string are already set for replacement.
    TreeRangeSet<Integer> ranges = TreeRangeSet.create();

    // Map to map Range to String to replace with.
    // E.g. [1, 3) -> 'abcd' indicates that the substring from 1 to 3 exclusive should
    // be replaced with the string 'ab'.
    // Note that the length of the string does not matter.
    Map<Range<Integer>, String> replacements = new TreeMap<>(new Comparator<Range<Integer>>() {
      @Override
      public int compare(Range<Integer> r1, Range<Integer> r2) {
        // Sort in descending order
        return Integer.compare(r2.upperEndpoint(), r1.upperEndpoint());
      }
    });

    // Range construction step: Iterate through all the keys,
    // and fill in ranges set & replacements map.
    for (String key : keys) {
      // Convert key to pattern, and create a matcher to find all
      // occurrences of the current string.
      Pattern keyPattern = Pattern.compile(Pattern.quote(key));
      Matcher matcher =  keyPattern.matcher(text);

      // Keep track of the String to replace key with
      String replacement = mappings.get(key);

      // Iterate until the key can no longer be found in text.
      while (matcher.find()) {
        // Get start & end indices of the string to be replaced.
        int startId = matcher.start();
        int endId = matcher.end();

        // Create a closed open range (closed since startId is inclusive,
        // and open because endId is exclusive)
        Range<Integer> range = Range.closedOpen(startId, endId);

        // Check for overlap. If startId & (endId - 1) are already contained
        // in our ranges, that means we overlap with the current ranges,
        // and should not consider this range.
        boolean inRange = ranges.contains(startId) || ranges.contains(endId - 1);

        // No overlap; Update ranges set & replacements map
        if (!inRange) {
          ranges.add(range);
          replacements.put(range, replacement);
        }
      }
    }

    // Go through each entry that we want to replace. Since we used
    // a TreeMap, we have an order that will not break things;
    // We first replace the substring with the largest end index, which,
    // because of overlap, will not affect the previous range indices
    // because no ranges overlap in our range set.
    // If we did not have this order, then we would have to update all indices
    // of all ranges upon replacement.
    for (Map.Entry<Range<Integer>, String> replaceEntry : replacements.entrySet()) {
      // Get range end points
      int startId = replaceEntry.getKey().lowerEndpoint();
      int endId = replaceEntry.getKey().upperEndpoint();

      // Combine strings: L + M + R, where:
      // L - substring from start of string until endpoint
      // M - middle string (the one that we use as replacement)
      // R - remainder of the string after replacement
      String left = text.substring(0, startId);
      String middle = replaceEntry.getValue();
      String end = text.substring(endId);
      text = left + middle + end;
    }

    return text;
  }
}
