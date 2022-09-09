package com.kcb.pay.modules.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kcb.pay.modules.entity.PersonEntity;
import org.mapstruct.Mapper;

@Mapper
public interface PersonDao extends BaseMapper<PersonEntity> {

}
