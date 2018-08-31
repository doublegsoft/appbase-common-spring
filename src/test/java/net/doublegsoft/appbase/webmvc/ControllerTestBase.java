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

package net.doublegsoft.appbase.webmvc;

import java.io.File;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.sql.Timestamp;

import org.apache.catalina.startup.Tomcat;
import org.apache.http.HttpResponse;
import org.hsqldb.Server;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import net.doublegsoft.appbase.SqlParams;
import net.doublegsoft.appbase.dao.JdbcCommonDataAccess;
import net.doublegsoft.appbase.sql.SqlManager;

/**
 *
 *
 * @author <a href="mailto:guo.guo.gan@gmail.com">Christian Gann</a>
 *
 * @since 1.0
 */
public class ControllerTestBase {

  protected static final String ROOT_URL = "http://localhost:58080/";

  private static Tomcat tomcat = new Tomcat();

  private static Server hsqldb = new Server();

  @SuppressWarnings("resource")
  @BeforeClass
  public static void start() throws Exception {
    stop();
    hsqldb.setPort(28888);
    hsqldb.setDatabaseName(0, "test");
    hsqldb.setDatabasePath(0, "mem:test");
    hsqldb.start();

    ApplicationContext ctx = new ClassPathXmlApplicationContext("web/WEB-INF/spring-base.xml",
        "web/WEB-INF/spring-appbase.xml");
    SqlManager sm = ctx.getBean(SqlManager.class);
    JdbcCommonDataAccess cda = ctx.getBean(JdbcCommonDataAccess.class);
    try {
      cda.execute(sm.getSql("drop"));
    } catch (Exception ex) {

    }
    try {
      cda.execute(sm.getSql("create"));
    } catch (Exception ex) {

    }

    // insert
    for (int i = 0; i < 100; i++) {
      SqlParams params = new SqlParams();
      params.set("id", i);
      params.set("name", "这是名字" + i);
      params.set("lmt", new Timestamp(System.currentTimeMillis()).toString());
      cda.execute(sm.getSql("insert", params));
    }
    cda.close();

    tomcat.addWebapp("", new File("src/test/resources/web").getAbsolutePath());
    tomcat.setPort(58080);

    tomcat.start();
    // will block the continuous tests
    // tomcat.getServer().await();
  }

  @AfterClass
  public static void stop() throws Exception {
    tomcat.stop();
    tomcat.destroy();
    tomcat = new Tomcat();
    hsqldb.stop();
    hsqldb.shutdown();
  }

  protected String processResponse(HttpResponse resp) throws Exception {
    InputStreamReader reader = new InputStreamReader(resp.getEntity().getContent());
    StringWriter sw = new StringWriter();
    char[] buf = new char[1024];
    int len = 0;
    while ((len = reader.read(buf)) != -1) {
      sw.write(buf, 0, len);
    }
    return sw.toString();
  }

}
