package com.baasexample.demo.model.dbMo;

import lombok.Data;

/**
 * 注释写在这
 *
 * @author Monhey
 */
@Data
public class ChannelInfo {
    private int id;
    private String name;
    private int networkId;
    private String peers;
}
