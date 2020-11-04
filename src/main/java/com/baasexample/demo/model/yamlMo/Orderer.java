package com.baasexample.demo.model.yamlMo;

import lombok.Data;

/**
 * 前端传来json里面所包含的orderer数据
 *
 * @author Monhey
 */
@Data
public class Orderer {
    private String orderName;
    private String orderport;
    public String toString(){
        return this.getOrderName()+":"+this.getOrderport();
    }
}
