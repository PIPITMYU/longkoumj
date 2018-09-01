package com.yzt.logic.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.yzt.logic.mj.domain.Action;
import com.yzt.logic.mj.domain.Player;
import com.yzt.logic.mj.domain.RoomResp;
import com.yzt.logic.util.JudegHu.checkHu.Hulib;
import com.yzt.logic.util.JudegHu.checkHu.TableMgr;
import com.yzt.logic.util.redis.RedisUtil;

/**
 * 
 * @author wsw_007
 *
 */
public class MahjongUtils {

	static {
		// 加载胡的可能
		TableMgr.getInstance().load();
	}

	/**
	 * 获得所需要的牌型(龙口麻将) 并打乱牌型
	 * 
	 * @return
	 */
	public static List<Integer> getPais() {
		// 1-9万 ,10-18饼,19-27条,28-31 东南西北,32-34 中发白
		ArrayList<Integer> pais = new ArrayList<Integer>();
		for (int j = 0; j < 4; j++) {
			for (int i = 1; i <= 34; i++) {
				pais.add(i);
			}
		}
		// 2.洗牌
		Collections.shuffle(pais);
		return pais;
	}

	/**
	 * 给手牌排序
	 * 
	 * @param pais
	 * @return
	 */
	public static List<Integer> paiXu(List<Integer> pais) {
		Collections.sort(pais);
		return pais;
	}

	/**
	 * 删除用户指定的一张牌
	 * 
	 * @param currentPlayer
	 * @return
	 */
	public static void removePai(Player currentPlayer, Integer action) {
		Iterator<Integer> pai = currentPlayer.getCurrentMjList().iterator();
		while (pai.hasNext()) {
			Integer item = pai.next();
			if (item == action) {
				pai.remove();
				break;
			}
		}
	}
	
	/**
	 * 删除集合中指定的一张牌
	 * 
	 * @param currentPlayer
	 * @return
	 */
	public static void removePai(List<Integer> list, Integer action) {
		Iterator<Integer> pai = list.iterator();
		while (pai.hasNext()) {
			Integer item = pai.next();
			if (item.equals(action)) {
				pai.remove();
				break;
			}
		}
	}

	/**
	 * 
	 * @param room
	 *            房间
	 * @param currentPlayer
	 *            当前操作的玩家
	 * @return 返回需要通知的操作的玩家ID
	 */
	public static Long nextActionUserId(RoomResp room, Long lastUserId) {
		Long[] playerIds = room.getPlayerIds();

		for (int i = 0; i < playerIds.length; i++) {
			if (lastUserId.equals(playerIds[i])) {
				if (i == playerIds.length - 1) { // 如果是最后 一个,则取第一个.
					return playerIds[0];
				} else {
					return playerIds[i + 1];
				}
			}
		}
		return -100l;
	}

	/**
	 * 定混牌//上滚定混
	 * 
	 * @param pais  第一次发牌后剩余的牌
	 * @return
	 */
	public static Integer dingHunPai(List<Integer> pais, RoomResp room) {
		List<Integer> faPai = faPai(pais, 1);
		Integer hunPai = faPai.get(0);
		room.setDingHunPai(hunPai);
		if (hunPai == 31) {
			hunPai = 28;
		}else if(hunPai == 34){
			hunPai = 32;
		}else if (hunPai % 9 == 0) {// 上滚定混
			hunPai = hunPai - 8;
		} else { // 正常混牌
			hunPai = hunPai + 1;
		}
		return hunPai;
	}

	/**
	 * 龙口麻将胡牌规则 
	 * 只能碰牌,不能吃牌
	 * 只能自摸,不能点炮
	 * 有七小对,杠上开花,飘胡,混吊,清一色(算平胡的分)
	 *
	 * @return true 为可以胡牌牌型
	 */

	public static boolean isShouBaYi(Player p, Integer type) {
		if (type == Cnst.CHECK_TYPE_BIERENCHU) {
			if (p.getCurrentMjList().size() <= 4) {
				return false;
			}
			return true;
		}
		if (type == Cnst.CHECK_TYPE_ZIJIMO || type == Cnst.CHECK_TYPE_HAIDIANPAI) {
			if (p.getCurrentMjList().size() <= 5) {
				return false;
			}
			return true;
		}
		return false;
	}
	

