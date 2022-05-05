package de.unistuttgart.cambio.synchronizer.runs;

import org.apache.commons.lang3.StringUtils;

public class FaultLoadDefinition extends LoadDefinition {

    private final boolean hasWorkloadDefinition;
    private final FaultLoadType type;
    private final String data;

    public FaultLoadDefinition(FaultLoadType type) {
        this(type,null);
        if (type != FaultLoadType.NONE)
            throw new IllegalArgumentException("Only NONE is allowed for this constructor. Please use NONE type or provide a data String.");
    }

    public FaultLoadDefinition(FaultLoadType type, String data) {
        this.type = type;
        this.data = data;
        hasWorkloadDefinition = !StringUtils.isAllBlank(data);
    }

    @Override
    public boolean hasLoadDefinition() {
        return hasWorkloadDefinition;
    }

    public FaultLoadType getFaultLoadType() {
        return type;
    }

    public String getData() {
        return data;
    }
}
