package com.heylichen.ir.util;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Created by Chen Li on 2018/7/31.
 */
public final class FilteUtil {

  public static final File createClasspathFile(String classpath) throws URISyntaxException {
    URL url = FilteUtil.class.getResource(classpath);
    return new File(url.toURI());
  }
}
