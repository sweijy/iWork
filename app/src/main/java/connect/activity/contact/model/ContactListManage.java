package connect.activity.contact.model;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import connect.activity.base.BaseApplication;
import connect.activity.base.compare.FriendCompara;
import connect.activity.home.bean.ContactBean;
import connect.database.green.DaoHelper.ContactHelper;
import connect.database.green.bean.ContactEntity;
import connect.database.green.bean.GroupEntity;
import connect.ui.activity.R;
import connect.utils.PinyinUtil;
import protos.Connect;

public class ContactListManage {

    private FriendCompara friendCompare = new FriendCompara();

    /**
     * group list
     * @return
     */
    public List<ContactBean> getGroupData(){
        List<GroupEntity> localGroup = ContactHelper.getInstance().loadGroupCommonAll();
        ArrayList<ContactBean> groupList = new ArrayList<>();
        for(GroupEntity groupEntity : localGroup){
            ContactBean contactBean = new ContactBean();
            contactBean.setName(groupEntity.getName());
            contactBean.setUid(groupEntity.getIdentifier());
            contactBean.setAvatar(groupEntity.getAvatar());
            contactBean.setStatus(2);
            groupList.add(contactBean);
        }
        return groupList;
    }

    /**
     * friend list
     * @return
     */
    public ArrayList<ContactBean> getFriendList(){
        List<ContactEntity> loacalFriend = ContactHelper.getInstance().loadAll();
        return sortContactFriend("",loacalFriend);
    }

    public ArrayList<ContactBean> getFriendListExcludeSys(String pubKeyExc){
        List<ContactEntity> loacalFriend = ContactHelper.getInstance().loadFriend();
        return sortContactFriend(pubKeyExc,loacalFriend);
    }

    private ArrayList<ContactBean> sortContactFriend(String pubKeyExc ,List<ContactEntity> loacalFriend){
        String sysName = BaseApplication.getInstance().getString(R.string.app_name);
        Collections.sort(loacalFriend, friendCompare);
        ArrayList<ContactBean> friendList = new ArrayList<>();
        for(ContactEntity friendEntity : loacalFriend){
            if(friendEntity.getUid().equals(pubKeyExc))
                continue;

            ContactBean contactBean = new ContactBean();
            contactBean.setName(friendEntity.getName());
            contactBean.setAvatar(friendEntity.getAvatar());
            contactBean.setUid(friendEntity.getUid());
            contactBean.setOu(friendEntity.getOu());
            contactBean.setGender(friendEntity.getGender() == null ? 1 : friendEntity.getGender());
            if(friendEntity.getUid().equals(sysName) && friendEntity.getName().equals(sysName)){
                contactBean.setStatus(6);
                friendList.add(contactBean);
            }else{
                contactBean.setStatus(1);
                friendList.add(contactBean);
            }
        }
        return friendList;
    }

    /**
     * 通信录item顶部字母逻辑
     * 2（群组）、3（常用好友） --------- “show”
     * 4（好友）、6（机器人） ----------- “首字母”
     * 其他 ------------ ""
     * @param currBean
     * @param lastBean
     * @return
     */
    public String checkShowFriendTop(ContactBean currBean,ContactBean lastBean){
        char curFirstChar = TextUtils.isEmpty(currBean.getName()) ? '#' : currBean.getName().charAt(0);
        if(lastBean == null){
            if(currBean.getStatus() == 2 || currBean.getStatus() == 3){
                return "show";
            }else if(currBean.getStatus() == 4 || currBean.getStatus() == 6){
                return PinyinUtil.chatToPinyin(curFirstChar);
            }else{
                return "";
            }
        }
        char lastFirstChar = TextUtils.isEmpty(lastBean.getName()) ? '#' : lastBean.getName().charAt(0);

        if(currBean.getStatus() == 2 || currBean.getStatus() == 3){ // Group and Frequent contacts
            if(lastBean.getStatus() != currBean.getStatus()){
                return "show";
            }else{
                return "";
            }
        }else{ // Friend
            if(lastBean.getStatus() != 4 && lastBean.getStatus() != 6){
                return PinyinUtil.chatToPinyin(curFirstChar);
            }else{
                String currLetter = PinyinUtil.chatToPinyin(curFirstChar);
                String lastLetter = PinyinUtil.chatToPinyin(lastFirstChar);
                if(currLetter.equals(lastLetter)){
                    return "";
                }else{
                    return currLetter;
                }
            }
        }
    }

    public ContactEntity convertContactEntity(Connect.Workmate workmate) {
        if (workmate == null)
            return null;
        ContactEntity contactEntity = new ContactEntity();
        contactEntity.setName(workmate.getName());
        contactEntity.setAvatar(workmate.getAvatar());
        contactEntity.setPublicKey(workmate.getPubKey());
        contactEntity.setEmpNo(workmate.getEmpNo());
        contactEntity.setMobile(workmate.getMobile());
        contactEntity.setGender(workmate.getGender());
        contactEntity.setTips(workmate.getTips());
        contactEntity.setRegisted(workmate.getRegisted());
        contactEntity.setUid(workmate.getUid());
        contactEntity.setOu(workmate.getOU());
        return contactEntity;
    }

}
