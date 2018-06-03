package com.zz.flight.controller;

import com.zz.flight.common.Const;
import com.zz.flight.common.ResponseCode;
import com.zz.flight.common.ServerResponse;
import com.zz.flight.entity.Request;
import com.zz.flight.entity.User;
import com.zz.flight.service.FlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.jws.soap.SOAPBinding;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/flight")
public class FlightController {

    @Autowired
    private FlightService flightService;

    //获取所有没有接受的请求列表
    @GetMapping
    @ResponseBody
    public ServerResponse<Page> getList( @RequestParam(value = "pageIndex",defaultValue ="0") int pageIndex,
                                            @RequestParam(value = "pageSize",defaultValue = "10") int pageSize){

        return flightService.listAllByStatus(0,pageIndex,pageSize);

    }

    //获取所有
    @GetMapping("/all")
    @ResponseBody
    public ServerResponse<Page> getAllList(@RequestParam(value = "pageIndex",defaultValue ="0") int pageIndex,
                                           @RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
        return flightService.listAll(pageIndex,pageSize);
    }


    //新建请求
    @PostMapping
    @ResponseBody
    public ServerResponse<Request> addRequest(Request request,HttpSession session){
        User curUser = (User) session.getAttribute(Const.CURRENT_USER);
        //检查是否登录
        if(curUser==null) return ServerResponse.creatByErrorCodeMessage(ResponseCode.NEEDLOG_IN.getCode(),ResponseCode.NEEDLOG_IN.getDesc());
        //检查邮箱是否认证
        if(curUser.getEmailChecked()==Const.EmailChecked.EMAIL_INVALID) return ServerResponse.creatByErrorMessage("Please validate your email first");
        if(curUser.getRole()!=Const.Role.ROLE_CUSTOMER) return ServerResponse.creatByErrorMessage("您不能提出请求");
        return flightService.addRequest(request,curUser.getId(),curUser.getUserName());
    }


    //接受请求
    @PatchMapping("/{id}")
    @ResponseBody
    public ServerResponse takeRequest(@PathVariable("id")Long id, HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.creatByErrorCodeMessage(ResponseCode.NEEDLOG_IN.getCode(),ResponseCode.NEEDLOG_IN.getDesc());
        }
        if(user.getEmailChecked()==Const.EmailChecked.EMAIL_INVALID) return ServerResponse.creatByErrorMessage("请先验证邮箱");
        if(user.getRole()==Const.Role.ROLE_CUSTOMER){
            return ServerResponse.creatByErrorMessage("不能接受此请求，您不是接机志愿者");
        }
        return flightService.takeRequest(id,user.getId(),user.getUserName(),user.getEmail());
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ServerResponse<Request> getRequestById(@PathVariable("id") Long id){
        return flightService.getRequestById(id);
    }

    //查找某个请求通过请求人 当前用户
    @GetMapping("/user")
    @ResponseBody
    public ServerResponse<Page> getRequestByUsername( HttpSession session,
                                              @RequestParam(value = "pageIndex",defaultValue ="0") int pageIndex,
                                              @RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null) return ServerResponse.creatByErrorCodeMessage(ResponseCode.NEEDLOG_IN.getCode(),ResponseCode.NEEDLOG_IN.getDesc());
        return flightService.getRequestByUserId(user.getId(),pageIndex,pageSize,user.getId());
    }

//    //取消某个请求 当前用户的
//    @DeleteMapping("/user")
//    @ResponseBody
//    public ServerResponse cancelByUserId(HttpSession session){
//        User user = (User) session.getAttribute(Const.CURRENT_USER);
//        if(user==null) return ServerResponse.creatByErrorCodeMessage(ResponseCode.NEEDLOG_IN.getCode(),ResponseCode.NEEDLOG_IN.getDesc());
//        return flightService.cancelByCurUser(user.getId(),user.getRole());
//    }

    //按照request id来删除
    @DeleteMapping("/{id}")
    @ResponseBody
    public ServerResponse cancelById(@PathVariable("id") Long id,HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null) return ServerResponse.creatByErrorCodeMessage(ResponseCode.NEEDLOG_IN.getCode(),ResponseCode.NEEDLOG_IN.getDesc());
        return flightService.cancelById(id,user.getRole(),user.getId());
    }




}
