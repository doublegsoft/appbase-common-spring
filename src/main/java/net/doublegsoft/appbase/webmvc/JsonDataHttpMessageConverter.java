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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.charset.Charset;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.StreamUtils;

import net.doublegsoft.appbase.JsonData;

/**
 * Converts http response as a {@link JsonData} object within spring mvc framework.
 * <p>
 * The example configuration in spring: {@code
 * <bean class="org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter" 
 *       p:customArgumentResolver-ref="objectMapResolver">
 *     <property name="messageConverters">
 *         <array>
 *             <bean class="org.springframework.http.converter.StringHttpMessageConverter">
 *                 <property name="supportedMediaTypes" value="text/plain;charset=UTF-8" />
 *             </bean>
 *             <bean class="net.doublegsoft.appbase.webmvc.JsonDataHttpMessageConverter"/>
 *         </array>
 *     </property>
 * </bean>
 * }
 *
 * @author <a href="mailto:guo.guo.gan@gmail.com">Christian Gann</a>
 *
 * @since 1.0
 */
public class JsonDataHttpMessageConverter extends AbstractHttpMessageConverter<JsonData> {

    /**
     * 
     */
    public JsonDataHttpMessageConverter() {
        super(new MediaType("text", "json", Charset.forName("utf-8")));
    }

    /**
     * @see org.springframework.http.converter.AbstractHttpMessageConverter#supports(java.lang.Class)
     */
    @Override
    protected boolean supports(Class<?> clazz) {
        return JsonData.class == clazz;
    }

    /**
     * @see org.springframework.http.converter.AbstractHttpMessageConverter#readInternal(java.lang.Class,
     *      org.springframework.http.HttpInputMessage)
     */
    @Override
    protected JsonData readInternal(Class<? extends JsonData> clazz, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {
        ObjectInputStream ois = new ObjectInputStream(inputMessage.getBody());
        try {
            return (JsonData) ois.readObject();
        } catch (ClassNotFoundException ex) {
            throw new IOException(ex);
        }
    }

    /**
     * @see org.springframework.http.converter.AbstractHttpMessageConverter#writeInternal(java.lang.Object,
     *      org.springframework.http.HttpOutputMessage)
     */
    @Override
    protected void writeInternal(JsonData t, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {
        StreamUtils.copy(t.toString(), Charset.forName("utf-8"), outputMessage.getBody());
    }
}
