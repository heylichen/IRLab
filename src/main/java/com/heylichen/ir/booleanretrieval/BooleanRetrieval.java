package com.heylichen.ir.booleanretrieval;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Chen Li on 2017/5/29.
 */
public class BooleanRetrieval {

  public List<String> and(List<String> a, List<String> b) {
    boolean aEmpty = a == null || a.isEmpty();
    boolean bEmpty = b == null || b.isEmpty();
    if (aEmpty || bEmpty) {
      return Collections.emptyList();
    }
    int i = 0;
    int j = 0;
    List<String> results = new ArrayList<>();
    while (i < a.size() && j < b.size()) {
      String docIdOfA = a.get(i);
      String docIdOfB = b.get(j);
      int compare = docIdOfA.compareTo(docIdOfB);
      if (compare == 0) {
        i++;
        j++;
        results.add(docIdOfA);
      } else if (compare < 0) {
        i++;
      } else if (compare > 0) {
        j++;
      }
    }
    return results;
  }

  public List<String> or(List<String> a, List<String> b) {
    boolean aEmpty = a == null || a.isEmpty();
    boolean bEmpty = b == null || b.isEmpty();
    if (aEmpty && bEmpty) {
      return Collections.emptyList();
    } else if (aEmpty && !bEmpty) {
      return b;
    } else if (!aEmpty && bEmpty) {
      return a;
    }

    int i = 0;
    int j = 0;
    List<String> results = new ArrayList<>();
    while (i < a.size() && j < b.size()) {
      String docIdOfA = a.get(i);
      String docIdOfB = b.get(j);
      int compare = docIdOfA.compareTo(docIdOfB);
      if (compare == 0) {
        i++;
        j++;
        results.add(docIdOfA);
      } else if (compare < 0) {
        i++;
        results.add(docIdOfA);
      } else if (compare > 0) {
        j++;
        results.add(docIdOfB);
      }
    }
    if (i < a.size()) {
      results.addAll(a.subList(i, a.size()));
    }
    if (j < b.size()) {
      results.addAll(b.subList(j, b.size()));
    }
    return results;

  }

  public List<String> andNot(List<String> a, List<String> b) {
    boolean bEmpty = b == null || b.isEmpty();
    if (bEmpty) {
      return a;
    }

    boolean aEmpty = a == null || a.isEmpty();
    if (aEmpty) {
      return Collections.emptyList();
    }

    int i = 0;
    int j = 0;
    List<String> results = new ArrayList<>();
    while (i < a.size() && j < b.size()) {
      String docIdOfA = a.get(i);
      String docIdOfB = b.get(j);
      int compare = docIdOfA.compareTo(docIdOfB);
      if (compare == 0) {
        i++;
        j++;
      } else if (compare < 0) {
        i++;
        results.add(docIdOfA);
      } else if (compare > 0) {
        j++;
      }
    }
    if (j >= b.size() && i < a.size()) {
      results.addAll(a.subList(i, a.size()));
    }
    return results;
  }

}
