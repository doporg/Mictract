package com.baasexample.demo.model.dbMo;

import com.google.gson.Gson;
import lombok.Data;

/**
 * 注释写在这
 *
 * @author Monhey
 */
@Data
public class NetInfo {
    private int id;
    private String NetName;
    private String Description;
    private String CreateTime;
    private int Status;
}
