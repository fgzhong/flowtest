package com.mypro.spider.example.example2pro.model;

import java.io.Serializable;
import java.util.List;

import com.google.common.base.Objects;
import com.mypro.spider.parse.ParseModel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.collect.Lists;

/**
 * @author fgzhong
 * @description: ${description}
 * @since 2019/1/28
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeiboModel implements Serializable,ParseModel {

    private String url; // url
    
    private String source; // 来源网站
    
    private String starterId; // 发表人id
    
    private String starterName; // 发表人用户名
    
    private String title; // 标题
    
    private String bakeTitle; // 副标题
    
    private String postTime; // 发帖时间
    
    private Integer viewNum; // 查看人数
    
    private Integer reviewNum; // 回复数
    
    private Integer reviewNumBak; // 回复数
    
    private String content; // 内容
    
    private List<String> picUrl; // 图片URL
    
    private List<String> videoUrl; // 视频URL
    
    private List<ReviewModel> reviews = Lists.newArrayList(); // 回复
    
    private String score; // 分数
    
    private Integer up; // 支持
    
    private Integer middle; // 中间
    
    private Integer down; // 反对
    
    private String editor; // 责编
    
    private List<String> route; // 索引路径
    
    private List<String> tag; // 标签
    
    private String web;  // web 来源网站
    
    private String key; // 如果是评论，帖子翻页等信息标志其归属

    
    private Integer reposts; //  转发

    
    private WeiboModel retweeted;  // 转发内容

    
    private List<String> thumbnail; // 视频缩略图

    public List<String> getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(List<String> thumbnail) {
        this.thumbnail = thumbnail;
    }

    public Integer getReposts() {
        return reposts;
    }

    public void setReposts(Integer reposts) {
        this.reposts = reposts;
    }

    public WeiboModel getRetweeted() {
        return retweeted;
    }

    public void setRetweeted(WeiboModel retweeted) {
        this.retweeted = retweeted;
    }

    public WeiboModel(String web) {
        this.web = web;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
        if (this.key == null || this.key.length() == 0) {
            this.key = url;
        }
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getStarterId() {
        return starterId;
    }

    public void setStarterId(String starterId) {
        this.starterId = starterId;
    }

    public String getStarterName() {
        return starterName;
    }

    public void setStarterName(String starterName) {
        this.starterName = starterName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBakeTitle() {
        return bakeTitle;
    }

    public void setBakeTitle(String bakeTitle) {
        this.bakeTitle = bakeTitle;
    }

    public String getPostTime() {
        return postTime;
    }

    public void setPostTime(String postTime) {
        this.postTime = postTime;
    }

    public Integer getViewNum() {
        return viewNum;
    }

    public void setViewNum(Integer viewNum) {
        this.viewNum = viewNum;
    }

    public Integer getReviewNum() {
        return reviewNum;
    }

    public void setReviewNum(Integer reviewNum) {
        this.reviewNum = reviewNum;
    }

    public Integer getReviewNumBak() {
        return reviewNumBak;
    }

    public void setReviewNumBak(Integer reviewNumBak) {
        this.reviewNumBak = reviewNumBak;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(List<String> picUrl) {
        this.picUrl = picUrl;
    }

    public List<String> getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(List<String> videoUrl) {
        this.videoUrl = videoUrl;
    }

    public List<ReviewModel> getReviews() {
        return reviews;
    }

    public void setReviews(List<ReviewModel> reviews) {
        this.reviews = reviews;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public Integer getUp() {
        return up;
    }

    public void setUp(Integer up) {
        this.up = up;
    }

    public Integer getMiddle() {
        return middle;
    }

    public void setMiddle(Integer middle) {
        this.middle = middle;
    }

    public Integer getDown() {
        return down;
    }

    public void setDown(Integer down) {
        this.down = down;
    }

    public String getEditor() {
        return editor;
    }

    public void setEditor(String editor) {
        this.editor = editor;
    }

    public List<String> getRoute() {
        return route;
    }

    public void setRoute(List<String> route) {
        this.route = route;
    }

    public List<String> getTag() {
        return tag;
    }

    public void setTag(List<String> tag) {
        this.tag = tag;
    }

    public String getWeb() {
        return web;
    }

    public void setWeb(String web) {
        this.web = web;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

}

