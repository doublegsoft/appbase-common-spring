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
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import net.doublegsoft.appbase.JsonData;
import net.doublegsoft.appbase.ObjectMap;
import net.doublegsoft.appbase.domain.FileStoreService;

/**
 * It handles an input stream and store in specific directory.
 *
 * @author <a href="mailto:guo.guo.gan@gmail.com">Christian Gann</a>
 *
 * @since 1.0
 */
@RequestMapping("/api/file")
public class FileUploadController extends BaseController {

    public static final String ERR_FILE_TOO_BIG = "文件超大";

    public static final String ERR_DIRECTORY_NOT_FOUND = "未传入存储目录标识值";

    public static final String RET_FILE_STORE_PATH = "filepath";

    private static final Logger TRACER = LoggerFactory.getLogger(FileUploadController.class);

    private long maxSize = 1024 * 1024 * 1024;

    private FileStoreService fileStoreService;

    @RequestMapping("/upload")
    @ResponseBody
    public JsonData upload(ObjectMap criteria, @RequestParam("file") CommonsMultipartFile file) {
        JsonData retVal = new JsonData();
        if (file.getSize() > 1024000000) {
            retVal.error(ERR_FILE_TOO_BIG);
            return retVal;
        }
        String directoryKey = criteria.get("directoryKey");
        if (directoryKey == null) {
            retVal.error(ERR_DIRECTORY_NOT_FOUND);
            return retVal;
        }
        String ext = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);

        InputStream in = null;
        try {
            in = file.getInputStream();
            String filepath = getFileStoreService().store(getRequest().getServletContext().getRealPath("/"),
                    directoryKey, criteria, ext, in);
            retVal.set(RET_FILE_STORE_PATH, filepath);
        } catch (IOException ex) {
            TRACER.error(ex.getMessage(), ex);
        }
        return retVal;
    }

    public long getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(long maxSize) {
        this.maxSize = maxSize;
    }

    public FileStoreService getFileStoreService() {
        return fileStoreService;
    }

    public void setFileStoreService(FileStoreService fileStoreService) {
        this.fileStoreService = fileStoreService;
    }

}
