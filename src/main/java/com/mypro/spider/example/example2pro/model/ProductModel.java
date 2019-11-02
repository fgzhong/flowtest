package com.mypro.spider.example.example2pro.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.mypro.spider.parse.ParseModel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.collect.Lists;

/**
 * @author fgzhong
 * @since 2019/3/1
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductModel implements Serializable, ParseModel {

    
    private String productUrl;

    
    private String productType;

    
    private String productName;

    
    private String productValue;

    
    private String productModel;

    
    private List<ReviewModel> productReview = Lists.newArrayList();

    
    private List<String> productPicUrl;

    
    private Map<String,String> productParameters;

    
    private List<String> productRoute;

    
    private List<String> productTag;

    
    private String productScore;

    
    private Map<String,String> productDetailScore;

    
    private String web;

    
    private List<String> thumbnail;

    
    private Map<String,String> mainparam;

    
    private String description;
    
    private String key;

    
    private Map<String,String> productPic;

    public Map<String,String> getProductPic() {
        return productPic;
    }

    public void setProductPic(Map<String,String> productPic) {
        this.productPic = productPic;
    }

    
    private Map<String,String> productMainParameters;

    public Map<String,String> getProductMainParameters() {
        return productMainParameters;
    }

    public void setProductMainParameters(
            Map<String,String> productMainParameters) {
        this.productMainParameters = productMainParameters;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getWeb() {
        return web;
    }

    public void setWeb(String web) {
        this.web = web;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ProductModel(String web) {
        this.web = web;
    }

    public Map<String,String> getProductDetailScore() {
        return productDetailScore;
    }

    public void setProductDetailScore(Map<String,String> productDetailScore) {
        this.productDetailScore = productDetailScore;
    }

    public List<String> getProductRoute() {
        return productRoute;
    }

    public void setProductRoute(List<String> productRoute) {
        this.productRoute = productRoute;
    }

    public List<String> getProductTag() {
        return productTag;
    }

    public void setProductTag(List<String> productTag) {
        this.productTag = productTag;
    }

    public void setProductParameters(Map<String,String> productParameters) {
        this.productParameters = productParameters;
    }

    public String getProductScore() {
        return productScore;
    }

    public void setProductScore(String productScore) {
        this.productScore = productScore;
    }

    public String getProductUrl() {
        return productUrl;
    }

    public void setProductUrl(String productUrl) {
        this.productUrl = productUrl;
        if (this.key == null || this.key.length() == 0) { this.key = productUrl;}
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductValue() {
        return productValue;
    }

    public void setProductValue(String productValue) {
        this.productValue = productValue;
    }

    public String getProductModel() {
        return productModel;
    }

    public void setProductModel(String productModel) {
        this.productModel = productModel;
    }

    public List<ReviewModel> getProductReview() {
        return productReview;
    }

    public void setProductReview(List<ReviewModel> productReview) {
        this.productReview = productReview;
    }

    public List<String> getProductPicUrl() {
        return productPicUrl;
    }

    public void setProductPicUrl(List<String> productPicUrl) {
        this.productPicUrl = productPicUrl;
    }

    public Map<String,String> getProductParameters() {
        return productParameters;
    }

    public Map<String,String> getMainparam() {
        return mainparam;
    }

    public void setMainparam(Map<String,String> mainparam) {
        this.mainparam = mainparam;
    }

    public List<String> getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(List<String> thumbnail) {
        if (thumbnail == null) {
            this.thumbnail = null;
        }else {
            int size = thumbnail.size();
            for (int i = 0; i < size; i++) {
                if (thumbnail.get(i) == null) {
                    thumbnail.remove(i);
                }
            }
            this.thumbnail = thumbnail;
        }
    }

}

