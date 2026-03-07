package com.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.entity.CoscartEntity;
import com.entity.CosorderEntity;
import com.service.CoscartService;
import com.service.CosOrderFlowService;
import com.service.CosorderService;
import com.utils.CosRoleUtil;
import com.utils.R;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

//cos闂傚倸鍊搁崐椋庣矆娓氣偓楠炴牠顢曢敂钘変罕闂佺硶鍓濋悷褔鎯岄幘缁樼厽闁瑰瓨绻傞ˉ鐐差熆鐠轰警鍎戠紒鈾€鍋撻梻浣告啞濞插繘宕濈仦鍓ь洸濡わ絽鍟崑鈩冪箾閸℃绠版い蹇ｅ墮椤法鎲撮崟鎯扳偓鍧楁煛鐏炲墽娲撮柛鈺嬬節瀹曟帡濡堕崱娆愭瘔闂傚倷鑳堕幊鎾诲床閺屻儺鏁勫璺侯儐閸欏繘鏌嶈閸撶喖寮婚敐鍛傜喖宕归鐐嚄婵?
@RestController
@RequestMapping("/cosorder")
public class CosorderController {
    private static final String PAY_UNPAID = "未支付";
    private static final String PAY_PAID = "已支付";
    private static final String ORDER_PENDING_CONFIRM = "待确认";
    private static final String DESIGNER_WAIT_CLAIM = "待接单";
    private static final String DESIGNER_CLAIMED = "已认领";
    @Autowired private CosorderService cosorderService;
    @Autowired private CoscartService coscartService;
    @Autowired private CosOrderFlowService cosOrderFlowService;
    @Autowired private JdbcTemplate jdbcTemplate;

    private Long uid(HttpServletRequest req){ return Long.valueOf(req.getSession().getAttribute("userId").toString()); }
    private String utable(HttpServletRequest req){ return req.getSession().getAttribute("tableName").toString(); }
    private String role(HttpServletRequest req){ return req.getSession().getAttribute("role").toString(); }
    private String roleCode(HttpServletRequest req){ return CosRoleUtil.normalize(role(req), utable(req)); }
    private boolean isAdmin(HttpServletRequest req){ return CosRoleUtil.isAdmin(role(req), utable(req)); }
    private boolean isDesigner(HttpServletRequest req){ return CosRoleUtil.DESIGNER.equals(roleCode(req)); }

