package com.zz.flight.service.impl;

import com.sun.org.apache.regexp.internal.RE;
import com.zz.flight.common.Const;
import com.zz.flight.common.ResponseCode;
import com.zz.flight.common.ServerResponse;
import com.zz.flight.entity.Request;
import com.zz.flight.entity.User;
import com.zz.flight.repository.RequestRepository;
import com.zz.flight.repository.UserRepository;
import com.zz.flight.service.FlightService;
import com.zz.flight.util.EmailUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Date;

@Service("FlightService")
public class FlightServiceImlp implements FlightService {

    @Autowired
    RequestRepository requestRepository;
    @Autowired
    UserRepository userRepository;

    //list all by status
    public ServerResponse<Page> listAllByStatus(int status, int pageIndex, int pageSize) {
        Sort sort =  new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(pageIndex,pageSize,sort);

        Page<Request> page  = requestRepository.findAllByStatus(status,pageable);

        if(page.getTotalElements()>0){
            return ServerResponse.creatBySuccess(page);
        }

        return ServerResponse.creatByErrorMessage("cant find");
    }

    //list all
    public ServerResponse<Page> listAll(int pageIndex, int pageSize){
        Sort sort =  new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(pageIndex,pageSize,sort);
        Page<Request> page  = requestRepository.findAllByStatusGreaterThanEqual(0,pageable);
        return ServerResponse.creatBySuccess(page);
    }

    //创建Request
    public ServerResponse<Request> addRequest(Request request,Long id,String username){
        if(request==null) return ServerResponse.creatByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        //检查id是否已经有request
        Request alreadyRequest = requestRepository.findByRequestUserIdAndStatus(id,Const.RequestStatus.REQUEST_NOT_TAKEN);
        if(alreadyRequest!=null) return ServerResponse.creatByErrorMessage("你已经提出了接机需求");
        alreadyRequest = requestRepository.findByRequestUserIdAndStatus(id,Const.RequestStatus.REQUEST_TAKEN);
        if(alreadyRequest!=null) return ServerResponse.creatByErrorMessage("你已经提出了接机需求");
        //设置 提出请求的人的name和id
        request.setRequestUserId(id);
        request.setRequestUserName(username);
        if(request.getAirport()==null||request.getFlightInfo()==null||request.getRequestUserId()==null||request.getTime()==null||request.getRequestUserName()==null){
            return ServerResponse.creatByErrorMessage("Please fill all information");
        }
        request.setStatus(Const.RequestStatus.REQUEST_NOT_TAKEN);
        //设置status 和 更新时间
        Date now = new Date();
        request.setUpdateTime(now);
        request.setCreateTime(now);
        //save
        requestRepository.save(request);
        return ServerResponse.creatBySuccess("success",request);
    }

    //接受request
    public ServerResponse takeRequest(Long requestId,Long userId,String username,String email){
        Request request = requestRepository.findById(requestId).orElse(null);
        if(request==null) return ServerResponse.creatByErrorMessage("cant find request");
        //设置status taken_id
        request.setStatus(Const.RequestStatus.REQUEST_TAKEN);
        request.setTakenUserId(userId);
        request.setTakenUserName(username);
        request.setUpdateTime(new Date());
        //save
        requestRepository.save(request);
        return ServerResponse.creatBySuccess("success");
    }

    //查找request 当前用户的
    public ServerResponse<Page> getRequestByUserId(Long userId,int pageIndex,int pageSize,Long curUserId){
        Sort sort =  new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(pageIndex,pageSize,sort);
        Page<Request> page = requestRepository.findAllByRequestUserIdAndStatusNotOrTakenUserIdAndStatusNot(userId,-1,userId,-1,pageable);
        //如果不是当前登录用户查看
//        if(!userId.equals(curUserId)){
//            for(int i=0;i<page.getTotalElements();i++){
//                page.getContent().get(i).set
//            }
//        }
        return ServerResponse.creatBySuccess(page);
    }

    //按requestId 查找request
    public ServerResponse<Request> getRequestById(Long id){
        Request request = requestRepository.findById(id).orElse(null);
        if(request==null) return ServerResponse.creatByErrorMessage("找不到该信息");
        return ServerResponse.creatBySuccess(request);
    }


//    //取消某次请求按照当前user 不可行
//    public ServerResponse cancelByCurUser(Long id,int role){
//        Request request = requestRepository.findByRequestUserId(id);
//        if(request==null) return ServerResponse.creatByErrorMessage("找不到该信息");
//        //如果是volunteer
//        if(role==Const.Role.ROLE_V0LUNTEER){
//            request.setTakenUserName(StringUtils.EMPTY);
//            request.setTakenUserId(null);
//            request.setStatus(Const.RequestStatus.REQUEST_NOT_TAKEN);
//        }
//        //如果是admin
//        if(role==Const.Role.ROLE_ADMIN){
//            request.setStatus(Const.RequestStatus.REQUEST_CANCELED);
//        }
//        request.setUpdateTime(new Date());
//        requestRepository.save(request);
//        return ServerResponse.creatBySuccessMessage("Success");
//    }

    //取消request按照request id
    public ServerResponse cancelById(Long id,int role,Long userId){
        Request request = requestRepository.findById(id).orElse(null);
        if(request==null) return ServerResponse.creatByErrorMessage("找不到该信息");
        if(role!=Const.Role.ROLE_ADMIN && !userId.equals(request.getRequestUserId()) && !userId.equals(request.getTakenUserId())) return ServerResponse.creatByErrorMessage("不能操作");
        //如果是admin
        if(role==Const.Role.ROLE_ADMIN || role==Const.Role.ROLE_CUSTOMER){
            request.setStatus(Const.RequestStatus.REQUEST_CANCELED);
        }
        if(role==Const.Role.ROLE_V0LUNTEER){
            request.setTakenUserName(StringUtils.EMPTY);
            request.setTakenUserId(null);
            request.setStatus(Const.RequestStatus.REQUEST_NOT_TAKEN);
        }
        request.setUpdateTime(new Date());
        requestRepository.save(request);
        return ServerResponse.creatBySuccessMessage("Success");
    }

}
