package com.yzt.logic.mj.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.yzt.netty.client.WSClient;

/**
 * Created by Administrator on 2017/7/8.
 */
public class RoomResp extends Room {

	private static final long serialVersionUID = -5308844344084689942L;
	private List<Integer> currentMjList;// 房间内剩余麻将集合；
	private Long zhuangId;
	// 本房间状态，1等待玩家入坐；2游戏中；3小结算
	private Integer state;
	private Integer lastNum;// 房间剩余圈数
	private Integer position; //房间当前风向指向的位置
	private Integer xiaoJuNum;// 当前第几局
	private Long xjst;// 小局开始时间
	private Integer circleWind;// 圈风...
	private Integer totolCircleNum;//进行几圈
	private Integer roomType;// 房间模式，房主模式1；自由模式2
	private DissolveRoom dissolveRoom;// 申请解散信息
	private Integer lastChuPai;// 最后出的牌
	private Long lastChuPaiUserId;// 最后出牌的玩家
	private Integer lastFaPai;// 最后发的牌
	private Long lastFaPaiUserId;//最后发牌人
	private Integer createDisId;
	private Integer applyDisId;
	private Integer outNum;
	private List<Long> guoUserIds = new ArrayList<Long>();// 动作 点击过的人
	private Integer wsw_sole_main_id;// 大接口id
	private Integer wsw_sole_action_id;// 吃碰杠出牌发牌id
	private String openName;

	private Long[] playerIds;// 玩家id集合
	private Long lastPengGangUser;// 最后碰杠的玩家

	private Collection<WSClient> group;// 房间 4个channel集合
	private List<List<Integer>> xiaoJuInfo = new ArrayList<List<Integer>>();// 小结算info
	private Integer hunPai; // 混牌
	private List<Integer> nextAction;// 玩家动作
	private Long nextActionUserId;// 执行动作的玩家
	private Integer lastAction;//上个动作 
	private Long lastActionUserId;//上个执行动作的玩家
	private Boolean huangZhuang;
	private Long winPlayerId;//当局胡牌的玩家
	private Integer dingHunPai;//翻出来的牌,定混用,比混小1.
	private Integer startPosition;//代开中点开局的方位
	private Integer zhangMaoType; //1是允许,2是不允许.
	private Integer woMaoType; //1是允许,2是不允许.
	private Integer liangMaoChangJing; // 开局的亮毛场景,所有人,只能选择是否亮毛,不能有其他吃碰杠操作. 0 是亮毛场景,1不是. 2,是正式开局流程
	public void initRoom() {
		this.lastChuPai = null;
		this.lastChuPaiUserId = null;
		this.lastPengGangUser = null;
		this.guoUserIds = new ArrayList<Long>();
		this.hunPai = null;
		this.dissolveRoom = null;
		this.nextAction = null;
		this.nextActionUserId = null;
		this.huangZhuang = false;
		this.xjst = null;
		this.lastAction = null;
		this.lastActionUserId = null;
		this.lastFaPai = null;
		this.lastFaPaiUserId = null;
		this.winPlayerId = null;
		this.dingHunPai = null;
		this.startPosition = null;
		this.liangMaoChangJing = 0;
		this.position = 1; //默认指向东
	}

	
	public Integer getLiangMaoChangJing() {
		return liangMaoChangJing;
	}

	public void setLiangMaoChangJing(Integer liangMaoChangJing) {
		this.liangMaoChangJing = liangMaoChangJing;
	}

	public Integer getPosition() {
		return position;
	}
	public void setPosition(Integer position) {
		this.position = position;
	}

	public Integer getZhangMaoType() {
		return zhangMaoType;
	}

	public void setZhangMaoType(Integer zhangMaoType) {
		this.zhangMaoType = zhangMaoType;
	}

	public Integer getWoMaoType() {
		return woMaoType;
	}

