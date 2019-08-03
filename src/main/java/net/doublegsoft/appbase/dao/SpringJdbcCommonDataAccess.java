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

import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import net.doublegsoft.appbase.ObjectMap;
import net.doublegsoft.appbase.Pagination;

/**
 *
 *
 * @author <a href="mailto:guo.guo.gan@gmail.com">Christian Gann</a>
 *
 * @since 1.0
 */
@Component
public class SpringJdbcCommonDataAccess implements CommonDataAccess {

  private JdbcTemplate jdbc;

  /**
   * @see net.doublegsoft.appbase.dao.CommonDataAccess#insert(java.lang.String)
   */
  @Override
  public ObjectMap insert(String sql) throws DataAccessException {
    KeyHolder holder = new GeneratedKeyHolder();
    jdbc.update(new PreparedStatementCreator() {

      @Override
      public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
        PreparedStatement retVal = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        return retVal;
      }
    }, holder);

    return new ObjectMap(holder.getKeys());
  }

  /**
   * @see net.doublegsoft.appbase.dao.CommonDataAccess#execute(java.lang.String)
   */
  @Override
  public void execute(String sql) throws DataAccessException {
    jdbc.update(sql);
  }

  /**
   * @see net.doublegsoft.appbase.dao.CommonDataAccess#paginate(java.lang.String, int, int)
   */
  @Override
  public Pagination<ObjectMap> paginate(String sql, int start, int limit) throws DataAccessException {
    Pagination<ObjectMap> retVal = new Pagination<>();
    String countSql = "select count(*) from (" + sql + ") as subquery;";
    retVal.setTotal(value(countSql));
    jdbc.query(sql, new RowMapper<ObjectMap>() {
      /**
       * @see org.springframework.jdbc.core.RowMapper#mapRow(java.sql.ResultSet, int)
       */
      @Override
      public ObjectMap mapRow(ResultSet rs, int rowNum) throws SQLException {
        if ((start + limit < rowNum) && limit > 0) {
          return null;
        }
        ResultSetMetaData metadata = rs.getMetaData();
        int columnCount = metadata.getColumnCount();
        ObjectMap obj = new ObjectMap();
        for (int i = 1; i <= columnCount; ++i) {
          obj.set(metadata.getColumnLabel(i), rs.getObject(i));
        }
        retVal.add(obj);
        return null;
      }
    });
    return retVal;
  }

  /**
   * @see net.doublegsoft.appbase.dao.CommonDataAccess#list(java.lang.String)
   */
  @Override
  public List<List<Object>> list(String sql) throws DataAccessException {
    List<List<Object>> retVal = new ArrayList<>();
    jdbc.query(sql, new RowMapper<ObjectMap>() {
      /**
       * @see org.springframework.jdbc.core.RowMapper#mapRow(java.sql.ResultSet, int)
       */
      @Override
      public ObjectMap mapRow(ResultSet rs, int rowNum) throws SQLException {
        ResultSetMetaData metadata = rs.getMetaData();
        List<Object> row = new ArrayList<>();
        int columnCount = metadata.getColumnCount();
        for (int i = 1; i <= columnCount; ++i) {
          row.add(rs.getObject(i));
        }
        retVal.add(row);
        return null;
      }
    });
    return retVal;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.doublegsoft.appbase.dao.CommonDataAccess#many2(java.lang.String)
   */
  @Override
  public <T> List<T> many2(String sql) throws DataAccessException {
    return null;
  }

  @Override
  public void batch(List<String> sqls) throws DataAccessException {
    // TODO Auto-generated method stub

  }

  @Override
  public void prepare(String sql, Object... objs) throws DataAccessException {

  }

  @Override
  public List<ObjectMap> prepareQuery(String sql, Object... objs) throws DataAccessException {
    return null;
  }

  @Override
  public <T> Pagination<T> paginate(String sql, Class<T> klass, int start, int limit) throws DataAccessException {
    return null;
  }

  @Override
  public List<ObjectMap> many(String sql) throws DataAccessException {
    return null;
  }

  @Override
  public <T> List<T> many(String sql, Class<T> klass) throws DataAccessException {
    return null;
  }

  @Override
  public <T> T single(String sql, Class<T> klass) throws DataAccessException {
    return null;
  }

  @Override
  public List<ObjectMap> call(String sql) throws DataAccessException {
    return null;
  }

  /**
   * @see net.doublegsoft.appbase.dao.CommonDataAccess#value(java.lang.String)
   */
  @SuppressWarnings("unchecked")
  @Override
  public <T> T value(String sql) throws DataAccessException {
    return (T) jdbc.queryForObject(sql, Object.class);
  }

  /**
   * @see net.doublegsoft.appbase.dao.CommonDataAccess#single(java.lang.String)
   */
  @Override
  public ObjectMap single(String sql) throws DataAccessException {
    return new ObjectMap(jdbc.queryForMap(sql));
  }

  @Autowired
  public void setDataSource(DataSource dataSource) {
    jdbc = new JdbcTemplate(dataSource);
  }
}
