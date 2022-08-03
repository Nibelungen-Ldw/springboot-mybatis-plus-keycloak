package com.wistron.keycloak.controller;


import com.wistron.keycloak.entity.EsgSdmBpAi;
import com.wistron.keycloak.service.EsgSdmBpAiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author liudewang
 * @since 2022-07-27
 */
@RestController
//@RequestMapping("/springboot/esg-sdm-bp-ai")
public class EsgSdmBpAiController {
    @Autowired
    EsgSdmBpAiService esgSdmBpAiService;
    @GetMapping(value = "/getAllUserInfos",produces = {"application/json;charset=utf-8"})
    public List<EsgSdmBpAi> getAllUserInfos(){
        List<EsgSdmBpAi> list = esgSdmBpAiService.list(null);
        return list;

    }

}

