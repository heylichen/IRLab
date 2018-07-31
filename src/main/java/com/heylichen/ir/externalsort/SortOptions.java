package com.heylichen.ir.externalsort;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Chen Li on 2018/7/31.
 */
@Setter
@Getter
public class SortOptions {

  private File input;
  private File output;
  private File tempDirectory;
  private long blockBytes;
  private Charset charset;
  private Comparator<String> comparator;
  private String tmpPrefix;
  private String tmpPostfix;

  private SortOptions(File input, File output, File tempDirectory, Charset charset,
                      Comparator<String> comparator, String tmpPrefix, String tmpPostfix) {
    this.input = input;
    this.output = output;
    this.tempDirectory = tempDirectory;
    this.charset = charset;
    this.comparator = comparator;
    this.tmpPrefix = tmpPrefix;
    this.tmpPostfix = tmpPostfix;
  }

  public static final SortOptionsBuilder builder(File input, File tmpDirectory) {
    SortOptionsBuilder builder = new SortOptionsBuilder();
    return builder.input(input).tmpDirectory(tmpDirectory);
  }

  public static final class SortOptionsBuilder {

    private File input;
    private File output;
    private File tmpDirectory;
    private Charset charset;
    private Comparator<String> comparator;
    private String tmpPrefix;
    private String tmpPostfix;

    private SortOptionsBuilder() {
      setDefaultValues();
    }

    private void setDefaultValues() {
      this.comparator = Comparator.naturalOrder();
      this.tmpPrefix = "sort_";
      this.tmpPostfix = ".tmp";
      this.charset = StandardCharsets.UTF_8;
    }

    public SortOptionsBuilder input(File input) {
      this.input = input;
      return this;
    }

    public SortOptionsBuilder output(File output) {
      this.output = output;
      return this;
    }

    public SortOptionsBuilder tmpDirectory(File tmpDirectory) {
      this.tmpDirectory = tmpDirectory;
      return this;
    }

    public SortOptionsBuilder charset(Charset charset) {
      this.charset = charset;
      return this;
    }

    public SortOptionsBuilder comparator(Comparator<String> comparator) {
      this.comparator = comparator;
      return this;
    }

    public SortOptionsBuilder tmpPrefix(String tmpPrefix) {
      this.tmpPrefix = tmpPrefix;
      return this;
    }

    public SortOptionsBuilder tmpPostfix(String tmpPostfix) {
      this.tmpPostfix = tmpPostfix;
      return this;
    }

    public SortOptions build() {
      return new SortOptions(input, output, tmpDirectory, charset, comparator, tmpPrefix, tmpPostfix);
    }
  }
}
