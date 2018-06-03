package com.zz.flight.controller;

import com.sun.org.apache.regexp.internal.RE;
import com.zz.flight.common.Const;
import com.zz.flight.common.ResponseCode;
import com.zz.flight.common.ServerResponse;
import com.zz.flight.entity.User;
import com.zz.flight.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;



@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    //创建用户
    @PostMapping("/new")
    @ResponseBody
    public ServerResponse addUser(User user){
        return userService.addUser(user);
    }

    //注册管理员
    @PostMapping("/admin")
    @ResponseBody
    public ServerResponse addAdmin(String userName,String password,String email,String adminToken){
        if (!adminToken.equals(Const.ADMIN_TOKEN)){
            return ServerResponse.creatByErrorMessage("密码错误");
        }
        return userService.addAdmin(userName,email,password);
    }

    //注册接机人员
    @PostMapping("/volunteer")
    @ResponseBody
    public ServerResponse addVolunteer(User user){
        return userService.addVolunteer(user);
    }

    //检查用户名邮箱是否注册
    @PostMapping("/check_valid")
    @ResponseBody
    public ServerResponse checkValid(String str,String type){
        return userService.checkValid(str,type);
    }


    //login
    @PostMapping
    @ResponseBody
    public ServerResponse logIn(String username, String password, HttpSession session){
        ServerResponse<User> response = userService.login(username,password);
        if(response.isSuccess()){
            session.setAttribute(Const.CURRENT_USER,response.getData());
        }
        return response;
    }

    //检查登录状态并返回user
    @GetMapping("/check_login")
    @ResponseBody
    public ServerResponse<User> checkLogin(HttpSession session){
        User curUser = (User) session.getAttribute(Const.CURRENT_USER);
        if(curUser==null) return ServerResponse.creatByErrorMessage("用户没有登录");
        return ServerResponse.creatBySuccess(curUser);
    }

    //用户退出
    @GetMapping("/logout")
    @ResponseBody
    public ServerResponse<String> logout(HttpSession session){
        session.removeAttribute(Const.CURRENT_USER);
        return ServerResponse.creatBySuccessMessage("Log out success");
    }

    //用户修改
    @PatchMapping
    @ResponseBody
    public ServerResponse<User> updateUser(User user,HttpSession session){
        //check if log in
        User currentUser =(User) session.getAttribute(Const.CURRENT_USER);
        if(currentUser == null)return ServerResponse.creatByErrorMessage("User doesn't login");
        user.setId(currentUser.getId());
        ServerResponse response = userService.updateUser(user);
        if(response.isSuccess()) session.setAttribute(Const.CURRENT_USER,response.getData());
        return response;
    }

    //获取某个账户的信息
    @GetMapping("/{id}")
    @ResponseBody
    public ServerResponse<User> getInfo(@PathVariable("id") Long id,HttpSession session){
        if(id==null) return ServerResponse.creatByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        User curUser = (User) session.getAttribute(Const.CURRENT_USER);
        if(curUser==null) return ServerResponse.creatByErrorCodeMessage(ResponseCode.NEEDLOG_IN.getCode(),ResponseCode.NEEDLOG_IN.getDesc());
        return userService.getInfo(id,curUser);
    }

    @GetMapping
    @ResponseBody
    public ServerResponse<User> getCurUser(HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null) return ServerResponse.creatByErrorCodeMessage(ResponseCode.NEEDLOG_IN.getCode(), ResponseCode.NEEDLOG_IN.getDesc());
        //置空密码
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.creatBySuccess(user);
    }

    //send email 激活
    @GetMapping("/email/{username}")
    @ResponseBody
    public ServerResponse sendEmail(@PathVariable("username") String username){
        return userService.getValidateEmail(username);
    }

    //激活地址
    @PatchMapping("/email/{username}/{token}")
    @ResponseBody
    public ServerResponse<String> validateEmail(@PathVariable("username")String username,@PathVariable("token")String token){
        return userService.validateEmail(username,token);
    }

    //获取重置密码邮件
    @GetMapping("/password/{username}")
    @ResponseBody
    public ServerResponse getEmailToChangePass(@PathVariable("username") String username){
        return userService.getEmailToChangePass(username);
    }

    //重置密码
    @PatchMapping("/password/email/{username}")
    @ResponseBody
    public ServerResponse changePass(@PathVariable("username")String username,String token,String newPass){
        return userService.changePass(username,token,newPass);
    }

    //登录状态重置密码
    @PatchMapping("/password/{id}")
    @ResponseBody
    public ServerResponse resetPass(@PathVariable("id")Long id,String newPass,HttpSession session){
        User curUser = (User) session.getAttribute(Const.CURRENT_USER);
        if(curUser.getId().equals(id)) return ServerResponse.creatByErrorCodeMessage(ResponseCode.NEEDLOG_IN.getCode(),ResponseCode.NEEDLOG_IN.getDesc());
        return userService.resetPass(id,newPass);
    }

}
