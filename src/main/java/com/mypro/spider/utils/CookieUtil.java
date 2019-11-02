package com.mypro.spider.utils;

import java.util.Random;

/**
 * @author fgzhong
 * @description: ${description}
 * @since 2019/5/17
 */
public class CookieUtil {

    private final static Random RANDOM = new Random();

    private final static String[] WEI_BO = new String[]{
//            "TC-V5-G0=06f20d05fbf5170830ff70a1e1f1bcae; SUHB=0jBZMTUOeAssPX; ALF=1589089737; TC-Page-G0=c4376343b8c98031e29230e0923842a5|1557553827|1557553827; YF-V5-G0=125128c5d7f9f51f96971f11468b5a3f; SUB=_2AkMrhrnSf8PxqwJRmP4WzW3rZYl1wg3EieKd2kgJJRMxHRl-yT83qhUvtRB6AAaXPQp_dqTZlMv_-gEyP9xisPTGQ3Oc; YF-Page-G0=aac25801fada32565f5c5e59c7bd227b|1557992773|1557992689",
//            "YF-V5-G0=4e19e5a0c5563f06026c6591dbc8029f; SUB=_2AkMrgg63f8NxqwJRmP4WymjhbI9xzAnEieKd3v9sJRMxHRl-yT83qhNctRB6AAIgWEkZu-7nbXnHfmJivqyy8wOQUZKH",
//            "YF-V5-G0=70942dbd611eb265972add7bc1c85888; SUB=_2AkMrgg6Jf8NxqwJRmP4WymjhbI9xzAnEieKd3v9SJRMxHRl-yT83qk0ptRB6AAIgZpye_0ifqtf5W9LdUzRRodJ07JWe",
//            "YF-V5-G0=7e17aef9f70cd5c32099644f32a261c4; SUB=_2AkMrgg0hf8NxqwJRmP4WymjhbI9xzAnEieKd3vz6JRMxHRl-yT83qksstRB6AAIjzaKDTe75WUVHhfipbh7TcPH0Kl1N",
//            "YF-Page-G0=aac25801fada32565f5c5e59c7bd227b|1558074811|1558074811; SUB=_2AkMrgtr2f8NxqwJRmP4WymjhbI9xzAnEieKd3istJRMxHRl-yT83qmMetRB6AAL0GZd-Qzs4-rnJjE1597SF7ada2AIv; SUBP=0033WrSXqPxfM72-Ws9jqgMF55529P9D9WFJIEKnj8KdCEXoIOIQjOTP",
//            "SUB=_2AkMrgluZf8PxqwJRmP4WzW3rZYl1wg3EieKd3qpCJRMxHRl-yj83qlIYtRB6AAJ1drtm9-_gFepaIkYQ2K-caIVfQdWD; TC-V5-G0=4e714161a27175839f5a8e7411c8b98c",
//            "TC-V5-G0=841d8e04c4761f733a87c822f72195f3; SUB=_2AkMrgmnjf8PxqwJRmP4WzW3rZYl1wg3EieKd3pg4JRMxHRl-yj9jqlIptRB6AAJHDIqeO4eSc58rkWbnI-p-Tf2v9DeV",
//            "TC-V5-G0=634dc3e071d0bfd86d751caf174d764e; SUB=_2AkMrgmnDf8PxqwJRmP4WzW3rZYl1wg3EieKd3pgYJRMxHRl-yj9jqhcPtRB6AAJHLACSI9FJ9qiuaTzjvL8r5f3aENLR",
//            "wb_view_log=1280*8002; TC-V5-G0=cdcf495cbaea129529aa606e7629fea7; SUB=_2AkMrgmgpf8PxqwJRmP4WzW3rZYl1wg3EieKd3pnyJRMxHRl-yj9jqk1etRB6AAJGxl8UiqiTdSqUz_YYr8MSXs0wa9Vn",
//            "TC-V5-G0=cdcf495cbaea129529aa606e7629fea7; SUB=_2AkMrgmhif8PxqwJRmP4WzW3rZYl1wg3EieKd3pm5JRMxHRl-yj9jqh1ZtRB6AAJGjcfnveEZZe46xVjrEr2ux7gGjSNM",
//            "TC-V5-G0=1ac1bd7677fc7b61611a0c3a9b6aa0b4; SUB=_2AkMrgmhCf8PxqwJRmP4WzW3rZYl1wg3EieKd3pmZJRMxHRl-yj9jqlIJtRB6AAJGrS8wOBpRXPE5ZkIGF5pnp5nD7IPZ",
//            "wb_view_log=1280*8002; TC-V5-G0=7975b0b5ccf92b43930889e90d938495; SUB=_2AkMrgmigf8PxqwJRmP4WzW3rZYl1wg3EieKd3pl7JRMxHRl-yj9jqnwvtRB6AAJGT5kFp0w_XJ_d3zzJHhMblLRTrTbr",
//            "TC-V5-G0=7975b0b5ccf92b43930889e90d938495; SUB=_2AkMrgmjof8PxqwJRmP4WzW3rZYl1wg3EieKd3pkzJRMxHRl-yj9jqkkdtRB6AAJGB4Jij6E0oer-Q-Pvl25Fo3bUumpR",
//            "wb_view_log=1280*8002; TC-V5-G0=841d8e04c4761f733a87c822f72195f3; SUB=_2AkMrgmcwf8PxqwJRmP4WzW3rZYl1wg3EieKd3pbrJRMxHRl-yj9jqnEstRB6AAJJ32pN9Se__ulnSJr6bA6hjXpsxWDs",
//            "TC-V5-G0=10672b10b3abf31f7349754fca5d2248; SUB=_2AkMrgmcUf8PxqwJRmP4WzW3rZYl1wg3EieKd3pbPJRMxHRl-yj9jqlIAtRB6AAJJ-6kzrMksuhoc5S0_8p0FpcQ4mFye",
//            "wb_view_log=1280*8002; TC-V5-G0=0dba63c42a7d74c1129019fa3e7e6e7c; SUB=_2AkMrgme6f8PxqwJRmP4WzW3rZYl1wg3EieKd3pZhJRMxHRl-yj9jqkoytRB6AAJJVV4prAlRf-S4RUd2m_9OoaKY9AtH",
            "YF-V5-G0=4e19e5a0c5563f06026c6591dbc8029f; ULV=1542856153895:3:3:3:6946673129773.87.1542856153870:1542268290741; SINAGLOBAL=6774783524360.861.1542168187658; SUB=_2AkMstxFCf8NxqwJRmP0RxWrjbYx2yQDEieKa6-CZJRMxHRl-yT9jqlAGtRB6Bzc_rT4PWhpZGGOkFTRbIF-_rOsCdiKg; SUBP=0033WrSXqPxfM72-Ws9jqgMF55529P9D9WFr.bqve9YeaEMqXpRp7PJU"
    };


    public static String getWbCookie() {
        return WEI_BO[RANDOM.nextInt(WEI_BO.length)];
    }
    public static String getWbCookie(int a) {
        return WEI_BO[a];
    }

}
