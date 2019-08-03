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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClients;
import org.junit.Ignore;

/**
 * @author gg
 *
 */
public class FileUploadControllerTest extends ControllerTestBase {

  @Ignore
  public void test_upload() throws Exception {
    File file = new File("src/test/resources/file/upload.mp4");

    HttpClient hc = HttpClients.createDefault();
    HttpPost post = new HttpPost(ROOT_URL + "/api/file/upload.do");

    MultipartEntityBuilder builder = MultipartEntityBuilder.create();
    builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
    builder.addTextBody("addvcd", "500102");
    builder.addTextBody("directoryKey", "demo");
    builder.addBinaryBody("file", file);

    HttpEntity entity = builder.build();
    post.setEntity(entity);

    HttpResponse resp = hc.execute(post);
    String status = processResponse(resp);
    // Assert.assertEquals("200", status);
  }

}