	/**
	 * 龙口 牌型是否是清一色 
	 * 
	 * @param p玩家
	 * @return
	 */
	public static boolean isQingYiSe(Player p, RoomResp room, List<Integer> list) {
		//类型有4种,0是万 ,1是饼,2是条,3是风
		Integer leixing = 0;
		List<Integer> newList = getNewList(list);
		Iterator<Integer> iter1 = newList.iterator();
		while (iter1.hasNext()) {
			Integer item = iter1.next();
			if (item == room.getHunPai()) {
				iter1.remove();
			}
		}
		Collections.sort(newList);
		Integer pai = newList.get(0);
		leixing = (pai - 1) / 9;
		
		for (Integer shouPai : newList) {
			// 要检测的类型不再相同,直接返回
			if (leixing != (shouPai - 1) / 9) {
				return false;
			}
		}
		// 判断有动作的牌类型是否相同
		List<Action> actionList = p.getActionList();
		if (actionList.size() > 0) {
			for (Action action : actionList) {
				if (leixing != (action.getExtra() - 1) / 9) {
					return false;
				}
			}
		}
		return true;
	}
	/**
	 * * 检测动作集合
	 * 只能杠,暗杆,自摸胡.
	 * @param p
	 * @param pai
	 * @param room
	 * @param type 是否是自摸
	 * @param kaiJu 是否是开局
	 * @return
	 */
	public static List<Integer> checkActionList(Player p, Integer pai, RoomResp room, Integer type,Integer dongzuo) {
		
		Integer hunPai = room.getHunPai();
		List<Integer> actionList = new ArrayList<Integer>();
		
		//有四个胡牌,直接提示胡
		if( type == Cnst.CHECK_TYPE_ZIJIMO){
			int size = p.getCurrentMjList().size();
			int sizeNum = 0;
			for (int i = 0; i < size; i++) {
				if(p.getCurrentMjList().get(i).equals(hunPai)){
					sizeNum += 1;
				}
			}
			if(sizeNum == 4){
				actionList.add(500);
				actionList.add(0);
				return actionList;
			}
			
			//七小对也可以胡
			Integer hunNum = 0;
			List<Integer> newList = getNewList(p.getCurrentMjList());
			for (Integer i : p.getCurrentMjList()) {
				if (i == room.getHunPai()) {
					hunNum++;
				}
			}
			Iterator<Integer> it = newList.iterator();
			while (it.hasNext()) {
				Integer x = it.next();
				if (x == room.getHunPai()) {
					it.remove();
				}
			}
			boolean qiXiaoDui = isQiXiaoDui(p, room, newList, hunNum);
			if(qiXiaoDui){
				actionList.add(500);
				actionList.add(0);
				return actionList;
			}
		}
		
				
		//长毛组MAP
		Map<Integer, List<Integer>> zhangMaoZuHeMap = Cnst.getZhangMaoZuHeMap();
		//长毛组list
		ArrayList<List<Integer>> zhangmaozulist = Cnst.getZhangmaozulist();
		//只有开局才可以亮长毛 //出过牌.默认为false ,出过牌以后,就为true.
		if(!p.getIsChuGuoPai() && type == Cnst.CHECK_TYPE_ZIJIMO && !dongzuo.equals(-4)){
			for (int i = 0; i < zhangmaozulist.size(); i++) {
				if(p.getCurrentMjList().containsAll(zhangmaozulist.get(i))){
					for (Integer key : zhangMaoZuHeMap.keySet()) {
						if(zhangmaozulist.get(i).containsAll(zhangMaoZuHeMap.get(key))){
							actionList.add(key);//亮毛选项140
							if(!actionList.contains(-4)){
								actionList.add(-4);//过选项
							}
							return actionList;
						}
					}
				}
			}
		}
		//检测有没有补毛
//		if(p.getActionList().size() != 0 && type == Cnst.CHECK_TYPE_ZIJIMO){
//			List<Action> actionList2 = p.getActionList();
//			for (Action action : actionList2) {
//				if(action.getType() == Cnst.ACTION_TYPE_LIANG_MAO ){ 
//					Integer actionId = action.getActionId();
//					if(actionId < 140){
//						continue;
//					}
//					List<Integer> list = zhangMaoZuHeMap.get(actionId);
//					//手牌里面有亮毛组合的牌
//					List<Integer> shouPai = p.getCurrentMjList();
//					for (Integer maoPai : shouPai) {
//						if(list.contains(maoPai)){//32
//							Integer mao = Cnst.maoDuiPai.get(maoPai);//给前端返回长毛的action编码
//							actionList.add(mao);
//						}
//					}
//				}
//			}
//		}
		//新补毛检测,1条可以任意长毛,东南西北组合中,可以任意长毛.
		if(p.getActionList().size() != 0 && type == Cnst.CHECK_TYPE_ZIJIMO){
			List<Action> actionList2 = p.getActionList();
			for (Action action : actionList2) {
				if(action.getType() == Cnst.ACTION_TYPE_LIANG_MAO ){ 
					Integer actionId = action.getActionId();
					if(actionId < 140){
						continue;
					}
					//看亮毛集合是否包含风牌,如果有,任意一个风牌,都可以长毛.
 					boolean isFeng = false;
					boolean isZhong = false;
					List<Integer> list = zhangMaoZuHeMap.get(actionId);
					for (int i = 0; i < list.size(); i++) {
						if(list.get(i) >= 28 && list.get(i) <=31){
							isFeng = true;
						}
						if(list.get(i).equals(33) || list.get(i).equals(34) || list.get(i).equals(32)){ //只要包含一个发的组合,中必定可以长
							isZhong = true;
						}
					}
					//手牌里面有亮毛组合的牌
					List<Integer> shouPai = p.getCurrentMjList();
					for (Integer maoPai : shouPai) {
						//风牌可以加入任意风牌组.
						if(isFeng && maoPai >= 28 && maoPai <= 31){
							Integer mao = Cnst.maoDuiPai.get(maoPai);//给前端返回长毛的action编码
							actionList.add(mao);
						}
						//一条可以加入任意风牌组
						if(maoPai.equals(19)){
							Integer mao = Cnst.maoDuiPai.get(maoPai);//给前端返回长毛的action编码
							actionList.add(mao);
						}
						
						//红中长毛
						if(isZhong && maoPai.equals(32)){
							Integer mao = Cnst.maoDuiPai.get(maoPai);//给前端返回长毛的action编码
							actionList.add(mao);
						}
						//百长毛
						if(isZhong && maoPai.equals(33)){
							Integer mao = Cnst.maoDuiPai.get(maoPai);//给前端返回长毛的action编码
							actionList.add(mao);
						}
						//发财长毛
						if(isZhong && maoPai.equals(34)){
							Integer mao = Cnst.maoDuiPai.get(maoPai);//给前端返回长毛的action编码
							actionList.add(mao);
						}
						if(list.contains(maoPai)){
							Integer mao = Cnst.maoDuiPai.get(maoPai);//给前端返回长毛的action编码
							actionList.add(mao);
						}
					}
				}
			}
		}
		
		//如果长毛组合中,有混牌,并且房间类型是不允许握毛,那么有混牌的时候,应该自动长毛了.
		if(p.getIsLiangMaoYouHun() && room.getWoMaoType() == 2){
			List<Action> actionList2 = p.getActionList();
			for (Action action : actionList2) {
				if(action.getType() == Cnst.ACTION_TYPE_LIANG_MAO ){ 
					Integer actionId = action.getActionId();
					if(actionId < 140){
						continue;
					}
					List<Integer> list = zhangMaoZuHeMap.get(actionId);
					//手牌里面有亮毛组合的牌
					List<Integer> shouPai = p.getCurrentMjList();
					for (Integer maoPai : shouPai) {
						if(list.contains(maoPai) && maoPai.equals(hunPai)){
							Integer mao = Cnst.maoDuiPai.get(maoPai);//给前端返回长毛的action编码
							actionList.add(mao);
							Set<Integer> set = new HashSet<Integer>(actionList);
							ArrayList<Integer> arrayList = new ArrayList<Integer>(set);
							return arrayList;
						}
					}
				}
			}
		}
		// 2,自摸的时候,检测能不能暗杠.
		if (type == Cnst.CHECK_TYPE_ZIJIMO && pai != null) {
			List<Integer> checkAnGang = checkAnGang(p, hunPai,room);
			for (int i = 0; i < checkAnGang.size(); i++) {
				actionList.add(checkAnGang.get(i));
			}
			//自摸的时候,检测能不能碰杠
			List<Integer> pengGang = checkPengGang(p, pai);
			if (pengGang.size() != 0) {
				actionList.addAll(pengGang);
			}
		}

		//自摸检测能不能胡
		if(type == Cnst.CHECK_TYPE_ZIJIMO && pai != null){
			int[] huPaiZu1 = getCheckHuPai(p.getCurrentMjList(), pai);
     			if (Hulib.getInstance().get_hu_info(huPaiZu1, 34, hunPai - 1)) { 
				actionList.add(500);
			}
		}

		//检测能不能碰
		if (type == Cnst.CHECK_TYPE_BIERENCHU && checkPeng(p, pai, hunPai,room)) {
			Integer peng = peng(p, pai);
			actionList.add(peng);
		}
		// 检测别人出牌的时候,能不能点杠.
		if (type == Cnst.CHECK_TYPE_BIERENCHU && checkGang(p, pai,room)) {
			Integer gang = gang(p, pai, false,room);
			actionList.add(gang);
		}

		// 可以杠开
		if (room.getLastAction() != null && room.getLastAction() >= 90 && room.getLastAction() <= 124 && pai != null) {
			int[] huPaiZu = new int[34];
			huPaiZu = getCheckHuPai(p.getCurrentMjList(), pai);
			if (Hulib.getInstance().get_hu_info(huPaiZu, 34, hunPai - 1)) { 
				if(!actionList.contains(500)){
					actionList.add(500);
				}
			}
		}
		
		if(actionList.size() != 0){
			//(特殊需要,不能握毛的情况下,只有一个长毛按钮,没有过)
			if(room.getWoMaoType() == 2){
				int x = 0; //临时长毛选项
				for (int i = 0; i < actionList.size(); i++) {
					if(actionList.get(i) >= 160 && actionList.get(i) <= 172){
						x++;
					}
				}
				//动作集合不全是长毛的情况,也就是有吃碰杠.需要加过
				if(x != actionList.size()){
					actionList.add(0);
				}
			}else {
				actionList.add(0);
			}
		}
		//出重
		Set<Integer> set = new HashSet<Integer>(actionList);
		ArrayList<Integer> arrayList = new ArrayList<Integer>(set);
		return arrayList;
	}

