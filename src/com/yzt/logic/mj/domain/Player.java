package com.yzt.logic.mj.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by admin on 2017/6/26.
 */
/**
 * @author wsw_008
 *
 */
public class Player extends User {

	private static final long serialVersionUID = 5499026321284587471L;

	private Integer roomId;// 房间号
	private Integer state;	// out离开状态（断线）;online正常在线；
	private List<Integer> currentMjList = new ArrayList<Integer>();// 用户手中当前的牌
	private List<Integer> chuList = new ArrayList<Integer>();// 出牌的集合
	private Integer position;// 位置信息；详见cnst配置
	private String ip;
	
	private Boolean isZiMo; // 是不是自摸
	private Boolean isGangkai; //是不是杠开
	private Boolean isHu;//小结算用
	private Boolean isDian;//小结算用
	private Integer score;// 玩家这全游戏的总分
	private Integer thisScore;// 记录当玩家当前局
	private List<Integer> fanShu = new ArrayList<Integer>(); // 牌型记番,具体的番数.
	private String notice;// 跑马灯信息
	private Integer playStatus;// 用户当前状态， dating用户在大厅中; in刚进入房间，等待状态;prepared准备状态;game游戏中; xjs小结算
								
	private Integer huNum;// 胡的次数
	private Integer dianNum;// 点炮次数
	private Integer zhuangNum;// 坐庄次数
	private Integer zimoNum;// 自摸次数
	private Integer joinIndex;// 加入顺序
	private String channelId;// 通道id
	private Integer duiHunNum; // 怼混次数 (记番使用) 怼完了数量要减1

	private Integer qingYiSeNum; // 清一色
	private Integer pengPengHuNum; // 碰碰胡
	private Integer sanJiaBiNum;//三家闭门,初始为0 ,任意个玩家为1,就不是三家闭.
	private Long updateTime;// 更新用户数据时间
	private List<Action> actionList =new ArrayList<Action>();//统计用户所有动作 (吃碰杠等)
	private Integer huDePai;//胡的那张牌
	private HashMap<Integer,List<Integer>> zhangMaoMap;//长毛具体的牌的信息.
	private Integer zhangMaoNum; //长毛的数量.记分用 //初始为0
	private Integer zhangMaoFen; //跟杠分一样的长毛分.
	private Integer gangFen; //杠的分数
	private Double x_index; //定位的坐标
	private Double y_index; //定位的坐标
	private Boolean isChuGuoPai; //有没有出过牌
	private Boolean isLiangMaoYouHun; //亮毛组合中,有混牌.
	private List<Integer> liangList; //用来显示亮暗杠的list 0 是已经亮过了.非0是需要亮牌的值
	
	public void initPlayer(Integer roomId,Integer playStatus,Integer score) {
		// TODO
		if(roomId == null){
			this.position = null;
			this.joinIndex = null;	
			this.roomId = null;
			this.qingYiSeNum = 0;
			this.pengPengHuNum = 0;
			this.sanJiaBiNum = 0;
			this.huNum = 0;
			this.zhuangNum = 0;
			this.zimoNum = 0;
			this.dianNum = 0;
			this.gangFen = 0 ;
			this.zhangMaoNum = 0;
			this.liangList = null;
		}
		this.playStatus = playStatus;
		this.chuList = null;
		this.isHu = false;
		this.isDian = false;
		this.isGangkai = false; 
		this.isChuGuoPai = false;
		this.isLiangMaoYouHun = false;
		this.score = score;
		this.thisScore = 0;
		this.duiHunNum = 0;
		this.gangFen = 0;
		this.zhangMaoFen = 0;
		this.zhangMaoNum = 0;
		this.liangList = new ArrayList<Integer>();
		this.actionList = new ArrayList<Action>();
		this.huDePai = null;
		this.currentMjList = new ArrayList<Integer>();
		this.isZiMo = false;
		this.fanShu = new ArrayList<Integer>();
		this.zhangMaoMap = new HashMap<Integer, List<Integer>>();
	}

	
	public List<Integer> getLiangList() {
		return liangList;
	}


	public void setLiangList(List<Integer> liangList) {
		this.liangList = liangList;
	}


	public Integer getZhangMaoFen() {
		return zhangMaoFen;
	}

	public void setZhangMaoFen(Integer zhangMaoFen) {
		this.zhangMaoFen = zhangMaoFen;
	}


	public Boolean getIsLiangMaoYouHun() {
		return isLiangMaoYouHun;
	}

	public void setIsLiangMaoYouHun(Boolean isLiangMaoYouHun) {
		this.isLiangMaoYouHun = isLiangMaoYouHun;
	}

	public Boolean getIsChuGuoPai() {
		return isChuGuoPai;
	}

	public void setIsChuGuoPai(Boolean isChuGuoPai) {
		this.isChuGuoPai = isChuGuoPai;
	}

