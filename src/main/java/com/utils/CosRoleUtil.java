package com.utils;
//ceshi
import org.apache.commons.lang3.StringUtils;

public final class CosRoleUtil {
    public static final String ADMIN = "ADMIN";
    public static final String USER = "USER";
    public static final String DESIGNER = "DESIGNER";

    private CosRoleUtil() {}

    public static String normalize(String role, String tableName) {
        if ("users".equalsIgnoreCase(StringUtils.defaultString(tableName))) {
            return ADMIN;
        }
        if ("yonghu".equalsIgnoreCase(StringUtils.defaultString(tableName))) {
            return USER;
        }
        if ("shejishi".equalsIgnoreCase(StringUtils.defaultString(tableName))) {
            return DESIGNER;
        }

        String safeRole = StringUtils.defaultString(role).trim();
        if ("管理员".equals(safeRole) || "admin".equalsIgnoreCase(safeRole) || "ADMIN".equalsIgnoreCase(safeRole)) {
            return ADMIN;
        }
        if ("用户".equals(safeRole) || "user".equalsIgnoreCase(safeRole) || "USER".equalsIgnoreCase(safeRole)) {
            return USER;
        }
        if ("设计师".equals(safeRole) || "designer".equalsIgnoreCase(safeRole) || "DESIGNER".equalsIgnoreCase(safeRole)) {
            return DESIGNER;
        }
        return USER;
    }

    public static boolean isAdmin(String role, String tableName) {
        return ADMIN.equals(normalize(role, tableName));
    }
}