	/**
	 * 检测能不能碰完以后再开杠.
	 * 
	 * @param p
	 * @return
	 */
	private static List<Integer> checkPengGang(Player p, Integer pai) {
		List<Action> actionList = p.getActionList();// 统计用户所有动作 (吃碰杠等)
		List<Integer> newList = getNewList(p.getCurrentMjList());
		List<Integer> gangList = new ArrayList<Integer>();
		for (int i = 0; i < actionList.size(); i++) {
			if (actionList.get(i).getType() == 2) {
				for (int m = 0; m < newList.size(); m++) {
					if (newList.get(m) == actionList.get(i).getExtra()) {
						gangList.add(newList.get(m) + 90);
					}
				}
			}
		}
		return gangList;
	}

	/**
	 * 混牌的数量
	 * 
	 * @return
	 */
	public static Integer hunNum(Player p, Integer pai) {
		Integer num = 0;
		for (int i = 0; i < p.getCurrentMjList().size(); i++) {
			if (p.getCurrentMjList().get(i) == pai) {
				num = num + 1;
			}
		}
		return num;
	}


	/**
	 * 牌型是否有刻牌(三个一样或者四个一样)
	 * 
	 * @param p
	 * @return true 是
	 */
	public static boolean isKePai(Player p, Integer hunPai, List<Integer> newList) {
		// 检测动作里面是否有刻
		List<Action> actionList = p.getActionList();
		// 1吃 2碰 3点杠 4碰杠 5暗杠
		for (Action action : actionList) {
			if (action.getType() != 1) {
				return true;
			}
		}
		// 检测手中有没有刻
		Set<Integer> distinct = new HashSet<Integer>();
		for (Integer integer : newList) {
			distinct.add(integer);
		}
		// 手牌中是否有3张,这3张移除必须能胡才行 比如12333
		Integer hunNum = hunNum(p, hunPai);
		int num = 0;
		for (Integer distinctPai : distinct) {
			num = 0;
			for (Integer p1 : newList) {
				if (distinctPai.equals(p1)) {
					num++;
				}
			}
			if (num >= 3) {
				int[] huPaiZu = getCheckHuPai(newList, null);
				// 将这3张牌移除
				huPaiZu[distinctPai - 1] = num - 3;
				if (Hulib.getInstance().get_hu_info(huPaiZu, 34, hunPai - 1)) {
					return true;
				}
			} else if (hunNum + num >= 3) {
				int[] huPaiZu = getCheckHuPai(newList, null);
				huPaiZu[hunPai - 1] = hunNum - (3 - num);
				huPaiZu[distinctPai - 1] = 0;
				if (Hulib.getInstance().get_hu_info(huPaiZu, 34, hunPai - 1)) {
					return true;
				}
			}
		}
		return false;
	}
	

