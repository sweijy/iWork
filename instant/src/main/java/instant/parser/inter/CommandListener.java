package instant.parser.inter;

import java.util.List;

import protos.Connect;

/**
 * SDK 解析具体的 分发的pb到APP  ，该接口在APP解析中实现 处理数据库及消息分发
 * Created by puin on 17-10-8.
 */

public interface CommandListener {

    void commandReceipt(boolean isSuccess,Object reqObj,Object serviceObj);

    void updateMsgSendState(String publickey,String msgid, int state);

    void loadAllContacts(List<Connect.Workmate> workmates) throws Exception;

    void contactChanges(Connect.WorkmateChangeRecords changeRecords);

    void commonGroups(Connect.UserCommonGroups commonGroups);

    void conversationMute(Connect.ManageSession manageSession);

    void updateGroupChange( Connect.GroupChange groupChange) throws Exception;

}
