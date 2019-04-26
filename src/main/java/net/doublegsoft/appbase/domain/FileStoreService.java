/*
 * Copyright 2016 doublegsoft.net
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.doublegsoft.appbase.domain;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import net.doublegsoft.appbase.ObjectMap;

/**
 * It handles an input stream and store in specific directory.
 *
 * @author <a href="mailto:guo.guo.gan@gmail.com">Christian Gann</a>
 *
 * @since 1.0
 */
public class FileStoreService {

  private final static Configuration FREEMARKER = new Configuration(Configuration.getVersion());

  private static Map<String, String> templates = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

  private static Map<String, Template> cache = new HashMap<>();

  private static final StringTemplateLoader TEMPLATE_LOADER = new StringTemplateLoader();

  static {
    FREEMARKER.setTemplateLoader(TEMPLATE_LOADER);
  }

  public void setResources(Map<String, String> resources) {
    templates.putAll(resources);
  }

  /**
   * Stores the http file input stream in a file.
   * 
   * @param realPath
   *          the real path in the server
   * 
   * @param directoryKey
   *          the directory key related with a specific directory to store, defined in spring
   * 
   * @param paths
   *          the path variables applying to template rendering
   * 
   * @param ext
   *          the file extension name
   * 
   * @param inputStream
   *          the input stream
   * 
   * @return the generated relative file path
   * 
   * @throws IOException
   *           in case of any errors
   */
  public String store(String realPath, String directoryKey, ObjectMap paths, String ext, InputStream inputStream)
      throws IOException {
    String fp = filepath(realPath, directoryKey, paths, ext);
    synchronized (FileStoreService.class) {
      FileOutputStream fileOutput = new FileOutputStream(new File(realPath, fp));
      if (inputStream == null) {
        return null;
      }
      byte[] buff = new byte[4096];
      int len;
      while ((len = inputStream.read(buff)) != -1) {
        fileOutput.write(buff, 0, len);
      }
      fileOutput.flush();
      fileOutput.close();
      inputStream.close();
    }
    return fp;
  }

  private String filepath(String realPath, String directoryKey, ObjectMap paths, String ext) throws IOException {
    Template tpl = cache.get(directoryKey);
    preset(paths);
    StringWriter writer = new StringWriter();
    if (tpl == null) {
      TEMPLATE_LOADER.putTemplate(directoryKey, templates.get(directoryKey));
      tpl = FREEMARKER.getTemplate(directoryKey, "UTF-8");
      cache.put(directoryKey, tpl);
    }
    try {
      tpl.process(paths, writer);
    } catch (TemplateException ex) {
      throw new IOException(ex);
    }
    writer.flush();
    writer.close();

    StringBuilder retVal = new StringBuilder();
    String dir = writer.toString();
    if (dir.indexOf("/") != 0) {
      dir = "/" + dir;
    }
    if (dir.lastIndexOf("/") != dir.length() - 1) {
      dir += "/";
    }
    retVal.append(dir);
    File d = new File(realPath, dir);
    if (!d.exists()) {
      d.mkdirs();
    }
    synchronized (FileStoreService.class) {
      String fn = System.currentTimeMillis() + "." + ext;
      retVal.append(fn);
    }
    return retVal.toString();
  }

  private void preset(ObjectMap paths) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(new Date());
    int year = cal.get(Calendar.YEAR);
    int month = cal.get(Calendar.MONTH) + 1;
    int day = cal.get(Calendar.DAY_OF_MONTH);
    paths.put("yearMonth", String.format("%04d%02d", year, month));
    paths.put("yearMonthDay", String.format("%04d%02d%02d", year, month, day));
  }

  public FileStoreService() {

  }
}
