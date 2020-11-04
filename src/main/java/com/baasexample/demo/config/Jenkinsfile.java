package com.baasexample.demo.config;


import com.baasexample.demo.model.yamlMo.Organization;

import java.util.List;

/**
 * 生成jenkinsfile
 *
 * @author 张富利
 */

public class Jenkinsfile {

    private String NameSpace;
    private String Script;

    public Jenkinsfile() {
    }
    public Jenkinsfile(String NameSpace){
        this.NameSpace = NameSpace;
    }

    //把buildpack和chaincode放进来
    public void setCreateFabricScript(String NameSpace){
        StringBuilder sb = new StringBuilder("");
        sb.append("cd /mnt/nfsdata/fabric\n");
        sb.append("cp fabricOps.sh /mnt/nfsdata/fabric/"+NameSpace+"\n");
        sb.append("cd "+NameSpace+"\n");
        sb.append("chmod 777 fabricOps.sh\n");
        sb.append("sh fabricOps.sh start\n");
        sb.append("cd ../\n");
        sb.append("cp -r pau "+NameSpace+"/\n");
        sb.append("cp -r chaincode "+NameSpace+"/\n");
        sb.append("cp -r buildpack "+NameSpace+"/");
        this.Script=sb.toString();
    }

    public void tarAndPushInPod(String podName1,String podName2,String NameSpace){
        StringBuilder sb = new StringBuilder("");
        sb.append("cd /mnt/nfsdata/fabric/\n");
        sb.append("kubectl cp marbles-org1.tgz -n "+NameSpace+" "+podName1+":/opt/gopath/src/github.com/hyperledger/fabric/peer/marbles-org1.tgz\n");
        sb.append("kubectl cp marbles-org2.tgz -n "+NameSpace+" "+podName2+":/opt/gopath/src/github.com/hyperledger/fabric/peer/marbles-org2.tgz\n");
        sb.append("kubectl exec "+podName1+" -n "+NameSpace+" -- peer lifecycle chaincode install marbles-org1.tgz\n");
        sb.append("kubectl exec "+podName2+" -n "+NameSpace+" -- peer lifecycle chaincode install marbles-org2.tgz");
        this.Script=sb.toString();
    }
    public void chaincodeOps(String cliPodName1,String cliPodName2,String peerPodName,String NameSpace){
        StringBuilder sb = new StringBuilder("");
        sb.append("cd /mnt/nfsdata/fabric/"+NameSpace+"/chaincode\n");
        sb.append("kubectl create -f k8s/\n");
        sb.append("kubectl cp org1Commit.sh -n "+NameSpace+" "+cliPodName1+":/opt/gopath/src/github.com/hyperledger/fabric/peer/org1Commit.sh\n");
        sb.append("kubectl cp org2Commit.sh -n "+NameSpace+" "+cliPodName2+":/opt/gopath/src/github.com/hyperledger/fabric/peer/org2Commit.sh\n");
        sb.append("kubectl cp peerCommit.sh -n "+NameSpace+" "+peerPodName+":/opt/gopath/src/github.com/hyperledger/fabric/peer/peerCommit.sh\n");
        sb.append("kubectl exec "+cliPodName1+" -n "+NameSpace+" -- sh org1Commit.sh\n");
        sb.append("kubectl exec "+cliPodName2+" -n "+NameSpace+" -- sh org2Commit.sh\n");
        sb.append("kubectl exec "+peerPodName+" -n "+NameSpace+" -- sh peerCommit.sh\n");
        this.Script=sb.toString();
    }
    public void chaincodeInvoke(String podName,String NameSpace){
        StringBuilder sb = new StringBuilder("");
        sb.append("cd /mnt/nfsdata/fabric/"+NameSpace+"/chaincode\n");
        sb.append("kubectl cp chaincodeOp.sh -n "+NameSpace+" "+podName+":/opt/gopath/src/github.com/hyperledger/fabric/peer/chaincodeOp.sh\n");
        this.Script=sb.toString();
    }
    public void CpAndExecFirstChannel(String podName,String NameSpace){
        StringBuilder sb = new StringBuilder("");
        //kubectl cp newmarbles-org1.tgz -n hyperledger cli-org1-6dc897958b-ldhmq:/opt/gopath/src/github.com/hyperledger/fabric/peer/newmarbles-org1.tgz
        sb.append("cd /mnt/nfsdata/fabric/"+NameSpace+"\n");
        sb.append("chmod 777 *\n");
        sb.append("kubectl cp firstChannel.sh -n "+NameSpace+" "+podName+":/opt/gopath/src/github.com/hyperledger/fabric/peer/firstChannel.sh\n");
        sb.append("kubectl exec "+podName+" -n "+NameSpace+" -- sh firstChannel.sh");
        this.Script=sb.toString();
    }
    public void CpAndExecSecChannel(String podName,String NameSpace){
        StringBuilder sb = new StringBuilder("");
        //kubectl cp newmarbles-org1.tgz -n hyperledger cli-org1-6dc897958b-ldhmq:/opt/gopath/src/github.com/hyperledger/fabric/peer/newmarbles-org1.tgz
        sb.append("cd /mnt/nfsdata/fabric/"+NameSpace+"\n");
        sb.append("kubectl cp firstChannel.sh -n "+NameSpace+" "+podName+":/opt/gopath/src/github.com/hyperledger/fabric/peer/secChannel.sh\n");
        sb.append("kubectl exec "+podName+" -n "+NameSpace+" -- sh secChannel.sh");
        this.Script=sb.toString();
    }
    public void setKubectlApply(List<Organization> orgList,String NameSpace){
        StringBuilder sb = new StringBuilder("");
        sb.append("cd /mnt/nfsdata/fabric/"+NameSpace+"\n");
        sb.append("kubectl create ns "+NameSpace+"\n");
        sb.append("kubectl create -f orderer-service/\n");
        for(Organization org:orgList){
            sb.append("kubectl create -f"+org.getOrgName()+"/\n");
        }
        this.Script=sb.toString();
    }
    public void setCpAndExplainBlockData(String Namespace,String podName,int height,String ChannelName){
        StringBuilder sb = new StringBuilder();
        sb.append("cd /usr/share/nginx/html\n");
        sb.append("mkdir "+Namespace+"\n");
        for(int i=height-1;i>=0;i--) {
            sb.append("kubectl cp -n " + Namespace + " " + podName + ":/opt/gopath/src/github.com/hyperledger/fabric/peer/"+ChannelName+"_" + i + ".block /usr/share/nginx/html/" + Namespace + "/"+ChannelName+"_" + i + ".block\n");
        }
        sb.append("cd /usr/share/nginx/html/"+Namespace+"\n");
        for(int i=height-1;i>=0;i--){
            sb.append("/root/go/bin/configtxgen -inspectBlock "+ChannelName+"_"+i+".block > "+ChannelName+"_"+i+".json\n");
        }
        this.Script = sb.toString();
    }

