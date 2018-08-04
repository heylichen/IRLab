package com.heylichen.ir.booleanretrieval;

import java.util.Objects;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Chen Li on 2018/8/4.
 */
@Getter
@Setter
public class Term implements Comparable<Term> {

  private String term;
  private int docFrequency;

  public Term(String term) {
    this.term = term;
    this.docFrequency = 0;
  }

  public Term(String term, int docFrequency) {
    this.term = term;
    this.docFrequency = docFrequency;
  }

  public void addDocFreqeuncy(int delta) {
    this.docFrequency += delta;
  }

  @Override
  public int compareTo(Term o) {
    return this.getTerm().compareTo(o.term);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Term term1 = (Term) o;
    return Objects.equals(term, term1.term);
  }

  @Override
  public int hashCode() {

    return Objects.hash(term);
  }
}
