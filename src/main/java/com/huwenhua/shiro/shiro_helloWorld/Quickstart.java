/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.huwenhua.shiro.shiro_helloWorld;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Simple Quickstart application showing how to use Shiro's API.
 *
 * @since 0.9 RC2
 */
public class Quickstart {

    private static final transient Logger log = LoggerFactory.getLogger(Quickstart.class);


    public static void main(String[] args) {

        // The easiest way to create a Shiro SecurityManager with configured
        // realms, users, roles and permissions is to use the simple INI config.
        // We'll do that by using a factory that can ingest a .ini file and
        // return a SecurityManager instance:

        // Use the shiro.ini file at the root of the classpath
        // (file: and url: prefixes load from files and urls respectively):
    	
    	// 读取shiro.ini配置文件并封装在SecurityManager并得到SecurityManager实例
        Factory<SecurityManager> factory = new IniSecurityManagerFactory("classpath:shiro.ini");
        SecurityManager securityManager = factory.getInstance();

        // for this simple example quickstart, make the SecurityManager
        // accessible as a JVM singleton.  Most applications wouldn't do this
        // and instead rely on their container configuration or web.xml for
        // webapps.  That is outside the scope of this simple quickstart, so
        // we'll just do the bare minimum so you can continue to get a feel
        // for things.
        
        //设置securityManager实例
        SecurityUtils.setSecurityManager(securityManager);

        // Now that a simple Shiro environment is set up, let's see what you can do:

        // get the currently executing user:
        
        // Subject是用于与项目程序打交道的
        // 获取当前的Subject。通过调用SecurityUtils.getSubject()即可获取到Subject对象
        Subject currentUser = SecurityUtils.getSubject();

        // Do some stuff with a Session (no need for a web or EJB container!!!)
        //测试使用Session
        //获取Session 则调用Subject对象的getSession()即可
        //在session中存放一个aValue值
        Session session = currentUser.getSession();
        session.setAttribute("someKey", "aValue");
        String value = (String) session.getAttribute("someKey");
        if (value.equals("aValue")) {
            log.info("❤❤-----------❤❤Retrieved the correct value! [" + value + "]");
        }

        // let's login the current user so we can check against roles and permissions:
        
        //Subject对象的isAuthenticated()方法用于测试用户是否已经被认证，就是是否已经登陆过
        //调用Subject的isAuthenticated()方法
        //感叹号取反：表示用户若没有登陆过没有被认证过
        if (!currentUser.isAuthenticated()) {
            //把用户名和密码封装到UsernamePasswordToken对象中
        	UsernamePasswordToken token = new UsernamePasswordToken("lonestarr", "vespa");
            //并设为记住我
        	token.setRememberMe(true);
            try {
            	//执行登陆
                currentUser.login(token);
                //UnknownAccountException：未知账户异常，没有指定用户抛出
                //IncorrectCredentialsException：错误凭证，密码错误时抛出
                //LockedAccountException：用户被锁定异常
            } catch (UnknownAccountException uae) {
                log.info("There is no user with username of " + token.getPrincipal());
            } catch (IncorrectCredentialsException ice) {
                log.info("Password for account " + token.getPrincipal() + " was incorrect!");
            } catch (LockedAccountException lae) {
                log.info("The account for username " + token.getPrincipal() + " is locked.  " +
                        "Please contact your administrator to unlock it.");
            }
            // ... catch more exceptions here (maybe custom ones specific to your application?
            //AuthenticationException：所有认证的异常父类，以上所解释的三种异常是该异常的子类
            catch (AuthenticationException ae) {
                //unexpected condition?  error?
            }
        }

        //say who they are:
        //print their identifying principal (in this case, a username):
        //通过Subject的getPrincipal()方法获取登陆认证的用户名
        log.info("❤❤-----------❤❤User [" + currentUser.getPrincipal() + "] logged in successfully.");

        //test a role:
        //测试是否有某一个角色
        if (currentUser.hasRole("schwartz")) {
            log.info("❤❤-----------❤❤May the Schwartz be with you!");
        } else {
            log.info("Hello, mere mortal.");
        }

        //test a typed permission (not instance-level)
        //测试用户是否具有某一个行为。调用Subject对象的isPermitted()方法
        if (currentUser.isPermitted("lightsaber:weild")) {
            log.info("❤❤-----------❤❤You may use a lightsaber ring.  Use it wisely.");
        } else {
            log.info("Sorry, lightsaber rings are for schwartz masters only.");
        }

        //a (very powerful) Instance Level permission:
        //测试用户是否具备某一个行为（更具体化的行为验证）
        if (currentUser.isPermitted("winnebago:drive:eagle5")) {
            log.info("❤❤-----------❤❤You are permitted to 'drive' the winnebago with license plate (id) 'eagle5'.  " +
                    "Here are the keys - have fun!");
        } else {
            log.info("Sorry, you aren't allowed to drive the 'eagle5' winnebago!");
        }

        //all done - log out!
        // 注销前查看用户是否登陆过(true)
        System.out.println("❤❤-----------❤❤"+currentUser.isAuthenticated());
        // 注销
        currentUser.logout();
        // 注销后查看用户是否登陆过(false)
        System.out.println("❤❤-----------❤❤"+currentUser.isAuthenticated());
        System.exit(0);
    }
}