	/***
	 * 根据出的牌 设置下个动作人和玩家
	 * 
	 * @param players
	 * @param room
	 * @param pai
	 */
	public static void getNextAction(List<Player> players, RoomResp room, Integer pai) {
		Integer maxAction = 0;
		Long nextActionUserId = -1L;
		List<Integer> nextAction = new ArrayList<Integer>();
		int index = -1;
		Long[] playIds = room.getPlayerIds();
		for (int i = 0; i < playIds.length; i++) {
			if (playIds[i].equals(room.getLastChuPaiUserId())) {
				index = i + 1;
				if (index == 4) {
					index = 0;
				}
				break;
			}
		}
		Long xiaYiJia = playIds[index];
		for (Player p : players) {
			if (!room.getGuoUserIds().contains(p.getUserId())) {
				List<Integer> checkActionList = checkActionList(p, pai, room, Cnst.CHECK_TYPE_BIERENCHU,0);
				if (checkActionList.size() == 0) {
					// 玩家没动作
					room.getGuoUserIds().add(p.getUserId());
				} else {
					Collections.sort(checkActionList);
					if (checkActionList.get(checkActionList.size() - 1) > maxAction) {
						nextActionUserId = p.getUserId();
						nextAction = checkActionList;
						maxAction = checkActionList.get(checkActionList.size() - 1);
					}
				}
			}
		}
		// 如果都没可执行动作 下一位玩家请求发牌
		if (maxAction == 0) {
			nextAction.add(-1);
			room.setNextAction(nextAction);
			// 取到上个出牌人的角标 下一位来发牌
			room.setNextActionUserId(xiaYiJia);
		} else {
			room.setNextAction(nextAction);
			room.setNextActionUserId(nextActionUserId);
		}

	}

	/**
	 * 检查玩家能不能碰
	 * 
	 * @param p
	 * @param Integer
	 *            peng 要碰的牌
	 * @return
	 */
	public static boolean checkPeng(Player p, Integer peng, Integer hunPai, RoomResp room) {
		int num = 0;
		for (Integer i : p.getCurrentMjList()) {
			if (i == peng) {
				num++;
			}
		}
		if (num >= 2 && !room.getDingHunPai().equals(peng)) {
			return true;
		}
		return false;
	}

	/**
	 * //与吃的那个牌能组合的List
	 * 
	 * @param p
	 * @param chi
	 * @return
	 */
	public static List<Integer> reChiList(Integer action, Integer chi) {
		ArrayList<Integer> arrayList = new ArrayList<Integer>();
		for (int i = 35; i <= 56; i++) {
			if (i == action) {
				int[] js = Cnst.chiMap.get(action);
				for (int j = 0; j < js.length; j++) {
					if (js[j] != chi) {
						arrayList.add(js[j]);
					}
				}
			}
		}
		return arrayList;
	}

	/**
	 * 执行动作吃! 返回原本手里的牌
	 * 
	 * @param p
	 * @param chi
	 * @return
	 */
	public static List<Integer> chi(Player p, Integer chi, Integer hunPai) {
		List<Integer> shouPai = getNoHunList(p.getCurrentMjList(), hunPai);
		Set<Integer> set = new HashSet<Integer>();
		List<Integer> reList = new ArrayList<Integer>();
		boolean a = false; // x<x+1<x+2
		boolean b = false; // x-1<x<x+1
		boolean c = false; // x-2<x-1<x

		// 万
		if (chi < 10) { // 基数34
			List<Integer> arr = new ArrayList<Integer>();
			arr.add(chi + 1);
			arr.add(chi + 2);
			if (shouPai.containsAll(arr)) {
				a = true;
			}
			List<Integer> arr1 = new ArrayList<Integer>();
			arr1.add(chi - 1);
			arr1.add(chi + 1);
			if (shouPai.containsAll(arr1)) {
				b = true;
			}
			List<Integer> arr2 = new ArrayList<Integer>();
			arr2.add(chi - 1);
			arr2.add(chi - 2);
			if (shouPai.containsAll(arr2)) {
				c = true;
			}

			if (a && chi != 9 && chi != 8) {
				set.add(34 + chi);
			}
			if (b && chi != 9) {
				set.add(33 + chi);
			}
			if (c) {
				set.add(32 + chi);
			}

			// 饼
		} else if (chi >= 10 && chi <= 18) { // 基数32
			List<Integer> arr = new ArrayList<Integer>();
			arr.add(chi + 1);
			arr.add(chi + 2);
			if (shouPai.containsAll(arr)) {
				a = true;
			}
			List<Integer> arr1 = new ArrayList<Integer>();
			arr1.add(chi - 1);
			arr1.add(chi + 1);
			if (shouPai.containsAll(arr1)) {
				b = true;
			}
			List<Integer> arr2 = new ArrayList<Integer>();
			arr2.add(chi - 1);
			arr2.add(chi - 2);
			if (shouPai.containsAll(arr2)) {
				c = true;
			}
			if (a & chi != 18 && chi != 17) {
				set.add(32 + chi);
			}
			if (b && chi != 10 && chi != 18) {
				set.add(31 + chi);
			}
			if (c && chi != 10 && chi != 11) {
				set.add(30 + chi);
			}
			// 条
		} else if (chi >= 19 && chi <= 27) { // 基数30
			List<Integer> arr = new ArrayList<Integer>();
			arr.add(chi + 1);
			arr.add(chi + 2);
			if (shouPai.containsAll(arr)) {
				a = true;
			}
			List<Integer> arr1 = new ArrayList<Integer>();
			arr1.add(chi - 1);
			arr1.add(chi + 1);
			if (shouPai.containsAll(arr1)) {
				b = true;
			}
			List<Integer> arr2 = new ArrayList<Integer>();
			arr2.add(chi - 1);
			arr2.add(chi - 2);
			if (shouPai.containsAll(arr2)) {
				c = true;
			}
			if (a & chi != 26 && chi != 27) {
				set.add(30 + chi);
			}
			if (b && chi != 19 && chi != 27) {
				set.add(29 + chi);
			}
			if (c && chi != 19 && chi != 20) {
				set.add(28 + chi);
			}
		}
		reList.addAll(set);
		return reList;
	}

