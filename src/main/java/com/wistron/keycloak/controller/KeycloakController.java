package com.wistron.keycloak.controller;

import lombok.extern.slf4j.Slf4j;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.representations.AccessToken;
import org.linguafranca.pwdb.kdb.KdbCredentials;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.HashMap;
import java.util.Set;


/**
 * @author Nieblungen_Liu
 */
@RestController
@Slf4j
public class KeycloakController {
    @GetMapping("/getinfos")
    public HashMap<Object, Object> search(Principal principal) {
        HashMap<Object, Object> hashMap = new HashMap<>();
        if (principal instanceof KeycloakPrincipal) {
            AccessToken accessToken = ((KeycloakPrincipal) principal).getKeycloakSecurityContext().getToken();
            System.out.println( accessToken.getTrustedCertificates());

            String preferredUsername = accessToken.getPreferredUsername();
            String subject = accessToken.getSubject();
            AccessToken.Access realmAccess = accessToken.getRealmAccess();
            Set<String> roles = realmAccess.getRoles();
            hashMap.put("username",preferredUsername);
            hashMap.put("roles",roles);
            hashMap.put("subject",subject);
            log.info("当前登录用户：{}, 角色：{},Subject：{}", preferredUsername, roles,subject);
        }
        return hashMap;

    }
    @GetMapping("/logout")
    public String logout(HttpServletRequest request) throws ServletException {
        request.logout();
        return "退出成功";
    }

    @GetMapping("/keycloakAuthen")
    public String testAuthen()   {
        return "你好喔！通过身份验证，欢迎您！";
    }

    @GetMapping("/keycloak")
    public String testKeycloak()   {
        return "你好喔！通过身份验证，欢迎您！";
    }
}
