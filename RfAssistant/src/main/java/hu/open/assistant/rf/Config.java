package hu.open.assistant.rf;

import hu.open.assistant.rf.model.report.limits.CmuReportLimits;
import hu.open.assistant.rf.model.report.limits.CmwReportLimits;

/**
 * Stores both local and global configurations for the application. This includes folder paths, report processing
 * parameters, default values, version control, preload behavior and admin password.
 */
public class Config {

    private double minCmuScriptVersion = 0;
    private double minCmwScriptVersion = 0;
    private double minVersion = 0;
    private int defaultValue = 0;
    private String supportPassword = "";
    private String defaultSource = "";
    private String defaultManufacturer = "";
    private String defaultPositions = "";
    private String defaultScripts = "";
    private String filterOption = "";
    private String displayOption = "";
    private String networkFolder = "";
    private String cmuReportPath = "";
    private String cmuDatabasePath = "";
    private String cmuCachePath = "";
    private String cmuShortcutPath = "";
    private String cmwReportPath = "";
    private String cmwCachePath = "";
    private String cmwDatabasePath = "";
    private CmuReportLimits cmuLimits = new CmuReportLimits();
    private CmwReportLimits cmwLimits = new CmwReportLimits();
    private boolean preloadCmu;
    private boolean preloadCmw;
    private boolean preloadReports;


    public Config() {

    }

    public CmuReportLimits getCmuLimits() {
        return cmuLimits;
    }

    public void setCmuLimits(CmuReportLimits cmuLimits) {
        this.cmuLimits = cmuLimits;
    }

    public CmwReportLimits getCmwLimits() {
        return cmwLimits;
    }

    public void setCmwLimits(CmwReportLimits cmwLimits) {
        this.cmwLimits = cmwLimits;
    }

    public String getNetworkFolder() {
        return networkFolder;
    }

    public void setNetworkFolder(String networkFolder) {
        this.networkFolder = networkFolder;
    }

    public String getDisplayOption() {
        return displayOption;
    }

    public void setDisplayOption(String displayOption) {
        this.displayOption = displayOption;
    }

    public String getFilterOption() {
        return filterOption;
    }

    public void setFilterOption(String filterOption) {
        this.filterOption = filterOption;
    }

    public String getSupportPassword() {
        return supportPassword;
    }

    public void setSupportPassword(String supportPassword) {
        this.supportPassword = supportPassword;
    }

    public int getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(int defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getDefaultManufacturer() {
        return defaultManufacturer;
    }

    public void setDefaultManufacturer(String defaultManufacturer) {
        this.defaultManufacturer = defaultManufacturer;
    }

    public String getDefaultCmuScript() {
        return getDefaultScript(0);
    }

    public String getDefaultAeroflexScript() {
        return getDefaultScript(1);
    }

    private String getDefaultScript(int index) {
        try {
            return defaultScripts.split(",")[index].split("\\.")[1];
        } catch (IndexOutOfBoundsException exception) {
            return "";
        }
    }

    public String getDefaultScripts() {
        return defaultScripts;
    }

    public void setDefaultScripts(String defaultScripts) {
        this.defaultScripts = defaultScripts;
    }

    public String getDefaultCmuBox() {
        return getDefaultBox(0);
    }

    public String getDefaultCmwBox() {
        return getDefaultBox(1);
    }

    private String getDefaultBox(int index) {
        try {
            return defaultPositions.split(",")[index].split("\\.")[0];
        } catch (IndexOutOfBoundsException exception) {
            return "";
        }
    }

    public String getDefaultPositions() {
        return defaultPositions;
    }

    public String getDefaultCmuPosition() {
        return getDefaultPosition(0);
    }

    public String getDefaultCmwPosition() {
        return getDefaultPosition(1);
    }

    private String getDefaultPosition(int index) {
        try {
            return defaultPositions.split(",")[index].split("\\.")[1];
        } catch (IndexOutOfBoundsException exception) {
            return "";
        }
    }

    public void setDefaultPositions(String defaultPositions) {
        this.defaultPositions = defaultPositions;
    }

    public double getMinCmuScriptVersion() {
        return minCmuScriptVersion;
    }

    public double getMinCmwScriptVersion() {
        return minCmwScriptVersion;
    }

    public void setMinCmuScriptVersion(double minCmuScriptVersion) {
        this.minCmuScriptVersion = minCmuScriptVersion;
    }

    public void setMinCmwScriptVersion(double minCmwScriptVersion) {
        this.minCmwScriptVersion = minCmwScriptVersion;
    }

    public double getMinVersion() {
        return minVersion;
    }

    public void setMinVersion(double minVersion) {
        this.minVersion = minVersion;
    }

    public String getDefaultSource() {
        return defaultSource;
    }

    public void setDefaultSource(String defaultSource) {
        this.defaultSource = defaultSource;
    }

    public String getCmuReportPath() {
        return cmuReportPath;
    }

    public String getCmuDatabasePath() {
        return cmuDatabasePath;
    }

    public String getCmuCachePath() {
        return cmuCachePath;
    }

    public String getCmwCachePath() {
        return cmwCachePath;
    }

    public String getCmuShortcutPath() {
        return cmuShortcutPath;
    }

    public String getCmwReportPath() {
        return cmwReportPath;
    }

    public String getCmwDatabasePath() {
        return cmwDatabasePath;
    }

    public void setCmuReportPath(String cmuReportPath) {
        this.cmuReportPath = cmuReportPath;
    }

    public void setCmuDatabasePath(String cmuDatabasePath) {
        this.cmuDatabasePath = cmuDatabasePath;
    }

    public void setCmuCachePath(String cmuCachePath) {
        this.cmuCachePath = cmuCachePath;
    }

    public void setCmwCachePath(String cmwCachePath) {
        this.cmwCachePath = cmwCachePath;
    }

    public void setCmuShortcutPath(String cmuShortcutPath) {
        this.cmuShortcutPath = cmuShortcutPath;
    }

    public void setCmwReportPath(String cmwReportPath) {
        this.cmwReportPath = cmwReportPath;
    }

    public void setCmwDatabasePath(String cmwDatabasePath) {
        this.cmwDatabasePath = cmwDatabasePath;
    }

    public boolean isPreloadCmu() {
        return preloadCmu;
    }

    public void setPreloadCmu(boolean preloadCmu) {
        this.preloadCmu = preloadCmu;
    }

    public boolean isPreloadCmw() {
        return preloadCmw;
    }

    public void setPreloadCmw(boolean preloadCmw) {
        this.preloadCmw = preloadCmw;
    }

    public boolean isPreloadReports() {
        return preloadReports;
    }

    public void setPreloadReports(boolean preloadReports) {
        this.preloadReports = preloadReports;
    }
}