	/**
	 * 执行动作杠
	 * 
	 * @param p
	 * @param gang
	 * @return
	 */
	public static Integer gang(Player p, Integer gang, Boolean pengGang,RoomResp room) {
		List<Integer> shouPai = p.getCurrentMjList();
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();

		if (pengGang) {
			List<Action> actionList = p.getActionList();// 统计用户所有动作 (吃碰杠等)
			for (int i = 0; i < actionList.size(); i++) {
				if (actionList.get(i).getType() == 2 && actionList.get(i).getExtra() == gang) {
					return 90 + gang;
				}
			}
		}

		for (Integer item : shouPai) {
			if (map.containsKey(item)) {
				map.put(item, map.get(item).intValue() + 1);
			} else {
				map.put(item, new Integer(1));
			}
		}

		Iterator<Integer> keys = map.keySet().iterator();
		while (keys.hasNext()) {
			Integer key = keys.next();
			if (map.get(key).intValue() == 3) { // 控制有几个重复的
				// System.out.println(key + "有重复的:" + map.get(key).intValue() +
				// "个 ");
				if (key == gang) {
					return 90 + gang;
				}
			}
			if(map.get(key).intValue() == 2 && key != room.getHunPai() && key == gang){
				return 90 + gang;
			}
		}

		return -100;
	}

	/**
	 * 执行动作碰
	 * 
	 * @param p
	 * @param peng
	 * @return 行为编码
	 */
	public static Integer peng(Player p, Integer peng) {
		return 56 + peng;
	}

	/**
	 * * 检测玩家能不能吃.10 与19特殊处理
	 * 
	 * @param p
	 * @param chi
	 * @param hunPai
	 *            不能吃
	 * @return
	 */
	public static boolean checkChi(Player p, Integer chi, Integer hunPai) {
		List<Integer> list = getNoHunList(p.getCurrentMjList(), hunPai);
		boolean isChi = false;
		List<Integer> arr = new ArrayList<Integer>();
		arr.add(chi + 1);
		arr.add(chi + 2);
		if (list.containsAll(arr)) {
			isChi = true;
		}
		List<Integer> arr1 = new ArrayList<Integer>();
		List<Integer> arr2 = new ArrayList<Integer>();
		if (chi != 10 && chi != 19) {
			arr1.add(chi - 1);
			arr1.add(chi + 1);
			if (list.containsAll(arr1)) {
				isChi = true;
			}
			arr2.add(chi - 1);
			arr2.add(chi - 2);
			if (list.containsAll(arr2)) {
				isChi = true;
			}
		}
		return isChi;
	}

	/**
	 * 执行暗杠
	 * 
	 * @param p
	 * @return 返回杠的牌
	 */
	public static Integer anGang(Player p) {
		List<Integer> shouPai = p.getCurrentMjList();
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();

		for (Integer item : shouPai) {
			if (map.containsKey(item)) {
				map.put(item, map.get(item).intValue() + 1);
			} else {
				map.put(item, new Integer(1));
			}
		}

		Iterator<Integer> keys = map.keySet().iterator();
		Integer gang = 0;
		while (keys.hasNext()) {
			Integer key = keys.next();
			if (map.get(key).intValue() == 4) { // 控制有几个重复的
				// System.out.println(key + "有重复的:" + map.get(key).intValue() +
				// "个 ");
				gang = key;
			}
		}

		Iterator<Integer> iter1 = p.getCurrentMjList().iterator();
		while (iter1.hasNext()) {
			Integer item = iter1.next();
			if (item == gang) {
				iter1.remove();
			}
		}
		return gang + 90;
	}

	/**
	 * 检查能不能暗杠
	 * 
	 * @param p
	 * @param gang
	 * @return
	 */
	public static List<Integer> checkAnGang(Player p, Integer hunPai,RoomResp room) {
		List<Integer> anGangList = new ArrayList<Integer>();
		List<Integer> shouPai = p.getCurrentMjList();
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();

		for (Integer item : shouPai) {
			if (map.containsKey(item)) {
				map.put(item, map.get(item).intValue() + 1);
			} else {
				map.put(item, new Integer(1));
			}
		}

		Iterator<Integer> keys = map.keySet().iterator();
		while (keys.hasNext()) {
			Integer key = keys.next();
			if (map.get(key).intValue() == 4 && key != hunPai) { // 控制有几个重复的
				anGangList.add(key + 90);
			}
			if(map.get(key).intValue() == 3 && key != hunPai && key == room.getDingHunPai()){
				anGangList.add(key+90);
			}
		}
		return anGangList;
	}

