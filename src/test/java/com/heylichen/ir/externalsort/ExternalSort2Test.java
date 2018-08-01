package com.heylichen.ir.externalsort;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

/**
 * Created by Chen Li on 2018/7/31.
 */
public class ExternalSort2Test {

  @Test
  public void generateInput() throws Exception {
    DecimalFormat df = new DecimalFormat("0000000");
    File input = new File("C:\\work\\github\\IRLab\\src\\test\\resources\\com\\heylichen\\ir\\externalsort\\input.txt");
    List<String> list = new ArrayList<>();
    int size = 5000000;
    for (int i = 0; i < size; i++) {
      list.add(df.format(size - i));
    }
    FileUtils.writeLines(input, list);
  }

  @Test
  public void externalSort() throws Exception {
    ExternalSort2 sort = new ExternalSort2();
    File input = new File("C:\\work\\github\\IRLab\\src\\test\\resources\\com\\heylichen\\ir\\externalsort\\input.txt");
    File output =
        new File("C:\\work\\github\\IRLab\\src\\test\\resources\\com\\heylichen\\ir\\externalsort\\output.txt");
    File tmpDir = new File("C:\\work\\github\\IRLab\\src\\test\\resources\\com\\heylichen\\ir\\externalsort");
    SortOptions options = SortOptions.builder(input, tmpDir)
        .output(output)
        .maxBlockBytes(1024 * 1024 * 100)
        .gzip(false)
        .build();
    long start = System.currentTimeMillis();
    SortContext context = sort.sort(options);
    long end = System.currentTimeMillis();
    System.out.println("using " + (end - start) + " ms");

  }
}