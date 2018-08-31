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

package net.doublegsoft.appbase.service;

import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Autowired;

import net.doublegsoft.appbase.SqlParams;
import net.doublegsoft.appbase.dao.CommonDataAccess;
import net.doublegsoft.appbase.dao.DataAccessException;
import net.doublegsoft.appbase.service.AbstractService;
import net.doublegsoft.appbase.sql.SqlManager;

/**
 *
 *
 * @author <a href="mailto:guo.guo.gan@gmail.com">Christian Gann</a>
 *
 * @since 1.0
 */
public class ProgrammaticTransactionTestService extends AbstractService {

    private SqlManager sqlManager;

    public void failed() throws Exception {
        for (int i = 0; i < 100; i++) {
            SqlParams params = new SqlParams();
            params.set("id", i);
            params.set("name", "这是名字" + i);
            params.set("lmt", new Timestamp(System.currentTimeMillis()).toString());
            try {
                commonDataAccess.execute(sqlManager.getSql("insert", params));
                if (i == 30) {
                    throw new Exception();
                }
            } catch (DataAccessException ex) {
                throw new Exception("rollback");
            }
        }
    }

    public void success() throws Exception {
        for (int i = 0; i < 100; i++) {
            SqlParams params = new SqlParams();
            params.set("id", i);
            params.set("name", "这是名字" + i);
            params.set("lmt", new Timestamp(System.currentTimeMillis()).toString());
            try {
                commonDataAccess.execute(sqlManager.getSql("insert", params));
            } catch (DataAccessException ex) {
                throw new Exception("rollback");
            }
        }
    }

    @Override
    @Autowired
    public void setCommonDataAccess(CommonDataAccess commonDataAccess) {
        this.commonDataAccess = commonDataAccess;
    }

    @Autowired
    public void setSqlManager(SqlManager sqlManager) {
        this.sqlManager = sqlManager;
    }
}
