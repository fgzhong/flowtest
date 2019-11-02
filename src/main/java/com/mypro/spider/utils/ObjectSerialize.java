package com.mypro.spider.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.util.Map;

/**
 * @author fgzhong
 * @description: jdk 序列化和反序列化
 * @since 2019/5/31
 */
public class ObjectSerialize {

    public static void serialize() {
        JSONObject s = JSONObject.parseObject("{\"key\":\"http://weibo.com/aj/v6/comment/big?ajwvr=6&id=4364602877771627&filter=all&page=5&sudaref=weibo.com&display=0&retcode=6102\",\"reviews\":[{\"reviewContent\":\"回复@AI生活AI阳哥:我发觉每次在这里评论完之后，我的微博都有一些“鸡”去点赞。。。 新一这货到底是什么来路？\",\"reviewId\":\"2169226363\",\"reviewName\":\"余生-轨迹\",\"reviewPicUrl\":[\"https://h5.sinaimg.cn/upload/1017/172/2019/04/15/feed_19pai32x28_liuyi_default.png\"],\"reviewTime\":\"4月24日 16:53\",\"reviewUp\":0},{\"reviewContent\":\"你真的是个好博主应该推给我女朋友\",\"reviewId\":\"5124327441\",\"reviewName\":\"张立炜这名字\",\"reviewTime\":\"4月24日 16:47\",\"reviewUp\":0},{\"reviewContent\":\"看你看完电影后怎么做才知道值不值得\",\"reviewId\":\"5298361017\",\"reviewName\":\"Z一十月三S\",\"reviewPicUrl\":[\"https://h5.sinaimg.cn/upload/1017/172/2019/04/15/feed_19pai32x28_weixiao_default.png\"],\"reviewTime\":\"4月24日 16:43\",\"reviewUp\":0},{\"reviewContent\":\"两百都花了还在乎这点买爆米花和可乐的钱吗\",\"reviewId\":\"2805490847\",\"reviewName\":\"Tears丶落\",\"reviewPicUrl\":[\"https://h5.sinaimg.cn/upload/1005/526/2019/04/04/icon_wenda_v3_v2.png\"],\"reviewTime\":\"4月24日 16:42\",\"reviewUp\":0},{\"reviewContent\":\"不算贵吧，现在谈妹子哪有不花钱的\",\"reviewId\":\"3826932131\",\"reviewName\":\"物电小霸王\",\"reviewTime\":\"4月24日 16:36\",\"reviewUp\":0},{\"reviewContent\":\"你抽奖得时候没觉得钱来的容易吗？？？\",\"reviewId\":\"2874408444\",\"reviewName\":\"Mr_陌小主\",\"reviewTime\":\"4月24日 16:18\",\"reviewUp\":0},{\"reviewContent\":\"醒了没有\",\"reviewId\":\"1984450887\",\"reviewName\":\"Mr_zhou_90\",\"reviewPicUrl\":[\"https://h5.sinaimg.cn/upload/1017/172/2019/04/15/feed_19pai32x28_xiatian_default.png\"],\"reviewTime\":\"4月24日 16:09\",\"reviewUp\":0},{\"reviewContent\":\"人家的？\",\"reviewId\":\"6403551679\",\"reviewName\":\"LXH并非所见\",\"reviewTime\":\"4月24日 16:05\",\"reviewUp\":0},{\"reviewContent\":\"这么败家要我赶紧分手\",\"reviewId\":\"2705500507\",\"reviewName\":\"LioDouble\",\"reviewTime\":\"4月24日 15:54\",\"reviewUp\":0},{\"reviewContent\":\"老沾别人女朋友的背光，脸都不要了，醒醒吧，回家多打几发飞机吧\",\"reviewId\":\"3855144572\",\"reviewName\":\"一年改一次名字都不可以吗\",\"reviewPicUrl\":[\"https://h5.sinaimg.cn/upload/1005/526/2019/04/04/icon_wenda_v3_v2.png\"],\"reviewTime\":\"4月24日 15:52\",\"reviewUp\":0},{\"reviewContent\":\"分手\",\"reviewId\":\"1797306904\",\"reviewName\":\"66张小強哥\",\"reviewTime\":\"4月24日 15:50\",\"reviewUp\":0},{\"reviewContent\":\"9.9包邮？\",\"reviewId\":\"5217408194\",\"reviewName\":\"雪阳丶夙愿\",\"reviewPicUrl\":[\"https://h5.sinaimg.cn/upload/1059/495/2018/11/06/pcpanda2x.png\"],\"reviewTime\":\"4月24日 15:49\",\"reviewUp\":0},{\"reviewContent\":\"行了行了该走了，人家男朋友该来了\",\"reviewId\":\"3990979056\",\"reviewName\":\"逍遙隨心_\",\"reviewPicUrl\":[\"https://h5.sinaimg.cn/upload/1059/495/2018/11/06/pcpanda2x.png\"],\"reviewTime\":\"4月24日 15:45\",\"reviewUp\":0},{\"reviewContent\":\"回复@掉肉的胖子:嗦嘎\",\"reviewId\":\"6631213195\",\"reviewName\":\"小白啥不懂\",\"reviewTime\":\"4月24日 15:42\",\"reviewUp\":0},{\"reviewContent\":\"回复@小白啥不懂:能吃说明能约。\",\"reviewId\":\"2151067914\",\"reviewName\":\"掉肉的胖子\",\"reviewTime\":\"4月24日 15:42\",\"reviewUp\":0},{\"reviewContent\":\"回复@守1份堅歭:让拉稀的来，不能让他吃到一点儿硬菜\",\"reviewId\":\"3189344387\",\"reviewName\":\"一只温文尔雅的骆驼\",\"reviewPicUrl\":[\"https://h5.sinaimg.cn/upload/1059/495/2018/11/06/pcpanda2x.png\"],\"reviewTime\":\"4月24日 15:41\",\"reviewUp\":1},{\"reviewContent\":\"首映结束补一枪\",\"reviewId\":\"2678836857\",\"reviewName\":\"有妖气有内涵\",\"reviewPicUrl\":[\"https://h5.sinaimg.cn/upload/1005/526/2019/04/04/icon_wenda_v3_v2.png\",\"https://h5.sinaimg.cn/upload/1059/495/2018/11/06/pcpanda2x.png\"],\"reviewTime\":\"4月24日 15:40\",\"reviewUp\":0},{\"reviewContent\":\"你可别装逼了\",\"reviewId\":\"7007856903\",\"reviewName\":\"你是腰椎间盘吗\",\"reviewTime\":\"4月24日 15:38\",\"reviewUp\":0}],\"url\":\"http://weibo.com/aj/v6/comment/big?ajwvr=6&id=4364602877771627&filter=all&page=5&sudaref=weibo.com&display=0&retcode=6102\",\"web\":\"weibo\"}");
        JSONObject s1 = JSONObject.parseObject("{\"key\":\"http://weibo.com/aj/v6/comment/big?ajwvr=6&id=4364602877771627&filter=all&page=5&sudaref=weibo.com&display=0&retcode=6102\",\"reviews\":[{\"reviewContent\":\"回复@AI生活AI阳哥:我发觉每次在这里评论完之后，我的微博都有一些“鸡”去点赞。。。 新一这货到底是什么来路？\",\"reviewId\":\"2169226363\",\"reviewName\":\"余生-轨迹\",\"reviewPicUrl\":[\"https://h5.sinaimg.cn/upload/1017/172/2019/04/15/feed_19pai32x28_liuyi_default.png\"],\"reviewTime\":\"4月24日 16:53\",\"reviewUp\":0},{\"reviewContent\":\"你真的是个好博主应该推给我女朋友\",\"reviewId\":\"5124327441\",\"reviewName\":\"张立炜这名字\",\"reviewTime\":\"4月24日 16:47\",\"reviewUp\":0},{\"reviewContent\":\"看你看完电影后怎么做才知道值不值得\",\"reviewId\":\"5298361017\",\"reviewName\":\"Z一十月三S\",\"reviewPicUrl\":[\"https://h5.sinaimg.cn/upload/1017/172/2019/04/15/feed_19pai32x28_weixiao_default.png\"],\"reviewTime\":\"4月24日 16:43\",\"reviewUp\":0},{\"reviewContent\":\"两百都花了还在乎这点买爆米花和可乐的钱吗\",\"reviewId\":\"2805490847\",\"reviewName\":\"Tears丶落\",\"reviewPicUrl\":[\"https://h5.sinaimg.cn/upload/1005/526/2019/04/04/icon_wenda_v3_v2.png\"],\"reviewTime\":\"4月24日 16:42\",\"reviewUp\":0},{\"reviewContent\":\"不算贵吧，现在谈妹子哪有不花钱的\",\"reviewId\":\"3826932131\",\"reviewName\":\"物电小霸王\",\"reviewTime\":\"4月24日 16:36\",\"reviewUp\":0},{\"reviewContent\":\"你抽奖得时候没觉得钱来的容易吗？？？\",\"reviewId\":\"2874408444\",\"reviewName\":\"Mr_陌小主\",\"reviewTime\":\"4月24日 16:18\",\"reviewUp\":0},{\"reviewContent\":\"醒了没有\",\"reviewId\":\"1984450887\",\"reviewName\":\"Mr_zhou_90\",\"reviewPicUrl\":[\"https://h5.sinaimg.cn/upload/1017/172/2019/04/15/feed_19pai32x28_xiatian_default.png\"],\"reviewTime\":\"4月24日 16:09\",\"reviewUp\":0},{\"reviewContent\":\"人家的？\",\"reviewId\":\"6403551679\",\"reviewName\":\"LXH并非所见\",\"reviewTime\":\"4月24日 16:05\",\"reviewUp\":0},{\"reviewContent\":\"这么败家要我赶紧分手\",\"reviewId\":\"2705500507\",\"reviewName\":\"LioDouble\",\"reviewTime\":\"4月24日 15:54\",\"reviewUp\":0},{\"reviewContent\":\"老沾别人女朋友的背光，脸都不要了，醒醒吧，回家多打几发飞机吧\",\"reviewId\":\"3855144572\",\"reviewName\":\"一年改一次名字都不可以吗\",\"reviewPicUrl\":[\"https://h5.sinaimg.cn/upload/1005/526/2019/04/04/icon_wenda_v3_v2.png\"],\"reviewTime\":\"4月24日 15:52\",\"reviewUp\":0},{\"reviewContent\":\"分手\",\"reviewId\":\"1797306904\",\"reviewName\":\"66张小強哥\",\"reviewTime\":\"4月24日 15:50\",\"reviewUp\":0},{\"reviewContent\":\"9.9包邮？\",\"reviewId\":\"5217408194\",\"reviewName\":\"雪阳丶夙愿\",\"reviewPicUrl\":[\"https://h5.sinaimg.cn/upload/1059/495/2018/11/06/pcpanda2x.png\"],\"reviewTime\":\"4月24日 15:49\",\"reviewUp\":0},{\"reviewContent\":\"行了行了该走了，人家男朋友该来了\",\"reviewId\":\"3990979056\",\"reviewName\":\"逍遙隨心_\",\"reviewPicUrl\":[\"https://h5.sinaimg.cn/upload/1059/495/2018/11/06/pcpanda2x.png\"],\"reviewTime\":\"4月24日 15:45\",\"reviewUp\":0},{\"reviewContent\":\"回复@掉肉的胖子:嗦嘎\",\"reviewId\":\"6631213195\",\"reviewName\":\"小白啥不懂\",\"reviewTime\":\"4月24日 15:42\",\"reviewUp\":0},{\"reviewContent\":\"回复@小白啥不懂:能吃说明能约。\",\"reviewId\":\"2151067914\",\"reviewName\":\"掉肉的胖子\",\"reviewTime\":\"4月24日 15:42\",\"reviewUp\":0},{\"reviewContent\":\"回复@守1份堅歭:让拉稀的来，不能让他吃到一点儿硬菜\",\"reviewId\":\"3189344387\",\"reviewName\":\"一只温文尔雅的骆驼\",\"reviewPicUrl\":[\"https://h5.sinaimg.cn/upload/1059/495/2018/11/06/pcpanda2x.png\"],\"reviewTime\":\"4月24日 15:41\",\"reviewUp\":1},{\"reviewContent\":\"首映结束补一枪\",\"reviewId\":\"2678836857\",\"reviewName\":\"有妖气有内涵\",\"reviewPicUrl\":[\"https://h5.sinaimg.cn/upload/1005/526/2019/04/04/icon_wenda_v3_v2.png\",\"https://h5.sinaimg.cn/upload/1059/495/2018/11/06/pcpanda2x.png\"],\"reviewTime\":\"4月24日 15:40\",\"reviewUp\":0},{\"reviewContent\":\"你可别装逼了\",\"reviewId\":\"7007856903\",\"reviewName\":\"你是腰椎间盘吗\",\"reviewTime\":\"4月24日 15:38\",\"reviewUp\":0}],\"url\":\"http://weibo.com/aj/v6/comment/big?ajwvr=6&id=4364602877771627&filter=all&page=5&sudaref=weibo.com&display=0&retcode=6102\",\"web\":\"weibo\"}");
        //Initializes The Object
        s.getJSONArray("reviews").addAll(s1.getJSONArray("reviews"));
        System.out.println(JSON.toJSONString(s));

        //Write Obj to File
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(new FileOutputStream("/Users/zhuowenwei/Documents/mypro/spideres/src/main/resources/test/tempFile"));
            oos.writeObject(s);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            org.apache.commons.io.IOUtils.closeQuietly(oos);
        }

        //Read Obj from File
        File file = new File("/Users/zhuowenwei/Documents/mypro/spideres/src/main/resources/test/tempFile");
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(new FileInputStream(file));
            Map newUser = (Map) ois.readObject();
            System.out.println(JSON.toJSONString(newUser));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            org.apache.commons.io.IOUtils.closeQuietly(ois);
            try {
                org.apache.commons.io.FileUtils.forceDelete(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
