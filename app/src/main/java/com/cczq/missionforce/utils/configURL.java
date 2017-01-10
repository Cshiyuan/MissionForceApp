package com.cczq.missionforce.utils;

/**
 * 作为存储所有URL的类
 * Created by Shyuan on 2016/9/23.
 */

public class configURL {

    //服务器的基本地址
    private static String base_URL = "http://119.29.186.160/";
    //验证登陆的地址
    public static String URL_LOGIN = base_URL + "MF_phalapi/Public/missionforce/?service=User.loginUser";
    //进行注册的地址
    public static String URL_REGISTER = base_URL + "MF_phalapi/Public/missionforce/?service=User.registerUser";
    //进行任务查询地址
    public static String URL_CHEACKMISSION = base_URL + "MF_phalapi/Public/missionforce/?service=Mission.getMissionByUID";
    //提交任务时间
    public static String URL_COMMITMISSION = base_URL + "MF_phalapi/Public/missionforce/?service=Mission.ChangeMissionTime";
    //查询用户小组
    public static String URL_CHECKGROUP = base_URL + "MF_phalapi/Public/missionforce/?service=Group.GetGroupByUID";
    //查询小组成员
    public static String URL_CHECKGROUPUSERS = base_URL + "MF_phalapi/Public/missionforce/?service=Group.getUsersByGID";
    //查询小组成员
    public static String URL_COMMITGROUPUSERS = base_URL + "MF_phalapi/Public/missionforce/?service=Group.AddAssignMissionToUser";
    //发起投票
    public static String URL_CREATEVOTE = base_URL + "MF_phalapi/Public/missionforce/?service=Group.AddVote";
    //向小组加入成员
    public static String URL_ADDMEMBERTOGROUP = base_URL + "MF_phalapi/Public/missionforce/?service=Group.AddUserToGroupByEmail";
    //向小组加入成员
    public static String URL_CHECKVOTETHEME = base_URL + "MF_phalapi/Public/missionforce/?service=Group.GetVoteThemeByGID";
//    Group.GetVoteThemeByGID
    //利用TID查询投票信息
    public static String URL_CHECKVOTEINFO = base_URL + "MF_phalapi/Public/missionforce/?service=Group.GetVoteOptionsByTID";
    //投票
    public static String URL_TOVOTE = base_URL + "MF_phalapi/Public/missionforce/?service=Group.UserVoteToOption";

    public static String URL_CREATEGROUP = base_URL + "MF_phalapi/Public/missionforce/?service=Group.CreateGroupByUID";
}
