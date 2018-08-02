package com.heylichen.ir.booleanretrieval;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.springframework.util.CollectionUtils;

/**
 * Created by Chen Li on 2017/5/29.
 */
public class SimpleInvertIndexer {

  private Splitter splitter = Splitter.on(" ").trimResults();

  public Map<String, Set<String>> index(List<JSONObject> docs) {
    Map<String, List<String>> docIdToTokens = tokenize(docs);
    docIdToTokens = normalize(docIdToTokens);
    return genInvertIndex(docIdToTokens);
  }

  /**
   * tokenize the documents
   * expect a document to have id, content fields.
   */
  public Map<String, List<String>> tokenize(List<JSONObject> docs) {
    if (CollectionUtils.isEmpty(docs)) {
      return Collections.emptyMap();
    }
    Map<String, List<String>> docIdToTokens = new HashMap<>();
    for (JSONObject doc : docs) {
      String id = doc.getString("id");
      String content = doc.getString("content");
      List<String> tokens = splitter.splitToList(content);

      docIdToTokens.put(id, Lists.newArrayList(tokens));
    }
    return docIdToTokens;
  }

  public Map<String, List<String>> normalize(Map<String, List<String>> docIdToTokens) {
    return docIdToTokens;
  }


  /**
   * generate invert index
   * key is term, value is docId list in whitch the term occurs.
   * result map sorted by key.
   *
   * @return a Map, key is term, value is the docId list.
   */
  public Map<String, Set<String>> genInvertIndex(Map<String, List<String>> docIdToTokens) {
    if (docIdToTokens == null || docIdToTokens.isEmpty()) {
      return Collections.emptyMap();
    }
    Map<String, Set<String>> termsToDocIds = new TreeMap<>();
    for (Map.Entry<String, List<String>> stringListEntry : docIdToTokens.entrySet()) {
      String docId = stringListEntry.getKey();
      List<String> tokens = stringListEntry.getValue();

      if (tokens == null || tokens.isEmpty()) {
        continue;
      }
      for (String token : tokens) {
        Set<String> containingDocIds = termsToDocIds.computeIfAbsent(token, k -> new TreeSet<>());
        containingDocIds.add(docId);
        termsToDocIds.put(token, containingDocIds);
      }
    }

    return termsToDocIds;
  }
}
