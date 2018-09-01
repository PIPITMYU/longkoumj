package com.yzt.logic.util.GameUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.yzt.logic.mj.domain.Action;
import com.yzt.logic.mj.domain.Player;
import com.yzt.logic.mj.domain.RoomResp;
import com.yzt.logic.util.BackFileUtil;
import com.yzt.logic.util.Cnst;
import com.yzt.logic.util.MahjongUtils;
import com.yzt.logic.util.RoomUtil;
import com.yzt.logic.util.redis.RedisUtil;

/**
 * 玩家分的统计
 * 
 * @author wsw_007
 *
 */
public class JieSuan {
	public static void xiaoJieSuan(String roomId) {
		RoomResp room = RedisUtil.getRoomRespByRoomId(roomId);
		List<Player> players = RedisUtil.getPlayerList(room);
		if(room.getHuangZhuang() != null && room.getHuangZhuang()){
			System.out.println("荒庄!不算分");
		}else{//正常结算 
			Player huPlayer = null;
			//先算出所有玩家的杠分,这个杠分是玩家赢其 他玩家的杠分.比如明杠1分,玩家的杠分得到3.
			for (Player player : players) {
				List<Action> actionList = player.getActionList();
				if(actionList.size() != 0){
					for (Action action : actionList) {
						//只有亮过的杠,才算分
						List<Integer> liangList = player.getLiangList();
						if(action.getType() == 4){ //明杠
							player.setGangFen(player.getGangFen() + 3);
							for (Player otherPlayer : players) {
								if(!otherPlayer.getUserId().equals(player.getUserId())){
									otherPlayer.setGangFen(otherPlayer.getGangFen() - 1);
								}
							}
						}else if(action.getType() == 3){//点杠
							player.setGangFen(player.getGangFen() + 3);
							for (Player otherPlayer : players) {
								if(!otherPlayer.getUserId().equals(player.getUserId())){
									otherPlayer.setGangFen(otherPlayer.getGangFen() - 1);
								}
							}
						}
						else if(action.getType() == 5 && liangList.contains(action.getExtra())){//暗杠
							player.setGangFen(player.getGangFen() + 6);
							for (Player otherPlayer : players) {
								if(!otherPlayer.getUserId().equals(player.getUserId())){
									otherPlayer.setGangFen(otherPlayer.getGangFen() - 2);
								}
							}
						}
					}
				}
				if (player.getIsHu()) {
					player.setHuNum(player.getHuNum() + 1);
					huPlayer = player;
				}
			}
			
			//这个算玩家的长毛的分,跟杠分是一样的.一个人赢了.其他三家都给钱
			for (Player player : players) {
				player.setZhangMaoFen(player.getZhangMaoNum() * 3 + player.getZhangMaoFen());
				for (Player otherPlayer : players) {
					if(!otherPlayer.getUserId().equals(player.getUserId())){
						otherPlayer.setZhangMaoFen(otherPlayer.getZhangMaoFen() - player.getZhangMaoNum());
					}
				}
			}
			
			//房间类型中.....长毛是否算分逻辑
			boolean zhangMaoFen = false;
			if(room.getZhangMaoType().equals(Cnst.ZHANG_MAO_SUAN_FEN)){
				zhangMaoFen = true;
			}
			//得到胡牌玩家的分数
			Integer fen = MahjongUtils.checkHuInfo(huPlayer, room, zhangMaoFen);
			if(zhangMaoFen){ //长毛算分
				if(huPlayer.getUserId().equals(room.getZhuangId())){ //胡的是庄
					fen = fen << 1; //庄家输赢番一番
					for (Player player : players) {
						if (player.getIsHu()) {
							player.setThisScore(fen * 3 + player.getGangFen() + player.getZhangMaoFen());
							player.setScore(player.getThisScore() + player.getScore());
						}else{
							player.setThisScore(player.getGangFen() - fen + player.getZhangMaoFen());
							player.setScore(player.getThisScore() + player.getScore());
						}
					}
				}else{ //胡的不是庄
					for (Player player : players) {
						if (player.getIsHu()) {
							player.setThisScore(fen * 4 + player.getGangFen() + player.getZhangMaoFen());
							player.setScore(player.getThisScore() + player.getScore());
						}else if (player.getUserId().equals(room.getZhuangId())) {
							player.setThisScore(- fen * 2 + player.getGangFen() + player.getZhangMaoFen());
							player.setScore(player.getThisScore() + player.getScore());
						}else{
							player.setThisScore(- fen + player.getGangFen() + player.getZhangMaoFen());
							player.setScore(player.getThisScore() + player.getScore());
						}
					}
				}
			}else{ //长毛不算分
				if(huPlayer.getUserId().equals(room.getZhuangId())){ //胡的是庄
					fen = fen << 1; //庄家输赢番一番
					for (Player player : players) {
						if (player.getIsHu()) {
							player.setThisScore(fen * 3 + player.getGangFen());
							player.setScore(player.getThisScore() + player.getScore());
						}else{
							player.setThisScore(- fen + player.getGangFen());
							player.setScore(player.getThisScore() + player.getScore());
						}
					}
				}else{ //胡的不是庄
					for (Player player : players) {
						if (player.getIsHu()) {
							player.setThisScore(fen * 4 + player.getGangFen());
							player.setScore(player.getThisScore() + player.getScore());
						}else if (player.getUserId().equals(room.getZhuangId())) {
							player.setThisScore(- fen * 2 + player.getGangFen());
							player.setScore(player.getThisScore() + player.getScore());
						}else{
							player.setThisScore(- fen + player.getGangFen());
							player.setScore(player.getThisScore() + player.getScore());
						}
					}
				}
			}
			
			if(room.getWinPlayerId().equals(room.getZhuangId())){
				//庄不变
			}else{
				//下个人坐庄
				int index = -1;
				Long[] playIds = room.getPlayerIds();
				for(int i=0;i<playIds.length;i++){
					if(playIds[i].equals(room.getZhuangId())){
						index = i+1;
						if(index == 4){
							index = 0;
						}
						break;
					}
				}
				room.setZhuangId(playIds[index]);
				room.setCircleWind(index+1);
				
				//不是第一局,并且圈风是东风 ,证明是下一圈了.
				if(room.getXiaoJuNum() != 1 && room.getCircleWind() == Cnst.WIND_EAST){
					room.setTotolCircleNum(room.getTotolCircleNum() == null ? 1:room.getTotolCircleNum()+1);
					room.setLastNum(room.getCircleNum() - room.getTotolCircleNum());
				}
			}
		}
		// 更新redis
		RedisUtil.setPlayersList(players);
		
		
		// 添加小结算信息
		List<Integer> xiaoJS = new ArrayList<Integer>();
		for (Player p : players) {
			xiaoJS.add(p.getThisScore());
		}
		room.addXiaoJuInfo(xiaoJS);
		// 初始化房间
		room.initRoom();
		RedisUtil.updateRedisData(room, null);
		// 写入文件
		List<Map<String, Object>> userInfos = new ArrayList<Map<String, Object>>();
		for (Player p : players) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("userId", p.getUserId());
			map.put("score", p.getThisScore());
			map.put("pais", p.getCurrentMjList());
			map.put("gangScore", p.getGangFen());
			if(p.getIsHu()){
				map.put("isWin", 1);
				map.put("winInfo", p.getFanShu());
			}else{
				map.put("isWin", 0);
			}
			if(p.getIsDian()){
				map.put("isDian", 1);
			}else{
				map.put("isDian", 0);
			}
			if(p.getActionList() != null && p.getActionList().size() > 0){
				List<Object> actionList = new ArrayList<Object>();
				for(Action action : p.getActionList()){
					if(action.getType() == Cnst.ACTION_TYPE_CHI){
						Map<String,Integer> actionMap = new HashMap<String, Integer>();
						actionMap.put("action", action.getActionId());
						actionMap.put("extra", action.getExtra());
						actionList.add(actionMap);
						
					}else if(action.getType() == Cnst.ACTION_TYPE_ANGANG){
						Map<String,Integer> actionMap = new HashMap<String, Integer>();
						actionMap.put("action", action.getActionId());
						actionMap.put("extra", action.getExtra());
						actionList.add(actionMap);
					}else{
						actionList.add(action.getActionId());
					}
				}
				map.put("actionList", actionList);
			}			
			userInfos.add(map);
		}
		JSONObject info = new JSONObject();
		info.put("lastNum", room.getLastNum());
		info.put("userInfo", userInfos);
		BackFileUtil.save(100102, room, null,info,null);
		// 小结算 存入一次回放
		BackFileUtil.write(room);

		// 大结算判定 (玩的圈数等于选择的圈数)
		if (room.getTotolCircleNum() == room.getCircleNum()) {
			// 最后一局 大结算
			room = RedisUtil.getRoomRespByRoomId(roomId);
			room.setState(Cnst.ROOM_STATE_YJS);
			RedisUtil.updateRedisData(room, null);
			// 这里更新数据库吧
			RoomUtil.updateDatabasePlayRecord(room);
		}
	}
}
