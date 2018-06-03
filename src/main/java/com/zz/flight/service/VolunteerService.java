package com.zz.flight.service;

import com.zz.flight.common.ServerResponse;
import org.springframework.data.domain.Page;

/**
 * 给管理员同意请求用
 */
public interface VolunteerService {
    ServerResponse<Page> listUnchecked(int status, int pageIndex, int pageSize);

    ServerResponse<Page> listALL(int pageIndex,int pageSize);

    ServerResponse permit(Long id);
}
