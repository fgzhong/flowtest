package com.mypro.spider.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mypro.spider.config.SpiderConfig;


/**
 * @author fgzhong
 * @description: http header 存储
 * @since 2019/2/21
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExtendMap {

    private static final String DEFAULT_CHARSET = "UTF-8";
    public static final String DEFAULT_HTTP_TYPE = "Get";
    public static final String HTTP_TYPE_POST = "Post";
    private static final String DEFAULT_HTTP_METHOD_SIMPLENAME = SpiderConfig.default_fetchHttpClass;

    @JsonIgnore
    private String httpType;
    @JsonIgnore
    private String httpMethodClassName;
    @JsonIgnore
    private String requestBody;
    @JsonIgnore
    private String charset;

    @JsonProperty("Accept")
    private String accept;
    @JsonProperty("Accept-Language")
    private String acceptLanguage;
    @JsonProperty("Accept-Encoding")
    private String acceptEncoding;
    @JsonProperty("Content-Encoding")
    private String contentEncoding;
    @JsonProperty("Content-Language")
    private String contentLanguage;
    @JsonProperty("Content-Type")
    private String contentType;
    @JsonProperty("Referer")
    private String referer;
    @JsonProperty("Cookie")
    private String cookie;
    @JsonProperty("x_requested_with")
    private String xRequestedWith;
    @JsonProperty("User-Agent")
    private String userAgent;

    public ExtendMap postHttpType() {
        this.httpType = HTTP_TYPE_POST;
        return this;
    }

    public ExtendMap accept(String accept) {
        this.accept = accept;
        return this;
    }

    public ExtendMap acceptLanguage(String acceptLanguage) {
        this.acceptLanguage = acceptLanguage;
        return this;
    }

    public ExtendMap acceptEncoding(String acceptEncoding) {
        this.acceptEncoding = acceptEncoding;
        return this;
    }

    public ExtendMap contentEncoding(String contentEncoding) {
        this.contentEncoding = contentEncoding;
        return this;
    }

    public ExtendMap contentLanguage(String contentLanguage) {
        this.contentLanguage = contentLanguage;
        return this;
    }

    public ExtendMap contentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public ExtendMap cookie(String cookie) {
        this.cookie = cookie;
        return this;
    }

    public ExtendMap referer(String referer) {
        this.referer = referer;
        return this;
    }

    public ExtendMap xRequestedWith(String xRequestedWith) {
        this.xRequestedWith = xRequestedWith;
        return this;
    }

    public ExtendMap userAgent(String userAgent) {
        this.userAgent = userAgent;
        return this;
    }

    public ExtendMap requestBody(String requestBody) {
        this.requestBody = requestBody;
        return this;
    }

    public ExtendMap charset(String charset) {
        this.charset = charset;
        return this;
    }

    public void setHttpType(String httpType) {
        this.httpType = httpType;
    }

    public void setHttpMethodClassName(String httpMethodClassName) {
        this.httpMethodClassName = httpMethodClassName;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public void setAccept(String accept) {
        this.accept = accept;
    }

    public void setAcceptLanguage(String acceptLanguage) {
        this.acceptLanguage = acceptLanguage;
    }

    public void setAcceptEncoding(String acceptEncoding) {
        this.acceptEncoding = acceptEncoding;
    }

    public void setContentEncoding(String contentEncoding) {
        this.contentEncoding = contentEncoding;
    }

    public void setContentLanguage(String contentLanguage) {
        this.contentLanguage = contentLanguage;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setReferer(String referer) {
        this.referer = referer;
    }

    public void setxRequestedWith(String xRequestedWith) {
        this.xRequestedWith = xRequestedWith;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getAccept() {
        return accept;
    }

    public String getAcceptLanguage() {
        return acceptLanguage;
    }

    public String getAcceptEncoding() {
        return acceptEncoding;
    }

    public String getContentEncoding() {
        return contentEncoding;
    }

    public String getContentLanguage() {
        return contentLanguage;
    }

    public String getContentType() {
        return contentType;
    }

    public String getReferer() {
        return referer;
    }

    public String getCookie() {
        return cookie;
    }

    public String getxRequestedWith() {
        return xRequestedWith;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public String getHttpMethodClassName() {
        return httpMethodClassName;
    }

    public String getHttpType() {
        return httpType == null ? DEFAULT_HTTP_TYPE : httpType;
    }

    public String getCharset() {
        return  charset;
    }

    public String getRequestBody() {
        return requestBody;
    }
}
