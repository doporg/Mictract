package com.baasexample.demo.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baasexample.demo.Mapper.ChannelMapper;
import com.baasexample.demo.model.dbMo.ChaincodeInfo;
import com.baasexample.demo.model.dbMo.ChannelInfo;
import com.baasexample.demo.model.dbMo.NewNetInfo;
import com.baasexample.demo.model.jsonMo.channelJsonModel;
import com.baasexample.demo.model.jsonMo.jsonModel;
import com.baasexample.demo.model.yamlMo.BlockData;
import com.baasexample.demo.model.yamlMo.TxData;
import com.baasexample.demo.service.FabricChannelService;
import com.baasexample.demo.service.JsonService;
import com.jcraft.jsch.SftpException;
import io.kubernetes.client.ApiException;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.cli.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * 注释写在这
 *
 * @author Monhey
 */
@CrossOrigin
@RestController
@EnableAutoConfiguration
public class ChannelController {
    @Autowired
    FabricChannelService fabricChannelService;
    @Autowired
    ChannelMapper channelMapper;
    @Autowired
    JsonService jsonService;

    @ApiOperation(value = "创建fabri通道", notes = "接口说明")
    @PostMapping("/v2/baas/createChannel")
    public String CreateChannel(@RequestBody String channelJson) throws InterruptedException, ApiException, ParseException, IOException, SftpException {
        channelJsonModel c = jsonService.CastJsonToChannelBean(channelJson);
        List<String> list = c.getPeers();
        StringBuilder sb = new StringBuilder("");
        for(int i=0;i<list.size();i++){
            if(i!=list.size()-1){
                sb.append(list.get(i)+",");
            }
            else{
                sb.append(list.get(i));
            }
        }
        String peerList = sb.toString();
        ChannelInfo ci = new ChannelInfo();
        ci.setName(c.getName());
        ci.setNetworkId(c.getNetworkId());
        ci.setPeers(peerList);
        channelMapper.insertChannel(ci);
        return fabricChannelService.createChannel(c.getNetworkId(),c.getName(),list);
    }

    @ApiOperation(value = "查询所有通道信息",notes = "接口说明")
    @GetMapping("/v2/baas/getAllChannel")
    public String getAllChannelInfo(){
        List<ChannelInfo> netList = channelMapper.findAllChannel();
        JSONArray array= JSONArray.parseArray(JSON.toJSONString(netList));
        return JSON.toJSONString(array);
    }

    @ApiOperation(value = "查询fabri通道块信息", notes = "接口说明")
    @GetMapping("/v2/baas/queryBlockData/{ChannelId}")
    public String queryChannelBlockData(@PathVariable(value = "ChannelId") int ChannelId) throws InterruptedException, ApiException, ParseException, IOException {
        List<BlockData> list =  fabricChannelService.QueryChannelBlock(ChannelId);
        JSONArray array= JSONArray.parseArray(JSON.toJSONString(list));
        return JSON.toJSONString(array);
    }

    @ApiOperation(value = "查询fabri通道交易信息", notes = "接口说明")
    @GetMapping("/v2/baas/queryTxData/{ChannelId}")
    public String queryChannelTXData(@PathVariable(value = "ChannelId") int ChannelId) throws InterruptedException, ApiException, ParseException, IOException {
        List<TxData> list =  fabricChannelService.QueryChannelTx(ChannelId);
        JSONArray array= JSONArray.parseArray(JSON.toJSONString(list));
        return JSON.toJSONString(array);
    }

}