	/**
	 * 检测玩家能不能杠 1,明杠,2暗杠,3 点杠
	 * 
	 * @param p
	 * @return
	 */
	public static boolean checkGang(Player p, Integer gang,RoomResp room) {
		List<Integer> shouPai = p.getCurrentMjList();
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		boolean gangOK = false;
		for (Integer item : shouPai) {
			if (map.containsKey(item)) {
				map.put(item, map.get(item).intValue() + 1);
			} else {
				map.put(item, new Integer(1));
			}
		}

		Iterator<Integer> keys = map.keySet().iterator();
		while (keys.hasNext()) {
			Integer key = keys.next();
			if (map.get(key).intValue() == 3) { // 控制有几个重复的
				// System.out.println(key + "有重复的:" + map.get(key).intValue() +
				// "个 ");
				if (key == gang) {
					// TODO
					// System.out.println("可以杠的牌是:"+key);
					gangOK = true;
				}
			}
			if(map.get(key).intValue() == 2 && key != room.getHunPai() && key == gang && gang == room.getDingHunPai()){
				gangOK = true;
			}
		}
		return gangOK;
	}

	/**
	 * 
	 * @param mahjongs
	 *            房间内剩余麻将的组合
	 * @param num
	 *            发的张数
	 * @return
	 */
	public static List<Integer> faPai(List<Integer> mahjongs, Integer num) {
		// 房间必须有牌，且牌的数量必须大于要发的牌数
		if (mahjongs == null || num == null || mahjongs.size() == 0 || mahjongs.size() < num) {
			return null;
		}
		// ArrayList
		// rrayList内部是使用可増长数组实现的，所以是用get和set方法是花费常数时间的，但是如果插入元素和删除元素，除非插入和删除的位置都在表末尾，否则代码开销会很大，因为里面需要数组的移动。
		List<Integer> result = new ArrayList<>();
		for (int i = mahjongs.size() - 1; i >= 0; i--) {
			result.add(mahjongs.get(i));
			mahjongs.remove(i);
			num--;
			if (num == 0) {
				break;
			}
		}
		return result;
	}
	/**
	 * 测试的发牌数据
	 * @param num
	 * @return
	 */
	public static List<Integer>faPai(Integer num) {
		List<Integer> result = new ArrayList<>();
//		result.add(1);
//		result.add(1);
//		result.add(1);
//		result.add(2);
//		result.add(2);
//		result.add(2);
//		result.add(3);
//		result.add(4);
//		result.add(5);
//		result.add(12);
//		result.add(13);
//		result.add(14);
//		result.add(9);
		
		//七小对
//		result.add(1);
//		result.add(1);
//		result.add(2);
//		result.add(2);
//		result.add(3);
//		result.add(4);
//		result.add(4);
//		result.add(3);
//		result.add(5);
//		result.add(5);
//		result.add(6);
//		result.add(6);
//		result.add(9);
		
		//刻牌.大对胡
//		result.add(1);
//		result.add(1);
//		result.add(5);
//		result.add(2);
//		result.add(2);
//		result.add(2);
//		result.add(3);
//		result.add(3);
//		result.add(3);
//		result.add(4);
//		result.add(4);
//		result.add(4);
//		result.add(9);
		
//		//长毛
//		result.add(1);
//		result.add(1);
//		result.add(1);
//		result.add(1);
//		result.add(2);
//		result.add(2);
//		result.add(3);
//		result.add(4);
//		result.add(5);
//		result.add(32);
//		result.add(33);
//		result.add(34);
//		result.add(9);
		
		//多个长毛
		result.add(28);
		result.add(29);
		result.add(30);
		result.add(1);
		result.add(2);
		result.add(3);
		result.add(4);
		result.add(5);
		result.add(6);
		result.add(4);
		result.add(7);
		result.add(8);
		result.add(8);
		
		//多个长毛
//		result.add(1);
//		result.add(10);
//		result.add(19);
//		result.add(2);
//		result.add(2);
//		result.add(3);
//		result.add(3);
//		result.add(3);
//		result.add(3);
//		result.add(6);
//		result.add(6);
//		result.add(6);
//		result.add(33);
		if(num ==14){
			result.add(28);
		}
		return result;
	}

	/**
	 * 牌型是不是碰碰胡(全是刻牌的牌型)
	 * 
	 * @param p
	 * @param hunNum
	 * @param newList
	 * @return
	 */
	public static boolean isPengPengHu(Player p, List<Integer> newList, Integer hunNum) {
		// 检测动作里面是否有刻
		List<Action> actionList = p.getActionList();
		// 1吃 2碰 3点杠 4碰杠 5暗杠
		for (Action action : actionList) {
			if (action.getType() == 1) {
				return false;
			}
		}
		// 检测手牌是不是都是刻
		int[] checkHuPai = getCheckHuPai(newList, null);
		int twoNum = 0;
		int oneNum = 0;
		for (Integer integer : checkHuPai) {
			if (integer == 1) {
				oneNum++;
			} else if (integer == 2) {
				twoNum++;
			} else if (integer == 3) {
			} else if (integer == 4) {
				return false;
			}
		}
		// 两张的需要1张混，1张需要两张混,但是将减少一张混的需求
		if ((twoNum + oneNum * 2 - 1) <= hunNum) {
			return true;
		}

		return false;
	}

	/**
	 * check peng
	 * 
	 * @param newList
	 * @param hunNum
	 * @return
	 */
	public static boolean isRally(List<Integer> newList, Integer hunNum) {
		// 检测手牌是不是都是刻
		int[] checkHuPai = getCheckHuPai(newList, null);
		int twoNum = 0;
		int oneNum = 0;
		for (Integer integer : checkHuPai) {
			if (integer == 1) {
				oneNum++;
			} else if (integer == 2) {
				twoNum++;
			} else if (integer == 3) {
			} else if (integer == 4) {
				return false;
			}
		}
		// 两张的需要1张混，1张需要两张混,但是将减少一张混的需求
		if ((twoNum + oneNum * 2 - 1) <= hunNum) {
			return true;
		}
		return false;
	}

