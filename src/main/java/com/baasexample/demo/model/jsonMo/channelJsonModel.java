package com.baasexample.demo.model.jsonMo;

import lombok.Data;

import java.util.List;

/**
 * 注释写在这
 *
 * @author Monhey
 */
@Data
public class channelJsonModel {
    private String name;
    private int networkId;
    private List<String> peers;
}
