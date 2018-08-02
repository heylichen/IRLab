package com.heylichen.ir.booleanretrieval;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.heylichen.ir.util.ResourceUtils;
import org.junit.Test;

/**
 * Created by Chen Li on 2017/5/29.
 */
public class SimpleInvertIndexerTest {

  @Test
  public void read() throws Exception {
    String path = "com/heylichen/ir/booleanretrieval/1docs.json";
    String json = null;

    json = ResourceUtils.readString(path, StandardCharsets.UTF_8);

    JSONArray ja = JSON.parseArray(json);
    List<JSONObject> list = ja.toJavaList(JSONObject.class);
    for (JSONObject jsonObject : list) {
      System.out.println(jsonObject.toJSONString());
    }
  }

  @Test
  public void indexTest() throws Exception {
    String path = "com/heylichen/ir/booleanretrieval/1docs.json";
    List<JSONObject> list = ResourceUtils.readJSONList(path, JSONObject.class);

    SimpleInvertIndexer simpleInvertIndexer = new SimpleInvertIndexer();
    Map<String, Set<String>> tokensToDocIds = simpleInvertIndexer.index(list);
    System.out.println(JSON.toJSONString(tokensToDocIds));
  }


  @Test
  public void indexTest2() throws Exception {
    String path = "com/heylichen/ir/booleanretrieval/2docs.json";
    List<JSONObject> list = ResourceUtils.readJSONList(path, JSONObject.class);

    SimpleInvertIndexer simpleInvertIndexer = new SimpleInvertIndexer();
    Map<String, Set<String>> tokensToDocIds = simpleInvertIndexer.index(list);
    System.out.println(JSON.toJSONString(tokensToDocIds));
  }

}