	/**
	 * 返回一个新的集合
	 * 
	 * @param old
	 * @return
	 */
	static List<Integer> getNewList(List<Integer> old) {
		List<Integer> newList = new ArrayList<Integer>();
		if (old != null && old.size() > 0) {
			for (Integer pai : old) {
				newList.add(pai);
			}
		}
		return newList;
	}

	/**
	 * 龙口麻将胡牌规则 
	 * 只能碰牌,不能吃牌
	 * 只能自摸,不能点炮
	 * 有七小对,杠上开花,飘胡,混吊,清一色(算平胡的分)
	 * 
	 * @param p
	 * @return 返回的是分数
	 */
	public static Integer checkHuInfo(Player p, RoomResp room,boolean zhangMao) {
		
		//底分为1.
		Integer fen = 1;
		//番 在龙口项目是1倍的意思,分固定.不用番了.
//		Integer fan = 1;
		// 胡牌信息
		List<Integer> fanInfo = new ArrayList<Integer>();
		
		//得到混牌数量
		Integer hunNum = 0;
		List<Integer> newList = getNewList(p.getCurrentMjList());
		for (Integer i : p.getCurrentMjList()) {
			if (i == room.getHunPai()) {
				hunNum++;
			}
		}
		Iterator<Integer> it = newList.iterator();
		while (it.hasNext()) {
			Integer x = it.next();
			if (x == room.getHunPai()) {
				it.remove();
			}
		}
		
		//有4个混则加10分,直接提示胡
		if(hunNum == 4){
			fanInfo.add(Cnst.SI_HUN_HU);
			p.setFanShu(fanInfo);
			return 10;
		}
		
		// 清一色与平胡都是1番,所以不用考虑清一色的番数.
		if (isQingYiSe(p, room, newList)) {
			p.setQingYiSeNum(p.getQingYiSeNum() + 1);
			fanInfo.add(Cnst.QING_YI_SE);
		}
		
		
		
		//七小对
		Integer qiDuiHunDiaoHu = 0;
		Integer daDuiHunDiaoHu = 0;
		if(isQiXiaoDui(p,room,newList,hunNum)){
			fanInfo.add(Cnst.QI_XIAO_DUI);
			fen = 2;
			qiDuiHunDiaoHu += 1;
		}
		//是否是碰碰胡(飘胡)(特殊要求,有长毛,不算飘)
		if (p.getZhangMaoNum() == 0 && isPengPengHu(p, newList, hunNum)) {
			p.setPengPengHuNum(p.getPengPengHuNum() + 1);
			fen = 2;
			daDuiHunDiaoHu += 1;
			fanInfo.add(Cnst.DA_DUI_HU);
		}
		//混吊胡
		if(isHunDiaoHu(p,room,newList,hunNum)){
			fen = 2;
			fanInfo.add(Cnst.HUN_DIAO_HU);
			qiDuiHunDiaoHu += 1;
			daDuiHunDiaoHu += 1;
		}
		//七对混吊胡
		if(qiDuiHunDiaoHu == 2){
			fen = 4;
			fanInfo.add(Cnst.QI_DUI_HUN_DIAO_HU);
			Iterator<Integer> iterator = fanInfo.iterator();
			while(iterator.hasNext()){
				Integer i = iterator.next();
				if(i == Cnst.HUN_DIAO_HU){
					iterator.remove();
				}
				if(i == Cnst.QI_XIAO_DUI){
					iterator.remove();
				}
			}
		}
		//大对混吊胡
		if(daDuiHunDiaoHu == 2){
			fen = 4;
			fanInfo.add(Cnst.DA_DUI_HUN_DIAO_HU);
			Iterator<Integer> iterator = fanInfo.iterator();
			while(iterator.hasNext()){
				Integer i = iterator.next();
				if(i == Cnst.HUN_DIAO_HU){
					iterator.remove();
				}
				if(i == Cnst.DA_DUI_HU){
					iterator.remove();
				}
			}
		}
		//杠上开花
		if(p.getIsGangkai()){
			fen = fen * 2;
			fanInfo.add(Cnst.GANG_SHANG_KAI_HUA);
		}
		if(fanInfo.size() == 0){
			fanInfo.add(Cnst.PIN_HU);
		}
		p.setFanShu(fanInfo);
		return fen;
	}


	/**
	 * 单调混胡
	 * @param p
	 * @param newList
	 * @param hunNum
	 * @return
	 */
	private static boolean isHunDiaoHu(Player p, RoomResp room,List<Integer> newList, Integer hunNum) {
		Integer hunPai = room.getHunPai();
		if(hunNum == 0){
			return false;
		}
		if(hunNum == 1 && newList.get(newList.size()-1) == hunPai){
			return false;
		}
		Iterator<Integer> iterator = newList.iterator();
		while(iterator.hasNext()){
			Integer hun = iterator.next();
			if(hun == hunPai){
				iterator.remove();
				break;
			}
		}
		int[] removeLastPai = getRemoveLastPai(newList,room.getLastFaPai());
		if(Hulib.getInstance().get_hu_info(removeLastPai, 34, hunPai - 1)){
			return true;
		}
		
		//七小对也可以胡
		Integer hunNum1 = 0;
		List<Integer> newList1 = getNewList(p.getCurrentMjList());
		for (Integer i : p.getCurrentMjList()) {
			if (i == room.getHunPai()) {
				hunNum1++;
			}
		}
		Iterator<Integer> it = newList1.iterator();
		while (it.hasNext()) {
			Integer x = it.next();
			if (x == room.getHunPai()) {
				it.remove();
			}
		}
		boolean qiXiaoDui = isQiXiaoDui(p, room, newList, hunNum);
		if(qiXiaoDui){
			return true;
		}
		return false;
	}

