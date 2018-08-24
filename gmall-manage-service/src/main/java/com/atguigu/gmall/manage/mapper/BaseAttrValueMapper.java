package com.atguigu.gmall.manage.mapper;

import com.atguigu.bean.BaseAttrInfo;
import com.atguigu.bean.BaseAttrValue;
import org.apache.ibatis.annotations.Param;
import org.junit.runners.Parameterized;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Set;

public interface BaseAttrValueMapper extends Mapper<BaseAttrValue> {


    List<BaseAttrInfo> selectAttrListByValueIds(@Param("ids") String join);
}
