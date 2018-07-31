package com.heylichen.ir.externalsort;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Comparator;

import lombok.Getter;
import lombok.Setter;


/**
 * @Author lichen2@tuniu.com
 * @Date 2018/7/31 13:34
 * @Description
 */
@Setter
@Getter
public class SortContext {

  private SortOptions options;
  private int bufferSize;

  public File getInput() {
    return getOptions().getInput();
  }

  public File getTempDirectory() {
    return getOptions().getTempDirectory();
  }

  public Charset getCharset() {
    return getOptions().getCharset();
  }

  public Comparator<String> getComparator() {
    return getOptions().getComparator();
  }
}
