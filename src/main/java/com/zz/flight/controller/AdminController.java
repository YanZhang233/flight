package com.zz.flight.controller;


import com.zz.flight.common.Const;
import com.zz.flight.common.ResponseCode;
import com.zz.flight.common.ServerResponse;
import com.zz.flight.entity.User;
import com.zz.flight.service.VolunteerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    VolunteerService volunteerService;
    @GetMapping
    @ResponseBody
    public ServerResponse<Page> getUnchecked(HttpSession session,@RequestParam(value = "pageIndex",defaultValue ="0") int pageIndex,
                                             @RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
        User user =(User) session.getAttribute(Const.CURRENT_USER);
        if(user == null) return ServerResponse.creatByErrorCodeMessage(ResponseCode.NEEDLOG_IN.getCode(),ResponseCode.NEEDLOG_IN.getDesc());
        if(user.getRole()!=Const.Role.ROLE_ADMIN) return ServerResponse.creatByErrorMessage("You are not an admin");
        return volunteerService.listUnchecked(Const.VolunteerStatus.VOLUNTEER_UNCHECKED,pageIndex,pageSize);
    }

    @GetMapping("/all")
    @ResponseBody
    public ServerResponse<Page> getAll(HttpSession session,@RequestParam(value = "pageIndex",defaultValue ="0") int pageIndex,
                                       @RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
        User user =(User) session.getAttribute(Const.CURRENT_USER);
        if(user == null) return ServerResponse.creatByErrorCodeMessage(ResponseCode.NEEDLOG_IN.getCode(),ResponseCode.NEEDLOG_IN.getDesc());
        if(user.getRole()!=Const.Role.ROLE_ADMIN) return ServerResponse.creatByErrorCodeMessage(ResponseCode.NEEDLOG_IN.getCode(),ResponseCode.NEEDLOG_IN.getDesc());
        return volunteerService.listALL(pageIndex,pageSize);
    }

    //通过请求
    @PatchMapping("/{id}")
    @ResponseBody
    public ServerResponse permit(HttpSession session,@PathVariable("id") Long id){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null) return ServerResponse.creatByErrorCodeMessage(ResponseCode.NEEDLOG_IN.getCode(),ResponseCode.NEEDLOG_IN.getDesc());
        if(user.getRole()!=Const.Role.ROLE_ADMIN) return ServerResponse.creatBySuccessMessage("You are not an admin");
        return volunteerService.permit(id);
    }
}
