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

package net.doublegsoft.appbase.dao;

import java.sql.Timestamp;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import net.doublegsoft.appbase.ObjectMap;
import net.doublegsoft.appbase.Pagination;
import net.doublegsoft.appbase.SqlParams;
import net.doublegsoft.appbase.service.DeclarativeTransactionTestService;
import net.doublegsoft.appbase.service.ProgrammaticTransactionTestService;
import net.doublegsoft.appbase.service.ServiceException;
import net.doublegsoft.appbase.sql.SqlManager;

/**
 *
 *
 * @author <a href="mailto:guo.guo.gan@gmail.com">Christian Gann</a>
 *
 * @since 1.0
 */
public class JdbcCommonDataAccessTest {

    static ApplicationContext ctxt;

    @BeforeClass
    public static void setup() throws Exception {
        ctxt = new ClassPathXmlApplicationContext("spring-common.xml");
        SqlManager sm = ctxt.getBean(SqlManager.class);
        CommonDataAccess cda = ctxt.getBean(JdbcCommonDataAccess.class);
        try {
            String sql = sm.getSql("drop");
            cda.execute(sql);
        } catch (Exception ex) {

        }
        try {
            String sql = sm.getSql("create");
            cda.execute(sql);
        } catch (Exception ex) {

        }
    }

    /**
     * test access.
     */
    @Test
    public void testCommonDataAccess() throws Exception {
        SqlManager sm = ctxt.getBean(SqlManager.class);
        CommonDataAccess cda = ctxt.getBean(JdbcCommonDataAccess.class);
        cda.execute(sm.getSql("delete"));

        // insert
        for (int i = 0; i < 100; i++) {
            SqlParams params = new SqlParams();
            params.set("id", i);
            params.set("name", "这是名字" + i);
            params.set("lmt", new Timestamp(System.currentTimeMillis()).toString());
            cda.execute(sm.getSql("insert", params));
        }

        // count
        Assert.assertEquals(100, (long) cda.value(sm.getSql("count")));

        // page
        Pagination<ObjectMap> page = cda.paginate(sm.getSql("page"), 0, 20);
        Assert.assertEquals(100, page.getTotal());
        Assert.assertEquals(20, page.getData().size());
        ObjectMap first = page.getData().get(0);
        Assert.assertEquals(new Long(0), first.get("id"));
        Assert.assertEquals("这是名字0", first.get("name"));

        // single
        SqlParams params = new SqlParams();
        params.set("id", 55);
        ObjectMap obj = cda.single(sm.getSql("id", params));
        Assert.assertEquals(new Long(55), obj.get("id"));
        Assert.assertEquals("这是名字55", obj.get("name"));

    }

    /**
     * test programmatic service
     */
    @Test
    public void testProgrammaticTransactionalWithSingleService() throws Exception {
        DeclarativeTransactionTestService service = ctxt.getBean(DeclarativeTransactionTestService.class);
        service.setAutoTransactionManagement(false);
        SqlManager sm = ctxt.getBean(SqlManager.class);
        CommonDataAccess cda = ctxt.getBean(JdbcCommonDataAccess.class);
        cda.execute(sm.getSql("delete"));
        service.begin();
        try {
            service.success();
            service.failed();
            service.commit();
        } catch (Exception ex) {
            service.rollback();
        }
        Assert.assertEquals(0, (long) cda.value(sm.getSql("count")));
        service.begin();
        service.success();
        service.commit();
        Assert.assertEquals(100, (long) cda.value(sm.getSql("count")));
    }

    @Test
    public void testProgrammaticTransactionalWithinMultiServices() throws Exception {
        DeclarativeTransactionTestService service0 = ctxt.getBean(DeclarativeTransactionTestService.class);
        ProgrammaticTransactionTestService service1 = ctxt.getBean(ProgrammaticTransactionTestService.class);
        SqlManager sm = ctxt.getBean(SqlManager.class);
        CommonDataAccess cda = ctxt.getBean(JdbcCommonDataAccess.class);
        cda.execute(sm.getSql("delete"));
        service0.begin();
        try {
            service1.success();
            service0.failed();
            service0.commit();
        } catch (Exception ex) {
            service0.rollback();
        }
        Assert.assertEquals(0, (long) cda.value(sm.getSql("count")));
        service0.begin();
        service0.success();
        service0.commit();
        Assert.assertEquals(100, (long) cda.value(sm.getSql("count")));
    }

    @Test
    public void testDeclarativeTransactionalWithSingleService() throws Exception {
        DeclarativeTransactionTestService service = ctxt.getBean(DeclarativeTransactionTestService.class);
        service.setAutoTransactionManagement(true);
        SqlManager sm = ctxt.getBean(SqlManager.class);
        CommonDataAccess cda = ctxt.getBean(JdbcCommonDataAccess.class);
        cda.execute(sm.getSql("delete"));
        try {
            service.failed();
        } catch (Exception ex) {
        }
        Assert.assertEquals(0, (long) cda.value(sm.getSql("count")));
        service.success();
        Assert.assertEquals(100, (long) cda.value(sm.getSql("count")));
        service.setAutoTransactionManagement(false);
    }

    @Test
    public void testDeclarativeTransactionalWithSingleService0() throws Exception {
        DeclarativeTransactionTestService service = ctxt.getBean(DeclarativeTransactionTestService.class);
        // service.setAutoTransactionManagement(true);
        SqlManager sm = ctxt.getBean(SqlManager.class);
        CommonDataAccess cda = ctxt.getBean(JdbcCommonDataAccess.class);
        cda.execute(sm.getSql("delete"));
        try {
            service.successAndFailed();
        } catch (Exception ex) {
            if (ex.getClass() != ServiceException.class) {
                Assert.fail(ex.getMessage());
            }
        }
        Assert.assertEquals(0, (long) cda.value(sm.getSql("count")));
        service.success();
        Assert.assertEquals(100, (long) cda.value(sm.getSql("count")));
        // service.setAutoTransactionManagement(false);
    }

    @Test
    public void testDeclarativeTransactionalWithinMultiServices() throws Exception {
        DeclarativeTransactionTestService dservice = ctxt.getBean(DeclarativeTransactionTestService.class);
        SqlManager sm = ctxt.getBean(SqlManager.class);
        CommonDataAccess cda = ctxt.getBean(JdbcCommonDataAccess.class);
        cda.execute(sm.getSql("delete"));
        try {
            dservice.success();
            dservice.failed();
        } catch (Exception ex) {

        }
        Assert.assertEquals(100, (long) cda.value(sm.getSql("count")));

        try {
            dservice.success();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        Assert.assertEquals(100, (long) cda.value(sm.getSql("count")));
    }

    @Test
    public void testParallel() throws Exception {
        DeclarativeTransactionTestService service = ctxt.getBean(DeclarativeTransactionTestService.class);

        for (int i = 0; i < 1000; ++i) {
            Thread t = new Thread(() -> {
                DeclarativeTransactionTestService srv = ctxt.getBean(DeclarativeTransactionTestService.class);
                try {
                    srv.success();
                } catch (Exception ex) {

                }
                System.out.println(Thread.currentThread().getName() + " finished.");
            });
            t.start();
            System.out.println(t.getName() + " started.");
        }
        CommonDataAccess cda = ctxt.getBean(JdbcCommonDataAccess.class);
        SqlManager sm = ctxt.getBean(SqlManager.class);
        Assert.assertEquals(100, (long) cda.value(sm.getSql("count")));
        service.setAutoTransactionManagement(false);
    }
}
