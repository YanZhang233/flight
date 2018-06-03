package com.zz.flight.service;

import com.zz.flight.common.ServerResponse;
import com.zz.flight.entity.Request;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FlightService {

    ServerResponse<Page> listAllByStatus(int status,int pageIndex,int pageSize);

    ServerResponse<Request> addRequest(Request request,Long id,String username);

    ServerResponse takeRequest(Long requestId,Long userId,String username,String email);

    ServerResponse<Page> getRequestByUserId(Long userId,int pageIndex,int pageSize,Long curUserId);

    ServerResponse<Request> getRequestById(Long id);

    ServerResponse<Page> listAll(int pageIndex, int pageSize);

    //ServerResponse cancelByCurUser(Long id,int role);

    ServerResponse cancelById(Long id,int role,Long userId);
}
