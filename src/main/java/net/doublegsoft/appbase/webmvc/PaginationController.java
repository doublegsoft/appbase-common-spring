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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import net.doublegsoft.appbase.JsonData;
import net.doublegsoft.appbase.ObjectMap;
import net.doublegsoft.appbase.SqlParams;
import net.doublegsoft.appbase.service.CommonService;
import net.doublegsoft.appbase.service.ServiceException;

/**
 *
 *
 * @author <a href="mailto:guo.guo.gan@gmail.com">Christian Gann</a>
 *
 * @since 1.0
 */
@RequestMapping(value = "/api")
@RestController
public class PaginationController extends BaseController {

  private static final Logger TRACER = LoggerFactory.getLogger(PaginationController.class);

  private CommonService commonService;

  @PostMapping("/paginate")
  @ResponseBody
  public JsonData paginate(String sqlId, int start, int limit, ObjectMap params) {
    SqlParams sqlParams = new SqlParams().set(params);
    try {
      return new JsonData(commonService.paginate(sqlId, start, limit, sqlParams));
    } catch (ServiceException ex) {
      TRACER.error(ex.getMessage(), ex);
      return new JsonData().error(ex.getMessage());
    }
  }

  @PostMapping("/paginate2")
  @ResponseBody
  public JsonData paginate2(ObjectMap params) {
    Integer start = params.get("start");
    Integer limit = params.get("limit");
    String sqlId = params.get("sqlId");
    SqlParams sqlParams = new SqlParams().set(params);
    try {
      return new JsonData(commonService.paginate(sqlId, start, limit, sqlParams));
    } catch (ServiceException ex) {
      TRACER.error(ex.getMessage(), ex);
      return new JsonData().error(ex.getMessage());
    }
  }

  public void setCommonService(CommonService commonService) {
    this.commonService = commonService;
  }

}