	/**
	 * 胡牌类型是否是七小对
	 * @param p
	 * @param room
	 * @param newList
	 * @return
	 */
	public static boolean isQiXiaoDui(Player p, RoomResp room, List<Integer> newList,Integer hunNum) {
		//如果有吃碰杠,就一定不是七小对
		if(p.getActionList().size() != 0){
			return false;
		}
		//将手牌去除混牌后,转换为数组.
		int[] checkHuPai = getCheckHuPai(newList,null);
		int len = checkHuPai.length;
		int num = 0;
		for (int i = 0; i < len ; i++) {
			if(checkHuPai[i] == 1){
				num++;
			}
			if(checkHuPai[i] >= 3){ //有三个一样的也不对
				return false;
			}
		}
		if(num == 0 || hunNum >= num){
			return true;
		}
		return false;
	}

	/**
	 * 是不是稀胡牌型
	 * 
	 * @param p
	 * @return
	 */
	public static boolean xiHu(Player p, Integer hunNum) {
		if (hunNum != 0) {
			return true;
		}
		return false;
	}

	/**
	 * 从牌桌上,把玩家吃碰杠的牌移除.
	 * 
	 * @param room
	 * @param players
	 */

	public static void removeCPG(RoomResp room, List<Player> players) {
		Player currentP = null;
		for (Player p : players) {
			if (p.getUserId().equals(room.getLastChuPaiUserId())) {
				currentP = p;
				List<Integer> chuList = p.getChuList();
				Iterator<Integer> iterator = chuList.iterator();
				while (iterator.hasNext()) {
					Integer pai = iterator.next();
					if (room.getLastChuPai() == pai) {
						iterator.remove();
						break;
					}
				}
			}
		}
		RedisUtil.updateRedisData(null, currentP);
	}

	/***
	 * 移除动作手牌
	 * 
	 * @param currentMjList
	 * @param chi
	 * @param action
	 * @param type
	 */
	public static void removeActionMj(List<Integer> currentMjList, List<Integer> chi, Integer action, Integer type) {
		Iterator<Integer> it = currentMjList.iterator(); // 遍历手牌,删除碰的牌
		switch (type) {
		case Cnst.ACTION_TYPE_CHI:
			int chi1 = 0;
			int chi2 = 0;
			a: while (it.hasNext()) {
				Integer x = it.next();
				if (x == chi.get(0) && chi1 == 0) {
					it.remove();
					chi1 = 1;
				}
				if (x == chi.get(1) && chi2 == 0) {
					it.remove();
					chi2 = 1;
				}
				if (chi1 == 1 && chi2 == 1) {
					break a;
				}
			}
			break;
		case Cnst.ACTION_TYPE_PENG:
			int num = 0;
			while (it.hasNext()) {
				Integer x = it.next();
				if (x == action - 56) {
					it.remove();
					num = num + 1;
					if (num == 2) {
						break;
					}
				}
			}
			break;
		case Cnst.ACTION_TYPE_ANGANG:
			List<Integer> gangPai = new ArrayList<Integer>();
			gangPai.add(action - 90);
			currentMjList.removeAll(gangPai);
			break;
		case Cnst.ACTION_TYPE_PENGGANG:
			gangPai = new ArrayList<Integer>();
			gangPai.add(action - 90);
			currentMjList.removeAll(gangPai);
			break;
		case Cnst.ACTION_TYPE_DIANGANG:
			gangPai = new ArrayList<Integer>();
			gangPai.add(action - 90);
			currentMjList.removeAll(gangPai);
			break;
		case Cnst.ACTION_TYPE_LIANG_MAO:
			Map<Integer, List<Integer>> zhangMaoZuHeMap = Cnst.getZhangMaoZuHeMap();
			List<Integer> list = zhangMaoZuHeMap.get(action);
			for (int i = 0; i < 3; i++) {
				Iterator<Integer> iterator = currentMjList.iterator();
				while(iterator.hasNext()){
					Integer next = iterator.next();
					if(next == list.get(i)){
						iterator.remove();
						break;
					}
				}
			}
			break;
		default:
			break;
		}
	}

	/***
	 * 获得 检测胡牌的 34位数组 只能胡自摸,不在检测别人打的胡.
	 * 
	 * @param currentList
	 * @param pai
	 * @return
	 */
	public static int[] getCheckHuPai(List<Integer> currentList, Integer pai) {
		int[] checkHuPai = new int[34];
		List<Integer> newList = getNewList(currentList);
//		if (pai != null) {
//			newList.add(pai);
//		}
		for (int i = 0; i < newList.size(); i++) {
			int a = checkHuPai[newList.get(i) - 1];
			checkHuPai[newList.get(i) - 1] = a + 1;
		}
		return checkHuPai;
	}

	/***
	 * 获得 检测胡牌的 34位数组 不包括摸得或者别人打的那张
	 * 
	 * @param currentList
	 * @param pai
	 * @return
	 */
	public static int[] getRemoveLastPai(List<Integer> currentList, Integer pai) {
		int[] checkHuPai = new int[34];
		Boolean hasRemove = false;
		for (int i = 0; i < currentList.size(); i++) {
			if (currentList.get(i) == pai && !hasRemove) {
				hasRemove = true;
				continue;
			}
			int a = checkHuPai[currentList.get(i) - 1];
			checkHuPai[currentList.get(i) - 1] = a + 1;
		}
		return checkHuPai;
	}

	/***
	 * 移除混牌 做判断用
	 * 
	 * @param currList
	 *            手牌
	 * @param hunPai
	 *            混牌
	 * @return
	 */
	public static List<Integer> getNoHunList(List<Integer> currList, Integer hunPai) {
		List<Integer> list = new ArrayList<Integer>();
		for (Integer i : currList) {
			if (i == hunPai) {
				continue;
			}
			list.add(i);
		}
		return list;
	}

}
