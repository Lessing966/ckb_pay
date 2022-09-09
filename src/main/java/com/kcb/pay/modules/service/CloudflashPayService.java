package com.kcb.pay.modules.service;

import com.kcb.pay.modules.dto.QueryTopupDTO;
import com.kcb.pay.modules.entity.PayRecordEntity;
import com.kcb.pay.modules.entity.PersonEntity;

import java.util.List;

public interface CloudflashPayService {
    PersonEntity selectPerson(String name, String account);

    PersonEntity selectPersonMoney(String account);

    String insertPayRecord(PersonEntity personEntity);

    PayRecordEntity queryTupup(QueryTopupDTO queryTopupDTO);

}