	public Double getX_index() {
		return x_index;
	}

	public void setX_index(Double x_index) {
		this.x_index = x_index;
	}

	public Double getY_index() {
		return y_index;
	}

	public void setY_index(Double y_index) {
		this.y_index = y_index;
	}

	public Boolean getIsGangkai() {
		return isGangkai;
	}

	public void setIsGangkai(Boolean isGangkai) {
		this.isGangkai = isGangkai;
	}

	public Integer getZhangMaoNum() {
		return zhangMaoNum;
	}

	public void setZhangMaoNum(Integer zhangMaoNum) {
		this.zhangMaoNum = zhangMaoNum;
	}

	public HashMap<Integer, List<Integer>> getZhangMaoMap() {
		return zhangMaoMap;
	}
	public void setZhangMaoMap(HashMap<Integer, List<Integer>> zhangMaoMap) {
		this.zhangMaoMap = zhangMaoMap;
	}
	public Boolean getIsZiMo() {
		return isZiMo;
	}

	public void setIsZiMo(Boolean isZiMo) {
		this.isZiMo = isZiMo;
	}


	public List<Integer> getFanShu() {
		return fanShu;
	}

	public void setFanShu(List<Integer> fanShu) {
		this.fanShu = fanShu;
	}


	public Integer getDuiHunNum() {
		return duiHunNum;
	}

	public void setDuiHunNum(Integer duiHunNum) {
		this.duiHunNum = duiHunNum;
	}


	public Integer getRoomId() {
		return roomId;
	}

	public void setRoomId(Integer roomId) {
		this.roomId = roomId;
	}

	public List<Integer> getCurrentMjList() {
		return currentMjList;
	}

	public void setCurrentMjList(List<Integer> currentMjList) {
		this.currentMjList = currentMjList;
	}

	public List<Integer> getChuList() {
		return chuList;
	}

	public void setChuList(List<Integer> chuList) {
		this.chuList = chuList;
	}

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}


	public Boolean getIsHu() {
		return isHu;
	}

	public void setIsHu(Boolean isHu) {
		this.isHu = isHu;
	}

	public Boolean getIsDian() {
		return isDian;
	}

	public void setIsDian(Boolean isDian) {
		this.isDian = isDian;
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	public Integer getThisScore() {
		return thisScore;
	}

	public void setThisScore(Integer thisScore) {
		this.thisScore = thisScore;
	}

	public String getNotice() {
		return notice;
	}

	public void setNotice(String notice) {
		this.notice = notice;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public Integer getPlayStatus() {
		return playStatus;
	}

	public void setPlayStatus(Integer playStatus) {
		this.playStatus = playStatus;
	}


	public Integer getHuNum() {
		return huNum;
	}

	public void setHuNum(Integer huNum) {
		this.huNum = huNum;
	}

	public Integer getDianNum() {
		return dianNum;
	}

	public void setDianNum(Integer dianNum) {
		this.dianNum = dianNum;
	}

	public Integer getZhuangNum() {
		return zhuangNum;
	}

	public void setZhuangNum(Integer zhuangNum) {
		this.zhuangNum = zhuangNum;
	}

	public Integer getZimoNum() {
		return zimoNum;
	}

	public void setZimoNum(Integer zimoNum) {
		this.zimoNum = zimoNum;
	}

	public Integer getJoinIndex() {
		return joinIndex;
	}

	public void setJoinIndex(Integer joinIndex) {
		this.joinIndex = joinIndex;
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public Long getUpdateTime() {
		return updateTime;
	}

	
	public Integer getGangFen() {
		return gangFen;
	}

	public void setGangFen(Integer gangFen) {
		this.gangFen = gangFen;
	}

	public void setUpdateTime(Long updateTime) {
		this.updateTime = updateTime;
	}

	public List<Action> getActionList() {
		return actionList;
	}

	public void setActionList(List<Action> actionList) {
		this.actionList = actionList;
	}
	//添加 动作
	public void addActionList(Action action){
		this.actionList.add(action);
	}

	
	public Integer getQingYiSeNum() {
		return qingYiSeNum;
	}



	public void setQingYiSeNum(Integer qingYiSeNum) {
		this.qingYiSeNum = qingYiSeNum;
	}



	public Integer getPengPengHuNum() {
		return pengPengHuNum;
	}

	public void setPengPengHuNum(Integer pengPengHuNum) {
		this.pengPengHuNum = pengPengHuNum;
	}



	public Integer getHuDePai() {
		return huDePai;
	}



	public void setHuDePai(Integer huDePai) {
		this.huDePai = huDePai;
	}



	public Integer getSanJiaBiNum() {
		return sanJiaBiNum;
	}



	public void setSanJiaBiNum(Integer sanJiaBiNum) {
		this.sanJiaBiNum = sanJiaBiNum;
	}
	
	
}
