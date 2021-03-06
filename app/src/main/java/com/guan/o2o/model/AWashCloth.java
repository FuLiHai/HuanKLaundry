package com.guan.o2o.model;

import com.google.gson.Gson;
import com.guan.o2o.utils.GsonUtil;
import com.guan.o2o.utils.LogUtil;

import java.util.List;

/**
 * @author Guan
 * @file com.guan.o2o.model
 * @date 2015/10/14
 * @Version 1.0
 */
public class AWashCloth {

    /**
     * washInfo : [{"WashHead":"http://192.168.1.33:8080/demo/image10.png","WashName":"帽子","Amount":"￥5"},{"WashHead":"http://192.168.1.33:8080/demo/image11.png","WashName":"衬衣","Amount":"￥10"},{"WashHead":"http://192.168.1.33:8080/demo/image12.png","WashName":"皮衣","Amount":"￥100"},{"WashHead":"http://192.168.1.33:8080/demo/image13.png","WashName":"羽绒服","Amount":"￥80"},{"WashHead":"http://192.168.1.33:8080/demo/image14.png","WashName":"唐装","Amount":"￥50"},{"WashHead":"http://192.168.1.33:8080/demo/image15.png","WashName":"西服","Amount":"￥150"},{"WashHead":"http://192.168.1.33:8080/demo/image16.png","WashName":"长袖风衣","Amount":"￥218"}]
     */
    public List<WashInfoEntity> washInfo;

    public static class WashInfoEntity {
        /**
         * WashHead : http://192.168.1.33:8080/demo/image10.png
         * WashName : 帽子
         * Amount : ￥5
         */
        private String WashHead;
        private String WashName;
        private String Amount;

        public void setWashHead(String WashHead) {
            this.WashHead = WashHead;
        }

        public void setWashName(String WashName) {
            this.WashName = WashName;
        }

        public void setAmount(String Amount) {
            this.Amount = Amount;
        }

        public String getWashHead() {
            return WashHead;
        }

        public String getWashName() {
            return WashName;
        }

        public String getAmount() {
            return Amount;
        }
    }

    /**
     * Gson解析Json数据
     */
    public static AWashCloth praseJson(String reponse) {
        GsonUtil gsonUtil = new GsonUtil();
        AWashCloth aWashCloth = gsonUtil.GsonToBean(reponse, AWashCloth.class);
        return aWashCloth;
    }



}
