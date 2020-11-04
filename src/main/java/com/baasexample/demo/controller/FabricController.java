package com.baasexample.demo.controller;

import com.baasexample.demo.Mapper.NetMapper;
import com.baasexample.demo.Mapper.NewNetMapper;
import com.baasexample.demo.config.SFTPUtil;
import com.baasexample.demo.model.dbMo.NetInfo;
import com.baasexample.demo.model.dbMo.NewNetInfo;
import com.baasexample.demo.model.jsonMo.jsonModel;
import com.baasexample.demo.model.yamlMo.Orderer;
import com.baasexample.demo.model.yamlMo.Organization;
import com.baasexample.demo.model.yamlMo.Peer;
import com.baasexample.demo.service.*;
import com.jcraft.jsch.SftpException;
import io.kubernetes.client.ApiException;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;
import sun.nio.ch.Net;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 注释写在这
 *
 * @author Monhey
 */
@CrossOrigin
@RestController
@EnableAutoConfiguration
public class FabricController {

    @Autowired
    FabricNetOperateService fabricNetOperateService;
    @Autowired
    JsonService jsonService;
    @Autowired
    FabricYamlGenerateService fabricYamlGenerateService;
    @Autowired
    NewNetMapper netMapper;
    @Autowired
    FabricK8sQueryService fabricK8sQueryService;
    @Autowired
    JenkinsService jenkinsService;
    @ApiOperation(value = "创建fabric网络", notes = "接口说明")
    @PostMapping("/v2/baas/createFabric")
    public String CreateFabric(@RequestBody String netWorkJson) throws IOException, ApiException, SftpException {
        jsonModel jm = jsonService.CastJsonToNetBean(netWorkJson);
        String NameSpace = jm.getName();
        List<Orderer> orderList = jm.getOrder();
        List<Organization> orgList = jm.getOrg();
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String current = sdf.format(date);
        NewNetInfo newNetInfo = new NewNetInfo();
        newNetInfo.setNamespace(NameSpace);
        newNetInfo.setOrdererList(orderList.toString());
        newNetInfo.setOrgList(orgList.toString());
        newNetInfo.setTls(jm.getTls());
        newNetInfo.setConsensus(jm.getConsensus());
        newNetInfo.setStatus(2);
        newNetInfo.setCreatetime(current);
        netMapper.insertNet(newNetInfo);
        fabricYamlGenerateService.replaceWithConfigtxTemplate(NameSpace,orgList,orderList,jm.getConsensus());
        fabricYamlGenerateService.replaceWithCryptoconfigTemplate(NameSpace,orderList,orgList);
        SFTPUtil sftpUtil = new SFTPUtil();
        sftpUtil.login();
        File file = new File("src/main/resources/configtx-"+NameSpace+".yaml");
        File file2 = new File("src/main/resources/crypto-config-"+NameSpace+".yaml");
        InputStream is = new FileInputStream(file);
        InputStream is2 = new FileInputStream(file2);
        sftpUtil.upload("/mnt","nfsdata/fabric/"+jm.getName(), "configtx.yaml",is);
        sftpUtil.upload("/mnt","nfsdata/fabric/"+jm.getName(), "crypto-config.yaml",is2);
        sftpUtil.logout();
        int NetId = netMapper.queryId(NameSpace);
        String jobName =jenkinsService.createCreatFabricJob(NameSpace);
        jenkinsService.buildJob(jobName);
        try {
            fabricNetOperateService.createV1Namespace(NameSpace);
            fabricNetOperateService.createV1ConfigMap(NameSpace);
            for (Orderer o : orderList) {
                fabricNetOperateService.createOrdererDeployment(o.getOrderName(), NameSpace);
                fabricNetOperateService.createOrdererService(o, NameSpace);
                fabricNetOperateService.createOrdererMetricService(o,NameSpace);
            }
            for (Organization org : orgList) {
                List<Peer> peerList = org.getPeers();
                fabricNetOperateService.createOrgCaDeployment(org.getOrgName(), NameSpace);
                fabricNetOperateService.createOrgCaService(org.getOrgName(), NameSpace);
                fabricNetOperateService.createCliDeployment(org.getOrgName(), NameSpace);
                for (Peer p : peerList) {
                    fabricNetOperateService.createPeerDeployment(p, org.getOrgName(), NameSpace);
                    fabricNetOperateService.createPeerService(p, org.getOrgName(), NameSpace);
                    fabricNetOperateService.createPeerMetricService(p,org.getOrgName(),NameSpace);
                }
            }
        }
        catch (ApiException e){
            System.err.println("Exception when calling CreateFabric");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
            netMapper.updateNetStatu(1,NetId);
            return "fail";
        }
        return "success";
    }

//
//    @ApiOperation(value = "根据查询fabric网络",notes = "接口说明")
//    @GetMapping("/v2/baas/queryFabric/{id}")
//    public String QueryFabricNetById( @PathVariable(value = "id") int id){
//        NewNetInfo netInfo = netMapper.findNetById(id);
//        return netInfo.toString();
//    }
//
//
//    @ApiOperation(value = "根据查询fabric网络",notes = "接口说明")
//    @GetMapping("/v2/baas/queryFabric")
//    public List<NewNetInfo> QueryFabric(){
//        List<NewNetInfo> netList = netMapper.getAllNet();
//        return netList;
//    }
//    @ApiOperation(value = "删除网络",notes = "接口说明")
//    @GetMapping("/v2/baas/deleteFabric/{id}")
//    public String QueryFabric(@PathVariable(value = "id")int id){
//        netMapper.updateNetStatu(0,id);
//        return "success";
//    }

}
