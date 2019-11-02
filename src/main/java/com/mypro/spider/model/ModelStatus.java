package com.mypro.spider.model;

import com.mypro.spider.parse.Outlink;

import java.util.Map;

/**
 * @author fgzhong
 * @description: ${description}
 * @since 2019/2/23
 */
public interface ModelStatus<T> {

    void successModel();
    void retryModel();
    void failModel();
    void dataErrorModel();
    T nextModel(Outlink o) throws CloneNotSupportedException;
//   <T> T successModel(T model);
//   <T> T retryModel(T model);
//   <T> T failModel(T model);
}
