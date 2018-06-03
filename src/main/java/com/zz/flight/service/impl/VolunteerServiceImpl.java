package com.zz.flight.service.impl;

import com.zz.flight.common.Const;
import com.zz.flight.common.ServerResponse;
import com.zz.flight.entity.User;
import com.zz.flight.entity.Volunteer;
import com.zz.flight.repository.UserRepository;
import com.zz.flight.repository.VolunteerRepository;
import com.zz.flight.service.VolunteerService;
import com.zz.flight.util.EmailUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service("VolunteerService")
public class VolunteerServiceImpl implements VolunteerService {
    @Autowired
    VolunteerRepository volunteerRepository;
    @Autowired
    UserRepository userRepository;
    //List 没处理的volunteer请求
    public ServerResponse<Page> listUnchecked(int status,int pageIndex, int pageSize){
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(pageIndex,pageSize,sort);
        Page<Volunteer> page = volunteerRepository.findAllByStatus(Const.VolunteerStatus.VOLUNTEER_UNCHECKED,pageable);
        if(page.getTotalElements()== 0 )return ServerResponse.creatByErrorMessage("No Request");
        return ServerResponse.creatBySuccess(page);
    }

    public ServerResponse<Page> listALL(int pageIndex,int pageSize){
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(pageIndex,pageSize);
        Page<Volunteer> page = volunteerRepository.findAll(pageable);
        if(page.getTotalElements()==0) return ServerResponse.creatByErrorMessage("None");
        return ServerResponse.creatBySuccess(page);
    }
    //通过volunteer的id permit
    public ServerResponse permit(Long id){
        Volunteer volunteer = volunteerRepository.findById(id).orElse(null);
        if(volunteer==null) return ServerResponse.creatByErrorMessage("cant find the ueser");
        //改成checked
        volunteer.setStatus(Const.VolunteerStatus.VOLUNTEER_CHECKED);
        volunteer.setUpdateTime(new Date());
        volunteerRepository.save(volunteer);
        //修改user role
        Long userId = volunteer.getApplyUserId();
        User user = userRepository.findById(userId).orElse(null);
        if(user==null) return ServerResponse.creatByErrorMessage("cant find the ueser");
        user.setRole(Const.Role.ROLE_V0LUNTEER);
        user.setUpdateTime(new Date());
        userRepository.save(user);
        String to = user.getEmail();
        String from = "zhuo9529@gmail.com";
        String title = "验证通过";
        String content = "<p>通过验证</p>";
        EmailUtil.sendEmail(from,to,content,title);
        return ServerResponse.creatBySuccessMessage("success");
    }


}
