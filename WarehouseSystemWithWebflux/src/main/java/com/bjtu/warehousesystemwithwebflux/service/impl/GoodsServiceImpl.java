package com.bjtu.warehousesystemwithwebflux.service.impl;

import com.bjtu.warehousesystemwithwebflux.pojo.Goods;
import com.bjtu.warehousesystemwithwebflux.pojo.Inbound;
import com.bjtu.warehousesystemwithwebflux.pojo.Outbound;
import com.bjtu.warehousesystemwithwebflux.pojo.UpdateGoodsInfoParam;
import com.bjtu.warehousesystemwithwebflux.repository.GoodsRepository;
import com.bjtu.warehousesystemwithwebflux.repository.InboundRepository;
import com.bjtu.warehousesystemwithwebflux.repository.OutboundRepository;
import com.bjtu.warehousesystemwithwebflux.service.GoodsService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private OutboundRepository outboundRepository;

    @Autowired
    private InboundRepository inboundRepository;

    @Override
    public Mono<Goods> registerGoods(Goods goods) {
        return goodsRepository.save(goods);
    }

    @Override
    public Mono<Goods> getGoodsById(String id) {
        return goodsRepository.findById(id);
    }

    @Override
    public Mono<Outbound> updateGoodsOutbound(UpdateGoodsInfoParam goodsInfo) {
        Outbound outbound = new Outbound();
        BeanUtils.copyProperties(goodsInfo,outbound);
        return outboundRepository.save(outbound);
    }

    @Override
    public Mono<Inbound> updateGoodsInbound(UpdateGoodsInfoParam goodsInfo) {
        Inbound inbound = new Inbound();
        BeanUtils.copyProperties(goodsInfo,inbound);
        return inboundRepository.save(inbound);
    }

    @Override
    public Mono<Void> deleteGoodsById(String id) {
        return goodsRepository.findById(id)
                .flatMap(goods -> goodsRepository.deleteById(id))
                .switchIfEmpty(Mono.empty());
    }

    @Override
    public Flux<Goods> listGoods(Integer pageNo, Integer pageSize, Integer warehouseId) {
        return goodsRepository.findGoodsByPageAndWarehouseId(pageNo, pageSize, warehouseId);
    }
}

