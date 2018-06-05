package com.zz.flight.service.impl;

import com.zz.flight.common.Const;
import com.zz.flight.common.ResponseCode;
import com.zz.flight.common.ServerResponse;
import com.zz.flight.common.TokenCache;
import com.zz.flight.entity.Request;
import com.zz.flight.entity.User;
import com.zz.flight.entity.Volunteer;
import com.zz.flight.repository.RequestRepository;
import com.zz.flight.repository.UserRepository;
import com.zz.flight.repository.VolunteerRepository;
import com.zz.flight.service.UserService;
import com.zz.flight.util.EmailUtil;
import com.zz.flight.util.MD5Util;
import com.zz.flight.util.UpdateUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.UUID;

@Service("UserService")
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RequestRepository requestRepository;
    @Autowired
    private VolunteerRepository volunteerRepository;

    //创建用户
    public ServerResponse addUser(User user){
        //检查用户名是否用过
        ServerResponse validResponse = this.checkValid(user.getUserName(),Const.USER_NAME);
        if(!validResponse.isSuccess()){
            return validResponse;
        }
        //检查邮箱是否用过
        validResponse = this.checkValid(user.getEmail(),Const.EMAIL);
        if(!validResponse.isSuccess()){
            return validResponse;
        }
        //检查手机
        validResponse = this.checkValid(user.getPhone(),Const.PHONE);
        if(!validResponse.isSuccess()){
            return validResponse;
        }
        user.setRole(Const.Role.ROLE_CUSTOMER);
        //MD5加密
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        //status设置
        user.setStatus(Const.Status.USER_VALID);
        //设置创建时间
        Date now = new Date();
        user.setCreateTime(now);
        user.setUpdateTime(now);
        //set email checked
        user.setEmailChecked(Const.EmailChecked.EMAIL_INVALID);
        userRepository.save(user);
        return ServerResponse.creatBySuccessMessage("Sign up success");
    }

    //创建管理员
    public ServerResponse addAdmin(String username,String email,String password){
        ServerResponse validResponse = this.checkValid(username,Const.USER_NAME);
        if(!validResponse.isSuccess()){
            return validResponse;
        }
        //检查邮箱是否用过
        validResponse = this.checkValid(email,Const.EMAIL);
        if(!validResponse.isSuccess()){
            return validResponse;
        }
        User user = new User();
        user.setUserName(username);
        user.setEmailChecked(Const.EmailChecked.EMAIL_VALID);
        user.setEmail(email);
        user.setPassword(MD5Util.MD5EncodeUtf8(password));
        user.setRole(Const.Role.ROLE_ADMIN);
        user.setStatus(Const.Status.USER_VALID);
        Date date = new Date();
        user.setCreateTime(date);
        user.setUpdateTime(date);
        userRepository.save(user);
        return ServerResponse.creatBySuccess("success",user);
    }

    //创建volunteer
    //先设置为普通用户，同时放进volunteer表 等待管理员通过
    public ServerResponse addVolunteer(User user){
        ServerResponse response = addUser(user);
        if(!response.isSuccess()) return response;
        //新建volunteer
        Volunteer volunteer = new Volunteer();
        User savedUser = userRepository.findByUserName(user.getUserName());
        if(savedUser == null) return ServerResponse.creatByErrorMessage("cant find the user");
        volunteer.setApplyUserId(savedUser.getId());
        volunteer.setApplyUserName(user.getUserName());
        volunteer.setStatus(Const.VolunteerStatus.VOLUNTEER_UNCHECKED);
        volunteer.setEmailValidate(savedUser.getEmailChecked());
        Date now = new Date();
        volunteer.setCreateTime(now);
        volunteer.setUpdateTime(now);
        //save
        volunteerRepository.save(volunteer);
        return ServerResponse.creatBySuccessMessage("等待管理员确认");
    }

    //检查是否注册
    public ServerResponse<String> checkValid(String str, String type){
        if(str == null) return ServerResponse.creatByErrorMessage("Wrong argument");
        if(StringUtils.isNotBlank(type)){
            //check
            if(Const.USER_NAME.equals(type)){
               if(userRepository.findByUserName(str)!=null){
                   return ServerResponse.creatByErrorMessage("Username has been used");
               }
            }
            if(Const.EMAIL.equals(type)){
               if(userRepository.findByEmail(str)!=null){
                   return ServerResponse.creatByErrorMessage("Email has been used");
               }
            }
            if(Const.PHONE.equals(type)){
                if(userRepository.findByPhone(str)!=null){
                    return ServerResponse.creatByErrorMessage("Phone num has been used");
                }
            }
        }else {
            return ServerResponse.creatByErrorMessage("Wrong argument");
        }
        return ServerResponse.creatBySuccessMessage("Check success");
    }

    //Log in
    public ServerResponse<User> login(String username,String password){
        User user = userRepository.findByUserName(username);
        //检查用户名是否存在
        if(user == null) return ServerResponse.creatByErrorMessage("Username doesn't exist");
        //得到加密后的密码
        String md5Password = MD5Util.MD5EncodeUtf8(password);
        //检查密码是否正确
        if(!StringUtils.equals(md5Password,user.getPassword())){
            return ServerResponse.creatByErrorMessage("Password wrong");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.creatBySuccess("Login success",user);
    }


    //用户修改
    public ServerResponse<User> updateUser(User user){
        if(user==null) return ServerResponse.creatByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        if(userRepository.findByEmail(user.getEmail())!= null) return ServerResponse.creatByErrorMessage("Email has been used");
        User pre = userRepository.findById(user.getId()).orElse(null);
        //新建一个User 只能更新规定的内容
        User newUser = new User();
        newUser.setActualName(user.getActualName());
        newUser.setGender(user.getGender());
        newUser.setGraduatedFrom(user.getGraduatedFrom());
        newUser.setHomeTown(user.getHomeTown());
        newUser.setMajor(user.getMajor());
        //获取当前时间
        newUser.setUpdateTime(new Date());
        //更新工具 不更新为null的
        UpdateUtil.copyNullProperties(pre,newUser);
        User savedUser = userRepository.save(newUser);
        savedUser.setPassword(StringUtils.EMPTY);
        savedUser.setRole(null);
        return ServerResponse.creatBySuccess(savedUser);
    }

    //查询用户信息
    public ServerResponse<User> getInfo(Long id, User curUser){
        //找到请求的用户 find
        User find = userRepository.findById(id).orElse(null);
        if(find==null) return ServerResponse.creatByErrorMessage("Cant find user");
       //检查这个人是不是自己的接机人
        Request request = requestRepository.findByRequestUserIdAndStatus(curUser.getId(),Const.RequestStatus.REQUEST_TAKEN);
        boolean isTakenUser;
        if(request == null) isTakenUser = false;
        else {
           isTakenUser  = (request.getTakenUserId()!=null)&& request.getTakenUserId().equals(id);
        }
        //不是自己或者管理员或者是你的接机人员,则不能看到手机号
        if(!curUser.getId().equals(id) && !isTakenUser && curUser.getRole()!=Const.Role.ROLE_ADMIN){
            //Phone置空
            find.setPhone(StringUtils.EMPTY);
        }
        //密码置空
        find.setPassword(StringUtils.EMPTY);
        //角色置空
        find.setRole(null);
        return ServerResponse.creatBySuccess(find);
    }


    //获取邮件
    public ServerResponse<String> getValidateEmail(String username){
        User user = userRepository.findByUserName(username);
        if(user==null) return ServerResponse.creatByErrorMessage("没有该用户");
        if(user.getEmailChecked()==Const.EmailChecked.EMAIL_VALID) return ServerResponse.creatByErrorMessage("已经激活");
        //随机产生string
        String token = UUID.randomUUID().toString();
        //token 放入cache
        TokenCache.setKey(TokenCache.TOKEN_PREFIX+user.getUserName(),token);
        //发邮件
        String to = user.getEmail();
        String from = "m18667015308@163.com";
        String content = "http://flight.zhuo9529.com/validateEmail.html?username="+user.getUserName()+"&token="+token;
        String title = "Validate your email";
        if(!EmailUtil.sendEmail(from,to,content,title)){
            return ServerResponse.creatByErrorMessage("send email error");
        }
        return ServerResponse.creatBySuccessMessage("Please check your email");
    }

    //激活邮件
    public ServerResponse<String> validateEmail(String username, String token){
        String getToken = TokenCache.getKey(TokenCache.TOKEN_PREFIX+username);
        if(!StringUtils.equals(token,getToken)){
            return ServerResponse.creatByErrorMessage("Token is not right");
        }
        User user = userRepository.findByUserName(username);
        user.setEmailChecked(Const.EmailChecked.EMAIL_VALID);
        user.setUpdateTime(new Date());
        userRepository.save(user);
        return ServerResponse.creatBySuccessMessage("validate success");
    }

    //获取邮件改密码
    public ServerResponse<String> getEmailToChangePass(String username){
        User user = userRepository.findByUserName(username);
        if(user==null) return ServerResponse.creatByErrorMessage("cant find user with this username");
        String to = user.getEmail();
        String from = "flightjoe@gmail.com";
        String title = "Reset your password";
        String token = UUID.randomUUID().toString();
        TokenCache.setKey(TokenCache.TOKEN_PREFIX+username,token);
        String content = "http://flight.zhuo9529.com/changePwd.html?"+"token="+token+"&"+"username="+username;
        if(!EmailUtil.sendEmail(from,to,content,title)){
            return ServerResponse.creatByErrorMessage("send email error");
        }
        return ServerResponse.creatBySuccessMessage("Please check your email");
    }

    //用邮件改密码
    public ServerResponse changePass(String username,String token,String newPass){
        String tokenToCheck = TokenCache.getKey(TokenCache.TOKEN_PREFIX+username);
        if(!StringUtils.equals(token,tokenToCheck)){
            return ServerResponse.creatByErrorMessage("Token is not right");
        }
        User user = userRepository.findByUserName(username);
        if(user==null) return ServerResponse.creatBySuccessMessage("cant find the user");
        //存加密过的
        user.setPassword(MD5Util.MD5EncodeUtf8(newPass));
        user.setUpdateTime(new Date());
        userRepository.save(user);
        return ServerResponse.creatBySuccessMessage("Reset success");
    }

    //登录后改密码
    public ServerResponse resetPass(Long id,String newPass){
        User user = userRepository.findById(id).orElse(null);
        if(user==null) return ServerResponse.creatByErrorMessage("cant find the user");
        String MD5NewPass = MD5Util.MD5EncodeUtf8(newPass);
        user.setPassword(MD5NewPass);
        user.setUpdateTime(new Date());
        userRepository.save(user);
        return ServerResponse.creatBySuccessMessage("Reset success");
    }

    //列出所有用户
    public ServerResponse<Page> listAllUsers(int pageIndex,int pageSize) {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(pageIndex, pageSize, sort);
        Page<User> page = userRepository.findAll(pageable);
        return ServerResponse.creatBySuccess(page);
    }

    //列入黑名单
    public ServerResponse deleteUser(Long id){
        User user = userRepository.findById(id).orElse(null);
        if(user==null) return ServerResponse.creatByErrorMessage("找不到该用户");
        if(user.getRole()==Const.Role.ROLE_ADMIN) return ServerResponse.creatByErrorMessage("没有权限");
        user.setStatus(Const.Status.USER_INVALID);
        user.setUpdateTime(new Date());
        userRepository.save(user);
        return ServerResponse.creatBySuccess("操作成功");
    }

    public ServerResponse validateUser(Long id){
        User user = userRepository.findById(id).orElse(null);
        if (user==null) return ServerResponse.creatByErrorMessage("找不到该用户");
        user.setStatus(Const.Status.USER_VALID);
        user.setUpdateTime(new Date());
        userRepository.save(user);
        return ServerResponse.creatBySuccess("操作成功");
    }
}
