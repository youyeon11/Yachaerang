package com.yachaerang.backend.global.util;

import com.yachaerang.backend.api.common.CodeEnum;
import com.yachaerang.backend.global.exception.GeneralException;
import com.yachaerang.backend.global.response.ErrorCode;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CodeEnumTypeHandler<E extends Enum<E> & CodeEnum>
        extends BaseTypeHandler<E> {

    private final Class<E> type;

    /*
    Type에 따른 생성자
     */
    public CodeEnumTypeHandler(Class<E> type) {
        if (type == null){
            LogUtil.error("type이 비어있습니다.");
            throw GeneralException.of(ErrorCode.INTERNAL_SERVER_ERROR);
        }
        this.type = type;
    }

    /*
    Enum -> VARCHAR 변환(Enum.name 기반)
     */
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i,
                                    E parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.getCode());
    }

    /*
    DB의 VARCHAR -> Enum으로 변환
     */
    @Override
    public E getNullableResult(ResultSet rs, String columnName)
            throws SQLException {
        String code = rs.getString(columnName);
        return code == null ? null : fromCode(code);
    }

    @Override
    public E getNullableResult(ResultSet rs, int columnIndex)
            throws SQLException {
        String code = rs.getString(columnIndex);
        return code == null ? null : fromCode(code);
    }

    @Override
    public E getNullableResult(CallableStatement cs, int columnIndex)
            throws SQLException {
        String code = cs.getString(columnIndex);
        return code == null ? null : fromCode(code);
    }

    /*
    각각의 Enum 클래스에 대한 fromCode 메서드 호출
     */
    private E fromCode(String code) {
        try {
            Method method = type.getMethod("fromCode", String.class);
            LogUtil.info("Received code from DB: " + code);
            LogUtil.info("Enum type: " + type.getName());

            for (E enumConstant : type.getEnumConstants()) {
                LogUtil.info("Enum: " + enumConstant.name() + ", Code: " + enumConstant.getCode());
            }

            return (E) method.invoke(null, code);
        } catch (InvocationTargetException e) {
            LogUtil.error("InvocationTargetException occurred", e.getCause());
            throw GeneralException.of(ErrorCode.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            LogUtil.error("Exception occurred", e);
            throw GeneralException.of(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}