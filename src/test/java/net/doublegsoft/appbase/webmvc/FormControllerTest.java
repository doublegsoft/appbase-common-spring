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

import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.junit.Assert;
import org.junit.Test;

import com.alibaba.fastjson.JSON;

/**
 *
 *
 * @author <a href="mailto:guo.guo.gan@gmail.com">Christian Gann</a>
 *
 * @since 1.0
 */
public class FormControllerTest extends ControllerTestBase {

  @Test
  public void testPost() throws Exception {
    HttpClient hc = HttpClients.createDefault();
    HttpPost post = new HttpPost(ROOT_URL + "/api/save.do");
    List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
    urlParameters.add(new BasicNameValuePair("sqlId", "insert"));
    // urlParameters.add(new BasicNameValuePair("id", "101"));
    urlParameters.add(new BasicNameValuePair("id", "100"));
    urlParameters.add(new BasicNameValuePair("name", "还是中文"));
    urlParameters.add(new BasicNameValuePair("lmt", new Timestamp(System.currentTimeMillis()).toString()));
    HttpEntity postParams = new UrlEncodedFormEntity(urlParameters, Charset.forName("utf-8"));
    post.setEntity(postParams);
    HttpResponse resp = hc.execute(post);
    String respString = processResponse(resp);
    Assert.assertEquals("{}", respString);
  }

  @Test
  public void testPostJson() throws Exception {
    HttpClient hc = HttpClients.createDefault();
    HttpPost post = new HttpPost(ROOT_URL + "/api/save.do");
    Map<String, Object> obj = new HashMap<>();
    obj.put("sqlId", "insert");
    obj.put("id", "101");
    obj.put("name", "还是中文");
    obj.put("lmt", new Timestamp(System.currentTimeMillis()).toString());

    StringEntity entity = new StringEntity(JSON.toJSONString(obj), "UTF-8");
    post.addHeader("content-type", "application/json");
    post.setEntity(entity);
    HttpResponse resp = hc.execute(post);
    String respString = processResponse(resp);
    Assert.assertEquals("{}", respString);
  }

  @Test
  public void testGet() throws Exception {
    HttpClient hc = HttpClients.createDefault();
    HttpGet get = new HttpGet(ROOT_URL + "/api/get.do?sqlId=id&idName=id&idValue=5&success=200&error=500");
    HttpResponse resp = hc.execute(get);
    String respString = processResponse(resp);
    Assert.assertNotNull(respString);
  }

}
