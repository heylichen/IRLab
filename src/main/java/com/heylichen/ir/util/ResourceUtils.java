package com.heylichen.ir.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

/**
 * Created by Chen Li on 2017/5/29.
 */
public class ResourceUtils {
  private static Logger LOGGER = LoggerFactory.getLogger(ResourceUtils.class);

  private ResourceUtils() {
  }

  public static File loadClasspathFile(String classpath) {
    ClassPathResource cpr = new ClassPathResource(classpath);
    try {
      return cpr.getFile();
    } catch (IOException e) {
      LOGGER.error("error getting file:{}. ", classpath, e);
      return null;
    }
  }

  public static String readString(String classpath, Charset cs) {
    File file = loadClasspathFile(classpath);
    byte[] bytes = new byte[0];
    try {
      bytes = Files.readAllBytes(file.toPath());
      return new String(bytes, cs);
    } catch (IOException e) {
      LOGGER.error("error reading file:{}, charset:{} ", classpath, cs, e);
      return null;
    }
  }

  public static <T> List<T> readJSONList(String classpath, Class<T> clazz) {
    String json = null;
    json = readString(classpath, StandardCharsets.UTF_8);
    JSONArray ja = JSON.parseArray(json);
    return ja.toJavaList(clazz);
  }

  public static <T> T readJSONObject(String classpath, Class<T> clazz) {
    String json = null;
    json = readString(classpath, StandardCharsets.UTF_8);
    return JSON.parseObject(json).toJavaObject(clazz);
  }
}