	public void setWoMaoType(Integer woMaoType) {
		this.woMaoType = woMaoType;
	}



	public Integer getHunPai() {
		return hunPai;
	}

	public void setHunPai(Integer hunPai) {
		this.hunPai = hunPai;
	}

	public List<Integer> getNextAction() {
		return nextAction;
	}

	public void setNextAction(List<Integer> nextAction) {
		this.nextAction = nextAction;
	}

	public Long getNextActionUserId() {
		return nextActionUserId;
	}

	public void setNextActionUserId(Long nextActionUserId) {
		this.nextActionUserId = nextActionUserId;
	}

	public List<Integer> getCurrentMjList() {
		return currentMjList;
	}

	public void setCurrentMjList(List<Integer> currentMjList) {
		this.currentMjList = currentMjList;
	}

	public Long getZhuangId() {
		return zhuangId;
	}

	public void setZhuangId(Long zhuangId) {
		this.zhuangId = zhuangId;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public Integer getLastNum() {
		return lastNum;
	}

	public void setLastNum(Integer lastNum) {
		this.lastNum = lastNum;
	}

	public Integer getXiaoJuNum() {
		return xiaoJuNum;
	}

	public void setXiaoJuNum(Integer xiaoJuNum) {
		this.xiaoJuNum = xiaoJuNum;
	}

	public Long getXjst() {
		return xjst;
	}

	public void setXjst(Long xjst) {
		this.xjst = xjst;
	}

	public Integer getCircleWind() {
		return circleWind;
	}

	public void setCircleWind(Integer circleWind) {
		this.circleWind = circleWind;
	}

	public Integer getRoomType() {
		return roomType;
	}

	public void setRoomType(Integer roomType) {
		this.roomType = roomType;
	}

	public DissolveRoom getDissolveRoom() {
		return dissolveRoom;
	}

	public void setDissolveRoom(DissolveRoom dissolveRoom) {
		this.dissolveRoom = dissolveRoom;
	}

	public Integer getCreateDisId() {
		return createDisId;
	}

	public void setCreateDisId(Integer createDisId) {
		this.createDisId = createDisId;
	}

	public Integer getApplyDisId() {
		return applyDisId;
	}

	public void setApplyDisId(Integer applyDisId) {
		this.applyDisId = applyDisId;
	}

	public Integer getOutNum() {
		return outNum;
	}

	public void setOutNum(Integer outNum) {
		this.outNum = outNum;
	}

	public List<Long> getGuoUserIds() {
		return guoUserIds;
	}

	public void setGuoUserIds(List<Long> guoUserIds) {
		this.guoUserIds = guoUserIds;
	}

	public Integer getWsw_sole_main_id() {
		return wsw_sole_main_id;
	}

	public void setWsw_sole_main_id(Integer wsw_sole_main_id) {
		this.wsw_sole_main_id = wsw_sole_main_id;
	}

	public Integer getWsw_sole_action_id() {
		return wsw_sole_action_id;
	}

	public void setWsw_sole_action_id(Integer wsw_sole_action_id) {
		this.wsw_sole_action_id = wsw_sole_action_id;
	}

	public String getOpenName() {
		return openName;
	}

	public void setOpenName(String openName) {
		this.openName = openName;
	}

	public Long[] getPlayerIds() {
		return playerIds;
	}

	public void setPlayerIds(Long[] playerIds) {
		this.playerIds = playerIds;
	}

	public Long getLastPengGangUser() {
		return lastPengGangUser;
	}

	public void setLastPengGangUser(Long lastPengGangUser) {
		this.lastPengGangUser = lastPengGangUser;
	}

	public Collection<WSClient> getGroup() {
		return group;
	}

	public void setGroup(Collection<WSClient> group) {
		this.group = group;
	}

	public List<List<Integer>> getXiaoJuInfo() {
		return xiaoJuInfo;
	}

	public void setXiaoJuInfo(List<List<Integer>> xiaoJuInfo) {
		this.xiaoJuInfo = xiaoJuInfo;
	}

	public void addXiaoJuInfo(List<Integer> list) {
		xiaoJuInfo.add(list);
	}


	public Boolean getHuangZhuang() {
		return huangZhuang;
	}

	public void setHuangZhuang(Boolean huangZhuang) {
		this.huangZhuang = huangZhuang;
	}

	public Integer getLastAction() {
		return lastAction;
	}

	public void setLastAction(Integer lastAction) {
		this.lastAction = lastAction;
	}

	public Long getLastActionUserId() {
		return lastActionUserId;
	}

	public void setLastActionUserId(Long lastActionUserId) {
		this.lastActionUserId = lastActionUserId;
	}

	public Long getLastChuPaiUserId() {
		return lastChuPaiUserId;
	}

	public void setLastChuPaiUserId(Long lastChuPaiUserId) {
		this.lastChuPaiUserId = lastChuPaiUserId;
	}

	public Integer getLastChuPai() {
		return lastChuPai;
	}

	public void setLastChuPai(Integer lastChuPai) {
		this.lastChuPai = lastChuPai;
	}

	public Integer getLastFaPai() {
		return lastFaPai;
	}

	public void setLastFaPai(Integer lastFaPai) {
		this.lastFaPai = lastFaPai;
	}

	public Long getLastFaPaiUserId() {
		return lastFaPaiUserId;
	}

	public void setLastFaPaiUserId(Long lastFaPaiUserId) {
		this.lastFaPaiUserId = lastFaPaiUserId;
	}

	public Long getWinPlayerId() {
		return winPlayerId;
	}

	public void setWinPlayerId(Long winPlayerId) {
		this.winPlayerId = winPlayerId;
	}

	@Override
	public String toString() {
		return "RoomResp [currentMjList=" + currentMjList + ", zhuangId="
				+ zhuangId + ", state=" + state + ", lastNum=" + lastNum
				+ ", xiaoJuNum=" + xiaoJuNum + ", xjst=" + xjst
				+ ", circleWind=" + circleWind + ", roomType=" + roomType
				+ ", dissolveRoom=" + dissolveRoom + ", lastChuPai="
				+ lastChuPai + ", lastChuPaiUserId=" + lastChuPaiUserId
				+ ", lastFaPai=" + lastFaPai + ", lastFaPaiUserId="
				+ lastFaPaiUserId 
				+ ", createDisId=" + createDisId + ", applyDisId=" + applyDisId
				+ ", outNum=" + outNum + ", guoUserIds=" + guoUserIds
				+ ", wsw_sole_main_id=" + wsw_sole_main_id
				+ ", wsw_sole_action_id=" + wsw_sole_action_id + ", openName="
				+ openName + ", playerIds=" + Arrays.toString(playerIds)
				+ ", lastPengGangUser=" + lastPengGangUser + ", group=" + group
				+ ", xiaoJuInfo=" + xiaoJuInfo + ", hunPai=" + hunPai
				+ ", nextAction=" + nextAction + ", nextActionUserId="
				+ nextActionUserId + ", lastAction=" + lastAction
				+ ", lastActionUserId=" + lastActionUserId + ", huangZhuang="
				+ huangZhuang + ", winPlayerId=" + winPlayerId + "]";
	}


	public Integer getTotolCircleNum() {
		return totolCircleNum;
	}


	public void setTotolCircleNum(Integer totolCircleNum) {
		this.totolCircleNum = totolCircleNum;
	}


	public Integer getDingHunPai() {
		return dingHunPai;
	}


	public void setDingHunPai(Integer dingHunPai) {
		this.dingHunPai = dingHunPai;
	}


	public Integer getStartPosition() {
		return startPosition;
	}


	public void setStartPosition(Integer startPosition) {
		this.startPosition = startPosition;
	}
	
	
	
}
