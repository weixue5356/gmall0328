package com.atguigu.gmall.cart.service.impl;

import ch.qos.logback.core.joran.conditional.ElseAction;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.atguigu.bean.CartInfo;
import com.atguigu.gmall.cart.mapper.CartInfoMapper;
import com.atguigu.gmall.utils.RedisUtil;
import com.atguigu.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartInfoMapper cartInfoMapper;


    @Autowired
    private RedisUtil redisUtil;




    //判断cartInfo是否存在
    @Override
    public CartInfo ifCartExist(CartInfo cartInfo) {

        CartInfo cartInfo1 = new CartInfo();
        cartInfo1.setUserId(cartInfo.getUserId());
        cartInfo1.setSkuId(cartInfo.getSkuId());

        CartInfo select = cartInfoMapper.selectOne(cartInfo1);

        return select;
    }


    //同步缓存
    @Override
    public void syncCache(String userId) {

        Jedis jedis = redisUtil.getJedis();
        CartInfo cartInfo = new CartInfo();
        cartInfo.setUserId(userId);
        List<CartInfo> cartInfos = cartInfoMapper.select(cartInfo);

          if (cartInfos == null || cartInfos.size() == 0) {
              jedis.del("carts:" + userId + ":info");
        } else {
              Map<String, String> stringStringHashMap = new HashMap<>();

              for (CartInfo info : cartInfos) {
                  stringStringHashMap.put(info.getId(), JSON.toJSONString(info));
              }
              jedis.hmset("carts:" + userId + ":info", stringStringHashMap);
              jedis.close();
          }
    }


    //保存cartInfo
    @Override
    public void saveCart(CartInfo cartInfo) {
        cartInfoMapper.insertSelective(cartInfo);
    }



    //更新cartInfo
    @Override
    public void updateCart(CartInfo cartInfoDb) {
        cartInfoMapper.updateByPrimaryKeySelective(cartInfoDb);
    }


    //从缓存中取数据
    @Override
    public List<CartInfo> getCartCache(String userId) {
        List<CartInfo> cartInfos = new ArrayList<>();
        Jedis jedis = redisUtil.getJedis();
        List<String> hvals = jedis.hvals("carts:" + userId + ":info");
          if(hvals != null && hvals.size() > 0) {
              for (String hval : hvals) {
                  CartInfo cartInfo = JSON.parseObject(hval, CartInfo.class);
                  cartInfos.add(cartInfo);
              }
          }

        return cartInfos;
    }

    @Override
    public void updateCartCheked(CartInfo cartInfo) {

        Example example = new Example(CartInfo.class);//update set a = ? b = ? where id = ? name = ?
        example.createCriteria().andEqualTo("userId",cartInfo.getUserId()).andEqualTo("skuId",cartInfo.getSkuId());

        cartInfoMapper.updateByExampleSelective(cartInfo,example);
       //同步缓存
        syncCache(cartInfo.getUserId());
    }


    //合并购物车
    @Override
    public void combineCart(List<CartInfo> cartInfoList, String useId) {
        //合并购物车

        if (cartInfoList != null) {
            for (CartInfo cartInfo : cartInfoList) {
                CartInfo info = ifCartExist(cartInfo);

                if (info == null) {

                    //插入
                    cartInfo.setUserId(useId);
                    cartInfoMapper.insertSelective(cartInfo);
                } else {
                    //更新
                    info.setSkuNum(cartInfo.getSkuNum() + info.getSkuNum());

                    info.setCartPrice(info.getSkuPrice().multiply(new BigDecimal(info.getSkuNum())));

                    cartInfoMapper.updateByPrimaryKeySelective(info);

                }

            }

        }
        //同步缓存
        syncCache(useId);


    }

    @Override
    public List<CartInfo> getCartCacheByCheckd(String userId) {
        List<CartInfo> cartInfos = new ArrayList<>();
        Jedis jedis = redisUtil.getJedis();
        List<String> hvals = jedis.hvals("carts:" + userId + ":info");
        if(hvals != null && hvals.size() > 0) {
            for (String hval : hvals) {
                CartInfo cartInfo = JSON.parseObject(hval, CartInfo.class);
                if (cartInfo.getIsChecked().equals("1")) {
                    cartInfos.add(cartInfo);
                }
            }
        }

        return cartInfos;
    }

    @Override
    public void deleteCartById(List<CartInfo> cartInfoList) {
          // delete from cart_info where id in ()
        for (CartInfo cartInfo : cartInfoList) {
            cartInfoMapper.deleteByPrimaryKey(cartInfo);
        }

        //同步缓存
        try {
            syncCache(cartInfoList.get(0).getUserId());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
