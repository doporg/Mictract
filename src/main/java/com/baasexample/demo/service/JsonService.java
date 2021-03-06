package com.baasexample.demo.service;

import com.alibaba.fastjson.JSON;
import com.baasexample.demo.model.jsonMo.chaincodeJsonModel;
import com.baasexample.demo.model.jsonMo.chaincodeOpInfo;
import com.baasexample.demo.model.jsonMo.channelJsonModel;
import com.baasexample.demo.model.jsonMo.jsonModel;
import org.springframework.stereotype.Service;

/**
 * 注释写在这
 *
 * @author Monhey
 */
@Service(value = "JsonService")
public class JsonService {
    /*
    * 将传进来的json string类型转化为定义好的jsonModel类型，主要用到了fastjson
    * */
    public jsonModel CastJsonToNetBean(String jsonText){
        jsonModel js = JSON.parseObject(jsonText,jsonModel.class);
        return js;
    }
    public channelJsonModel CastJsonToChannelBean(String jsonText){
        channelJsonModel js = JSON.parseObject(jsonText, channelJsonModel.class);
        return js;
    }
    public chaincodeJsonModel CastJsonToChaincodeBean(String jsonText){
        chaincodeJsonModel js = JSON.parseObject(jsonText, chaincodeJsonModel.class);
        return js;
    }

    public chaincodeOpInfo CastJsonToChaincodeOp(String jsonText){
        chaincodeOpInfo js = JSON.parseObject(jsonText, chaincodeOpInfo.class);
        return js;
    }
}
