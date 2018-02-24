package connect.utils;

/**
 * http request URI
 */
public class UriUtil {

    /**======================================================================================
     *                                Login
     * ======================================================================================
     */
    /** Upload a file */
    public static String UPLOAD_FILE = "/fs/v1/up";
    /** The private key is registered */
    public static String CONNECT_V3_LOGIN = "/connect/v3/login";
    /** The private key is registered */
    public static String CONNECT_V3_DEPARTMENT = "/connect/v3/department";
    /** Query the user information 1: name(en)  2:uid  3:name(zh)*/
    public static String CONNECT_V3_WORKMATE_SEARCH = "/connect/v3/workmate/search";
    public static String CONNECT_V3_USERS_FOLLOW = "/connect/v3/users/follow";
    public static String CONNECT_V3_PROXY_TOKEN = "/visitors/v1/staff/token";
    public static String CONNECT_V3_API_BANNERS = "/connect/v3/api/banners";
    public static String CONNECT_V3_API_APPLICATIONS = "/connect/v3/api/applications";
    public static String CONNECT_V3_PROXY_VISITOR_RECORDS = "/visitors/v1/staff/records";
    public static String CONNECT_V3_PROXY_EXAMINE_VERIFY = "/visitors/v1/staff/records/examine/verify";
    public static String CONNECT_V3_PROXY_RECORDS_HISTORY = "/visitors/v1/staff/records/history";

    public static String CONNECT_V3_API_APPLICATIONS_ADD = "/connect/v3/api/applications/add";
    public static String CONNECT_V3_API_APPLICATIONS_DEL = "/connect/v3/api/applications/del";

    /** 根据 部门id查询 所有员工 */
    public static String CONNECT_V3_DEPAERTMENT_WORKMATES = "/connect/v3/department/workmates";
    /** 群成员邀请入群 */
    public static String CONNECT_V3_GROUP_INVITE = "/connect/v3/group/invite";
    /** 获取仓库列表 */
    public static String STORES_V1_IWORK_LOGS = "/stores/v1/iwork/logs";
    /** 确认陌生人信息 */
    public static String STORES_V1_IWORK_LOG_COMFIRM = "/stores/v1/iwork/log/confirm";
    /** 查询陌生人信息详情 */
    public static String STORES_V1_IWORK_LOGS_DETAIL = "/stores/v1/iwork/logs/detail";
    /** 修改访客时间 */
    public static String VISITORS_V1_STAFF_RECORDS_UPDATE = "/visitors/v1/staff/records/update";
    /** The App update */
    public static String CONNECT_V1_VERSION = "/connect/v1/version";

    /**======================================================================================
     *                                setting
     * ======================================================================================
     */
    /** Query the user information 1: uid   2:connectid  "":username*/
    public static String CONNECT_V1_USER_SEARCH = "/connect/v1/users/search";
    /** set user avatar */
    public static String AVATAR_V1_SET = "/connect/v1/setting/avatar";
    /** delete account */
    public static String CONNECT_V3_PUBKEY = "/connect/v3/pubkey";

    /**======================================================================================
     *                                wallet
     * ======================================================================================
     */
    /** lucky packet detail */
    public static String WALLET_PACKAGE_INFO = "/wallet/v1/red_package/info";

    /**======================================================================================
     *                                      setting group
     * ======================================================================================
     */
    /** create group */
    public static String CREATE_GROUP = "/connect/v1/group";
    /** remove member */
    public static String GROUP_REMOVE = "/connect/v1/group/deluser";
    /** exit from group */
    public static String GROUP_QUIT = "/connect/v1/group/quit";
    /** command group */
    public static String GROUP_COMMON = "/connect/v1/group/set_common";
    /** remove command */
    public static String GROUP_RECOMMON = "/connect/v1/group/remove_common";
    /** modify member nick */
    public static String GROUP_MEMUPDATE = "/connect/v1/group/member_update";
    /** modify group nick */
    public static String GROUP_UPDATE = "/connect/v1/group/update";
    /** get group detail */
    public static String GROUP_PULLINFO = "/connect/v1/group/info";
    /** get group setting */
    public static String GROUP_SETTING_INFO = "/connect/v1/group/setting_info";
    /** group mute */
    public static String CONNECT_GROUP_MUTE = "/connect/v1/group/mute";

}
