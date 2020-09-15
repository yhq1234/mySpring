package com.example.entity;

import lombok.Data;
import org.apache.solr.client.solrj.beans.Field;

import java.io.Serializable;
@Data
public class SysUser implements Serializable {
    static final long serialVersionUID = 1L;

    @Field
    private Integer id;

    @Field
    private String name;

    @Field
    private String password;


}
