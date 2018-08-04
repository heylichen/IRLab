package com.heylichen.ir.booleanretrieval;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.heylichen.ir.util.ResourceUtils;
import org.junit.Test;

/**
 * Created by Chen Li on 2018/8/4.
 */
public class SimpleIndexQueryTest {

  @Test
  public void name() {

    String path = "com/heylichen/ir/booleanretrieval/1docs.json";
    List<JSONObject> list = ResourceUtils.readJSONList(path, JSONObject.class);

    SimpleInvertIndexer simpleInvertIndexer = new SimpleInvertIndexer();
    Map<Term, List<String>> tokensToDocIds = simpleInvertIndexer.index(list);

    SimpleIndexQuery indexQuery = new SimpleIndexQuery();
    indexQuery.setBooleanRetrieval(new BooleanRetrieval());
    indexQuery.setIndex(tokensToDocIds);

    List<String> docIds = indexQuery.andQuery(Arrays.asList("in", "home"));
    System.out.println(docIds);
  }
}