package com.baasexample.demo.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baasexample.demo.model.jsonMo.podModel;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.models.V1Pod;
import io.kubernetes.client.models.V1PodList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.stream.events.Namespace;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 注释写在这
 *
 * @author Monhey
 */
@Service(value = "FabricK8sQueryService")
public class FabricK8sQueryService {
    @Autowired
    k8sClientService k8sClientService;
    /**
     * 接收节点信息，就是kubectl get pods -n hyperledger的那些
     * */
    public Map<String,String> getNameSpacePodListStatu(String NameSpace) throws IOException, ApiException {
        k8sClientService k = new k8sClientService();
        k.setK8sClient();
        CoreV1Api apiInstance = new CoreV1Api();
        String namespace = "hyperledger";
        try {
            V1PodList result = apiInstance.listNamespacedPod(NameSpace, true, null, null, null, null, null, null, null, null);
            List<V1Pod> list = result.getItems();
            Map<String,String> map = new HashMap<>();
            for(V1Pod s:list){
                if(!s.getStatus().getPhase().equals("Failed"))
                    map.put(s.getMetadata().getName(),s.getStatus().getPhase());
            }
            return map;
        } catch (ApiException e) {
            System.err.println("Exception when calling CoreV1Api#getNameSpacePodListStatu");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
        return null;
    }
    public String getNameSpacePodList(String NameSpace) throws IOException, ApiException {
        k8sClientService k = new k8sClientService();
        k.setK8sClient();
        CoreV1Api apiInstance = new CoreV1Api();
        String namespace = "hyperledger";
        try {
            V1PodList result = apiInstance.listNamespacedPod(NameSpace, true, null, null, null, null, null, null, null, null);
            List<V1Pod> list = result.getItems();
            List<podModel> podList = new ArrayList<>();
            for(V1Pod s:list){
                if(!s.getStatus().getPhase().equals("Failed")){
                    podModel p = new podModel();
                    p.setName(s.getMetadata().getName());
                    p.setState(s.getStatus().getPhase());
                    podList.add(p);
                }
            }
            JSONArray array= JSONArray.parseArray(JSON.toJSONString(podList));
            return JSON.toJSONString(array);
        } catch (ApiException e) {
            System.err.println("Exception when calling CoreV1Api#getNameSpacePodListStatu");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
        return null;
    }
    public static void main(String[] args) throws IOException, ApiException {
        FabricK8sQueryService f = new FabricK8sQueryService();
        System.out.println(f.getNameSpacePodList("enhance"));
    }
}
