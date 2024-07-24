package com.stu.minote.util;

public class UserUtils {

    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String PHONE = "phone";
    public static final String LOGIN = "login";
    public static final String COUNT = "count";
    public static final String JWT = "jwt";
    public static final String TESTER_NAME = "tester_name";
    public static final String TESTER_PHONE = "tester_phone";
    public static final String TESTER_REMEMBER = "tester_remember";

    public static boolean isRememberTester() {
        return CommonUtil.getBooleanFromSP(TESTER_REMEMBER);
    }

    public static String getUserName() {
        return CommonUtil.getStringFromSP(USERNAME);
    }
    public static String getJwt() {
        return CommonUtil.getStringFromSP(JWT);
    }

    public static String getTesterName() {
        return CommonUtil.getStringFromSP(TESTER_NAME);
    }

    public static String getTesterPhone() {
        return CommonUtil.getStringFromSP(TESTER_PHONE);
    }

    public static int getCount() {
        return CommonUtil.getIntFromSP(COUNT, 0);
    }

    public static String getPassword() {
        return CommonUtil.getStringFromSP(PASSWORD);
    }

    public static void setUserName(String userName) {
        CommonUtil.putString2SP(USERNAME, userName);
    }

    public static void setPassword(String password) {
        CommonUtil.putString2SP(PASSWORD, password);
    }

    public static String getUserPhone() {
        return CommonUtil.getStringFromSP(PHONE);
    }

    public static void setUserPhone(String phone) {
        CommonUtil.putString2SP(PHONE, phone);
    }
    public static void setJwt(String jwt) {
        CommonUtil.putString2SP(JWT, jwt);
    }

    public static void setTesterPhone(String phone) {
        CommonUtil.putString2SP(TESTER_PHONE, phone);
    }

    public static void setTesterName(String name) {
        CommonUtil.putString2SP(TESTER_NAME, name);
    }

    public static void setCount(int count) {
        CommonUtil.putInt2SP(COUNT, count);
    }

    public static void setTesterRemember(boolean isMember) {
        CommonUtil.putBoolean2SP(TESTER_REMEMBER, isMember);
    }

    public static boolean isLogin() {
        return CommonUtil.getBooleanFromSP(LOGIN);
    }

    public static void setLogin(boolean login) {
        CommonUtil.putBoolean2SP(LOGIN, login);
    }

}
