package connect.database.green.dao;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import connect.database.green.bean.GroupEntity;
import connect.database.green.bean.MessageEntity;
import connect.database.green.bean.TransactionEntity;
import connect.database.green.bean.ConversionEntity;
import connect.database.green.bean.CurrencyAddressEntity;
import connect.database.green.bean.FriendRequestEntity;
import connect.database.green.bean.ConversionSettingEntity;
import connect.database.green.bean.ContactEntity;
import connect.database.green.bean.ParamEntity;
import connect.database.green.bean.GroupMemberEntity;
import connect.database.green.bean.RecommandFriendEntity;
import connect.database.green.bean.CurrencyEntity;

import connect.database.green.dao.GroupEntityDao;
import connect.database.green.dao.MessageEntityDao;
import connect.database.green.dao.TransactionEntityDao;
import connect.database.green.dao.ConversionEntityDao;
import connect.database.green.dao.CurrencyAddressEntityDao;
import connect.database.green.dao.FriendRequestEntityDao;
import connect.database.green.dao.ConversionSettingEntityDao;
import connect.database.green.dao.ContactEntityDao;
import connect.database.green.dao.ParamEntityDao;
import connect.database.green.dao.GroupMemberEntityDao;
import connect.database.green.dao.RecommandFriendEntityDao;
import connect.database.green.dao.CurrencyEntityDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig groupEntityDaoConfig;
    private final DaoConfig messageEntityDaoConfig;
    private final DaoConfig transactionEntityDaoConfig;
    private final DaoConfig conversionEntityDaoConfig;
    private final DaoConfig currencyAddressEntityDaoConfig;
    private final DaoConfig friendRequestEntityDaoConfig;
    private final DaoConfig conversionSettingEntityDaoConfig;
    private final DaoConfig contactEntityDaoConfig;
    private final DaoConfig paramEntityDaoConfig;
    private final DaoConfig groupMemberEntityDaoConfig;
    private final DaoConfig recommandFriendEntityDaoConfig;
    private final DaoConfig currencyEntityDaoConfig;

    private final GroupEntityDao groupEntityDao;
    private final MessageEntityDao messageEntityDao;
    private final TransactionEntityDao transactionEntityDao;
    private final ConversionEntityDao conversionEntityDao;
    private final CurrencyAddressEntityDao currencyAddressEntityDao;
    private final FriendRequestEntityDao friendRequestEntityDao;
    private final ConversionSettingEntityDao conversionSettingEntityDao;
    private final ContactEntityDao contactEntityDao;
    private final ParamEntityDao paramEntityDao;
    private final GroupMemberEntityDao groupMemberEntityDao;
    private final RecommandFriendEntityDao recommandFriendEntityDao;
    private final CurrencyEntityDao currencyEntityDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        groupEntityDaoConfig = daoConfigMap.get(GroupEntityDao.class).clone();
        groupEntityDaoConfig.initIdentityScope(type);

        messageEntityDaoConfig = daoConfigMap.get(MessageEntityDao.class).clone();
        messageEntityDaoConfig.initIdentityScope(type);

        transactionEntityDaoConfig = daoConfigMap.get(TransactionEntityDao.class).clone();
        transactionEntityDaoConfig.initIdentityScope(type);

        conversionEntityDaoConfig = daoConfigMap.get(ConversionEntityDao.class).clone();
        conversionEntityDaoConfig.initIdentityScope(type);

        currencyAddressEntityDaoConfig = daoConfigMap.get(CurrencyAddressEntityDao.class).clone();
        currencyAddressEntityDaoConfig.initIdentityScope(type);

        friendRequestEntityDaoConfig = daoConfigMap.get(FriendRequestEntityDao.class).clone();
        friendRequestEntityDaoConfig.initIdentityScope(type);

        conversionSettingEntityDaoConfig = daoConfigMap.get(ConversionSettingEntityDao.class).clone();
        conversionSettingEntityDaoConfig.initIdentityScope(type);

        contactEntityDaoConfig = daoConfigMap.get(ContactEntityDao.class).clone();
        contactEntityDaoConfig.initIdentityScope(type);

        paramEntityDaoConfig = daoConfigMap.get(ParamEntityDao.class).clone();
        paramEntityDaoConfig.initIdentityScope(type);

        groupMemberEntityDaoConfig = daoConfigMap.get(GroupMemberEntityDao.class).clone();
        groupMemberEntityDaoConfig.initIdentityScope(type);

        recommandFriendEntityDaoConfig = daoConfigMap.get(RecommandFriendEntityDao.class).clone();
        recommandFriendEntityDaoConfig.initIdentityScope(type);

        currencyEntityDaoConfig = daoConfigMap.get(CurrencyEntityDao.class).clone();
        currencyEntityDaoConfig.initIdentityScope(type);

        groupEntityDao = new GroupEntityDao(groupEntityDaoConfig, this);
        messageEntityDao = new MessageEntityDao(messageEntityDaoConfig, this);
        transactionEntityDao = new TransactionEntityDao(transactionEntityDaoConfig, this);
        conversionEntityDao = new ConversionEntityDao(conversionEntityDaoConfig, this);
        currencyAddressEntityDao = new CurrencyAddressEntityDao(currencyAddressEntityDaoConfig, this);
        friendRequestEntityDao = new FriendRequestEntityDao(friendRequestEntityDaoConfig, this);
        conversionSettingEntityDao = new ConversionSettingEntityDao(conversionSettingEntityDaoConfig, this);
        contactEntityDao = new ContactEntityDao(contactEntityDaoConfig, this);
        paramEntityDao = new ParamEntityDao(paramEntityDaoConfig, this);
        groupMemberEntityDao = new GroupMemberEntityDao(groupMemberEntityDaoConfig, this);
        recommandFriendEntityDao = new RecommandFriendEntityDao(recommandFriendEntityDaoConfig, this);
        currencyEntityDao = new CurrencyEntityDao(currencyEntityDaoConfig, this);

        registerDao(GroupEntity.class, groupEntityDao);
        registerDao(MessageEntity.class, messageEntityDao);
        registerDao(TransactionEntity.class, transactionEntityDao);
        registerDao(ConversionEntity.class, conversionEntityDao);
        registerDao(CurrencyAddressEntity.class, currencyAddressEntityDao);
        registerDao(FriendRequestEntity.class, friendRequestEntityDao);
        registerDao(ConversionSettingEntity.class, conversionSettingEntityDao);
        registerDao(ContactEntity.class, contactEntityDao);
        registerDao(ParamEntity.class, paramEntityDao);
        registerDao(GroupMemberEntity.class, groupMemberEntityDao);
        registerDao(RecommandFriendEntity.class, recommandFriendEntityDao);
        registerDao(CurrencyEntity.class, currencyEntityDao);
    }
    
    public void clear() {
        groupEntityDaoConfig.clearIdentityScope();
        messageEntityDaoConfig.clearIdentityScope();
        transactionEntityDaoConfig.clearIdentityScope();
        conversionEntityDaoConfig.clearIdentityScope();
        currencyAddressEntityDaoConfig.clearIdentityScope();
        friendRequestEntityDaoConfig.clearIdentityScope();
        conversionSettingEntityDaoConfig.clearIdentityScope();
        contactEntityDaoConfig.clearIdentityScope();
        paramEntityDaoConfig.clearIdentityScope();
        groupMemberEntityDaoConfig.clearIdentityScope();
        recommandFriendEntityDaoConfig.clearIdentityScope();
        currencyEntityDaoConfig.clearIdentityScope();
    }

    public GroupEntityDao getGroupEntityDao() {
        return groupEntityDao;
    }

    public MessageEntityDao getMessageEntityDao() {
        return messageEntityDao;
    }

    public TransactionEntityDao getTransactionEntityDao() {
        return transactionEntityDao;
    }

    public ConversionEntityDao getConversionEntityDao() {
        return conversionEntityDao;
    }

    public CurrencyAddressEntityDao getCurrencyAddressEntityDao() {
        return currencyAddressEntityDao;
    }

    public FriendRequestEntityDao getFriendRequestEntityDao() {
        return friendRequestEntityDao;
    }

    public ConversionSettingEntityDao getConversionSettingEntityDao() {
        return conversionSettingEntityDao;
    }

    public ContactEntityDao getContactEntityDao() {
        return contactEntityDao;
    }

    public ParamEntityDao getParamEntityDao() {
        return paramEntityDao;
    }

    public GroupMemberEntityDao getGroupMemberEntityDao() {
        return groupMemberEntityDao;
    }

    public RecommandFriendEntityDao getRecommandFriendEntityDao() {
        return recommandFriendEntityDao;
    }

    public CurrencyEntityDao getCurrencyEntityDao() {
        return currencyEntityDao;
    }

}
