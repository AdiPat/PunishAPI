package me.superbiebel.punishapi.data;


import lombok.Getter;
import me.superbiebel.punishapi.abstractions.ServiceRegistry;
import me.superbiebel.punishapi.data.services.OffenseProcessingTemplateStorage;
import me.superbiebel.punishapi.data.services.OffenseRecordStorage;
import me.superbiebel.punishapi.data.services.UserAccountService;
import me.superbiebel.punishapi.data.services.UserLockService;
import me.superbiebel.punishapi.dataobjects.OffenseHistoryRecord;
import me.superbiebel.punishapi.dataobjects.OffenseProcessingTemplate;
import me.superbiebel.punishapi.dataobjects.UserAccount;
import me.superbiebel.punishapi.exceptions.ServiceNotFoundException;
import org.apache.logging.log4j.LogManager;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * FOR INTERNAL USE ONLY!!!!
 */
public class Datamanager extends ServiceRegistry<Datamanager.DataServiceType> {
    
    @Getter
    private static final int MAXSERVICECOUNT = 2;
    
    public Datamanager() {
        super(new ConcurrentHashMap<>());
    }
    
    public enum DataServiceType {
        TEST,OFFENSE_PROCESSING_TEMPLATE_STORAGE,OFFENSE_RECORD_STORAGE,USER_LOCKING,USER_ACCOUNT_STORAGE
    }
    public void lockUser(UUID uuid) throws ServiceNotFoundException {
        UserLockService userLockService = (UserLockService) getService(Datamanager.DataServiceType.USER_LOCKING);
        userLockService.lockUser(uuid);
    }
    public void unlockUser(UUID uuid) throws ServiceNotFoundException {
        UserLockService userLockService = (UserLockService) getService(Datamanager.DataServiceType.USER_LOCKING);
        userLockService.unlockUser(uuid);
    }
    public void storeOffense(OffenseHistoryRecord offenseHistoryRecord) throws ServiceNotFoundException {
        OffenseRecordStorage service = (OffenseRecordStorage) getService(Datamanager.DataServiceType.OFFENSE_RECORD_STORAGE);
        service.storeOffenseRecord(offenseHistoryRecord);
    }
    public OffenseHistoryRecord retrieveOffense(UUID offenseUUID) throws ServiceNotFoundException {
        OffenseRecordStorage service = (OffenseRecordStorage) getService(Datamanager.DataServiceType.OFFENSE_RECORD_STORAGE);
        return service.retrieveOffenseRecord(offenseUUID);
    }
    public void createOffenseProcessingTemplate(OffenseProcessingTemplate template) throws ServiceNotFoundException {
        OffenseProcessingTemplateStorage service = (OffenseProcessingTemplateStorage) getService(Datamanager.DataServiceType.OFFENSE_PROCESSING_TEMPLATE_STORAGE);
        service.storeOffenseProcessingTemplate(template);
    }
    public OffenseProcessingTemplate retrieveOffenseProcessingTemplate(UUID templateUUID) throws ServiceNotFoundException {
        OffenseProcessingTemplateStorage service = (OffenseProcessingTemplateStorage) getService(Datamanager.DataServiceType.OFFENSE_PROCESSING_TEMPLATE_STORAGE);
        return service.retrieveOffenseProcessingTemplate(templateUUID);
    }
    public String testecho(String echo) {
        return echo;
    }
    
    UserAccount createUser(UUID userUUID, Map<String, String> attributes) throws ServiceNotFoundException {
        UserAccountService service = (UserAccountService) getService(DataServiceType.USER_ACCOUNT_STORAGE);
        service.createUser(userUUID,attributes);
        return UserAccount.builder().userUUID(userUUID).attributes(attributes).build();
    }
    
    UserAccount retrieveUser(UUID userUUID) throws ServiceNotFoundException {
        UserAccountService service = (UserAccountService) getService(DataServiceType.USER_ACCOUNT_STORAGE);
        return service.retrieveUser(userUUID);
    }
    
    void setAttribute(String key, String value) throws ServiceNotFoundException {
        UserAccountService service = (UserAccountService) getService(DataServiceType.USER_ACCOUNT_STORAGE);
        service.setAttribute(key, value);
    }
    
    void removeAttribute(String key) throws ServiceNotFoundException {
        UserAccountService service = (UserAccountService) getService(DataServiceType.USER_ACCOUNT_STORAGE);
        service.removeAttribute(key);
    }
    
    List<UserAccount> getByAttribute(String key, String value) throws ServiceNotFoundException {
        UserAccountService service = (UserAccountService) getService(DataServiceType.USER_ACCOUNT_STORAGE);
        return service.getByAttribute(key,value);
    }
    
    List<UserAccount> getBykey(String key) throws ServiceNotFoundException {
        UserAccountService service = (UserAccountService) getService(DataServiceType.USER_ACCOUNT_STORAGE);
        return service.getBykey(key);
    }
    
    List<UserAccount> getByValue(String value) throws ServiceNotFoundException {
        UserAccountService service = (UserAccountService) getService(DataServiceType.USER_ACCOUNT_STORAGE);
        return service.getByValue(value);
    }
    
    
    @Override
    protected void onServiceRegistryStartup(boolean force) {
        //to be implemented if needed
    }
    @Override
    protected void onServiceRegistryShutdown() {
        serviceRegistryMap.keys().asIterator().forEachRemaining(dataServiceType -> {
            try {
                this.removeService(dataServiceType, false);
            } catch (Exception e) {
                LogManager.getLogger().error("Could not unregister and shutdown servicetype: {}", dataServiceType, e);
            }
        });
    }
    @Override
    protected void onServiceRegistryKill() {
        //to be implemented if needed
    }
    public int serviceCount() {
        return serviceRegistryMap.size();
    }
}
