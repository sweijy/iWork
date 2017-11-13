package instant.parser.inter;

import protos.Connect;

/**
 * Created by Administrator on 2017/10/9.
 */

public interface UnreachableListener {

    void notFriendNotice(String publicKey);

    void blackFriendNotice(String publicKey);

    void notGroupMemberNotice(String groupKey);

    void saltNotMatch(String msgid, String rejectUid, Connect.ChatCookie cookie) throws Exception;

    void halfRandom(String msgid, String rejectUid) throws Exception;
}
