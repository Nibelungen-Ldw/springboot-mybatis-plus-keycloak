package com.wistron.keycloak.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Nieblungen_Liu
 */
@RestController
public class PersonInfoController {

    @Autowired
    private JdbcTemplate jdbc;

//	@Autowired
//	private PersonInfoServiceImpl personInfoService;
    // 根据id查询项目


    @RequestMapping(value = "/getDate")
    public Object get(int id){
        String sql = "select * from test3 where id=?";
        return jdbc.queryForList(sql, id);


    }

    @RequestMapping(value = "/delete")
    public Object delete(int id){
        String sql = "DELETE FROM test008	WHERE id=?";
        jdbc.update(sql,id);
        return "删除成功！";
    }

    @RequestMapping(value = "/update")
    public Object update(int id ,String name){
        String sql = "UPDATE test008	SET name=? 	WHERE id=?";
        System.err.println("delete");
        jdbc.update(sql,name,id);
        return "更新成功！";
    }



    @RequestMapping(value = "/add")
    public Object add( String name,int age,String tel,String address,int id){
        String sql = "INSERT INTO test008(	name, age, tel, address, id)	VALUES (?, ?, ?, ?, ?);";
        jdbc.update(sql,name,age,tel,address,id);
        return "新增成功";


    }

    @RequestMapping(value = "/get")
    public Object getPerson(int id){
        String sql = "select * from test008 where id=?";
        return jdbc.queryForMap(sql, id);

    }



    @RequestMapping(value = "/count")
    public Integer count( ){
        String sql = "SELECT count(name) 	FROM test008  ;";
        Integer k=jdbc.queryForObject(sql, Integer.class);
        return	k;
    }

}