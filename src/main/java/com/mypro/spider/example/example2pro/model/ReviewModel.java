package com.mypro.spider.example.example2pro.model;


import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.mypro.spider.parse.ParseModel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * @author fgzhong
 * @since 2019/1/28
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReviewModel implements Serializable, ParseModel {

    private String reviewId;
    private String reviewContent;
    private String reviewTime;
    private String reviewName;
    private List<String> reviewPicUrl;
    private List<String> reviewVideoUrl;
    private String reviewScore;
    private Integer reviewUp;
    private Integer reviewDown;
    private List<ReviewModel> reviewModels;
    private String reviewFloorNum;
    private ReviewModel reviewTo;
    private String revertContent;
    private Map<String,String> productDetailScore;
    private List<String> reviewTag;
    private String registerTime;

    public String getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(String registerTime) {
        this.registerTime = registerTime;
    }

    public List<String> getReviewTag() {
        return reviewTag;
    }

    public void setReviewTag(List<String> reviewTag) {
        this.reviewTag = reviewTag;
    }

    public Map<String,String> getProductDetailScore() {
        return productDetailScore;
    }

    public void setProductDetailScore(Map<String,String> productDetailScore) {
        this.productDetailScore = productDetailScore;
    }

    public String getRevertContent() {
        return revertContent;
    }

    public void setRevertContent(String revertContent) {
        this.revertContent = revertContent;
    }

    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

    public String getReviewContent() {
        return reviewContent;
    }

    public void setReviewContent(String reviewContent) {
        this.reviewContent = reviewContent;
    }

    public String getReviewTime() {
        return reviewTime;
    }

    public void setReviewTime(String reviewTime) {
        this.reviewTime = reviewTime;
    }

    public String getReviewName() {
        return reviewName;
    }

    public void setReviewName(String reviewName) {
        this.reviewName = reviewName;
    }

    public List<String> getReviewPicUrl() {
        return reviewPicUrl;
    }

    public void setReviewPicUrl(List<String> reviewPicUrl) {
        this.reviewPicUrl = reviewPicUrl;
    }

    public List<String> getReviewVideoUrl() {
        return reviewVideoUrl;
    }

    public void setReviewVideoUrl(List<String> reviewVideoUrl) {
        this.reviewVideoUrl = reviewVideoUrl;
    }

    public String getReviewScore() {
        return reviewScore;
    }

    public void setReviewScore(String reviewScore) {
        this.reviewScore = reviewScore;
    }

    public Integer getReviewUp() {
        return reviewUp;
    }

    public void setReviewUp(Integer reviewUp) {
        this.reviewUp = reviewUp;
    }

    public Integer getReviewDown() {
        return reviewDown;
    }

    public void setReviewDown(Integer reviewDown) {
        this.reviewDown = reviewDown;
    }

    public List<ReviewModel> getReviewModels() {
        return reviewModels;
    }

    public void setReviewModels(List<ReviewModel> reviewModels) {
        this.reviewModels = reviewModels;
    }

    public String getReviewFloorNum() {
        return reviewFloorNum;
    }

    public void setReviewFloorNum(String reviewFloorNum) {
        this.reviewFloorNum = reviewFloorNum;
    }

    public ReviewModel getReviewTo() {
        return reviewTo;
    }

    public void setReviewTo(ReviewModel reviewTo) {
        this.reviewTo = reviewTo;
    }

}

