package me.superbiebel.punishapi.offenseprocessing;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import lombok.Getter;
import me.superbiebel.punishapi.PunishCore;
import me.superbiebel.punishapi.abstractions.Service;
import me.superbiebel.punishapi.abstractions.ServiceRegistry;
import me.superbiebel.punishapi.data.Datamanager;
import me.superbiebel.punishapi.dataobjects.OffenseHistoryRecord;
import me.superbiebel.punishapi.dataobjects.OffenseProcessingRequest;
import me.superbiebel.punishapi.dataobjects.OffenseProcessingTemplate;
import me.superbiebel.punishapi.exceptions.FailedDataOperationException;
import me.superbiebel.punishapi.exceptions.FailedServiceOperationException;
import me.superbiebel.punishapi.exceptions.OffenseProcessingException;
import me.superbiebel.punishapi.exceptions.ServiceNotFoundException;
import me.superbiebel.punishapi.offenseprocessing.premade.jsoffenseprocessor.JSOffenseProcessor;
import org.jetbrains.annotations.NotNull;

public class OffenseManager extends ServiceRegistry<String> {
    
    private final PunishCore core;
    @Getter
    private final JSOffenseProcessor defaultOffenseProcessor;
    
    public OffenseManager(ConcurrentMap<String, Service<String>> serviceRegistryMap, PunishCore core, JSOffenseProcessor defaultOffenseProcessor) {
        super(serviceRegistryMap);
        this.core = core;
        this.defaultOffenseProcessor = defaultOffenseProcessor;
    }
    
    public OffenseManager(PunishCore core) {
        super(new ConcurrentHashMap<>());
        this.core = core;
        this.defaultOffenseProcessor = new JSOffenseProcessor(this.core.getDataAPI());
    }
    
    /**
     * 1. Receives the request
     * 2. Gets the UUID of the offenseProcessingTemplate it wants to trigger and will download this template
     * 3. A custom offenseprocessor that is already supplied will be used otherwise the default processor will be used (JS)
     * 4. The offenseprocessor will process this offense and then generate an OffenseHistoryRecord
     * 5. The generated offenseHistoryRecord will then be stored inside the database.
     */
    
    public OffenseHistoryRecord submitOffense(@NotNull OffenseProcessingRequest offenseProcessingRequest) throws FailedServiceOperationException, OffenseProcessingException, FailedDataOperationException, ServiceNotFoundException {
        Datamanager datamanager = core.getDatamanager();
        try {
            //Indicate that processing on this user begins.
            datamanager.lockUser(offenseProcessingRequest.getCriminalUUID());
            
            //Download this template.
            OffenseProcessingTemplate template = datamanager.retrieveOffenseProcessingTemplate(offenseProcessingRequest.getProcessingTemplateUUID());
            
            //Get the appropriate offense processor.
            IOffenseProcessor processor = getOffenseProcessor(template);
            
            if (processor.isScriptBased()) {
                //confirm that if the processor is script based, the file can actually be found and run.
                if (template.getScriptFile() == null) {
                    throw new IllegalArgumentException("Script file cannot be null in a script based offenseprocessor!");
                }
                if (!template.getScriptFile().exists()) {
                    throw new IllegalStateException("Script file that should be run does not exist!");
                }
                if (!template.getScriptFile().isFile()) {
                    throw new IllegalStateException("Script file is not a file!");
                }
            }
            return processor.processOffense(offenseProcessingRequest,template.getScriptFile());
        } finally {
            datamanager.unlockUser(offenseProcessingRequest.getCriminalUUID());
        }
    }
    public void submitOffenseWithoutProcessing(OffenseHistoryRecord offenseHistoryRecord) throws FailedDataOperationException {
        core.getDatamanager().storeOffenseRecord(offenseHistoryRecord);
    }
    
    private IOffenseProcessor getOffenseProcessor(@NotNull OffenseProcessingTemplate template) throws ServiceNotFoundException {
        IOffenseProcessor processor;
        if (template.getOffenseProcessorID().equals("DEFAULTJSPROCESSOR")) {
            processor = defaultOffenseProcessor;
        } else {
            processor = (IOffenseProcessor) super.getService(template.getOffenseProcessorID());
        }
        return processor;
    }
    
    @Override
    protected void onServiceRegistryStartup(boolean force) {
        //implement if needed
    }
    
    @Override
    protected void onServiceRegistryShutdown() {
        //implement if needed
    }
    
    @Override
    protected void onServiceRegistryKill() {
        //implement if needed
    }
}
