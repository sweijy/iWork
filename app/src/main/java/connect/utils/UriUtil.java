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
    public static String CONNECT_V3_LOGIN = addressService + "/bm/users/v1/login";
    /** The private key is registered */
    public static String CONNECT_V3_DEPARTMENT = addressService + "/bm/users/v1/department";
    /** Query the user information 1: name(en)  2:uid  3:name(zh)*/
    public static String CONNECT_V3_WORKMATE_SEARCH = addressService + "/bm/users/v1/workmate/search";

    public static String CONNECT_V3_USERS_FOLLOW = addressService + "/bm/users/v1/users/follow";

    public static String CONNECT_V3_API_BANNERS = addressService + "/bm/apps/v1/api/banners";

    public static String CONNECT_V3_API_APPLICATIONS = addressService + "/bm/apps/v1/api/applications";

    public static String CONNECT_V3_API_APPLICATIONS_ADD = addressService + "/bm/apps/v1/api/applications/add";

    public static String CONNECT_V3_API_APPLICATIONS_DEL = addressService + "/bm/apps/v1/api/applications/del";
    /** 根据 部门id查询 所有员工 */
    public static String CONNECT_V3_DEPAERTMENT_WORKMATES = addressService + "/bm/users/v1/department/workmates";
    /** 群成员邀请入群 */
    public static String CONNECT_V3_GROUP_INVITE = addressService + "/connect/v3/group/invite";
    /** The App update */
    public static String CONNECT_V1_VERSION = addressService + "/bm/users/v1/version";
    /** set user avatar */
    public static String AVATAR_V1_SET = addressService + "/bm/users/v1/setting/avatar";
    /** 同步组织架构 */
    public static String BM_USERS_V1_SYNC_WORKMATE = addressService + "/bm/users/v1/sync_workmate";
    /** 搜索群组 SearchGroup  SearchGroupResult */
    public static String BM_USERS_V1_GROUP_SEARCH = addressService + "/bm/users/v1/group/search";

    /** =================================== setting group ==================================== */
    /** create group */
    public static String CREATE_GROUP = addressService + "/bm/users/v1/group";
    /** remove member */
    public static String GROUP_REMOVE = addressService + "/bm/users/v1/group/deluser";
    /** exit from group */
    public static String GROUP_QUIT = addressService + "/bm/users/v1/group/quit";
    /** command group */
    public static String GROUP_COMMON = addressService + "/bm/users/v1/group/set_common";
    /** remove command */
    public static String GROUP_RECOMMON = addressService + "/bm/users/v1/group/remove_common";
    /** modify member nick */
    public static String GROUP_MEMUPDATE = addressService + "/bm/users/v1/group/member_update";
    /** modify group nick */
    public static String GROUP_UPDATE = addressService + "/bm/users/v1/group/update";
    /** get group detail */
    public static String GROUP_PULLINFO = addressService + "/bm/users/v1/group/info";
    /** get group setting */
    public static String GROUP_SETTING_INFO = addressService + "/bm/users/v1/group/setting_info";
    /** group mute */
    public static String CONNECT_GROUP_MUTE = addressService + "/bm/users/v1/group/mute";
    /** 获取常用群 */
    public static String BM_USERS_V1_GROUP_LIST = addressService + "/bm/users/v1/group/list";
    /** 获取群成员 */
    public static String BM_USERS_V1_GROUP_MEMBERS = addressService + "/bm/users/v1/group/members";
    /** 群设置 */
    public static String BM_USERS_V1_GROUP_SETTING = addressService + "/bm/users/v1/group/setting";
    /** 解散群组 */
    public static String BM_USERS_V1_GROUP_DISBANDED = addressService + "/bm/users/v1/group/disbanded";


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
