package com.baasexample.demo.controller;

import com.baasexample.demo.Mapper.ChaincodeMapper;
import com.baasexample.demo.Mapper.ChannelMapper;
import com.baasexample.demo.Mapper.NewNetMapper;
import com.baasexample.demo.config.SFTPUtil;
import com.baasexample.demo.model.dbMo.ChaincodeInfo;
import com.baasexample.demo.model.dbMo.ChannelInfo;
import com.baasexample.demo.model.dbMo.NewNetInfo;
import com.baasexample.demo.model.jsonMo.chaincodeJsonModel;
import com.baasexample.demo.service.FabricK8sQueryService;
import com.baasexample.demo.service.JenkinsService;
import com.baasexample.demo.service.JsonService;
import com.baasexample.demo.service.K8sYamlGenerateService;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.SftpStatVFS;
import io.kubernetes.client.ApiException;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 注释写在这
 *
 * @author Monhey
 */
@CrossOrigin
@RestController
@EnableAutoConfiguration
public class NewChaincodeController {
//    @Autowired
//    JsonService jsonService;
//    @Autowired
//    JenkinsService jenkinsService;
//    @Autowired
//    K8sYamlGenerateService k8sYamlGenerateService;
//    @Autowired
//    ChaincodeMapper chaincodeMapper;
//    @Autowired
//    FabricK8sQueryService fabricK8sQueryService;
//    @Autowired
//    NewNetMapper newNetMapper;
//    @Autowired
//    ChannelMapper channelMapper;
//    @ApiOperation(value = "创建fabric chaincode", notes = "接口说明")
//    @PostMapping("/v2/baas/createFabricChaincode")
//    public String CreateFabricChaincdoe(@RequestBody String chainCodeJson) throws IOException, ApiException, SftpException {
//        chaincodeJsonModel chaincodeJsonModel = jsonService.CastJsonToChaincodeBean(chainCodeJson);
//        int NetId = chaincodeJsonModel.getNetworkId();
//        int ChannelId = chaincodeJsonModel.getChannelId();
//        NewNetInfo info = newNetMapper.findNetById(NetId);
//        String chaincodeName= chaincodeJsonModel.getName();
//        String git = chaincodeJsonModel.getGit();
//        String NameSpace = info.getNamespace();
//        ChannelInfo cinfo = channelMapper.findChannelById(ChannelId);
//        String[] orgNameList = cinfo.getPeers().split(",");
//        List<String> cli = new ArrayList<>();
//        SFTPUtil sftpUtil = new SFTPUtil();
//        sftpUtil.login();
//        for(String s :orgNameList){
//            cli.add(s.split("-")[1]);
//        }
//        Map<String,String> map = fabricK8sQueryService.getNameSpacePodListStatu(NameSpace);
//        Set<String> set = map.keySet();
//        List<String> cliPodList = new ArrayList<>();
//        for(String s:set) {
//            for (String t : cli) {
//                if (s.contains("cli-" + t))
//                    cliPodList.add(s);
//            }
//        }
//        //创建链码YAML并上传
//        for(String orgName:cli){
////            k8sYamlGenerateService.generateChaincodeDeploymentYaml(chaincodeName,NameSpace,orgName,git,);
//            k8sYamlGenerateService.generateChaincodeServiceYaml(chaincodeName,NameSpace,orgName);
//            File chaincodeDepFile = new File("src/main/resources/chaincode-"+orgName+"-"+chaincodeName+"-deployment.yaml");
//            InputStream chaincoderDepIs = new FileInputStream(chaincodeDepFile);
//            sftpUtil.upload("/mnt","nfsdata/fabric/"+NameSpace+"/chaincode/k8s/", "chaincode-"+orgName+"-"+chaincodeName+"-deployment.yaml",chaincoderDepIs);
//            File chaincodeSvcFile = new File("src/main/resources/chaincode-"+orgName+"-"+chaincodeName+"-service.yaml");
//            InputStream chaincodeSvcIs = new FileInputStream(chaincodeSvcFile);
//            sftpUtil.upload("/mnt","nfsdata/fabric/"+NameSpace+"/chaincode/k8s/", "chaincode-"+orgName+"-"+chaincodeName+"-service.yaml",chaincodeSvcIs);
//            chaincodeDepFile.delete();
//            chaincodeSvcFile.delete();
//        }
//        sftpUtil.logout();
//        return null;
//    }

}
