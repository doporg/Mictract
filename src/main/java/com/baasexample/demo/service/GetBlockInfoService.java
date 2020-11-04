package com.baasexample.demo.service;

import com.baasexample.demo.model.yamlMo.BlockData;
import com.baasexample.demo.model.yamlMo.TxData;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 *
 *解析每一个块中的信息
 * @author Monhey
 */
@Service(value = "GetBlockInfoService")
public class GetBlockInfoService {

    public BlockData readBlockDataFile(String sourcePath){
        String line;
        int lineNum=0;
        List<String> dataList = new ArrayList<>();
        BufferedReader reader=null;
        BlockData bd = new BlockData();
        try{
            URL url = new URL(sourcePath);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            while ((line = reader.readLine()) != null){
                lineNum++;
                if(line.contains("tx_id")){
//                    System.out.println(line);
                    bd.setDataHash(line.split("\"")[3]);
                }
                if(line.contains("number")){
//                    System.out.println(line);
                    bd.setBlockNumber(Integer.parseInt(line.split("\"")[3]));
                }
                if(line.contains("previous_hash")){
//                    System.out.println(line);
                    if(bd.getBlockNumber()!=0)
                        bd.setPreHash(line.split("\"")[3]);
                    else
                        bd.setPreHash(null);
                }
                if(line.contains("timestamp")){
//                    System.out.println(line);
                    bd.setTimeStamp(line.split("\"")[3]);
                }
            }
        }
        catch (Exception ie){
            ie.printStackTrace();
        }finally{
            try{
                if(reader != null)
                    reader.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return bd;
    }
    public TxData readTxDataFile(String sourcePath){
        String line;
        int lineNum=0;
        List<String> dataList = new ArrayList<>();
        BufferedReader reader=null;
        TxData td = new TxData();
        try{
            URL url = new URL(sourcePath);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            while ((line = reader.readLine()) != null){
                lineNum++;
                if(line.contains("chaincode_id")){
                    line = reader.readLine();
                    td.setChainCodeName(line.split("\"")[3]);
                }
                if(line.contains("number")){
                    td.setBlockNumber(Integer.parseInt(line.split("\"")[3]));
                }
                if(line.contains("tx_id")){
                    td.setId(line.split("\"")[3]);
                }
                if(line.contains("timestamp")){
                    td.setTime(line.split("\"")[3]);
                }
            }
        }
        catch (Exception ie){
            ie.printStackTrace();
        }finally{
            try{
                if(reader != null)
                    reader.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return td;
    }

}
