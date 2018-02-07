package connect.database.green.dao;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import connect.database.green.bean.ConversionSettingEntity;
import connect.database.green.bean.FriendRequestEntity;
import connect.database.green.bean.CurrencyEntity;
import connect.database.green.bean.ContactEntity;
import connect.database.green.bean.ParamEntity;
import connect.database.green.bean.CurrencyAddressEntity;
import connect.database.green.bean.GroupMemberEntity;
import connect.database.green.bean.GroupEntity;
import connect.database.green.bean.ConversionEntity;
import connect.database.green.bean.MessageEntity;
import connect.database.green.bean.TransactionEntity;
import connect.database.green.bean.ApplicationEntity;
import connect.database.green.bean.OrganizerEntity;

import connect.database.green.dao.ConversionSettingEntityDao;
import connect.database.green.dao.FriendRequestEntityDao;
import connect.database.green.dao.CurrencyEntityDao;
import connect.database.green.dao.ContactEntityDao;
import connect.database.green.dao.ParamEntityDao;
import connect.database.green.dao.CurrencyAddressEntityDao;
import connect.database.green.dao.GroupMemberEntityDao;
import connect.database.green.dao.GroupEntityDao;
import connect.database.green.dao.ConversionEntityDao;
import connect.database.green.dao.MessageEntityDao;
import connect.database.green.dao.TransactionEntityDao;
import connect.database.green.dao.ApplicationEntityDao;
import connect.database.green.dao.OrganizerEntityDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig conversionSettingEntityDaoConfig;
    private final DaoConfig friendRequestEntityDaoConfig;
    private final DaoConfig currencyEntityDaoConfig;
    private final DaoConfig contactEntityDaoConfig;
    private final DaoConfig paramEntityDaoConfig;
    private final DaoConfig currencyAddressEntityDaoConfig;
    private final DaoConfig groupMemberEntityDaoConfig;
    private final DaoConfig groupEntityDaoConfig;
    private final DaoConfig conversionEntityDaoConfig;
    private final DaoConfig messageEntityDaoConfig;
    private final DaoConfig transactionEntityDaoConfig;
    private final DaoConfig applicationEntityDaoConfig;
    private final DaoConfig organizerEntityDaoConfig;

    private final ConversionSettingEntityDao conversionSettingEntityDao;
    private final FriendRequestEntityDao friendRequestEntityDao;
    private final CurrencyEntityDao currencyEntityDao;
    private final ContactEntityDao contactEntityDao;
    private final ParamEntityDao paramEntityDao;
    private final CurrencyAddressEntityDao currencyAddressEntityDao;
    private final GroupMemberEntityDao groupMemberEntityDao;
    private final GroupEntityDao groupEntityDao;
    private final ConversionEntityDao conversionEntityDao;
    private final MessageEntityDao messageEntityDao;
    private final TransactionEntityDao transactionEntityDao;
    private final ApplicationEntityDao applicationEntityDao;
    private final OrganizerEntityDao organizerEntityDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        conversionSettingEntityDaoConfig = daoConfigMap.get(ConversionSettingEntityDao.class).clone();
        conversionSettingEntityDaoConfig.initIdentityScope(type);

        friendRequestEntityDaoConfig = daoConfigMap.get(FriendRequestEntityDao.class).clone();
        friendRequestEntityDaoConfig.initIdentityScope(type);

        currencyEntityDaoConfig = daoConfigMap.get(CurrencyEntityDao.class).clone();
        currencyEntityDaoConfig.initIdentityScope(type);

        contactEntityDaoConfig = daoConfigMap.get(ContactEntityDao.class).clone();
        contactEntityDaoConfig.initIdentityScope(type);

        paramEntityDaoConfig = daoConfigMap.get(ParamEntityDao.class).clone();
        paramEntityDaoConfig.initIdentityScope(type);

        currencyAddressEntityDaoConfig = daoConfigMap.get(CurrencyAddressEntityDao.class).clone();
        currencyAddressEntityDaoConfig.initIdentityScope(type);

        groupMemberEntityDaoConfig = daoConfigMap.get(GroupMemberEntityDao.class).clone();
        groupMemberEntityDaoConfig.initIdentityScope(type);

        groupEntityDaoConfig = daoConfigMap.get(GroupEntityDao.class).clone();
        groupEntityDaoConfig.initIdentityScope(type);

        conversionEntityDaoConfig = daoConfigMap.get(ConversionEntityDao.class).clone();
        conversionEntityDaoConfig.initIdentityScope(type);

        messageEntityDaoConfig = daoConfigMap.get(MessageEntityDao.class).clone();
        messageEntityDaoConfig.initIdentityScope(type);

        transactionEntityDaoConfig = daoConfigMap.get(TransactionEntityDao.class).clone();
        transactionEntityDaoConfig.initIdentityScope(type);

        applicationEntityDaoConfig = daoConfigMap.get(ApplicationEntityDao.class).clone();
        applicationEntityDaoConfig.initIdentityScope(type);

        organizerEntityDaoConfig = daoConfigMap.get(OrganizerEntityDao.class).clone();
        organizerEntityDaoConfig.initIdentityScope(type);

        conversionSettingEntityDao = new ConversionSettingEntityDao(conversionSettingEntityDaoConfig, this);
        friendRequestEntityDao = new FriendRequestEntityDao(friendRequestEntityDaoConfig, this);
        currencyEntityDao = new CurrencyEntityDao(currencyEntityDaoConfig, this);
        contactEntityDao = new ContactEntityDao(contactEntityDaoConfig, this);
        paramEntityDao = new ParamEntityDao(paramEntityDaoConfig, this);
        currencyAddressEntityDao = new CurrencyAddressEntityDao(currencyAddressEntityDaoConfig, this);
        groupMemberEntityDao = new GroupMemberEntityDao(groupMemberEntityDaoConfig, this);
        groupEntityDao = new GroupEntityDao(groupEntityDaoConfig, this);
        conversionEntityDao = new ConversionEntityDao(conversionEntityDaoConfig, this);
        messageEntityDao = new MessageEntityDao(messageEntityDaoConfig, this);
        transactionEntityDao = new TransactionEntityDao(transactionEntityDaoConfig, this);
        applicationEntityDao = new ApplicationEntityDao(applicationEntityDaoConfig, this);
        organizerEntityDao = new OrganizerEntityDao(organizerEntityDaoConfig, this);

        registerDao(ConversionSettingEntity.class, conversionSettingEntityDao);
        registerDao(FriendRequestEntity.class, friendRequestEntityDao);
        registerDao(CurrencyEntity.class, currencyEntityDao);
        registerDao(ContactEntity.class, contactEntityDao);
        registerDao(ParamEntity.class, paramEntityDao);
        registerDao(CurrencyAddressEntity.class, currencyAddressEntityDao);
        registerDao(GroupMemberEntity.class, groupMemberEntityDao);
        registerDao(GroupEntity.class, groupEntityDao);
        registerDao(ConversionEntity.class, conversionEntityDao);
        registerDao(MessageEntity.class, messageEntityDao);
        registerDao(TransactionEntity.class, transactionEntityDao);
        registerDao(ApplicationEntity.class, applicationEntityDao);
        registerDao(OrganizerEntity.class, organizerEntityDao);
    }
    
    public void clear() {
        conversionSettingEntityDaoConfig.clearIdentityScope();
        friendRequestEntityDaoConfig.clearIdentityScope();
        currencyEntityDaoConfig.clearIdentityScope();
        contactEntityDaoConfig.clearIdentityScope();
        paramEntityDaoConfig.clearIdentityScope();
        currencyAddressEntityDaoConfig.clearIdentityScope();
        groupMemberEntityDaoConfig.clearIdentityScope();
        groupEntityDaoConfig.clearIdentityScope();
        conversionEntityDaoConfig.clearIdentityScope();
        messageEntityDaoConfig.clearIdentityScope();
        transactionEntityDaoConfig.clearIdentityScope();
        applicationEntityDaoConfig.clearIdentityScope();
        organizerEntityDaoConfig.clearIdentityScope();
    }

    public ConversionSettingEntityDao getConversionSettingEntityDao() {
        return conversionSettingEntityDao;
    }

    public FriendRequestEntityDao getFriendRequestEntityDao() {
        return friendRequestEntityDao;
    }

    public CurrencyEntityDao getCurrencyEntityDao() {
        return currencyEntityDao;
    }

    public ContactEntityDao getContactEntityDao() {
        return contactEntityDao;
    }

    public ParamEntityDao getParamEntityDao() {
        return paramEntityDao;
    }

    public CurrencyAddressEntityDao getCurrencyAddressEntityDao() {
        return currencyAddressEntityDao;
    }

    public GroupMemberEntityDao getGroupMemberEntityDao() {
        return groupMemberEntityDao;
    }

    public GroupEntityDao getGroupEntityDao() {
        return groupEntityDao;
    }

    public ConversionEntityDao getConversionEntityDao() {
        return conversionEntityDao;
    }

    public MessageEntityDao getMessageEntityDao() {
        return messageEntityDao;
    }

    public TransactionEntityDao getTransactionEntityDao() {
        return transactionEntityDao;
    }

    public ApplicationEntityDao getApplicationEntityDao() {
        return applicationEntityDao;
    }

    public OrganizerEntityDao getOrganizerEntityDao() {
        return organizerEntityDao;
    }

}