    public void setChaincodeInstall(String Namespace,String podName1,String podName2){
        StringBuilder sb = new StringBuilder();
        sb.append("cd /mnt/nfsdata/fabric/\n");
        sb.append("cp marbles-org1.tgz /mnt/ntsdata/fabric/"+Namespace+"\n");
        sb.append("cp marbles-org2.tgz /mnt/ntsdata/fabric/"+Namespace+"\n");
        sb.append("kubectl exec "+podName1+" -n "+Namespace+" -- peer lifecycle chaincode install marbles-org1.tgz");
        sb.append("kubectl exec "+podName2+" -n "+Namespace+" -- peer lifecycle chaincode install marbles-org2.tgz");
        this.Script=sb.toString();
    }
    public void setInstallChaincodeInChannle(String Namespace,String podName1,String podName2,String peerPodName,String ChannelName,String git){
        StringBuilder sb = new StringBuilder();
        sb.append("cd /mnt/nfsdata/fabric/"+Namespace+"\n");
        sb.append("kubectl exec "+podName1+" -n "+Namespace+" peer lifecycle chaincode approveformyorg --channelID "+ChannelName+" --name marbles --version 1.0 --init-required --package-id marbles:"+git+" --sequence 1 -o orderer0:7050 --tls --cafile $ORDERER_CA --signature-policy \"AND ('org1MSP.peer','org2MSP.peer')\"\n");
        sb.append("kubectl exec "+podName2+" -n "+Namespace+" peer lifecycle chaincode approveformyorg --channelID "+ChannelName+" --name marbles --version 1.0 --init-required --package-id marbles:"+git+" --sequence 1 -o orderer0:7050 --tls --cafile $ORDERER_CA --signature-policy \"AND ('org1MSP.peer','org2MSP.peer')\"\n");
        sb.append("kubectl exec "+peerPodName+" -n "+Namespace+" -- peer lifecycle chaincode commit -o orderer0:7050 --channelID "+ChannelName+" --name marbles --version 1.0 --sequence 1 --init-required --tls true --cafile $ORDERER_CA --peerAddresses peer0-org1:7051 --tlsRootCertFiles /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org1/peers/peer0-org1/tls/ca.crt --peerAddresses peer0-org2:7051 --tlsRootCertFiles /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org2/peers/peer0-org2/tls/ca.crt --signature-policy \"AND ('org1MSP.peer','org2MSP.peer')\"");
        this.Script=sb.toString();
    }
    public String getScript(){
        return this.Script;
    }

//    public Jenkinsfile(Long appEnvId, ArrayList<Stage> pipelineStage) {
//        this.appEnvId = appEnvId;
//        this.stages = "";
//
//        for (int i = 0; i < pipelineStage.size(); i++) {
//            Stage stage = pipelineStage.get(i);
//
//            String name = stage.getName();
//            this.stages += "stage(\'" + name + "\'){ \n";
//            ArrayList<Step> steps = stage.getSteps();
//            if (steps.size() == 0) {
//                this.stages += "echo \'  没有运行的脚本 \' \n";
//            }
//            for (int j = 0; j < steps.size(); j++) {
//                this.stages += "steps{\n";
//                Step task = steps.get(j);
//                Step.TaskType taskName = task.getTaskName();
//                String gitUrl = task.getGitUrl();
//                if(!task.getGitUrl().isEmpty()){
//                    this.git = task.getGitUrl();
//                    String folderGit = this.git.split("/")[this.git.split("/").length-1];
//                    this.dir = folderGit.split("[.]")[0];
//                }
//
//                String dockerUserName = task.getDockerUserName();
//                String respository = task.getRepository();
//                String dockerPassword = task.getDockerPassword();
//                String respositoryVersion = task.getRepositoryVersion();
//                String shell = task.getShell();
//                String deploy = task.getDeploy();
//                String ip = task.getIp();
//                String token = task.getToken();
//                String dockerRepoHost = "registry.dop.clsaa.com";
//                String dockerRepoPath = "/default";
//                String imageName = dockerRepoHost + dockerRepoPath;
//                try {
//                    if (respository.startsWith("http")) {
//                        dockerRepoHost = new URL(respository).getHost();
//                        dockerRepoPath = new URL(respository).getPath();
//                        imageName = dockerRepoHost + dockerRepoPath;
//                    } else {
//                        dockerRepoHost = respository.split("/")[0];
//                        imageName = respository;
//                    }
//                } catch (MalformedURLException e) {
//                    e.printStackTrace();
//                    BizAssert.justFailed(new BizCode(BizCodes.INVALID_PARAM.getCode()
//                            , e.getMessage()));
//                }
//                switch (taskName) {
//                    case PullCode:
//                        this.stages += "deleteDir() \n";
////                        this.stages += "git \"" + gitUrl + "\" \n";
//                        this.stages += "sh \'git clone \"" + gitUrl + "\" \'\n";
//                        if(!this.dir.isEmpty()){
//                            this.stages += "dir(\"" + this.dir + "\"){ \n";
//                            this.stages += "sh \'echo commitId `git rev-parse HEAD`\' \n";
//                            this.stages += "}\n";
//                        }
//                        break;
//                    case BuildMaven:
//                        if(!this.dir.isEmpty()){
//                            this.stages += "dir(\"" + this.dir + "\"){ \n";
//                            this.stages += "sh \'mvn --version \' \n";
//                            this.stages += "sh \"mvn -U -am clean package \" \n";
//                            this.stages += "}\n";
//                        }else{
//                            this.stages += "sh \'mvn --version \' \n";
//                            this.stages += "sh \"mvn -U -am clean package \" \n";
//                        }
//                        break;
//                    case BuildNode:
//                        this.stages += "sh \'npm --version \' \n";
//                        this.stages += "sh \'node --version \' \n";
////                        this.stages += "sh \'npm install \' \n";
//                        break;
//                    case BuildDjanggo:
////                        this.stages += "sh \'pip freeze > ./requirements.txt \' \n";
////                        this.stages += "sh \'pip install -r ./requirements.txt \' \n";
////                        this.stages += "sh \'python ./manage.py runserver \' \n";
//                        this.stages += "sh \'python --version \' \n";
//                        this.stages += "sh \'pip --version \' \n";
//                        break;
//                    case BuildDocker:
//                        if(this.dir.isEmpty()){
//                            this.stages += "sh \'docker build -t " + imageName + ":" + respositoryVersion + " ./\' \n";
//                        }else{
//                            this.stages += "dir(\"" + this.dir + "\"){ \n";
//                            this.stages += "sh \'docker build -t " + imageName + ":" + respositoryVersion + " ./\' \n";
//                            this.stages += "}\n";
//                        }
//
//                        break;
//                    case PushDocker:
//                        if(this.dir.isEmpty()){
//                            this.stages += "sh \'docker login -u \"" + dockerUserName + "\" -p \"" + dockerPassword + "\" " + dockerRepoHost + "\' \n";
//                            this.stages += "sh \'docker push " + imageName + ":" + respositoryVersion + "\' \n";
//                        }else{
//                            this.stages += "dir(\"" + this.dir + "\"){ \n";
//                            this.stages += "sh \'docker login -u \"" + dockerUserName + "\" -p \"" + dockerPassword + "\" " + dockerRepoHost + "\' \n";
//                            this.stages += "sh \'docker push " + imageName + ":" + respositoryVersion + "\' \n";
//                            this.stages += "}\n";
//                        }
//                        break;
//                    case CustomScript:
//                        this.stages += "sh \'" + shell + "\' \n";
//                    case Deploy:
//                        String[] deploys = deploy.split("---\n");
//                        for (int z = 0; z < deploys.length; z++) {
//                            Yaml yaml = new Yaml();
//                            if (deploys[z] == "") {
//                                break;
//                            }
//                            Map map = yaml.load(deploys[z]);
//                            Object apiVersion = map.get("apiVersion");
//                            Object kind = map.get("kind");
//                            Map metadata = (Map) map.get("metadata");
//                            Object namespace = metadata.get("namespace");
//                            Object deploymentName = metadata.get("name");
//                            if (kind.toString().equals("Deployment")) {
//                                // apiVersion: apps/v1beta1
//                                this.stages += "sh \'\'\'\n" +
//                                        "curl -X PUT -H \'Content-Type:application/yaml\' " +
//                                        "-k -H \'Bearer " + token + "\' " +
//                                        ip +
//                                        "/apis/" + apiVersion +
//                                        "/namespaces/" + namespace.toString() +
//                                        "/" + kind.toString().toLowerCase() + "s/" + deploymentName.toString() + " " +
//                                        "-d \'\n" +
//                                        deploys[z].trim() + "\n" +
//                                        "\'\n" +
//                                        " \'\'\'" + "\n";
//                                this.stages += "sh \'\'\'\n" +
//                                        "curl -X POST -H \'Content-Type:application/yaml\' " +
//                                        "-k -H \'Bearer " + token + "\' " +
//                                        ip +
//                                        "/apis/" + apiVersion +
//                                        "/namespaces/" + namespace.toString() +
//                                        "/" + kind.toString().toLowerCase() + "s " +
//                                        "-d \'\n" +
//                                        deploys[z].trim() + "\n" +
//                                        "\'\n" +
//                                        " \'\'\'" + "\n";
//                            } else {
//                                this.stages += "sh \'\'\'\n" +
//                                        "curl -X PUT -H \'Content-Type:application/yaml\' " +
//                                        "-k -H \'Bearer " + token + "\' " +
//                                        ip +
//                                        "/api/" + apiVersion +
//                                        "/namespaces/" + namespace.toString() +
//                                        "/" + kind.toString().toLowerCase() + "s/" + deploymentName.toString() + " " +
//                                        "-d \'\n" +
//                                        deploys[z].trim() + "\n" +
//                                        "\'\n" +
//                                        " \'\'\'" + "\n";
//                                this.stages += "sh \'\'\'\n" +
//                                        "curl -X POST -H \'Content-Type:application/yaml\' " +
//                                        "-k -H \'Bearer " + token + "\' " +
//                                        ip +
//                                        "/api/" + apiVersion +
//                                        "/namespaces/" + namespace.toString() +
//                                        "/" + kind.toString().toLowerCase() + "s " +
//                                        "-d \'\n" +
//                                        deploys[z].trim() + "\n" +
//                                        "\'\n" +
//                                        " \'\'\'" + "\n";
//                            }
//                        }
//                        break;
//                }
//                this.stages += "}\n";
//            }
//            this.stages += "}\n";
//        }
//    }

//    public String getScript() {
//        return ("pipeline {\n" +
//                "    agent any\n" +
//                "    stages {\n" +
//                this.stages + "\n" +
//                "    }\n" +
//                "}\n");
//    }


}
