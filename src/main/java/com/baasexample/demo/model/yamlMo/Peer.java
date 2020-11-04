package com.baasexample.demo.model.yamlMo;

import lombok.Data;

import java.util.List;

/**
 * json org包含的peer
 *
 * @author Monhey
 */
@Data
public class Peer {
    private String peerName;
    private List<String> ports;
//    public String toString(){
//        String res =this.getPeerName();
//        for(int i=0;i<ports.size();i++){
//            if(i==ports.size()-1){
//                r
//            }
//        }
//    }
}
