// java
package com.fastcampus.boardserver.config;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;

@Intercepts({
    @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})
})
public class SystemSqlLoggingInterceptor implements Interceptor {
    private static final Logger logger = LoggerFactory.getLogger("system");

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler handler = (StatementHandler) invocation.getTarget();
        BoundSql boundSql = handler.getBoundSql();
        Configuration configuration = (Configuration) handler.getParameterHandler()
                .getClass().getMethod("getConfiguration")
                .invoke(handler.getParameterHandler());
        String formatted = formatSql(configuration, boundSql);
        logger.info("[SQL_EXEC] {}", formatted);
        return invocation.proceed();
    }

    private String formatSql(Configuration configuration, BoundSql boundSql) {
        String sql = boundSql.getSql().replaceAll("\\s+", " ").trim();
        Object paramObj = boundSql.getParameterObject();
        List<ParameterMapping> paramMappings = boundSql.getParameterMappings();
        if (paramMappings == null || paramObj == null) {
            return sql;
        }

        MetaObject metaObject = configuration.newMetaObject(paramObj);
        for (ParameterMapping pm : paramMappings) {
            String prop = pm.getProperty();
            Object value;
            if (boundSql.hasAdditionalParameter(prop)) {
                value = boundSql.getAdditionalParameter(prop);
            } else if (metaObject.hasGetter(prop)) {
                value = metaObject.getValue(prop);
            } else {
                value = null;
            }

            String val;
            if (value == null) {
                val = "NULL";
            } else if (value instanceof String || value instanceof Date) {
                val = "'" + value.toString().replace("'", "''") + "'";
            } else {
                val = value.toString();
            }
            sql = sql.replaceFirst("\\?", Matcher.quoteReplacement(val));
        }
        return sql;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        // no-op
    }
}