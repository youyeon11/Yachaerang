package com.yachaerang.backend.global.handler;

import com.yachaerang.backend.api.common.MemberStatus;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedTypes(MemberStatus.class)
public class MemberStatusTypeHandler extends BaseTypeHandler<MemberStatus> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, MemberStatus parameter, JdbcType jdbcType) throws SQLException {
        // enum의 name을 String으로 저장
        ps.setString(i, parameter.name());
    }

    @Override
    public MemberStatus getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return value == null ? null : MemberStatus.valueOf(value);
    }

    @Override
    public MemberStatus getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return value == null ? null : MemberStatus.valueOf(value);
    }

    @Override
    public MemberStatus getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return value == null ? null : MemberStatus.valueOf(value);
    }
}