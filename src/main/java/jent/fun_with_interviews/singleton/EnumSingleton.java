package jent.fun_with_interviews.singleton;

public enum EnumSingleton {

    INSTANCE;

    // Add any field member and method as needed.
    // For example
    String flagName;
    Boolean flagStatus;

    public String getFlagName() {
        return flagName;
    }

    public void setFlagName(String flagName) {
        this.flagName = flagName;
    }

    public Boolean getFlagStatus() {
        return flagStatus;
    }

    public void setFlagStatus(Boolean flagStatus) {
        this.flagStatus = flagStatus;
    }
}