    @RequestMapping("/page")
    public R page(HttpServletRequest request){
        List<CosorderEntity> list = cosorderService.selectList(
                new EntityWrapper<CosorderEntity>()
                        .eq("user_id", uid(request))
                        .eq("user_table", utable(request))
                        .orderBy("id", false)
        );
        return R.ok().put("data", list);
    }

    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id, HttpServletRequest request){
        CosorderEntity o = cosorderService.selectById(id);
        if(o==null) return R.error("闂傚倸鍊峰ù鍥х暦閸偅鍙忛柟鎯板Г閳锋梻鈧箍鍎遍ˇ顖炲垂閸岀偞鐓熼柣鏃傤焾椤ュ鏌￠崱顓犵暤闁哄矉缍佸顒勫箰鎼淬垹鏋戦梻浣哥－缁垰顫忔繝姘劦妞ゆ巻鍋撶紒鐘茬Ч瀹曟洟鏌嗗鍛枃闂佸湱澧楀妯肩不濞差亝鐓熸俊顖濆亹鐢盯鏌?");
        if(!uid(request).equals(o.getUserId())) return R.error(403,"闂傚倸鍊搁崐椋庣矆娓氣偓楠炴牠顢曢敃鈧悿顕€鏌ｅΔ鈧悧濠囧矗韫囨稒鈷掗柛顐ゅ枍缁堕亶鏌ｉ幒宥囩煓闁哄本鐩、鏇㈡偐閹绘帒顫氶梻?");
        return R.ok().put("data", o);
    }

        @PostMapping("/submit")
    public R submit(@RequestBody Map<String,Object> body, HttpServletRequest request){
        Object cartIdsObj = body.get("cartIds");
        if(!(cartIdsObj instanceof List)) return R.error("cartIds闂傚倸鍊风粈渚€骞夐敓鐘冲仭闁靛鏅涚壕鍦喐閻楀牆绗掓慨瑙勭叀閺岋綁寮崹顔藉€梺缁樻尪閸庤尙鎹㈠☉銏犲耿婵☆垵妗ㄩ崚濠囨⒑?");

        @SuppressWarnings("unchecked")
        List<Object> ids = (List<Object>) cartIdsObj;
        if(ids.isEmpty()) return R.error("闂傚倷娴囧畷鍨叏閺夋嚚娲Ω閳轰胶顦у┑顔姐仜閸嬫捇鏌涢埞鎯т壕婵＄偑鍊栧濠氬磻閹炬番浜滈柨鏃囨椤ュ鏌嶈閸撴岸宕惔銊ョ閹兼番鍔岀粻顖滄喐閻楀牆淇柡浣告喘閺岋絽螣閼姐們鍋為梺鍦焾閹虫﹢寮婚埄鍐ㄧ窞閻忕偠妫勬竟瀣磽娴ｅ搫校闁搞劌鐏濋锝夊蓟閵夈儺娼婇梺闈涚墕閹冲秶妲?");

        List<Long> cartIds = new ArrayList<>();
        for(Object idObj : ids){
            if(idObj == null) continue;
            try{
                if(idObj instanceof Number){
                    cartIds.add(((Number) idObj).longValue());
                }else{
                    cartIds.add(Long.valueOf(idObj.toString()));
                }
            }catch (Exception ignore){
                return R.error("cartIds闂傚倷娴囬褏鈧稈鏅濈划娆撳箳濡や焦娅斿┑鐘垫暩婵參宕戦幘娣簻闊洦鎸炬晶娑欍亜閵夈儳澧﹂柡宀嬬秮楠炲洭顢楁径濠冾啀闂備線娼уΛ妤呮晝椤忓牆钃?");
            }
        }
        if(cartIds.isEmpty()) return R.error("闂傚倷娴囧畷鍨叏閺夋嚚娲Ω閳轰胶顦у┑顔姐仜閸嬫捇鏌涢埞鎯т壕婵＄偑鍊栧濠氬磻閹炬番浜滈柨鏃囨椤ュ鏌嶈閸撴岸宕惔銊ョ閹兼番鍔岀粻顖滄喐閻楀牆淇柡浣告喘閺岋絽螣閼姐們鍋為梺鍦焾閹虫﹢寮婚埄鍐ㄧ窞閻忕偠妫勬竟瀣磽娴ｅ搫校闁搞劌鐏濋锝夊蓟閵夈儺娼婇梺闈涚墕閹冲秶妲?");

        List<CoscartEntity> carts = coscartService.selectBatchIds(cartIds);
        if(carts==null || carts.isEmpty()) return R.error("闂傚倷娴囧畷鍨叏閻㈢绀夐柨鏇炲€搁崒銊╂煟閹伴潧澧繛鍛█閺岀喓绮欓崹顕呭妷婵犳鍠楃划鎾诲蓟閵娾晛绫嶉柛銉戝啫啸缂傚倷璁查崑鎾绘倵閿濆骸澧扮痪?");

        BigDecimal total = BigDecimal.ZERO;
        for(CoscartEntity c: carts){
            if(!uid(request).equals(c.getUserId())) return R.error(403,"闂傚倷娴囧畷鍨叏閻㈢绀夐柨鏇炲€搁崒銊╂煟閹伴潧澧繛鍛█閺岀喓绮欓崹顕呭妷婵犳鍠楃划鎾诲蓟閵娾晛绫嶉柛銉仢閹惧绠鹃柛顐ｇ箘閸╋綁鏌″畝鈧崰鎾诲箯閻樿鐏抽柧蹇ｅ亞瑜板孩绻濋悽闈涗户闁冲嘲鐗婄粋宥咁煥閸繄鍘?");
            total = total.add(c.getAmount()==null?BigDecimal.ZERO:c.getAmount());
        }

        Long addressId = parseLong(body.get("addressId"));
        Long bodyProfileId = parseLong(body.get("bodyProfileId"));

        Map<String,Object> addressSnapshot = normalizeAddressSnapshot(asMap(body.get("addressSnapshot")));
        if(addressSnapshot.isEmpty() && addressId != null){
            addressSnapshot = queryAddressSnapshot(addressId, uid(request), utable(request));
        }
        if(addressSnapshot.isEmpty()){
            addressSnapshot = buildLegacyAddressSnapshot(
                    str(body.get("contactName")),
                    str(body.get("contactPhone")),
                    str(body.get("address"))
            );
        }

        String fallbackSizeCode = firstCartSizeCode(carts);
        if(StringUtils.isNotBlank(fallbackSizeCode) && !isAllowedSizeCode(fallbackSizeCode)){
            return R.error(400, "閻忓繗娅ｉ悥婊勭閸涱喗鏆滈柟闀愯/M/L/XL");
        }
        Map<String,Object> bodySnapshot = normalizeBodyProfileSnapshot(asMap(body.get("bodyProfileSnapshot")), fallbackSizeCode);
        if(bodySnapshot.isEmpty() && bodyProfileId != null){
            bodySnapshot = queryBodySnapshot(bodyProfileId, uid(request), utable(request), fallbackSizeCode);
        }
        if(bodySnapshot.isEmpty()){
            bodySnapshot = buildBodySnapshotFromCarts(carts, fallbackSizeCode);
        }

        String contactName = firstNonBlank(str(body.get("contactName")), str(addressSnapshot.get("receiverName")));
        String contactPhone = firstNonBlank(str(body.get("contactPhone")), str(addressSnapshot.get("receiverPhone")));
        String addressText = firstNonBlank(str(body.get("address")), composeAddressText(addressSnapshot));

        CosorderEntity o = new CosorderEntity();
        o.setId(new Date().getTime() + (long)(Math.random()*1000));
        o.setOrderNo("COS" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
        o.setUserId(uid(request));
        o.setUserTable(utable(request));
        o.setTotalAmount(total);
        o.setPayStatus(PAY_UNPAID);
        o.setOrderStatus(ORDER_PENDING_CONFIRM);
        o.setPayType(StringUtils.isBlank(str(body.get("payType"))) ? "ALIPAY" : str(body.get("payType")));
        o.setContactName(contactName);
        o.setContactPhone(contactPhone);
        o.setAddress(addressText);
        o.setRemark(body.get("remark")==null?null:body.get("remark").toString());
        o.setItemsJson(JSON.toJSONString(carts));
        o.setAddressId(addressId);
        o.setBodyProfileId(bodyProfileId);
        o.setAddressSnapshotJson(addressSnapshot.isEmpty() ? null : JSON.toJSONString(addressSnapshot));
        o.setBodyProfileSnapshotJson(bodySnapshot.isEmpty() ? null : JSON.toJSONString(bodySnapshot));

        cosorderService.insert(o);
        cosOrderFlowService.recordOrderCreated(o, uid(request), roleCode(request), "用户提交订单");
        coscartService.deleteBatchIds(cartIds);

        return R.ok().put("data", o);
    }

    @PostMapping("/pay/{id}")
    public R pay(@PathVariable("id") Long id, @RequestBody(required = false) Map<String,Object> body, HttpServletRequest request){
        CosorderEntity o = cosorderService.selectById(id);
        if(o==null) return R.error("闂傚倸鍊峰ù鍥х暦閸偅鍙忛柟鎯板Г閳锋梻鈧箍鍎遍ˇ顖炲垂閸岀偞鐓熼柣鏃傤焾椤ュ鏌￠崱顓犵暤闁哄矉缍佸顒勫箰鎼淬垹鏋戦梻浣哥－缁垰顫忔繝姘劦妞ゆ巻鍋撶紒鐘茬Ч瀹曟洟鏌嗗鍛枃闂佸湱澧楀妯肩不濞差亝鐓熸俊顖濆亹鐢盯鏌?");
        if(!uid(request).equals(o.getUserId())) return R.error(403,"闂傚倸鍊搁崐椋庣矆娓氣偓楠炴牠顢曢敃鈧悿顕€鏌ｅΔ鈧悧濠囧矗韫囨稒鈷掗柛顐ゅ枍缁堕亶鏌ｉ幒宥囩煓闁哄本鐩、鏇㈡偐閹绘帒顫氶梻?");

        String err = cosOrderFlowService.markPaySuccessByOrderNo(
                o.getOrderNo(),
                null,
                "LEGACY" + System.currentTimeMillis(),
                uid(request),
                roleCode(request),
                "legacy pay endpoint"
        );
        if (StringUtils.isNotBlank(err)) {
            return R.error(400, err);
        }

        if(body!=null && body.get("payType")!=null) {
            o.setPayType(body.get("payType").toString());
            cosorderService.updateById(o);
        }
        return R.ok("闂傚倸鍊搁崐宄懊归崶顒€违闁逞屽墴閺屾稓鈧綆鍋呯亸浼存煏閸パ冾伃鐎殿喕绮欐俊姝岊槼闁革綆鍣ｉ弻锝夊閳轰胶浠梺鐑╂櫓閸ㄨ泛顕ｆ繝姘亜閻炴稈鈧厖澹曞┑鐐村灦椤忣亪顢旈崨顔煎伎闂佹悶鍎滈埀顒勫绩?濠电姷鏁告慨鐑姐€傞挊澹╋綁宕ㄩ弶鎴濈€銈呯箰閻楀棛绮堥崼鐔虹瘈闂傚牊绋撴晶鎰版煕?");
    }

    @PostMapping("/cancel/{id}")
    public R cancel(@PathVariable("id") Long id, HttpServletRequest request){
        String err = cosOrderFlowService.userCancel(
                id,
                uid(request),
                utable(request),
                uid(request),
                roleCode(request),
                "user canceled order"
        );
        if (StringUtils.isNotBlank(err)) {
            return R.error(400, err);
        }
        return R.ok("闂傚倸鍊峰ù鍥х暦閸偅鍙忛柟鎯板Г閳锋梻鈧箍鍎遍ˇ顖炲垂閸岀偞鐓熼柣鏃傤焾椤ュ鏌￠崱顓犵暤闁哄矉缍佸顒€鈻庨悙顒傛瀮闂備礁鎽滈崰搴ㄥ箠濮椻偓瀵鎮㈤搹鍦紲濠碘槅鍨靛▍锝夋偡閵娧呯＝濞达絿顭堢痪褔鎮楃粭娑樻噺瀹?");
    }

    
    @PostMapping("/confirm-receipt/{id}")
    public R confirmReceipt(@PathVariable("id") Long id, HttpServletRequest request){
        String err = cosOrderFlowService.userConfirmReceipt(
                id,
                uid(request),
                utable(request),
                uid(request),
                roleCode(request),
                "user confirmed receipt"
        );
        if (StringUtils.isNotBlank(err)) {
            return R.error(400, err);
        }
        return R.ok("缂傚倸鍊搁崐鐑芥嚄閸洘鎯為幖娣妼閻骞栧ǎ顒€濡肩紒鎰殕缁绘盯骞嬪▎蹇曞姶闂佽桨绀佸ú銈夊煘閹达附鍋愰悗鍦Т椤ユ繄绱撻崒娆掝唹闁哄懐濞€瀵鎮㈢喊杈ㄦ櫖濠电姴锕ら崰姘跺汲椤撱垺鍋℃繝濠傛噹椤ｅジ鏌ｉ埡濠傜仸鐎殿喖顭烽幃銏㈡偘閳ュ厖澹曞┑鐐村灦椤忣亪顢旈崨顔煎伎闂佹悶鍎滈埀顒勫绩?");
    }
    @GetMapping("/admin/page")
    public R adminPage(@RequestParam Map<String, Object> params, HttpServletRequest request){
        if(!isAdmin(request)) {
            return R.error(403, "闂傚倸鍊搁崐椋庣矆娓氣偓楠炴牠顢曢敃鈧悿顕€鏌ｅΔ鈧悧濠囧矗韫囨稒鈷掗柛顐ゅ枍缁堕亶鏌ｉ幒宥囩煓闁哄本鐩、鏇㈡偐閹绘帒顫氶梻?");
        }

        int page = parseInt(params.get("page"), 1);
        int limit = parseInt(params.get("limit"), 10);
        if(page < 1) page = 1;
        if(limit < 1) limit = 10;
        if(limit > 100) limit = 100;

        StringBuilder where = new StringBuilder(" where 1=1 ");
        List<Object> args = new ArrayList<>();

        String orderNo = str(params.get("orderNo"));
        if(StringUtils.isNotBlank(orderNo)) {
            where.append(" and order_no like ? ");
            args.add("%" + orderNo.trim() + "%");
        }

        String payStatus = str(params.get("payStatus"));
        if(StringUtils.isNotBlank(payStatus)) {
            where.append(" and pay_status = ? ");
            args.add(payStatus.trim());
        }

        String orderStatus = str(params.get("orderStatus"));
        if(StringUtils.isNotBlank(orderStatus)) {
            where.append(" and order_status = ? ");
            args.add(orderStatus.trim());
        }

        Long userId = parseLong(params.get("userId"));
        if(userId != null) {
            where.append(" and user_id = ? ");
            args.add(userId);
        }

        String dateFrom = normalizeDateBoundary(str(params.get("dateFrom")), false);
        if(StringUtils.isNotBlank(dateFrom)) {
            where.append(" and addtime >= ? ");
            args.add(dateFrom);
        }

        String dateTo = normalizeDateBoundary(str(params.get("dateTo")), true);
        if(StringUtils.isNotBlank(dateTo)) {
            where.append(" and addtime <= ? ");
            args.add(dateTo);
        }

        List<Object> listArgs = new ArrayList<>(args);
        listArgs.add(limit);
        listArgs.add((page - 1) * limit);

        List<Map<String, Object>> list = jdbcTemplate.queryForList(
                "select * from cosorder " + where + " order by id desc limit ? offset ?",
                listArgs.toArray()
        );

        Long total = jdbcTemplate.queryForObject(
                "select count(1) from cosorder " + where,
                args.toArray(),
                Long.class
        );

        Map<String, Object> data = new HashMap<>();
        data.put("list", list);
        data.put("total", total == null ? 0 : total);
        data.put("page", page);
        data.put("limit", limit);

        return R.ok().put("data", data);
    }

    @GetMapping("/admin/detail/{id}")
    public R adminDetail(@PathVariable("id") Long id, HttpServletRequest request){
        if(!isAdmin(request)) {
            return R.error(403, "闂傚倸鍊搁崐椋庣矆娓氣偓楠炴牠顢曢敃鈧悿顕€鏌ｅΔ鈧悧濠囧矗韫囨稒鈷掗柛顐ゅ枍缁堕亶鏌ｉ幒宥囩煓闁哄本鐩、鏇㈡偐閹绘帒顫氶梻?");
        }

        Map<String, Object> order = cosOrderFlowService.queryOrderById(id);
        if(order == null) {
            return R.error("闂傚倸鍊峰ù鍥х暦閸偅鍙忛柟鎯板Г閳锋梻鈧箍鍎遍ˇ顖炲垂閸岀偞鐓熼柣鏃傤焾椤ュ鏌￠崱顓犵暤闁哄矉缍佸顒勫箰鎼淬垹鏋戦梻浣哥－缁垰顫忔繝姘劦妞ゆ巻鍋撶紒鐘茬Ч瀹曟洟鏌嗗鍛枃闂佸湱澧楀妯肩不濞差亝鐓熸俊顖濆亹鐢盯鏌?");
        }

        Map<String, Object> data = new HashMap<>();
        data.put("order", order);
        data.put("statusLogs", cosOrderFlowService.listStatusLogs(id));
        return R.ok().put("data", data);
    }

    @PostMapping("/admin/transition")
    public R adminTransition(@RequestBody Map<String, Object> body, HttpServletRequest request){
        if(!isAdmin(request)) {
            return R.error(403, "闂傚倸鍊搁崐椋庣矆娓氣偓楠炴牠顢曢敃鈧悿顕€鏌ｅΔ鈧悧濠囧矗韫囨稒鈷掗柛顐ゅ枍缁堕亶鏌ｉ幒宥囩煓闁哄本鐩、鏇㈡偐閹绘帒顫氶梻?");
        }

        Long orderId = parseLong(body.get("orderId"));
        String toOrderStatus = str(body.get("toOrderStatus"));
        String remark = str(body.get("remark"));

        String err = cosOrderFlowService.adminTransition(
                orderId,
                toOrderStatus,
                uid(request),
                roleCode(request),
                remark
        );

        if(StringUtils.isNotBlank(err)) {
            return R.error(400, err);
        }

        return R.ok("闂傚倸鍊搁崐鐑芥嚄閸撲礁鍨濇い鏍亹閳ь剨绠撳畷濂稿Ψ閵夛附袣闂備礁鎼粙渚€宕㈡總鍛婂€块柛顭戝亖娴滄粓鏌熸潏鍓хɑ缁绢厼鐖奸弻娑㈠棘鐠恒剱銈囩磼鏉堛劌绗氱紒妤冨枑缁绘繈宕堕…鎴斿亾瀹ュ應鏀介柣鎰暯閹封€趁瑰搴濋偗闁靛棔绀佽灃濞达絿鎳撻鎾绘⒑閸涘﹦缂氶柛搴㈡緲闇夐柣鎴ｅГ閳?");
    }

    @GetMapping("/admin/status-log/{orderId}")
    public R adminStatusLog(@PathVariable("orderId") Long orderId, HttpServletRequest request){
        if(!isAdmin(request)) {
            return R.error(403, "闂傚倸鍊搁崐椋庣矆娓氣偓楠炴牠顢曢敃鈧悿顕€鏌ｅΔ鈧悧濠囧矗韫囨稒鈷掗柛顐ゅ枍缁堕亶鏌ｉ幒宥囩煓闁哄本鐩、鏇㈡偐閹绘帒顫氶梻?");
        }
        return R.ok().put("data", cosOrderFlowService.listStatusLogs(orderId));
    }


    @GetMapping("/designer/pool")
    public R designerPool(@RequestParam Map<String, Object> params, HttpServletRequest request){
        if(!isDesigner(request)) {
            return R.error(403, "闂傚倸鍊搁崐椋庣矆娓氣偓楠炴牠顢曢敃鈧悿顕€鏌ｅΔ鈧悧濠囧矗韫囨稒鈷掗柛顐ゅ枍缁堕亶鏌ｉ幒宥囩煓闁哄本鐩、鏇㈡偐閹绘帒顫氶梻?");
        }

        int page = parseInt(params.get("page"), 1);
        int limit = parseInt(params.get("limit"), 10);
        if(page < 1) page = 1;
        if(limit < 1) limit = 10;
        if(limit > 100) limit = 100;

        StringBuilder where = new StringBuilder(" where pay_status=? and (designer_status is null or designer_status='' or designer_status=?) and (designer_id is null or designer_id=0) ");
        List<Object> args = new ArrayList<>();
        args.add(PAY_PAID);
        args.add(DESIGNER_WAIT_CLAIM);

        String orderNo = str(params.get("orderNo"));
        if(StringUtils.isNotBlank(orderNo)) {
            where.append(" and order_no like ? ");
            args.add("%" + orderNo.trim() + "%");
        }

        List<Object> listArgs = new ArrayList<>(args);
        listArgs.add(limit);
        listArgs.add((page - 1) * limit);

        List<Map<String, Object>> list = jdbcTemplate.queryForList(
                "select * from cosorder " + where + " order by id desc limit ? offset ?",
                listArgs.toArray()
        );

        Long total = jdbcTemplate.queryForObject(
                "select count(1) from cosorder " + where,
                args.toArray(),
                Long.class
        );

        Map<String, Object> data = new HashMap<>();
        data.put("list", list);
        data.put("total", total == null ? 0 : total);
        data.put("page", page);
        data.put("limit", limit);

        return R.ok().put("data", data);
    }

    @PostMapping("/designer/claim")
    public R designerClaim(@RequestBody Map<String, Object> body, HttpServletRequest request){
        if(!isDesigner(request)) {
            return R.error(403, "闂傚倸鍊搁崐椋庣矆娓氣偓楠炴牠顢曢敃鈧悿顕€鏌ｅΔ鈧悧濠囧矗韫囨稒鈷掗柛顐ゅ枍缁堕亶鏌ｉ幒宥囩煓闁哄本鐩、鏇㈡偐閹绘帒顫氶梻?");
        }

        Long orderId = parseLong(body.get("orderId"));
        if(orderId == null) {
            return R.error(400, "orderId婵犵數濮烽弫鎼佸磻閻愬搫鍨傞柛顐ｆ礀缁犱即鏌涘┑鍕姢闁活厽鎹囬弻锝夊箣閿濆棭妫勭紓浣插亾闁稿本绋愮换鍡樸亜閺嶃劎绠撻柛姘秺閺屽秶绱掑Ο娲绘闂佸搫鐫欓崱娆戞澑闂佽鍎抽崯鎸庢綇閸涘瓨鈷?");
        }

        Long designerId = uid(request);
        String designerTable = utable(request);

        int updated = jdbcTemplate.update(
                "update cosorder set designer_id=?, designer_table=?, designer_status=?, designer_take_time=now() " +
                        "where id=? and pay_status=? and (designer_status is null or designer_status='' or designer_status=?) " +
                        "and (designer_id is null or designer_id=0)",
                designerId,
                designerTable,
                DESIGNER_CLAIMED,
                orderId,
                PAY_PAID,
                DESIGNER_WAIT_CLAIM
        );

        if(updated > 0) {
            return R.ok("闂傚倸鍊峰ù鍥х暦閸偅鍙忛柟鎯板Г閳锋梻鈧箍鍎遍ˇ顖滃鐟欏嫮绡€闂傚牊渚楅崕宥夋煃瑜滈崜娆撳磹閸фぜ鈧線寮撮姀鈩冩珖闂侀€炲苯澧扮紒顔碱煼閺佹劖寰勭€ｎ剙骞嶉梻鍌欑贰閸欏繒绮婚幋鐐存珷妞ゆ柨顫曟禍?");
        }

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "select id,pay_status,designer_id,designer_table,designer_status from cosorder where id=? limit 1",
                orderId
        );
        if(rows.isEmpty()) {
            return R.error(404, "闂傚倸鍊峰ù鍥х暦閸偅鍙忛柟鎯板Г閳锋梻鈧箍鍎遍ˇ顖炲垂閸岀偞鐓熼柣鏃傤焾椤ュ鏌￠崱顓犵暤闁哄矉缍佸顒勫箰鎼淬垹鏋戦梻浣哥－缁垰顫忔繝姘劦妞ゆ巻鍋撶紒鐘茬Ч瀹曟洟鏌嗗鍛枃闂佸湱澧楀妯肩不濞差亝鐓熸俊顖濆亹鐢盯鏌?");
        }

        Map<String, Object> current = rows.get(0);
        String payStatus = str(current.get("pay_status"));
        if(!PAY_PAID.equals(payStatus)) {
            return R.error(400, "婵犵數濮烽弫鎼佸磻濞戙埄鏁嬫い鎾跺枑閸欏繘鏌熺紒銏犳灈缂佺姷濞€楠炴牕菐椤掆偓婵¤偐绱掗埀顒勫礋椤栨稓鍙嗛梺缁樻煥閹碱偄鐡紓鍌氬€风拋鏌ュ疾濠婂牆桅闁告洦鍨伴崘鈧梺闈浤涢崟顐ｇ彯闂備焦鐪归崺鍕垂闁秵鍎庢い鏍仜缁犳牠鎮峰▎蹇擃仾濠殿垱鎸冲濠氬醇閻旀亽鈧帒霉閻樺眰鍋㈤柟顔筋殘閹叉挳宕熼鍌ゆФ闂備礁鎽滄慨鐢告偋閺囥垺鍋╅柣鎴ｅГ閺呮彃顭块懜鐬垿寮查鈧埞鎴︽偐閼碱兛绮甸梺鍛婃⒐濞茬喖鐛崘顏佸亾閿濆骸浜楃紒?");
        }

        Long currentDesignerId = parseLong(current.get("designer_id"));
        String currentDesignerTable = str(current.get("designer_table"));
        String currentDesignerStatus = str(current.get("designer_status"));

        if(currentDesignerId != null && currentDesignerId > 0) {
            if(designerId.equals(currentDesignerId) && StringUtils.equalsIgnoreCase(designerTable, currentDesignerTable)) {
                return R.ok("闂傚倸鍊峰ù鍥х暦閸偅鍙忛柡澶嬪殮濞差亜顫呴柕鍫濇噽閿涚喖姊洪崷顓℃闁革綆鍣ｅ顐﹀炊閳哄倸鏋戦梺缁橆殔閻楀棛绮幒鏃傛／妞ゆ挻绋戞禍楣冩⒒閸屾瑧顦﹂柟纰卞亜鐓ら柕濞炬櫅绾剧粯绻涢幋娆忕仼闁告艾缍婇弻鐔兼倻濮楀棙鐣烽梺缁樻尰濞茬喖寮婚妸鈺佹闁圭瀛╅崕鎾寸節绾版ǚ鍋撻懠顒傜厯闂佸搫鏈惄顖涗繆閹间焦鏅滈柛鎾楀懎甯ㄩ梻鍌欐祰椤曟牠宕规导鏉戠柈闁哄鍨归弳锕傛煟閺冨倵鎷￠柡浣割儔閺屾稑鈽夐崡鐐茬闂佺锕ュú鐔奉潖缂佹ɑ濯撮柛娑橈工閺嗗牆顪冮妶鍐ㄥ婵☆偅顨堥崣鍛存⒑绾懏褰ч梻鍕瀹?");
            }
            return R.error(409, "闂傚倸鍊峰ù鍥х暦閸偅鍙忛柟鎯板Г閳锋梻鈧箍鍎遍ˇ顖炲垂閸岀偞鐓熼柣鏃傤焾椤ュ鏌￠崱顓犵暤闁哄矉缍佸顒€鈻庨悙顒傛瀮闂備礁鎽滈崰搴ㄥ箠濮椻偓瀵濡堕崱娆撴闂佸憡鎸稿璺侯熆閹炬枼鏀介柣鎰版涧鐢爼鏌ｉ弽褋鍋㈢€殿喛顕ч埥澶婎煥閸涱垱婢戦梻浣告惈閸熺娀宕戦幘缁樼厸閻庯綆浜滈顐ょ磼鏉堛劌娴い銏″哺瀹曘劑顢橀悜鍡橀獎闂傚倷鑳堕幊鎾诲床閺屻儺鏁勫鑸靛姉瀹撲線鏌熸潏鍓х暠缂佺姵鐩濠氬醇閻旇　濮囬梺绋匡工闁帮絽顫忛崫鍕懷囧炊瑜忛崝鎾⒑閹肩偛濡洪柛妤佸▕楠炲啳顦规鐐叉喘閺屽洭鏁傞懞銉︾彎缂傚倸鍊搁崐鐑芥倿閿曞倹鏅濇い鎰╁€栭～?");
        }

        if(StringUtils.isNotBlank(currentDesignerStatus) && !DESIGNER_WAIT_CLAIM.equals(currentDesignerStatus)) {
            return R.error(409, "闂傚倸鍊峰ù鍥х暦閸偅鍙忛柟鎯板Г閳锋梻鈧箍鍎遍ˇ顖炲垂閸岀偞鐓熼柣鏃傤焾椤ュ鏌￠崱顓犵暤闁哄矉缍佸顒勫垂椤旇棄鈧垶姊虹粙娆惧剱闁告梹鐟╁濠氬Χ婢跺﹣绱堕梺闈涱樈閸犳袙瀹€鍕拺缂佸顑欓崕鎰版煙缁嬫鐓兼鐐茬箻瀹曘劑顢氶崨顔а囨煙閼圭増褰х紒鏌ョ畺瀹曚即骞掑Δ浣叉嫽婵炶揪绲介幉锟犲疮閻愮儤鐓熼柣鏇氱閻忥妇鈧娲栫紞濠囧箖娴犲绀堥柛娆忣槹濞呭秶绱撻崒姘偓鐑芥倿閿曞倹鏅濇い鎰╁€栭～?");
        }

        return R.error(409, "闂傚倸鍊峰ù鍥х暦閸偅鍙忛柟鎯板Г閳锋梻鈧箍鍎遍ˇ顖炲垂閸岀偞鐓熼柣鏃傤焾椤ュ鏌￠崱顓犵暤闁哄矉缍佸顒€鈻庨悙顒傛瀮闂備礁鎽滈崰搴ㄥ箠濮椻偓瀵濡堕崱娆撴闂佸憡鎸稿璺侯熆閹炬枼鏀介柣鎰版涧鐢爼鏌ｉ弽褋鍋㈡鐐插暟閹叉挳宕熼鈧鎾绘⒒閸屾氨澧涚紒瀣墦瀹曟垿骞橀弬銉︽杸闁诲函绲婚崝澶愬磻閹捐纾奸柣鎴濇閺呮繈姊洪幐搴㈩梿濠殿喓鍊曢埢宥夊冀椤撶喎鈧敻鏌涜箛鎿冩Ц濞存粓绠栧娲川婵犲倸顫嶉梺绋块閸熷潡鍩㈤幘璇茶摕闁靛鑵归幏娲⒑閸涘﹦鈽夐柨鏇樺€曢湁妞ゆ洍鍋撻柡宀嬬節閸┾偓妞ゆ帒瀚悞鑲┾偓骞垮劚閹虫捇骞楅弴鐔虹瘈闁汇垽娼у瓭濠电偛鐪伴崐妤呭箟閹绢喗鏅濋柛灞炬皑椤旀劕鈹戦悜鍥╃У闁告挻鐟︽穱濠囧礂閼测晝顔曢梺鍛婁緱閸嬪嫰鎮橀弻銉︾厵?");
    }


    @PostMapping("/designer/start")
    public R designerStart(@RequestBody Map<String, Object> body, HttpServletRequest request){
        if(!isDesigner(request)) {
            return R.error(403, "闂傚倸鍊搁崐椋庣矆娓氣偓楠炴牠顢曢敃鈧悿顕€鏌ｅΔ鈧悧濠囧矗韫囨稒鈷掗柛顐ゅ枍缁堕亶鏌ｉ幒宥囩煓闁哄本鐩、鏇㈡偐閹绘帒顫氶梻?");
        }

        Long orderId = parseLong(body.get("orderId"));
        if(orderId == null) {
            return R.error(400, "orderId婵犵數濮烽弫鎼佸磻閻愬搫鍨傞柛顐ｆ礀缁犱即鏌涘┑鍕姢闁活厽鎹囬弻锝夊箣閿濆棭妫勭紓浣插亾闁稿本绋愮换鍡樸亜閺嶃劎绠撻柛姘秺閺屽秶绱掑Ο娲绘闂佸搫鐫欓崱娆戞澑闂佽鍎抽崯鎸庢綇閸涘瓨鈷?");
        }

        String remark = str(body.get("remark"));
        String err = cosOrderFlowService.designerStartProduction(
                orderId,
                uid(request),
                utable(request),
                uid(request),
                roleCode(request),
                remark
        );

        if(StringUtils.isNotBlank(err)) {
            return R.error(400, err);
        }

        return R.ok("闂傚倷娴囬褏鈧稈鏅犻、娆撳冀椤撶偟鐛ラ梺鍦劋椤ㄥ懐澹曟繝姘厵闁绘劦鍓氶悘閬嶆煛閳ь剟鎳為妷锝勭盎闂佸搫鍟崐鐢稿箯閿熺姵鐓曢幖杈剧磿缁犲鏌＄仦鍓ф创闁糕晪绻濆畷鎺楀Χ閸℃瑦娈鹃梻鍌欒兌閹虫捇宕查弻銉ョ疇閹兼番鍔夐埀顒婄畵婵℃悂鍩℃担渚Ч婵＄偑鍊栭崝鎴﹀磹閹捐秮褰掓倻閼恒儮鎷?");
    }

    @PostMapping("/designer/ship")
    public R designerShip(@RequestBody Map<String, Object> body, HttpServletRequest request){
        if(!isDesigner(request)) {
            return R.error(403, "闂傚倸鍊搁崐椋庣矆娓氣偓楠炴牠顢曢敃鈧悿顕€鏌ｅΔ鈧悧濠囧矗韫囨稒鈷掗柛顐ゅ枍缁堕亶鏌ｉ幒宥囩煓闁哄本鐩、鏇㈡偐閹绘帒顫氶梻?");
        }

        Long orderId = parseLong(body.get("orderId"));
        if(orderId == null) {
            return R.error(400, "orderId婵犵數濮烽弫鎼佸磻閻愬搫鍨傞柛顐ｆ礀缁犱即鏌涘┑鍕姢闁活厽鎹囬弻锝夊箣閿濆棭妫勭紓浣插亾闁稿本绋愮换鍡樸亜閺嶃劎绠撻柛姘秺閺屽秶绱掑Ο娲绘闂佸搫鐫欓崱娆戞澑闂佽鍎抽崯鎸庢綇閸涘瓨鈷?");
        }

        String remark = str(body.get("remark"));
        String err = cosOrderFlowService.designerShip(
                orderId,
                uid(request),
                utable(request),
                uid(request),
                roleCode(request),
                remark
        );

        if(StringUtils.isNotBlank(err)) {
            return R.error(400, err);
        }

        return R.ok("闂傚倸鍊搁崐椋庣矆娓氣偓楠炲鏁撻悩鍐蹭画闂佹寧娲栭崐鎼佸垂閸屾埃鏀介柛灞剧矤閻掑墽绱掗悩鍨毈闁哄被鍔岄埞鎴﹀幢閳哄倐锔剧磽娴ｅ搫顎撶紓宥勭窔瀵鎮㈤崗鑲╁姺闂佹寧娲嶉崑鎾愁熆瑜滈崰妤呭Φ?");
    }
    @GetMapping("/designer/mine")
    public R designerMine(@RequestParam Map<String, Object> params, HttpServletRequest request){
        if(!isDesigner(request)) {
            return R.error(403, "闂傚倸鍊搁崐椋庣矆娓氣偓楠炴牠顢曢敃鈧悿顕€鏌ｅΔ鈧悧濠囧矗韫囨稒鈷掗柛顐ゅ枍缁堕亶鏌ｉ幒宥囩煓闁哄本鐩、鏇㈡偐閹绘帒顫氶梻?");
        }

        int page = parseInt(params.get("page"), 1);
        int limit = parseInt(params.get("limit"), 10);
        if(page < 1) page = 1;
        if(limit < 1) limit = 10;
        if(limit > 100) limit = 100;

        StringBuilder where = new StringBuilder(" where designer_id=? and designer_table=? ");
        List<Object> args = new ArrayList<>();
        args.add(PAY_PAID);
        args.add(DESIGNER_WAIT_CLAIM);

        String orderNo = str(params.get("orderNo"));
        if(StringUtils.isNotBlank(orderNo)) {
            where.append(" and order_no like ? ");
            args.add("%" + orderNo.trim() + "%");
        }

        List<Object> listArgs = new ArrayList<>(args);
        listArgs.add(limit);
        listArgs.add((page - 1) * limit);

        List<Map<String, Object>> list = jdbcTemplate.queryForList(
                "select * from cosorder " + where + " order by designer_take_time desc, id desc limit ? offset ?",
                listArgs.toArray()
        );

        Long total = jdbcTemplate.queryForObject(
                "select count(1) from cosorder " + where,
                args.toArray(),
                Long.class
        );

        Map<String, Object> data = new HashMap<>();
        data.put("list", list);
        data.put("total", total == null ? 0 : total);
        data.put("page", page);
        data.put("limit", limit);

        return R.ok().put("data", data);
    }
    private static String normalizeDateBoundary(String raw, boolean endOfDay) {
        if(StringUtils.isBlank(raw)) {
            return null;
        }
        String safe = raw.trim();
        if(safe.length() == 10) {
            return safe + (endOfDay ? " 23:59:59" : " 00:00:00");
        }
        return safe;
    }

    private static Map<String,Object> asMap(Object raw){
        if(raw == null){
            return new LinkedHashMap<>();
        }
        if(raw instanceof Map){
            return new LinkedHashMap<>((Map<String, Object>) raw);
        }
        if(raw instanceof JSONObject){
            return new LinkedHashMap<>((JSONObject) raw);
        }
        if(raw instanceof String){
            String txt = String.valueOf(raw).trim();
            if(StringUtils.isBlank(txt)){
                return new LinkedHashMap<>();
            }
            try {
                JSONObject json = JSON.parseObject(txt);
                return new LinkedHashMap<>(json);
            } catch (Exception ignore){
                return new LinkedHashMap<>();
            }
        }
        return new LinkedHashMap<>();
    }

    private Map<String,Object> queryAddressSnapshot(Long addressId, Long userId, String userTable){
        List<Map<String,Object>> rows = jdbcTemplate.queryForList(
                "select receiver_name as receiverName,receiver_phone as receiverPhone,province,city,district,detail_address as detailAddress " +
                        "from cos_user_address where id=? and user_id=? and user_table=? and deleted=0 limit 1",
                addressId, userId, userTable
        );
        if(rows.isEmpty()){
            return new LinkedHashMap<>();
        }
        return normalizeAddressSnapshot(rows.get(0));
    }

    private Map<String,Object> queryBodySnapshot(Long profileId, Long userId, String userTable, String fallbackSizeCode){
        List<Map<String,Object>> rows = jdbcTemplate.queryForList(
                "select profile_name as profileName,height_cm as heightCm,weight_kg as weightKg,waist_cm as waistCm,bust_cm as bustCm,hip_cm as hipCm,shoulder_cm as shoulderCm " +
                        "from cos_user_body_profile where id=? and user_id=? and user_table=? and deleted=0 limit 1",
                profileId, userId, userTable
        );
        if(rows.isEmpty()){
            return new LinkedHashMap<>();
        }
        return normalizeBodyProfileSnapshot(rows.get(0), fallbackSizeCode);
    }

    private static Map<String,Object> normalizeAddressSnapshot(Map<String,Object> raw){
        Map<String,Object> snapshot = new LinkedHashMap<>();
        if(raw == null || raw.isEmpty()){
            return snapshot;
        }
        putIfNotBlank(snapshot, "receiverName", pick(raw, "receiverName", "receiver_name", "contactName", "contact_name"));
        putIfNotBlank(snapshot, "receiverPhone", pick(raw, "receiverPhone", "receiver_phone", "contactPhone", "contact_phone"));
        putIfNotBlank(snapshot, "province", pick(raw, "province"));
        putIfNotBlank(snapshot, "city", pick(raw, "city"));
        putIfNotBlank(snapshot, "district", pick(raw, "district"));
        putIfNotBlank(snapshot, "detailAddress", pick(raw, "detailAddress", "detail_address", "address"));
        return snapshot;
    }

    private static Map<String,Object> normalizeBodyProfileSnapshot(Map<String,Object> raw, String fallbackSizeCode){
        Map<String,Object> snapshot = new LinkedHashMap<>();
        if(raw == null || raw.isEmpty()){
            return snapshot;
        }
        putIfNotBlank(snapshot, "profileName", pick(raw, "profileName", "profile_name"));
        putIfNotBlank(snapshot, "heightCm", pick(raw, "heightCm", "height_cm"));
        putIfNotBlank(snapshot, "weightKg", pick(raw, "weightKg", "weight_kg"));
        putIfNotBlank(snapshot, "waistCm", pick(raw, "waistCm", "waist_cm"));
        putIfNotBlank(snapshot, "bustCm", pick(raw, "bustCm", "bust_cm"));
        putIfNotBlank(snapshot, "hipCm", pick(raw, "hipCm", "hip_cm"));
        putIfNotBlank(snapshot, "shoulderCm", pick(raw, "shoulderCm", "shoulder_cm"));
        String sizeCode = firstNonBlank(str(pick(raw, "sizeCode", "size_code")), fallbackSizeCode);
        if(StringUtils.isNotBlank(sizeCode) && !isAllowedSizeCode(sizeCode)){
            sizeCode = null;
        }
        if(StringUtils.isNotBlank(sizeCode)){
            snapshot.put("sizeCode", sizeCode);
        }
        return snapshot;
    }

    private Map<String,Object> buildBodySnapshotFromCarts(List<CoscartEntity> carts, String fallbackSizeCode){
        if(carts == null || carts.isEmpty()){
            return new LinkedHashMap<>();
        }
        for(CoscartEntity cart : carts){
            if(cart == null || StringUtils.isBlank(cart.getCustomSnapshotJson())){
                continue;
            }
            try {
                JSONObject root = JSON.parseObject(cart.getCustomSnapshotJson());
                Map<String,Object> nested = asMap(root.get("bodyProfileSnapshot"));
                Map<String,Object> normalized = normalizeBodyProfileSnapshot(nested, firstNonBlank(str(root.get("sizeCode")), fallbackSizeCode));
                if(!normalized.isEmpty()){
                    return normalized;
                }
                normalized = normalizeBodyProfileSnapshot(asMap(root), firstNonBlank(str(root.get("sizeCode")), fallbackSizeCode));
                if(!normalized.isEmpty()){
                    return normalized;
                }
            } catch (Exception ignore){
                // ignore parse error
            }
        }
        return new LinkedHashMap<>();
    }

    private static Map<String,Object> buildLegacyAddressSnapshot(String contactName, String contactPhone, String address){
        Map<String,Object> snapshot = new LinkedHashMap<>();
        if(StringUtils.isNotBlank(contactName)){
            snapshot.put("receiverName", contactName.trim());
        }
        if(StringUtils.isNotBlank(contactPhone)){
            snapshot.put("receiverPhone", contactPhone.trim());
        }
        if(StringUtils.isNotBlank(address)){
            snapshot.put("detailAddress", address.trim());
        }
        return snapshot;
    }

    private static String composeAddressText(Map<String,Object> addressSnapshot){
        if(addressSnapshot == null || addressSnapshot.isEmpty()){
            return null;
        }
        StringBuilder sb = new StringBuilder();
        appendAddressPart(sb, str(addressSnapshot.get("province")));
        appendAddressPart(sb, str(addressSnapshot.get("city")));
        appendAddressPart(sb, str(addressSnapshot.get("district")));
        appendAddressPart(sb, str(addressSnapshot.get("detailAddress")));
        return sb.length() == 0 ? null : sb.toString();
    }

    private static void appendAddressPart(StringBuilder sb, String value){
        if(StringUtils.isBlank(value)){
            return;
        }
        sb.append(value.trim());
    }

    private static String firstCartSizeCode(List<CoscartEntity> carts){
        if(carts == null || carts.isEmpty()){
            return null;
        }
        for(CoscartEntity cart : carts){
            if(cart == null || StringUtils.isBlank(cart.getCustomSnapshotJson())){
                continue;
            }
            try {
                JSONObject root = JSON.parseObject(cart.getCustomSnapshotJson());
                String sizeCode = str(root.get("sizeCode"));
                if(StringUtils.isNotBlank(sizeCode)){
                    return sizeCode;
                }
            } catch (Exception ignore){
                // ignore parse error
            }
        }
        return null;
    }

    private static Object pick(Map<String,Object> source, String... keys){
        if(source == null || keys == null){
            return null;
        }
        for(String key : keys){
            if(source.containsKey(key)){
                return source.get(key);
            }
        }
        return null;
    }

    private static void putIfNotBlank(Map<String,Object> target, String key, Object value){
        String text = str(value);
        if(StringUtils.isNotBlank(text)){
            target.put(key, text.trim());
        }
    }

    private static boolean isAllowedSizeCode(String sizeCode){
        if(StringUtils.isBlank(sizeCode)){
            return false;
        }
        String safe = sizeCode.trim().toUpperCase(Locale.ROOT);
        return "S".equals(safe) || "M".equals(safe) || "L".equals(safe) || "XL".equals(safe);
    }

    private static String firstNonBlank(String... values){
        if(values == null){
            return null;
        }
        for(String value : values){
            if(StringUtils.isNotBlank(value)){
                return value.trim();
            }
        }
        return null;
    }
    private static Long parseLong(Object value){
        if(value == null) return null;
        if(value instanceof Number) return ((Number)value).longValue();
        try {
            return Long.valueOf(String.valueOf(value));
        } catch (Exception ignore) {
            return null;
        }
    }

    private static int parseInt(Object value, int defaultValue){
        if(value == null) return defaultValue;
        if(value instanceof Number) return ((Number)value).intValue();
        try {
            return Integer.parseInt(String.valueOf(value));
        } catch (Exception ignore) {
            return defaultValue;
        }
    }

    private static String str(Object value){
        return value == null ? null : String.valueOf(value);
    }
}
