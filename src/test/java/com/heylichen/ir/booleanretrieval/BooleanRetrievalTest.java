package com.heylichen.ir.booleanretrieval;

import java.util.Collections;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import org.junit.Test;

/**
 * Created by Chen Li on 2017/5/29.
 */
public class BooleanRetrievalTest {

  @Test
  public void and() throws Exception {
    BooleanRetrieval booleanRetrieval = new BooleanRetrieval();
    List<String> docIdsA = Lists.newArrayList("1", "2", "3", "4", "6", "9", "12", "13");
    List<String> docIdsB = Lists.newArrayList("1", "4", "6", "9", "13", "14", "19", "21");
    Collections.sort(docIdsA);
    Collections.sort(docIdsB);
    List<String> andResutls = booleanRetrieval.and(docIdsA, docIdsB);
    System.out.println(JSON.toJSONString(andResutls));
  }

  @Test
  public void or() throws Exception {
    BooleanRetrieval booleanRetrieval = new BooleanRetrieval();
    List<String> docIdsA = Lists.newArrayList("1", "2", "3", "4", "6", "9", "12", "13");
    List<String> docIdsB = Lists.newArrayList("1", "4", "6", "9", "13", "14", "19", "21");
    List<String> andResutls = booleanRetrieval.or(docIdsA, docIdsB);
    System.out.println(JSON.toJSONString(andResutls));
  }

  @Test
  public void or1() throws Exception {
    BooleanRetrieval booleanRetrieval = new BooleanRetrieval();
    List<String> docIdsA = Lists.newArrayList("1", "2", "3");
    List<String> docIdsB = Lists.newArrayList("4", "5");
    List<String> andResutls = booleanRetrieval.or(docIdsA, docIdsB);
    System.out.println(JSON.toJSONString(andResutls));
  }

  @Test
  public void andOr1() throws Exception {
    BooleanRetrieval booleanRetrieval = new BooleanRetrieval();
    List<String> docIdsA = Lists.newArrayList("1", "2", "3");
    List<String> docIdsB = Lists.newArrayList("1", "2", "3");
    List<String> andResutls = booleanRetrieval.andNot(docIdsA, docIdsB);
    System.out.println(JSON.toJSONString(andResutls));

  }


  @Test
  public void andOr2() throws Exception {
    BooleanRetrieval booleanRetrieval = new BooleanRetrieval();
    List<String> docIdsA = Lists.newArrayList("1", "2", "3");
    List<String> docIdsB = Lists.newArrayList();
    List<String> andResutls = booleanRetrieval.andNot(docIdsA, docIdsB);
    System.out.println(JSON.toJSONString(andResutls));

  }

  @Test
  public void andOr3() throws Exception {
    BooleanRetrieval booleanRetrieval = new BooleanRetrieval();
    List<String> docIdsA = Lists.newArrayList("1", "2", "3");
    List<String> docIdsB = Lists.newArrayList("1");
    List<String> andResutls = booleanRetrieval.andNot(docIdsA, docIdsB);
    System.out.println(JSON.toJSONString(andResutls));
  }

  @Test
  public void andOr4() throws Exception {
    BooleanRetrieval booleanRetrieval = new BooleanRetrieval();
    List<String> docIdsA = Lists.newArrayList("2", "3", "4");
    List<String> docIdsB = Lists.newArrayList("2");
    List<String> andResutls = booleanRetrieval.andNot(docIdsB, docIdsA);
    System.out.println(JSON.toJSONString(andResutls));
  }
}