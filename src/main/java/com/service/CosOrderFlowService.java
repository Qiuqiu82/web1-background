package com.service;
import com.entity.CosorderEntity;
import java.util.List;
import java.util.Map;

public interface CosOrderFlowService {
    void recordOrderCreated(CosorderEntity order, Long operatorId, String operatorRole, String remark);

    String markPaySuccessByOrderNo(String orderNo,
                                   String payOrderNo,
                                   String channelTradeNo,
                                   Long operatorId,
                                   String operatorRole,
                                   String remark);

    String adminTransition(Long orderId,
                           String toOrderStatus,
                           Long operatorId,
                           String operatorRole,
                           String remark);

    String designerStartProduction(Long orderId,
                                   Long designerId,
                                   String designerTable,
                                   Long operatorId,
                                   String operatorRole,
                                   String remark);

    String designerShip(Long orderId,
                        Long designerId,
                        String designerTable,
                        Long operatorId,
                        String operatorRole,
                        String remark);

    String userCancel(Long orderId,
                      Long userId,
                      String userTable,
                      Long operatorId,
                      String operatorRole,
                      String remark);

    List<Map<String, Object>> listStatusLogs(Long orderId);

    Map<String, Object> queryOrderById(Long orderId);
}
