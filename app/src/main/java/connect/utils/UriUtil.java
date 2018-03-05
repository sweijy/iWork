package connect.utils;

/**
 * http request URI
 */
public class UriUtil {

    /** iWork服务器地址 */
    private static String  addressService = ConfigUtil.getInstance().serverAddress();
    /** 访客服务器地址 */
    private static String  addressVisitor = ConfigUtil.getInstance().visitorAddress();
    /** 仓库服务器地址 */
    private static String  addressWarehouse = ConfigUtil.getInstance().warehouseAddress();

    /** =================================== iWork ==================================== */
    /** Upload a file */
    public static String UPLOAD_FILE = addressService + "/fs/v1/up";
    /** The private key is registered */
    public static String CONNECT_V3_LOGIN = addressService + "/connect/v3/login";
    /** The private key is registered */
    public static String CONNECT_V3_DEPARTMENT = addressService + "/connect/v3/department";
    /** Query the user information 1: name(en)  2:uid  3:name(zh)*/
    public static String CONNECT_V3_WORKMATE_SEARCH = addressService + "/connect/v3/workmate/search";

    public static String CONNECT_V3_USERS_FOLLOW = addressService + "/connect/v3/users/follow";

    public static String CONNECT_V3_API_BANNERS = addressService + "/connect/v3/api/banners";

    public static String CONNECT_V3_API_APPLICATIONS = addressService + "/connect/v3/api/applications";

    public static String CONNECT_V3_API_APPLICATIONS_ADD = addressService + "/connect/v3/api/applications/add";

    public static String CONNECT_V3_API_APPLICATIONS_DEL = addressService + "/connect/v3/api/applications/del";
    /** 根据 部门id查询 所有员工 */
    public static String CONNECT_V3_DEPAERTMENT_WORKMATES = addressService + "/connect/v3/department/workmates";
    /** 群成员邀请入群 */
    public static String CONNECT_V3_GROUP_INVITE = addressService + "/connect/v3/group/invite";
    /** The App update */
    public static String CONNECT_V1_VERSION = addressService + "/connect/v1/version";
    /** Query the user information 1: uid   2:connectid  "":username*/
    public static String CONNECT_V1_USER_SEARCH = addressService + "/connect/v1/users/search";
    /** set user avatar */
    public static String AVATAR_V1_SET = addressService + "/connect/v1/setting/avatar";
    /** delete account */
    public static String CONNECT_V3_PUBKEY = addressService + "/connect/v3/pubkey";

    /** =================================== setting group ==================================== */
    /** create group */
    public static String CREATE_GROUP = addressService + "/connect/v1/group";
    /** remove member */
    public static String GROUP_REMOVE = addressService + "/connect/v1/group/deluser";
    /** exit from group */
    public static String GROUP_QUIT = addressService + "/connect/v1/group/quit";
    /** command group */
    public static String GROUP_COMMON = addressService + "/connect/v1/group/set_common";
    /** remove command */
    public static String GROUP_RECOMMON = addressService + "/connect/v1/group/remove_common";
    /** modify member nick */
    public static String GROUP_MEMUPDATE = addressService + "/connect/v1/group/member_update";
    /** modify group nick */
    public static String GROUP_UPDATE = addressService + "/connect/v1/group/update";
    /** get group detail */
    public static String GROUP_PULLINFO = addressService + "/connect/v1/group/info";
    /** get group setting */
    public static String GROUP_SETTING_INFO = addressService + "/connect/v1/group/setting_info";
    /** group mute */
    public static String CONNECT_GROUP_MUTE = addressService + "/connect/v1/group/mute";

    /** =================================== 访客系统 ==================================== */
    /** 修改访客时间 */
    public static String VISITORS_V1_STAFF_RECORDS_UPDATE = addressVisitor + "/visitors/v1/staff/records/update";

    public static String CONNECT_V3_PROXY_VISITOR_RECORDS = addressVisitor + "/visitors/v1/staff/records";

    public static String CONNECT_V3_PROXY_EXAMINE_VERIFY = addressVisitor + "/visitors/v1/staff/records/examine/verify";

    public static String CONNECT_V3_PROXY_RECORDS_HISTORY = addressVisitor + "/visitors/v1/staff/records/history";

    public static String CONNECT_V3_PROXY_TOKEN = addressVisitor + "/visitors/v1/staff/token";

    /** =================================== 仓库系统 ==================================== */
    /** 获取仓库列表 */
    public static String STORES_V1_IWORK_LOGS = addressWarehouse + "/stores/v1/iwork/logs";
    /** 确认陌生人信息 */
    public static String STORES_V1_IWORK_LOG_COMFIRM = addressWarehouse + "/stores/v1/iwork/log/confirm";
    /** 查询陌生人信息详情 */
    public static String STORES_V1_IWORK_LOGS_DETAIL = addressWarehouse + "/stores/v1/iwork/logs/detail";


}
