package com.wistron.keycloak;

import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.security.keyvault.secrets.SecretClientBuilder;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import com.wistron.keycloak.service.EsgSdmBpAiService;
import com.wistron.keycloak.utils.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.wistron.keycloak.entity.EsgSdmBpAi;
//import com.wistron.keycloak.service.impl.CityServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.DigestUtils;
import sun.misc.BASE64Encoder;

import java.security.Key;

/**
 * @author Nieblungen_Liu
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestKeyValue {
    //    @Autowired
//    SecretClient secretClient;
//    @Autowired
//    MeterRegistryCustomizer meterRegistryCustomizer;
//    @Autowired
//    CityServiceImpl cityService;
    @Autowired
    EsgSdmBpAiService esgSdmBpAiService;
    @Autowired
    KeycloakAdminUtils KeycloakAdmin;

    @Test
    public void test1() {
//        System.out.println(secretClient);
//        System.out.println(meterRegistryCustomizer);
//        System.out.println(cityService);
        SecretClient secretClient = new SecretClientBuilder()
                .vaultUrl("https://wisdmdvpocakv.vault.azure.net/")
                .credential(new DefaultAzureCredentialBuilder().build())
                .buildClient();
        System.out.println(secretClient);


    }

    @Test
    public void test2() {
        //获取密文（密码加盐）
        String salt = Pbkdf2Sha256Utils.encode("123456");
        System.out.println("salt===" + salt);
        System.out.println(salt);
        boolean verification = Pbkdf2Sha256Utils.verification("123456", salt);
        System.out.println(verification);


        /**
         * Python生成的密码和密文
         * admin123456
         * PBKDF2&SHA256$2000$SzNgPdzz$50f22e207abec8e837bce97642a46f965f19d992217d7df9be496700b286345d
         * PBKDF2&SHA256$2000$VzmO4yOZ$71891148cfbdd9103aaa511d20dc52431c8947ce4a00d89708231ec76053f6f3
         * PBKDF2&SHA256$2000$3xuRb8AR$6bff0310fd35c88572633b00d36e9039fef3e68c6e37b14204958946e8738e93
         */
        String oldPassword7 = "PBKDF2&SHA256$2000$SzNgPdzz$50f22e207abec8e837bce97642a46f965f19d992217d7df9be496700b286345d";
        String oldPassword8 = "PBKDF2&SHA256$2000$VzmO4yOZ$71891148cfbdd9103aaa511d20dc52431c8947ce4a00d89708231ec76053f6f3";
        String oldPassword9 = "PBKDF2&SHA256$2000$3xuRb8AR$6bff0310fd35c88572633b00d36e9039fef3e68c6e37b14204958946e8738e93";
        boolean verification7 = Pbkdf2Sha256Utils.verification("admin123456", oldPassword7);
        boolean verification8 = Pbkdf2Sha256Utils.verification("admin123456", oldPassword8);
        boolean verification9 = Pbkdf2Sha256Utils.verification("admin123456", oldPassword9);
        System.out.println(verification7);
        System.out.println(verification8);
        System.out.println(verification9);
    }

    @Test
    public void test3() {
        String str = "a,b,c,,";
        String[] ary = str.split(",");
        // 预期大于 3，结果是 3
        System.out.println(ary.length);
    }

    @Test
    public void testlogin() {
        KeycloakAdminUtils keycloakAdminUtils = new KeycloakAdminUtils();
//        String token = keycloakAdminUtils.userLogin("k8sdevwihavatar", "Wits.NieblungenLiu",
//                "R$y5tM#j3a", "admin-cli", null);
//        System.out.println(token);
                //登录成功，返回token
        String token = keycloakAdminUtils.userLogin("k8sdevwihavatar", "dewang2015@outlook.com",
                "123456", "demoapp", null);
//        String token = keycloakAdminUtils.userLogin("k8sdevwihavatar", "liudexiang",
//                "123456", "demoapp", null);
        System.out.println(token);
    }

    @Test
    public void testCreate() {
        EsgSdmBpAi esgSdmBpAi = new EsgSdmBpAi();
        esgSdmBpAi.setRealm("k8sdevwihavatar");
        esgSdmBpAi.setClientName("demoapp");
        esgSdmBpAi.setUsername("LIANGZEQUN");
        esgSdmBpAi.setPassword("123456");
        esgSdmBpAi.setRoles("visitor");
        //保存到数据库
        esgSdmBpAiService.save(esgSdmBpAi);
        //根据用户名查询出来
        QueryWrapper<EsgSdmBpAi> objectQueryWrapper = new QueryWrapper<>();
        objectQueryWrapper.eq("username","LIANGZEQUN");
        EsgSdmBpAi esgSdmBpAiServiceOne = esgSdmBpAiService.getOne(objectQueryWrapper);

        KeycloakAdminUtils keycloakAdminUtils = new KeycloakAdminUtils();
        keycloakAdminUtils.removeUser(esgSdmBpAiServiceOne.getUsername(), esgSdmBpAiServiceOne.getRealm());
        //角色必须创建后，才能进行分配
//        keycloakAdminUtils.createRole("visitor", "k8sdevwihavatar");
//        keycloakAdminUtils.createUser("LIANGZEQUN","123456","k8sdevwihavatar");
        //用户重新创建不会返回user的ID TODO 多角色分配需要重新创建新的方法
        keycloakAdminUtils.createUserOnRoleName(esgSdmBpAiServiceOne.getUsername(), esgSdmBpAiServiceOne.getPassword(),
                esgSdmBpAiServiceOne.getRealm(), esgSdmBpAiServiceOne.getRoles());
        //TODO username、realm唯一性校验



    }

    @Test
    public void testClient() {
        KeycloakAdminUtils keycloakAdminUtils = new KeycloakAdminUtils();
        String clientSecret = keycloakAdminUtils.createClient("liudewang", "k8sdevwihavatar");
        System.out.println(clientSecret);
    }

    @Test
    public void testMD5() {
        String encrypt = MD5.encrypt("123456");
        System.out.println(encrypt);


        String digestAsHex = DigestUtils.md5DigestAsHex("123456".getBytes());
        System.out.println(digestAsHex);

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        //加密
        String encode = passwordEncoder.encode("123456");
        //明文和密文比对
        boolean matches = passwordEncoder.matches("123456", encode);
        System.out.println(encode + "——matches:" + matches);


    }

    @Test
    public  void  testCreateUser(){
        KeycloakAdmin.removeUser("liudexiang", "k8sdevwihavatar");
        //用户名和邮箱地址必须唯一
        KeycloakAdmin.createUserOnRoleName("liudexiang","123456","k8sdevwihavatar","visitor",
                "dewang2015@outlook.com","tony","teacher");


    }

    @Test
    public void testRandomPassword(){
        PasswordGenerator passwordGenerator = new PasswordGenerator(7, 4);
        String password = passwordGenerator.generateRandomPassword();
        System.out.println(password);
    }

    @Test
    public void testAES(){
        Key key = AESUtils.createKey();
        byte[] jdkAES = AESUtils.jdkAES("123456", key);
        System.out.println(new BASE64Encoder().encode(jdkAES));
        byte[] decrypt = AESUtils.decrypt(jdkAES, key);
        // (new BASE64Decoder()).decodeBuffer(key)  base64编码和new string 不一样
        System.out.println(new String(decrypt));
    }





















}
