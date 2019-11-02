package com.mypro.spark.stream.listener;/**
 * @Author create by fgzhong
 * @Date 2019/9/17
 * @Description
 */

import org.apache.spark.streaming.scheduler.*;

/**
 *   create by fgzhong on 2019/9/17
 */
public class Listener implements StreamingListener {

    @Override
    public void onStreamingStarted(StreamingListenerStreamingStarted streamingStarted) {
    }

    @Override
    public void onReceiverStarted(StreamingListenerReceiverStarted receiverStarted) {
    }

    @Override
    public void onReceiverError(StreamingListenerReceiverError receiverError) {
    }

    @Override
    public void onReceiverStopped(StreamingListenerReceiverStopped receiverStopped) {
    }

    /** 每个批次提交的事件 */
    @Override
    public void onBatchSubmitted(StreamingListenerBatchSubmitted batchSubmitted) {
    }

    /** 每个批次启动的事件 */
    @Override
    public void onBatchStarted(StreamingListenerBatchStarted batchStarted) {
    }

    /** 每个批次完成的事件  */
    @Override
    public void onBatchCompleted(StreamingListenerBatchCompleted batchCompleted) {
    }

    @Override
    public void onOutputOperationStarted(StreamingListenerOutputOperationStarted outputOperationStarted) {
    }

    @Override
    public void onOutputOperationCompleted(StreamingListenerOutputOperationCompleted outputOperationCompleted) {
    }
}
