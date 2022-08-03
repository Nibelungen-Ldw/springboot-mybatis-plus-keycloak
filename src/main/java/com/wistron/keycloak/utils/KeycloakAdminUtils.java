package com.wistron.keycloak.utils;

import lombok.Data;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Nieblungen_Liu
 */
    @Configuration
    public class KeycloakAdminUtils {

        private static final Logger LOG = LoggerFactory.getLogger(KeycloakAdminUtils.class);
        @Value("${AUTH_SERVER_URL}")
        private String AUTH_SERVER_URL;
        final String REALM = "k8sdevwihavatar";
        final String CLIENT_ID = "demoapp";
        final String USER_NAME = "Wits.NieblungenLiu";
        final String PASSWORD = "R$y5tM#j3a";


//        final String ROLE_NAME_USER = "testuser";
//        final String CLIENT_NAME = "testClient";
        // acceccToken过期时间 12h (单位：s)
        final Integer ACCESS_TOKEN_LIFESPAN = 12*60*60;


        /**
         * 创建realm
         * @param newRealm
         */
        public boolean createRealm(String newRealm) {
            Keycloak keycloak = Keycloak.getInstance(AUTH_SERVER_URL, REALM, USER_NAME, PASSWORD, CLIENT_ID);
            try {
                RealmRepresentation realm = new RealmRepresentation();
                realm.setRealm(newRealm);
                realm.setEnabled(true);
                realm.setSslRequired("NONE");
                realm.setAccessTokenLifespan(ACCESS_TOKEN_LIFESPAN);
                keycloak.realms().create(realm);
                return true;
            } catch (Exception e){
                LOG.error("create realm failed,for",e);
                return false;
            } finally {
                keycloak.close();
            }
        }

        /**
         * 修改realm名称
         * @param newRealm
         * @param oldRealm
         * @return
         */
        public boolean updateRealm(String newRealm,String oldRealm) {
            Keycloak keycloak = Keycloak.getInstance(AUTH_SERVER_URL, REALM, USER_NAME, PASSWORD, CLIENT_ID);
            try {
                RealmRepresentation realm = new RealmRepresentation();
                realm.setRealm(newRealm);
                realm.setEnabled(true);
                realm.setSslRequired("NONE");
                realm.setAccessTokenLifespan(ACCESS_TOKEN_LIFESPAN);
                keycloak.realm(oldRealm).update(realm);
                return true;
            } catch (Exception e){
                LOG.error("update realm failed,for",e);
                return false;
            } finally {
                keycloak.close();
            }
        }

        /**
         * 删除realm
         * @param realm
         * @return
         */
        public boolean removeRealm(String realm) {
            Keycloak keycloak = Keycloak.getInstance(AUTH_SERVER_URL, REALM, USER_NAME, PASSWORD, CLIENT_ID);
            try {
                keycloak.realm(realm).remove();
                return true;
            } catch (Exception e){
                LOG.error("remove realm failed,for",e);
                return false;
            } finally {
                keycloak.close();
            }
        }

        /**
         * 创建客户端(需要client_secret)
         * @param clientId
         * @param realmName
         * @return client_secret 客户端密钥
         */
        public String createClient(String clientId, String realmName) {
            Keycloak keycloak = Keycloak.getInstance(AUTH_SERVER_URL, REALM, USER_NAME, PASSWORD, CLIENT_ID);
            try {
                ClientRepresentation client = new ClientRepresentation();
                client.setClientId(clientId);
                // client.setSecret("");
                client.setRedirectUris(new ArrayList<String>());
                client.setBearerOnly(false);
                client.setPublicClient(false);
                client.setDirectAccessGrantsEnabled(true);
                Response response = keycloak.realm(realmName).clients().create(client);
                response.close();
                String cId = getCreatedId(response);
                LOG.info("client created with id: "+ cId);
                // 生成新密钥
                CredentialRepresentation credentialRepresentation = keycloak.realm(realmName).clients().get(cId).generateNewSecret();
                return credentialRepresentation.getValue();
            } catch (Exception e){
                LOG.error("create client failed,for",e);
                return null;
            } finally {
                keycloak.close();
            }
        }

        /**
         * 创建客户端(Public)
         * @param clientId
         * @param realmName
         */
        public boolean createClientPublic(String clientId, String realmName) {
            Keycloak keycloak = Keycloak.getInstance(AUTH_SERVER_URL, REALM, USER_NAME, PASSWORD, CLIENT_ID);
            try {
                ClientRepresentation client = new ClientRepresentation();
                client.setClientId(clientId);
                client.setRedirectUris(new ArrayList<String>());
                client.setBearerOnly(false);
                client.setPublicClient(true);
                client.setDirectAccessGrantsEnabled(true);
                Response response = keycloak.realm(realmName).clients().create(client);
                response.close();
                if(!response.getStatusInfo().equals(Response.Status.CREATED)) {
                    return false;
                }
                return true;
            } catch (Exception e){
                LOG.error("create client public failed,for",e);
                return false;
            } finally {
                keycloak.close();
            }
        }

        /**
         * 创建role
         * @param roleName
         * @param realmName
         * @return
         */
        public boolean createRole(String roleName, String realmName) {
            Keycloak keycloak = Keycloak.getInstance(AUTH_SERVER_URL, REALM, USER_NAME, PASSWORD, CLIENT_ID);
            try {
                RoleRepresentation roleRepresentation = new RoleRepresentation();
                roleRepresentation.setName(roleName);
                keycloak.realm(realmName).roles().create(roleRepresentation);
                return true;
            } catch (Exception e){
                LOG.error("create role failed,for",e);
                return false;
            } finally {
                keycloak.close();
            }
        }

        /**
         * 创建user
         * @param userName
         * @param password
         * @param realm
         */
        public boolean createUser(String userName, String password, String realm) {
            Keycloak keycloak = Keycloak.getInstance(AUTH_SERVER_URL, REALM, USER_NAME, PASSWORD, CLIENT_ID);
            try {
                UserRepresentation user = new UserRepresentation();
                // userName唯一且不可修改
                user.setUsername(userName);
                user.setEnabled(true);
                Response createUserResponse = keycloak.realm(realm).users().create(user);
                createUserResponse.close();
                String userId = getCreatedId(createUserResponse);
                LOG.info("User created with id: "+ userId);
                // 设置密码
                CredentialRepresentation passwordCred = new CredentialRepresentation();
                passwordCred.setTemporary(false);
                passwordCred.setValue(password);
                passwordCred.setType(CredentialRepresentation.PASSWORD);
                keycloak.realm(realm).users().get(userId).resetPassword(passwordCred);
                // 设置角色
                // RoleRepresentation userRealmRole = keycloak.realm(realm).roles().get(roleName).toRepresentation();
                // keycloak.realm(realm).users().get(userId).roles().realmLevel().add(Arrays.asList(userRealmRole));
                return true;
            } catch (Exception e){
                LOG.error("create user failed,for",e);
                return false;
            } finally {
                keycloak.close();
            }
        }
        /**
         * 删除user
         * @param userName
         * @param realm
         * @return
         */
        public boolean removeUser(String userName, String realm) {
            Keycloak keycloak = Keycloak.getInstance(AUTH_SERVER_URL, REALM, USER_NAME, PASSWORD, CLIENT_ID);
            try {
                // 用户不能重名, 精确搜索
                List<UserRepresentation> userRepresentationList = keycloak.realm(realm).users().search(userName,true);
                if(userRepresentationList.size() > 0){
                    String id = userRepresentationList.get(0).getId();
                    keycloak.realm(realm).users().get(id).remove();
                    return true;
                } else {
                    LOG.error("No Such User !");
                    return false;
                }
            } catch (Exception e){
                LOG.error("remove user failed,for",e);
                return false;
            } finally {
                keycloak.close();
            }
        }

    /**
     * 创建user
     * @param userName
     * @param password
     * @param realm
     * @param roleName
     */
    public boolean createUserOnRoleName(String userName, String password, String realm,String roleName) {
        Keycloak keycloak = Keycloak.getInstance(AUTH_SERVER_URL, REALM, USER_NAME, PASSWORD, CLIENT_ID);
        try {
            UserRepresentation user = new UserRepresentation();
            // userName唯一且不可修改
            user.setUsername(userName);
            user.setEnabled(true);
            Response createUserResponse = keycloak.realm(realm).users().create(user);
            createUserResponse.close();
            String userId = getCreatedId(createUserResponse);
            LOG.info("User created with id: "+ userId);
            // 设置密码
            CredentialRepresentation passwordCred = new CredentialRepresentation();
            passwordCred.setTemporary(false);
            passwordCred.setValue(password);
            passwordCred.setType(CredentialRepresentation.PASSWORD);
            keycloak.realm(realm).users().get(userId).resetPassword(passwordCred);
            // 设置角色
            RoleRepresentation userRealmRole = keycloak.realm(realm).roles().get(roleName).toRepresentation();
            keycloak.realm(realm).users().get(userId).roles().realmLevel().add(Arrays.asList(userRealmRole));
            return true;
        } catch (Exception e){
            LOG.error("create user failed,for",e);
            return false;
        } finally {
            keycloak.close();
        }
    }
    /**
     * 创建user
     * @param userName
     * @param password
     * @param realm
     * @param roleName
     * @param emailAdress
     * @param firstName
     * @param lastName
     */
    public boolean createUserOnRoleName(String userName, String password, String realm,String roleName,
                                        String emailAdress,String firstName,String lastName) {
        Keycloak keycloak = Keycloak.getInstance(AUTH_SERVER_URL, REALM, USER_NAME, PASSWORD, CLIENT_ID);
        try {
            UserRepresentation user = new UserRepresentation();
            // userName唯一且不可修改
            //TODO 设置邮箱地址

            user.setUsername(userName);
            user.setEmail(emailAdress);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEnabled(true);
            Response createUserResponse = keycloak.realm(realm).users().create(user);
            createUserResponse.close();
            String userId = getCreatedId(createUserResponse);
            LOG.info("User created with id: "+ userId);
            // 设置密码
            CredentialRepresentation passwordCred = new CredentialRepresentation();
            passwordCred.setTemporary(false);
            passwordCred.setValue(password);
            passwordCred.setType(CredentialRepresentation.PASSWORD);
            keycloak.realm(realm).users().get(userId).resetPassword(passwordCred);
            // 设置角色
            RoleRepresentation userRealmRole = keycloak.realm(realm).roles().get(roleName).toRepresentation();
            keycloak.realm(realm).users().get(userId).roles().realmLevel().add(Arrays.asList(userRealmRole));

            return true;
        } catch (Exception e){
            LOG.error("create user failed,for",e);
            return false;
        } finally {
            keycloak.close();
        }
    }

        /**
         * 更新密码
         * @param userName
         * @param password
         * @param realm
         */
        public boolean updatePassword(String userName, String password, String realm) {
            Keycloak keycloak = Keycloak.getInstance(AUTH_SERVER_URL, REALM, USER_NAME, PASSWORD, CLIENT_ID);
            try {
                // 用户不能重名, 精确搜索
                List<UserRepresentation> userRepresentationList = keycloak.realm(realm).users().search(userName,true);
                if(userRepresentationList.size() > 0){
                    String id = userRepresentationList.get(0).getId();
                    // 设置密码
                    CredentialRepresentation passwordCred = new CredentialRepresentation();
                    passwordCred.setTemporary(false);
                    passwordCred.setValue(password);
                    passwordCred.setType(CredentialRepresentation.PASSWORD);
                    keycloak.realm(realm).users().get(id).resetPassword(passwordCred);
                    return true;
                } else {
                    LOG.error("No Such User !");
                    return false;
                }
            } catch (Exception e){
                LOG.error("update password failed,for",e);
                return false;
            } finally {
                keycloak.close();
            }
        }

        /**
         * 获取全部realms
         * @return
         */
        public List<String> getRealms() {
            Keycloak keycloak = Keycloak.getInstance(AUTH_SERVER_URL, REALM, USER_NAME, PASSWORD, CLIENT_ID);
            try {
                List<RealmRepresentation> realms = keycloak.realms().findAll();
                List<String> collect = realms.stream().map(RealmRepresentation::getRealm).collect(Collectors.toList());
                return collect;
            } catch (Exception e){
                LOG.error("get all realms failed,for",e);
                return null;
            } finally {
                keycloak.close();
            }
        }


        /**
         * keyclaok会把创建成功的id通过url返回
         * @param response
         * @return
         */
        public static String getCreatedId(Response response) {

            URI location = response.getLocation();

            if (!response.getStatusInfo().equals(Response.Status.CREATED)) {
                Response.StatusType statusInfo = response.getStatusInfo();
                throw new WebApplicationException("Create method returned status " +
                        statusInfo.getReasonPhrase() + " (Code: " + statusInfo.getStatusCode() + "); expected status: Created (201)", response);
            }

            if (location == null) {
                return null;
            }

            String path = location.getPath();
            return path.substring(path.lastIndexOf('/') + 1);
        }
    /**
     * 模拟登录，返回token
     * @param realm
     * @param userName
     * @param password
     * @param client
     * @param clientSecret
     * @return
     */
    public String userLogin(String realm, String userName, String password, String client, String clientSecret) {
        Keycloak keycloak = null;
        try {
            keycloak = Keycloak.getInstance(AUTH_SERVER_URL, realm, userName, password, client, clientSecret);
            String accessTokenString = keycloak.tokenManager().getAccessTokenString();
            return accessTokenString;
        } catch (Exception e){
            LOG.error("get all realms failed,for",e);
            //账号或密码不对 401 或 clientSecret 不对
            return null;
        } finally {
            if (!keycloak.isClosed()){
                keycloak.close();
            }
        }
    }


    }

