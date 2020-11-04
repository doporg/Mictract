package com.baasexample.demo.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baasexample.demo.Mapper.NewNetMapper;
import com.baasexample.demo.config.SFTPUtil;
import com.baasexample.demo.model.dbMo.NewNetInfo;
import com.baasexample.demo.model.jsonMo.chaincodeJsonModel;
import com.baasexample.demo.model.jsonMo.channelJsonModel;
import com.baasexample.demo.model.jsonMo.jsonModel;
import com.baasexample.demo.model.jsonMo.podModel;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 注释写在这
 *
 * @author Monhey
 */
@CrossOrigin
@RestController
@EnableAutoConfiguration
public class NewFabricController {
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
    @Autowired
    K8sYamlGenerateService k8sYamlGenerateService;
    @ApiOperation(value = "创建fabric网络", notes = "接口说明")
    @PostMapping("/v2/baas/createFabricNet")
    public String CreateFabric(@RequestBody String netWorkJson) throws IOException, ApiException, SftpException {
        jsonModel jm = jsonService.CastJsonToNetBean(netWorkJson);
        String NameSpace = jm.getName();
        fabricYamlGenerateService.formJsonStorageFile(netWorkJson,NameSpace);
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
        String jobName =jenkinsService.createCreatFabricJob(NameSpace);
        jenkinsService.buildJob(jobName);
        for(Orderer orderer:orderList){
            String ordererName = orderer.getOrderName();
            List<String> otherOrdererList = new ArrayList<>();
            for(Orderer o:orderList){
                if(!o.getOrderName().equals(ordererName))
                    otherOrdererList.add(o.getOrderName());
            }
            k8sYamlGenerateService.generateOrdererDeploymentYaml(ordererName,otherOrdererList,NameSpace);
            k8sYamlGenerateService.generateOrdererServiceYaml(ordererName,NameSpace);
            File ordererDepFile = new File("src/main/resources/"+ordererName+"-deployment.yaml");
            InputStream ordererDepIs = new FileInputStream(ordererDepFile);
            sftpUtil.upload("/mnt","nfsdata/fabric/"+NameSpace+"/orderer-service", ordererName+"-deployment.yaml",ordererDepIs);
            File ordererSvcFile = new File("src/main/resources/"+ordererName+"-svc.yaml");
            InputStream ordererSvcIs = new FileInputStream(ordererSvcFile);
            sftpUtil.upload("/mnt","nfsdata/fabric/"+NameSpace+"/orderer-service", ordererName+"-svc.yaml",ordererSvcIs);
            ordererDepFile.delete();
            ordererSvcFile.delete();
        }
        for(Organization org:orgList){
            List<Peer> peerList = org.getPeers();
            String orgName = org.getOrgName();
            k8sYamlGenerateService.generateCaDeploymentYaml(orgName,NameSpace);
            k8sYamlGenerateService.generateCaServiceYaml(orgName,NameSpace);
            k8sYamlGenerateService.generateCliDeploymentYaml(orgName,NameSpace);
            File caDepFile = new File("src/main/resources/"+orgName+"-ca-deployment.yaml");
            InputStream caDepIs = new FileInputStream(caDepFile);
            sftpUtil.upload("/mnt","nfsdata/fabric/"+NameSpace+"/"+orgName, orgName+"-ca-deployment.yaml",caDepIs);
            File caSvcFile = new File("src/main/resources/"+orgName+"-ca-svc.yaml");
            InputStream caSvcIs = new FileInputStream(caSvcFile);
            sftpUtil.upload("/mnt","nfsdata/fabric/"+NameSpace+"/"+orgName, orgName+"-ca-svc.yaml",caSvcIs);
            File cliDepFile = new File("src/main/resources/"+orgName+"-cli-deployment.yaml");
            InputStream cliDepIs = new FileInputStream(cliDepFile);
            sftpUtil.upload("/mnt","nfsdata/fabric/"+NameSpace+"/"+orgName, orgName+"-cli-deployment.yaml",cliDepIs);
            caDepFile.delete();
            caSvcFile.delete();
            cliDepFile.delete();
            for(Peer p:peerList){
                String peerName = p.getPeerName();
                k8sYamlGenerateService.generatePeerDeploymentYaml(peerName,NameSpace,orgName);
                k8sYamlGenerateService.generatePeerServiceYaml(peerName,NameSpace);
                File peerDepFile = new File("src/main/resources/"+peerName+"-deployment.yaml");
                InputStream peerDepIs= new FileInputStream(peerDepFile);
                sftpUtil.upload("/mnt","nfsdata/fabric/"+NameSpace+"/"+orgName, peerName+"-deployment.yaml",peerDepIs);
                File peerSvcFile = new File("src/main/resources/"+peerName+"-svc.yaml");
                InputStream peerSvcIs = new FileInputStream(peerSvcFile);
                sftpUtil.upload("/mnt","nfsdata/fabric/"+NameSpace+"/"+orgName, peerName+"-svc.yaml",peerSvcIs);
                peerDepFile.delete();
                peerSvcFile.delete();
            }
        }
        k8sYamlGenerateService.generateConfigMap(NameSpace);
        File configFile = new File("src/main/resources/builders-config.yaml");
        InputStream configIs= new FileInputStream(configFile);
        sftpUtil.upload("/mnt","nfsdata/fabric/"+NameSpace+"/"+orgList.get(0).getOrgName(), "builders-config.yaml",configIs);
        file.delete();
        file2.delete();
        sftpUtil.logout();
        String job2 = jenkinsService.createCreatK8sFabricJob(NameSpace,orgList);
        jenkinsService.buildJob(job2);
        return "Success";
    }

