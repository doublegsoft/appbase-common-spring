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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
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
public class PaginationControllerTest extends ControllerTestBase {

  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Test
  public void test() throws Exception {
    HttpClient hc = HttpClients.createDefault();
    HttpPost post = new HttpPost(ROOT_URL + "/api/paginate.do");
    // HttpPost post = new HttpPost("http://localhost:8080/paginate.do");
    List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
    urlParameters.add(new BasicNameValuePair("sqlId", "page"));
    urlParameters.add(new BasicNameValuePair("start", "0"));
    urlParameters.add(new BasicNameValuePair("limit", "20"));

    HttpEntity postParams = new UrlEncodedFormEntity(urlParameters);
    post.setEntity(postParams);
    HttpResponse resp = hc.execute(post);

    String json = processResponse(resp);
    System.out.println(json);
    Map<String, Object> page = (Map<String, Object>) JSON.parse(json);
    if (page.get("error") != null) {
      Assert.fail(page.get("error").toString());
    }
    Assert.assertEquals(100, page.get("total"));
    Assert.assertEquals(20, ((List) page.get("data")).size());
  }

}
