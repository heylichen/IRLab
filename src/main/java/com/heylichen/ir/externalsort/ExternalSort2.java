package com.heylichen.ir.externalsort;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author lichen2@tuniu.com
 * @Date 2018/7/31 11:13
 * @Description
 */
public class ExternalSort2 {

  private float useMemoryRatio = 0.5f;
  private int maxTmpFiles = 100;

  private long estimateAvailableMemory() {
    System.gc();
    // http://stackoverflow.com/questions/12807797/java-get-available-memory
    Runtime r = Runtime.getRuntime();
    long allocatedMemory = r.totalMemory() - r.freeMemory();
    long presFreeMemory = r.maxMemory() - allocatedMemory;
    return presFreeMemory;
  }

  private long estimateBestSizeOfBlocks(final long sizeOfFile, final long maxMemory, SortOptions options) {
    if (options.getBlockBytes() > 0) {
      return options.getBlockBytes();
    }
    // we don't want to open up much more than maxtmpfiles temporary
    // files, better run
    // out of memory first.
    long blockSize = (long) (maxMemory * useMemoryRatio);
    if (blockSize > sizeOfFile) {
      blockSize = sizeOfFile;
    }
    long tmpFiles = sizeOfFile / blockSize + 1;
    if (tmpFiles > maxTmpFiles) {
      throw new IllegalArgumentException("exceed max temp files ! max:" + maxTmpFiles + " need: " + tmpFiles);
    }
    return blockSize;
  }

  public void sort(SortOptions options) throws IOException {
    //
    SortContext context = new SortContext();
    context.setOptions(options);

    long availableMemory = estimateAvailableMemory();
    long inputBytes = context.getInput().length();
    long blockBytes = estimateBestSizeOfBlocks(inputBytes, availableMemory, options);
    int bufferSize = 1024 * 1024 * 10;
    context.setBufferSize(bufferSize);

    //sortAndSave
    List<String> lines = new ArrayList<>();
    List<File> tmpFiles = new ArrayList<>();
    try (BufferedReader bufferedReader = newBufferedReader(context.getInput(), context.getCharset(), bufferSize)) {
      String line = null;
      long tmpFileBytes = 0;
      while ((line = bufferedReader.readLine()) != null) {
        tmpFileBytes += StringSizeEstimator.estimatedSizeOf(line);
        lines.add(line + "\n");
        if (tmpFileBytes >= blockBytes) {
          Collections.sort(lines, context.getComparator());
//          if (tmpFiles.size() == 8) {
//            List<String> lines1 = Arrays.asList(lines.get(0), lines.get(lines.size() / 2), lines.get(lines.size() - 1));
//            System.out.println(lines1);
//            Collections.sort(lines, context.getComparator());
//            System.out.println(lines1);
//          }
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
    //merge files and sort
    PriorityQueue<BufferedLineReader> minPq =
        new PriorityQueue<>(12,
                            (a, b) -> context.getComparator().compare(a.peek(), b.peek()));
    for (File tmpFile : tmpFiles) {
      minPq.offer(new BufferedLineReader(newBufferedReader(tmpFile, context.getCharset(), bufferSize)));
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
        writeFile(outputFile, outputLines, options.getCharset(), bufferSize, true);
        fileBytes = 0;
        outputLines.clear();
      }
    }
    if (!outputLines.isEmpty()) {
      writeFile(outputFile, outputLines, options.getCharset(), bufferSize, true);
      outputLines.clear();
    }
  }

  private BufferedReader newBufferedReader(File file, Charset charset, int size) throws IOException {
    return new BufferedReader(new InputStreamReader(new FileInputStream(file), charset), size);
  }

  private BufferedWriter newBufferedWriter(File file, Charset charset, int size, boolean append) throws IOException {
    return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, append), charset), size);
  }

  private File writeTempFile(Integer index, List<String> lines, SortContext context, boolean append)
      throws IOException {
    SortOptions options = context.getOptions();
    File tmpFile =
        File.createTempFile(options.getTmpPrefix() + index + "_", options.getTmpPostfix(), context.getTempDirectory());
    return writeFile(tmpFile, lines, context.getCharset(), context.getBufferSize(), append);
  }

  private File writeFile(File tmpFile, List<String> lines, Charset charset, int size, boolean append)
      throws IOException {
    try (BufferedWriter writer = newBufferedWriter(tmpFile, charset, size, append)) {
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
