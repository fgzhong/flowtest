package com.mypro.spider.fetch;

import com.mypro.spider.constant.ProtocolStatus;
import com.mypro.spider.model.AbstractModel;
import com.mypro.spider.parse.Content;
import com.mypro.spider.parse.ParseModel;
import com.mypro.spider.parse.Outlink;
import com.mypro.spider.parse.ParseFactory;
import com.mypro.spider.protocol.ProtocolFactory;
import com.mypro.spider.protocol.ProtocolOutput;
import com.mypro.spider.utils.WriteDataUtil;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.List;

/**
 * @author fgzhong
 * @description: ${description}
 * @since 2019/1/25
 */
public class FetcherRunnable implements Runnable{

    private static final Logger LOG = LoggerFactory
            .getLogger(MethodHandles.lookup().lookupClass());

    private static final String CONTENT = "_content_";
    private static final String MODEL = "_model_";
    private static final String ERROR = "_error_";

    private Counter sucCtr;
    private Counter failCtr;
    private Counter rtyCtr;

    private Context context;
    private String httpClassName;
    private String url;
    private AbstractModel model;
    private boolean parse;

    public FetcherRunnable(Context context, AbstractModel model, boolean parse, Counter... counters) {
        this.context = context;
        this.httpClassName = model.getHttpClass();
        this.url = model.getUrl();
        this.model = model;
        this.parse = parse;
        this.sucCtr = counters[0];
        this.failCtr = counters[1];
        this.rtyCtr = counters[2];
    }


    @Override
    public void run() {

        ProtocolOutput output = ProtocolFactory.getProtocol(this.httpClassName).getProtocolOutput(this.url, this.model.getMap());
        try {
            switch (output.getStatus()) {
                case ProtocolStatus.SUCCESS : {
                    Content content = output.getContent();
                    content.setParseClass(this.model.getParseClass());
                    WriteDataUtil.write(this.context,CONTENT + model.getUid(), content.toJson());
                    this.sucCtr.increment(1);
                    this.model.successModel();
                    if (parse) {
                        try {
                            List<Object> result = ParseFactory.getParse(content);
                            if (result != null) {
                                for (Object o : result) {
                                    if (o instanceof Outlink) {
                                        AbstractModel nextModel = this.model.nextModel((Outlink) o);
                                        WriteDataUtil.write(this.context, nextModel.getUid(), nextModel);
                                    } else if (o instanceof ParseModel) {
                                        WriteDataUtil.write(this.context,MODEL + o.getClass().getSimpleName() + "_" +this.model.getUid(), ParseModel.toJson((ParseModel) o));
                                    } else {
                                        LOG.warn(" 写入错误格式数据 ：{} ", o.toString());
                                    }
                                }
                            }
                        } catch (Exception e) {
                            this.sucCtr.increment(-1);
                            this.failCtr.increment(1);
                            this.model.dataErrorModel();
                            String error = StringUtils.stringifyException(e);
                            LOG.warn(" parse 失败, content : {} , error : {} ", content.getUrl(), error);
                        }
                    }
                } break;
                case ProtocolStatus.RETRY : {
                    this.rtyCtr.increment(1);
                    this.model.retryModel();
                } break;
                case ProtocolStatus.FAIL : {
                    this.failCtr.increment(1);
                    this.model.failModel();
                } break;
                default: {
                    LOG.error(" ProtocolStatus 出错，检查程序， ProtocolStatus ：", output.getStatus());
                }
            }
            WriteDataUtil.write(this.context, model.getUid(), model);
        } catch (Exception e) {
            this.model.failModel();
            LOG.error(" 数据 出错 ：{}, error : {} ", url, StringUtils.stringifyException(e));
        }
    }

}
