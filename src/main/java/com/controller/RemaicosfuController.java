package com.controller;

import com.annotation.IgnoreAuth;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.entity.RemaicosfuEntity;
import com.entity.StoreupEntity;
import com.entity.view.RemaicosfuView;
import com.service.RemaicosfuService;
import com.service.StoreupService;
import com.utils.MPUtil;
import com.utils.PageUtils;
import com.utils.R;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 热卖cos服
 * 后端接口
 * @author 
 * @email 
 * @date 2023-03-07 22:24:10
 */
@RestController
@RequestMapping("/remaicosfu")
public class RemaicosfuController {
    @Autowired
    private RemaicosfuService remaicosfuService;

    @Autowired
    private StoreupService storeupService;

    


    /**
     * 后端列表
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params,RemaicosfuEntity remaicosfu,
                @RequestParam(required = false) Double fuzhuangjiagestart,
                @RequestParam(required = false) Double fuzhuangjiageend,
		HttpServletRequest request){
        EntityWrapper<RemaicosfuEntity> ew = new EntityWrapper<RemaicosfuEntity>();
                if(fuzhuangjiagestart!=null) ew.ge("fuzhuangjiage", fuzhuangjiagestart);
                if(fuzhuangjiageend!=null) ew.le("fuzhuangjiage", fuzhuangjiageend);

		PageUtils page = remaicosfuService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, remaicosfu), params), params));

        return R.ok().put("data", page);
    }
    
    /**
     * 前端列表
     */
	@IgnoreAuth
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params,RemaicosfuEntity remaicosfu, 
                @RequestParam(required = false) Double fuzhuangjiagestart,
                @RequestParam(required = false) Double fuzhuangjiageend,
		HttpServletRequest request){
        EntityWrapper<RemaicosfuEntity> ew = new EntityWrapper<RemaicosfuEntity>();
                if(fuzhuangjiagestart!=null) ew.ge("fuzhuangjiage", fuzhuangjiagestart);
                if(fuzhuangjiageend!=null) ew.le("fuzhuangjiage", fuzhuangjiageend);

		PageUtils page = remaicosfuService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, remaicosfu), params), params));
        return R.ok().put("data", page);
    }

	/**
     * 列表
     */
    @RequestMapping("/lists")
    public R list( RemaicosfuEntity remaicosfu){
       	EntityWrapper<RemaicosfuEntity> ew = new EntityWrapper<RemaicosfuEntity>();
      	ew.allEq(MPUtil.allEQMapPre( remaicosfu, "remaicosfu")); 
        return R.ok().put("data", remaicosfuService.selectListView(ew));
    }

	 /**
     * 查询
     */
    @RequestMapping("/query")
    public R query(RemaicosfuEntity remaicosfu){
        EntityWrapper< RemaicosfuEntity> ew = new EntityWrapper< RemaicosfuEntity>();
 		ew.allEq(MPUtil.allEQMapPre( remaicosfu, "remaicosfu")); 
		RemaicosfuView remaicosfuView =  remaicosfuService.selectView(ew);
		return R.ok("查询热卖cos服成功").put("data", remaicosfuView);
    }
	
    /**
     * 后端详情
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        RemaicosfuEntity remaicosfu = remaicosfuService.selectById(id);
		remaicosfu.setClicknum(remaicosfu.getClicknum()+1);
		remaicosfu.setClicktime(new Date());
		remaicosfuService.updateById(remaicosfu);
        return R.ok().put("data", remaicosfu);
    }

    /**
     * 前端详情
     */
	@IgnoreAuth
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id){
        RemaicosfuEntity remaicosfu = remaicosfuService.selectById(id);
		remaicosfu.setClicknum(remaicosfu.getClicknum()+1);
		remaicosfu.setClicktime(new Date());
		remaicosfuService.updateById(remaicosfu);
        return R.ok().put("data", remaicosfu);
    }
    


    /**
     * 赞或踩
     */
    @RequestMapping("/thumbsup/{id}")
    public R vote(@PathVariable("id") String id,String type){
        RemaicosfuEntity remaicosfu = remaicosfuService.selectById(id);
        if(type.equals("1")) {
        	remaicosfu.setThumbsupnum(remaicosfu.getThumbsupnum()+1);
        } else {
        	remaicosfu.setCrazilynum(remaicosfu.getCrazilynum()+1);
        }
        remaicosfuService.updateById(remaicosfu);
        return R.ok("投票成功");
    }

    /**
     * 后端保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody RemaicosfuEntity remaicosfu, HttpServletRequest request){
    	remaicosfu.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());
    	//ValidatorUtils.validateEntity(remaicosfu);
        remaicosfuService.insert(remaicosfu);
        return R.ok();
    }
    
    /**
     * 前端保存
     */
    @RequestMapping("/add")
    public R add(@RequestBody RemaicosfuEntity remaicosfu, HttpServletRequest request){
    	remaicosfu.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());
    	//ValidatorUtils.validateEntity(remaicosfu);
        remaicosfuService.insert(remaicosfu);
        return R.ok();
    }



    /**
     * 修改
     */
    @RequestMapping("/update")
    @Transactional
    public R update(@RequestBody RemaicosfuEntity remaicosfu, HttpServletRequest request){
        //ValidatorUtils.validateEntity(remaicosfu);
        remaicosfuService.updateById(remaicosfu);//全部更新
        return R.ok();
    }


    

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
        remaicosfuService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }
    
    /**
     * 提醒接口
     */
	@RequestMapping("/remind/{columnName}/{type}")
	public R remindCount(@PathVariable("columnName") String columnName, HttpServletRequest request, 
						 @PathVariable("type") String type,@RequestParam Map<String, Object> map) {
		map.put("column", columnName);
		map.put("type", type);
		
		if(type.equals("2")) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Calendar c = Calendar.getInstance();
			Date remindStartDate = null;
			Date remindEndDate = null;
			if(map.get("remindstart")!=null) {
				Integer remindStart = Integer.parseInt(map.get("remindstart").toString());
				c.setTime(new Date()); 
				c.add(Calendar.DAY_OF_MONTH,remindStart);
				remindStartDate = c.getTime();
				map.put("remindstart", sdf.format(remindStartDate));
			}
			if(map.get("remindend")!=null) {
				Integer remindEnd = Integer.parseInt(map.get("remindend").toString());
				c.setTime(new Date());
				c.add(Calendar.DAY_OF_MONTH,remindEnd);
				remindEndDate = c.getTime();
				map.put("remindend", sdf.format(remindEndDate));
			}
		}
		
		Wrapper<RemaicosfuEntity> wrapper = new EntityWrapper<RemaicosfuEntity>();
		if(map.get("remindstart")!=null) {
			wrapper.ge(columnName, map.get("remindstart"));
		}
		if(map.get("remindend")!=null) {
			wrapper.le(columnName, map.get("remindend"));
		}


		int count = remaicosfuService.selectCount(wrapper);
		return R.ok().put("count", count);
	}
	
	/**
     * 前端智能排序
     */
	@IgnoreAuth
    @RequestMapping("/autoSort")
    public R autoSort(@RequestParam Map<String, Object> params,RemaicosfuEntity remaicosfu, HttpServletRequest request,String pre){
        EntityWrapper<RemaicosfuEntity> ew = new EntityWrapper<RemaicosfuEntity>();
        Map<String, Object> newMap = new HashMap<String, Object>();
        Map<String, Object> param = new HashMap<String, Object>();
		Iterator<Map.Entry<String, Object>> it = param.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Object> entry = it.next();
			String key = entry.getKey();
			String newKey = entry.getKey();
			if (pre.endsWith(".")) {
				newMap.put(pre + newKey, entry.getValue());
			} else if (StringUtils.isEmpty(pre)) {
				newMap.put(newKey, entry.getValue());
			} else {
				newMap.put(pre + "." + newKey, entry.getValue());
			}
		}
		params.put("sort", "clicknum");
        params.put("order", "desc");
		PageUtils page = remaicosfuService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, remaicosfu), params), params));
        return R.ok().put("data", page);
    }


    /**
     * 协同算法（按收藏推荐）
     */
    @RequestMapping("/autoSort2")
    public R autoSort2(@RequestParam Map<String, Object> params,RemaicosfuEntity remaicosfu, HttpServletRequest request){
        String userId = request.getSession().getAttribute("userId").toString();
        String inteltypeColumn = "fuzhuangkuanshi";
        List<StoreupEntity> storeups = storeupService.selectList(new EntityWrapper<StoreupEntity>().eq("type", 1).eq("userid", userId).eq("tablename", "remaicosfu").orderBy("addtime", false));
        List<String> inteltypes = new ArrayList<String>();
        Integer limit = params.get("limit")==null?10:Integer.parseInt(params.get("limit").toString());
        List<RemaicosfuEntity> remaicosfuList = new ArrayList<RemaicosfuEntity>();
        //去重
        if(storeups!=null && storeups.size()>0) {
            for(StoreupEntity s : storeups) {
                remaicosfuList.addAll(remaicosfuService.selectList(new EntityWrapper<RemaicosfuEntity>().eq(inteltypeColumn, s.getInteltype())));
            }
        }
        EntityWrapper<RemaicosfuEntity> ew = new EntityWrapper<RemaicosfuEntity>();
        params.put("sort", "id");
        params.put("order", "desc");
        PageUtils page = remaicosfuService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, remaicosfu), params), params));
        List<RemaicosfuEntity> pageList = (List<RemaicosfuEntity>)page.getList();
        if(remaicosfuList.size()<limit) {
            int toAddNum = (limit-remaicosfuList.size())<=pageList.size()?(limit-remaicosfuList.size()):pageList.size();
            for(RemaicosfuEntity o1 : pageList) {
                boolean addFlag = true;
                for(RemaicosfuEntity o2 : remaicosfuList) {
                    if(o1.getId().intValue()==o2.getId().intValue()) {
                        addFlag = false;
                        break;
                    }
                }
                if(addFlag) {
                    remaicosfuList.add(o1);
                    if(--toAddNum==0) break;
                }
            }
        } else if(remaicosfuList.size()>limit) {
            remaicosfuList = remaicosfuList.subList(0, limit);
        }
        page.setList(remaicosfuList);
        return R.ok().put("data", page);
    }







}
