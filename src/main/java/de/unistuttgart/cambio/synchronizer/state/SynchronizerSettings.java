package de.unistuttgart.cambio.synchronizer.state;

import java.io.File;

public final class SynchronizerSettings {
    public static SynchronizerSettings synchronizerSettings_instance = new SynchronizerSettings();

    //Optional Settings
    private String influxDBURL;

    //Mandatory Settings
    //with defaults
    private String[] loadgeneratorIPs = new String[]{};
    private File filesystemOutputRoot = new File("./results");

    //without defaults



    public static SynchronizerSettings getSettings() {
        return synchronizerSettings_instance;
    }

    public String[] getLoadgeneratorIPs() {
        return loadgeneratorIPs;
    }

    public void setLoadgeneratorIPs(String[] loadgeneratorIPs) {
        this.loadgeneratorIPs = loadgeneratorIPs;
    }

    public String getInfluxDBURL() {
        return influxDBURL;
    }

    public File getFilesystemOutputRoot() {
        return filesystemOutputRoot;
    }

    public void setFilesystemOutputRoot(File filesystemOutputRoot) {
        this.filesystemOutputRoot = filesystemOutputRoot;
    }
}
