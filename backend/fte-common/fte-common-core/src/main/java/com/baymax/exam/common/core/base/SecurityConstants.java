package com.baymax.exam.common.core.base;

//Api接口前缀

public interface SecurityConstants {
    /**
     * 内部api前缀
     */
    String API_INNER_PRE="/inner";
    /**
     * 公开api前缀
     */
    String API_OPEN_PRE="/open";
    /**
     * JWT存储权限前缀
     */
    String AUTHORITY_PREFIX = "ROLE_";

    /**
     * JWT存储权限属性
     */
    String AUTHORITY_CLAIM_NAME = "authorities";


    /**
     * 内部
     */
    String FROM_IN = "Y";

    /**
     * 标志
     */
    String FROM = "from";

    /**
     * 请求header
     */
    String HEADER_FROM_IN = FROM + "=" + FROM_IN;

    /**
     * 认证信息Http请求头
     */
    String JWT_TOKEN_HEADER = "Authorization";
    String REQUEST_HEADER_KEY_CLIENT_REAL_IP="ClientRealIp";

    /**
     * JWT令牌前缀
     */
    String JWT_TOKEN_PREFIX = "Bearer ";
    String USER_TOKEN_HEADER="Token";
    int CLASS_MAX_SIZE=200;
}
