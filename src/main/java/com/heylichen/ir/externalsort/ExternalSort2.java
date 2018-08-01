package com.heylichen.ir.externalsort;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author lichen2@tuniu.com
 * @Date 2018/7/31 11:13
 * @Description
 */
public class ExternalSort2 {

  private float maxUseMemoryRatio = 0.2f;

  private long estimateAvailableMemory() {
    System.gc();
    // http://stackoverflow.com/questions/12807797/java-get-available-memory
    Runtime r = Runtime.getRuntime();
    long allocatedMemory = r.totalMemory() - r.freeMemory();
    long presFreeMemory = r.maxMemory() - allocatedMemory;
    return presFreeMemory;
  }

  private long estimateBestSizeOfBlocks(final long maxMemory, SortOptions options) {
    // we don't want to open up much more than maxtmpfiles temporary
    // files, better run
    // out of memory first.
    long blockSize = (long) (maxMemory * maxUseMemoryRatio);
    if (options.getMaxBlockBytes() > 0 && blockSize > options.getMaxBlockBytes()) {
      blockSize = options.getMaxBlockBytes();
    }
    return blockSize;
  }

  public SortContext sort(SortOptions options) throws IOException {
    //
    SortContext context = new SortContext();
    context.setDetails(new JSONObject());
    context.setOptions(options);

    long availableMemory = estimateAvailableMemory();
    long blockBytes = estimateBestSizeOfBlocks(availableMemory, options);
    int bufferSize = 1024 * 1024 * 10;
    context.setBufferSize(bufferSize);

    //sortAndSave
    List<String> lines = new ArrayList<>();
    List<File> tmpFiles = new ArrayList<>();
    try (BufferedReader bufferedReader = newBufferedReader(context.getInput(), context.getCharset(), bufferSize,
                                                           false)) {
      String line = null;
      long tmpFileBytes = 0;
      while ((line = bufferedReader.readLine()) != null) {
        tmpFileBytes += StringSizeEstimator.estimatedSizeOf(line);
        lines.add(line + "\n");
        if (tmpFileBytes >= blockBytes) {
          Collections.sort(lines, context.getComparator());
          File tmpFile = writeTempFile(tmpFiles.size(), lines, context, false);
          tmpFiles.add(tmpFile);
          lines.clear();
          tmpFileBytes = 0;
        }
      }
      if (!lines.isEmpty()) {
        Collections.sort(lines, context.getComparator());
        File tmpFile = writeTempFile(tmpFiles.size(), lines, context, false);
        tmpFiles.add(tmpFile);
        lines.clear();
      }
    }
    context.setDetails("tmpFiles", tmpFiles.size());
    //merge files and sort
    PriorityQueue<BufferedLineReader> minPq =
        new PriorityQueue<>(12,
                            (a, b) -> context.getComparator().compare(a.peek(), b.peek()));
    for (File tmpFile : tmpFiles) {
      minPq.offer(
          new BufferedLineReader(newBufferedReader(tmpFile, context.getCharset(), bufferSize, options.isGzip())));
    }

    List<String> outputLines = new ArrayList<>();
    File outputFile = options.getOutput();
    long fileBytes = 0;
    String line = null;
    while (!minPq.isEmpty()) {
      BufferedLineReader lineReader = minPq.remove();
      line = lineReader.peek();
      outputLines.add(line + "\n");
      lineReader.reload();
      if (!lineReader.isEmpty()) {
        minPq.offer(lineReader);
      } else {
        lineReader.close();
      }
      fileBytes += StringSizeEstimator.estimatedSizeOf(line);
      if (fileBytes >= blockBytes) {
        writeFile(outputFile, outputLines, options.getCharset(), bufferSize, true, false);
        fileBytes = 0;
        outputLines.clear();
      }
    }
    if (!outputLines.isEmpty()) {
      writeFile(outputFile, outputLines, options.getCharset(), bufferSize, true, false);
      outputLines.clear();
    }
    return context;
  }

  private BufferedReader newBufferedReader(File file, Charset charset, int size, boolean gzip) throws IOException {
    InputStream in = new FileInputStream(file);
    if (gzip) {
      in = new GZIPInputStream(in);
    }
    return new BufferedReader(new InputStreamReader(in, charset), size);
  }

  private BufferedWriter newBufferedWriter(File file, Charset charset, int size, boolean append, boolean gzip)
      throws IOException {
    OutputStream os = new FileOutputStream(file, append);
    if (gzip) {
      os = new GZIPOutputStream(os);
    }
    return new BufferedWriter(new OutputStreamWriter(os, charset), size);
  }

  private File writeTempFile(Integer index, List<String> lines, SortContext context, boolean append)
      throws IOException {
    SortOptions options = context.getOptions();
    String postfix = options.getTmpPostfix();
    if (options.isGzip()) {
      postfix += ".gz";
    }
    File tmpFile =
        File.createTempFile(options.getTmpPrefix() + index + "_", postfix, context.getTempDirectory());
    tmpFile.deleteOnExit();
    return writeFile(tmpFile, lines, context.getCharset(), context.getBufferSize(), append, options.isGzip());
  }

  private File writeFile(File tmpFile, List<String> lines, Charset charset, int size, boolean append, boolean gzip)
      throws IOException {
    try (BufferedWriter writer = newBufferedWriter(tmpFile, charset, size, append, gzip)) {
      for (String line : lines) {
        writer.write(line);
      }
      return tmpFile;
    }
  }

  @Getter
  @Setter
  public static final class BufferedLineReader implements Closeable {

    private BufferedReader bufferedReader;
    private String cacheLine;

    public BufferedLineReader(BufferedReader bufferedReader) throws IOException {
      this.bufferedReader = bufferedReader;
      reload();
    }

    public String peek() {
      return cacheLine;
    }

    public void reload() throws IOException {
      this.cacheLine = bufferedReader.readLine();
    }

    public boolean isEmpty() {
      return cacheLine == null;
    }

    @Override
    public void close() throws IOException {
      bufferedReader.close();
    }
  }
}
