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
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.alibaba.fastjson.JSON;

import net.doublegsoft.appbase.ObjectMap;

/**
 * Resovles http request as a {@link ObjectMap} object to pass as a parameter within spring mvc framework.
 * <p>
 * The example configuration in spring: {@code
 * <bean id="objectMapResolver" class="net.doublegsoft.appbase.webmvc.ObjectMapArgumentResolver" lazy-init="false"/>
 * <bean class="org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter"
 *       p:customArgumentResolver-ref="objectMapResolver">
 * </bean>
 * }
 * 
 * @author <a href="mailto:guo.guo.gan@gmail.com">Christian Gann</a>
 * 
 * @since 1.0
 */
public class ObjectMapArgumentResolver implements HandlerMethodArgumentResolver, WebArgumentResolver {

  public static final String CONTENT_TYPE_JSON = "application/json";

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return parameter.getParameterType() == ObjectMap.class;
  }

  @Override
  public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
      NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
    ObjectMap retVal;
    HttpServletRequest req = webRequest.getNativeRequest(HttpServletRequest.class);
    if (CONTENT_TYPE_JSON.equals(req.getContentType())) {
      StringBuilder body = new StringBuilder();
      byte[] buff = new byte[4096];
      int len = 0;

      while ((len = req.getInputStream().read(buff)) != -1) {
        body.append(new String(buff, 0, len, Charset.forName("UTF-8")));
      }
      retVal = map2ObjectMap((Map<String, Object>) JSON.parse(body.toString()));
      if (body.length() == 0) {
        req.getParameterMap().entrySet().forEach(entry -> {
          String[] values = entry.getValue();
          retVal.put(entry.getKey(), values == null || values.length == 0 ? "" : values[0]);
        });
      }
    } else {
      retVal = new ObjectMap();
      Iterator<String> params = webRequest.getParameterNames();
      while (params.hasNext()) {
        String param = params.next();
        String[] values = webRequest.getParameterValues(param);
        if (values == null || values.length == 0) {
          continue;
        }
        if (param.contains("[]")) {
          param = param.replaceAll("\\[\\]", "");
          retVal.set(param, Arrays.asList(values));
        } else {
          retVal.set(param, values[0]);
        }
      }
    }

    return retVal;
  }

  @Override
  public Object resolveArgument(MethodParameter methodParameter, NativeWebRequest webRequest) throws Exception {
    if (methodParameter.getParameterType() != ObjectMap.class) {
      return UNRESOLVED;
    }
    return resolveArgument(methodParameter, null, webRequest, null);
  }

  private static ObjectMap map2ObjectMap(Map<String, Object> data) {
    ObjectMap retVal = new ObjectMap();
    if (data == null) {
      return retVal;
    }
    for (Map.Entry<String, Object> entry : data.entrySet()) {
      if (entry.getValue() == null) {
        retVal.put(entry.getKey(), null);
        continue;
      }
      if (entry.getValue().getClass() == JSONObject.class) {
        retVal.put(entry.getKey(), map2ObjectMap((Map<String, Object>) entry.getValue()));
      } else if (entry.getValue().getClass() == JSONArray.class) {
        JSONArray array = (JSONArray)entry.getValue();
        for (int i = 0; i < array.size(); i++) {
          Object obj = array.get(i);
          if (obj != null && obj.getClass() == JSONObject.class) {
            retVal.add(entry.getKey(), map2ObjectMap((Map<String, Object>) obj));
          } else {
            retVal.add(entry.getKey(), obj);
          }
        }
      } else {
        retVal.put(entry.getKey(), entry.getValue());
      }
    }
    return retVal;
  }

}
