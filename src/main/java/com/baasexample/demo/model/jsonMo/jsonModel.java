package com.baasexample.demo.model.jsonMo;

import com.baasexample.demo.model.yamlMo.Orderer;
import com.baasexample.demo.model.yamlMo.Organization;
import lombok.Data;

import java.util.List;

/**
 * 注释写在这
 *
 * @author Monhey
 */
@Data
public class jsonModel {
    private String name;
    private List<Orderer> order;
    private List<Organization> org;
    private String consensus;
    private String tls;
}
