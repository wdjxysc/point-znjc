package com.whty.point.utils;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName:  JdbcUtils   
 * @Description:TODO(自定义的数据操作工具类)   
 * @author: 曾肖 
 * @date:   2019年5月30日 上午9:49:33   
 *     
 * @Copyright: 2019
 */
@Slf4j
public final class JdbcUtils {

    private static final Logger logger = LoggerFactory.getLogger(JdbcUtils.class);
    
    private static String driver;
    private static String url;
    private static String username;
    private static String password;
    private static String dbname;
    
    public static void initMysql(String url, String username, String password, String dbname) {
    	setDriver("com.mysql.jdbc.Driver");
        setUrl(url);
        setUsername(username);
        setPassword(password);
        setDbname(dbname);
    }
    
    @SuppressWarnings("unchecked")
	public static <T> T get(String sql, Object[] params) {
    	Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        T result = null;
    	try {
    		logger.info("SQL:" + sql);
    		logger.info("params:[{}]", params);
    		conn = getConn();
            pst = conn.prepareStatement(sql);
            setParams(pst, params);
            rs = pst.executeQuery();
            if (rs != null) {
            	if(rs.getRow() > 1) {
            		log.error("");
            		throw new Exception("返回了多个结果");
            	}
                ResultSetMetaData metaData = rs.getMetaData();
                int count = metaData.getColumnCount();
                if(count == 1) {
                	while (rs.next()) {
                        for (int i = 1; i <= count; i++) {
                        	String name = metaData.getColumnName(1);
                            Object value = rs.getObject(name);
                            result = (T) String.valueOf(value);
                        }
                    }
                }else {
                	 Map<String, Object> map = new LinkedHashMap<>();
                     for (int i = 1; i <= count; i++) {
                         String name = metaData.getColumnName(i);
                         Object value = rs.getObject(name);
                         map.put(name, value);
                     }
                     return (T) map;
                }
            }
		} catch (Exception e) {
			log.error("查询异常", e);
		}finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
    	return result;
    }
    
    public static List<Map<String, Object>> list(String sql, Object[] params) {
    	Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        List<Map<String, Object>> list = null;
    	try {
    		logger.info("SQL:" + sql);
    		logger.info("params:[{}]", params);
    		conn = getConn();
            pst = conn.prepareStatement(sql);
            setParams(pst, params);
            rs = pst.executeQuery();
            list = convertToMapList(rs);
		} catch (Exception e) {
    	    log.error("查询失败", e);
		}finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
    	return list;
    }
    
    private static void setParams(PreparedStatement pstmt, Object[] params) throws SQLException {
        if (params != null && params.length > 0) {
            int i = 1;
            for (Object param : params) {
                pstmt.setObject(i++, param);
            }
        }
    }
    private static List<Map<String, Object>> convertToMapList(ResultSet rs) throws SQLException {
        List<Map<String, Object>> mapList = new ArrayList<>();
        if (rs != null) {
            ResultSetMetaData metaData = rs.getMetaData();
            int count = metaData.getColumnCount();
            while (rs.next()) {
                Map<String, Object> map = new LinkedHashMap<>();
                for (int i = 1; i <= count; i++) {
                    String name = metaData.getColumnName(i);
                    Object value = rs.getObject(i);
                    map.put(name, value);
                }
                mapList.add(map);
            }
        }
        return mapList;
    }
    private static Connection getConn() {
        Connection conn = null;
        try {
        	//classLoader,加载对应驱动
//            Class.forName("com.mysql.cj.jdbc.Driver");
            Class.forName("com.mysql.jdbc.Driver");
            conn = (Connection) DriverManager.getConnection(getUrl(), getUsername(), getPassword());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    public static int update(String sql){
        Connection conn = null;
        PreparedStatement pst = null;
        int result = -1;
        try {
            conn = getConn();
            pst = conn.prepareStatement(sql);
            result = pst.executeUpdate();
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return result;
    }

    public static boolean insert(String sql){
        Connection conn = null;
        PreparedStatement pst = null;
        boolean result = false;
        try {
            conn = getConn();
            pst = conn.prepareStatement(sql);
            result = pst.execute(sql);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return result;
    }
	public static String getDriver() {
		return driver;
	}

	public static void setDriver(String driver) {
		JdbcUtils.driver = driver;
	}

	public static String getUrl() {
		return url;
	}

	public static void setUrl(String url) {
		JdbcUtils.url = url;
	}

	public static String getUsername() {
		return username;
	}

	public static void setUsername(String username) {
		JdbcUtils.username = username;
	}

	public static String getPassword() {
		return password;
	}

	public static void setPassword(String password) {
		JdbcUtils.password = password;
	}

	public static String getDbname() {
		return dbname;
	}

	public static void setDbname(String dbname) {
		JdbcUtils.dbname = dbname;
	}
	
}
