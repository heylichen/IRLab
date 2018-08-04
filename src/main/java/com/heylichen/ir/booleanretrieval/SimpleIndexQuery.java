package com.heylichen.ir.booleanretrieval;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Chen Li on 2018/8/4.
 */
@Getter
@Setter
public class SimpleIndexQuery {


  private Map<Term, List<String>> index;
  private BooleanRetrieval booleanRetrieval;

  /**
   * find docIds contains all terms
   */
  public List<String> andQuery(List<String> terms) {
    List<Term> orderedTerms = new ArrayList<>();
    for (String term : terms) {
      Term t = new Term(term);
      List<String> docIdsOfTerm = index.get(t);
      if (docIdsOfTerm != null) {
        t.setDocFrequency(docIdsOfTerm.size());
        orderedTerms.add(t);
      }
    }
    Collections.sort(orderedTerms);

    List<String> docIds = null;
    if (!orderedTerms.isEmpty()) {
      Term term = orderedTerms.get(0);
      List<String> docIdsOfTerm = index.get(term);
      docIds = docIdsOfTerm;
    } else {
      return Collections.emptyList();
    }
    for (int i = 1; i < orderedTerms.size(); i++) {
      Term term = orderedTerms.get(i);
      List<String> docIdsOfTerm = index.get(term);
      if (docIdsOfTerm == null) {
        continue;
      }
      docIds = booleanRetrieval.and(docIds, docIdsOfTerm);
    }
    return docIds;
  }
}