    @ApiOperation(value = "根据查询fabric网络",notes = "接口说明")
    @GetMapping("/v2/baas/queryFabricTest/{id}")
    public String QueryFabricNetById( @PathVariable(value = "id") int id) throws IOException {
        NewNetInfo netInfo = netMapper.findNetById(id);
        String nameSpace = netInfo.getNamespace();
        return fabricYamlGenerateService.getFromYaml("src/main/resources/"+nameSpace+"-netInfo");
    }

    @ApiOperation(value = "根据查询fabric的pod列表",notes = "接口说明")
    @GetMapping("/v2/baas/queryFabricPod/{namespace}")
    public String QueryFabricPodByNamespace(@PathVariable(value = "namespace") String namespace) throws IOException, ApiException {
//        NewNetInfo netInfo = netMapper.findNetById(id);
//        String nameSpace = netInfo.getNamespace();
       return fabricK8sQueryService.getNameSpacePodList(namespace);
    }

    @ApiOperation(value = "删除网络",notes = "接口说明")
    @GetMapping("/v2/baas/deleteFabric/{id}")
    public String QueryFabric(@PathVariable(value = "id")int id){
        netMapper.updateNetStatu(0,id);
        return "success";
    }

    @ApiOperation(value = "查询所有fabric网络", notes = "接口说明")
    @GetMapping("/v2/baas/queryAllFabric")
    public String Create1Fabric() throws IOException {
        List<NewNetInfo> netList = netMapper.getAllNet();
        List<String> result = new ArrayList<>();
        for(NewNetInfo net:netList){
            String nameSpace = net.getNamespace();
            result.add(fabricYamlGenerateService.getFromYaml("src/main/resources/"+nameSpace+"-netInfo"));
        }
        JSONArray array= JSONArray.parseArray(JSON.toJSONString(result));
        return JSON.toJSONString(array);
    }
    @PostMapping("/v2/baas/testFont")
    public void test(@RequestBody String netWorkJson) throws IOException {
        channelJsonModel c =jsonService.CastJsonToChannelBean(netWorkJson);
        System.out.println(c.toString());
    }
}
