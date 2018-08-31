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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

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
@RequestMapping("/api")
public class FormController extends BaseController {

    private static final Logger TRACER = LoggerFactory.getLogger(FormController.class);

    private CommonService commonService;

    /**
     * Saves the form data in database. And is applied in ajax post.
     * 
     * <p>
     * The form data are encapsulated in {@link javax.servlet.http.HttpServletRequest} object which could be accessed in
     * spring framework internally.
     * 
     * @param sqlId
     *            the sql id
     * 
     * @param criteria
     *            the criteria which encapsulates http request parameters
     * 
     * @return the empty json data if succeeded; the json data with error message if failed
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @ResponseBody
    public JsonData save(String sqlId, ObjectMap criteria) {
        if (sqlId == null) {
            sqlId = criteria.get("sqlId");
        }
        SqlParams params = new SqlParams().set(criteria);
        try {
            commonService.execute(sqlId, params);
        } catch (ServiceException ex) {
            TRACER.error(ex.getMessage(), ex);
            return new JsonData().error(ex.getMessage());
        }
        return new JsonData();
    }

    /**
     * Gets an unique form data with the given id name and value pair. And is applied in ajax post.
     * 
     * <p>
     * The form data are encapsulated in {@link javax.servlet.http.HttpServletRequest} object which could be accessed in
     * spring framework internally.
     * 
     * @param sqlId
     *            the sql id
     * 
     * @param idName
     *            the id name which is used in sql template
     * 
     * @param idValue
     *            the id value
     * 
     * @return the json data with form data if succeeded or the json data with error message if failed
     */
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    @ResponseBody
    public JsonData read(String sqlId, String idName, String idValue) {
        SqlParams params = new SqlParams().set(idName, idValue);
        try {
            ObjectMap obj = commonService.single(sqlId, params);
            return new JsonData(obj);
        } catch (ServiceException ex) {
            TRACER.error(ex.getMessage(), ex);
            return new JsonData().error(ex.getMessage());
        }
    }

    @Autowired
    public void setCommonService(CommonService commonService) {
        this.commonService = commonService;
    }
}
