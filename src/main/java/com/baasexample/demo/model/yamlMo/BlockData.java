package com.baasexample.demo.model.yamlMo;

import lombok.Data;

/**
 * 注释写在这
 *
 * @author Monhey
 */
@Data
public class BlockData {
    private String DataHash;
    private int blockNumber;
    private String PreHash;
    private String TimeStamp;
}
