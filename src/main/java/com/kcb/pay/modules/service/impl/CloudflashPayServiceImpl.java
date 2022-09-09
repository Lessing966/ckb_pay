package com.kcb.pay.modules.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kcb.pay.common.DateUtils;
import com.kcb.pay.modules.dao.PayRecordDao;
import com.kcb.pay.modules.dao.PersonDao;
import com.kcb.pay.modules.dao.TopupRecordDao;
import com.kcb.pay.modules.dto.QueryTopupDTO;
import com.kcb.pay.modules.entity.PayRecordEntity;
import com.kcb.pay.modules.entity.PersonEntity;
import com.kcb.pay.modules.entity.TopupRecordEntity;
import com.kcb.pay.modules.service.CloudflashPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;

@Service
public class CloudflashPayServiceImpl implements CloudflashPayService {

    @Autowired
    PersonDao personDao;
    @Autowired
    PayRecordDao payRecordDao;
    @Autowired
    TopupRecordDao topupRecordDao;

    @Override
    public PersonEntity selectPerson(String name, String account) {
       return personDao.selectOne(new QueryWrapper<PersonEntity>().eq("name",name).eq("phone",account).eq("status",0));
    }

    @Override
    public PersonEntity selectPersonMoney(String account) {
        return personDao.selectOne(new QueryWrapper<PersonEntity>().eq("phone",account).eq("status",0));
    }

    @Transactional
    @Override
    public String insertPayRecord(PersonEntity personEntity) {
        PayRecordEntity pay =new PayRecordEntity();
        personDao.updateById(personEntity);
        //phone记录
        pay.setPId(personEntity.getId());
        pay.setAccount(personEntity.getPhone());
        pay.setApplyAmount(personEntity.getApplyAmount());
        pay.setApplyId(personEntity.getApplyId());
        pay.setCreateTime(DateUtils.getStringDate());
        pay.setUpdateTime(DateUtils.getStringDate());
        pay.setOutId(String.valueOf(System.currentTimeMillis()));
        payRecordDao.insert(pay);
        //web充值记录
        TopupRecordEntity topupRecord =new TopupRecordEntity();
        topupRecord.setCard(personEntity.getCard());
        topupRecord.setDId(personEntity.getDId());
        topupRecord.setUId(personEntity.getUId());
        topupRecord.setPId(personEntity.getId());
        topupRecord.setName(personEntity.getName());
        topupRecord.setTime(DateUtils.dateToStrLong(new Date()));
        topupRecord.setExpense(personEntity.getExpense());
        topupRecord.setFname("云闪付APP");
        topupRecord.setType("银联充值");
        topupRecord.setUrl(personEntity.getUrl());
        topupRecord.setSum(personEntity.getApplyAmount());
        topupRecordDao.insert(topupRecord);
        return pay.getOutId();
    }

    @Override
    public PayRecordEntity queryTupup(QueryTopupDTO queryTopupDTO) {
        return payRecordDao.selectOne(new QueryWrapper<PayRecordEntity>().eq("apply_id",queryTopupDTO.getOutId()));
    }


}
