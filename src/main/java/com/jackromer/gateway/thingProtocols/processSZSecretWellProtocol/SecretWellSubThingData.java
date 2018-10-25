package com.jackromer.gateway.thingProtocols.processSZSecretWellProtocol;

/**
 * @Description: 密码设备实体数据类
 * @author: jackromer
 * @version: 1.0, 2018年07月17日
 */
public class SecretWellSubThingData {
    /**
     * 设备编号
     */
    private String id;
    /**
     * 设备编号
     */
    private String thingType;
    /**
     * 产品型号
     */
    private String productLevel;
    /**
     * 硬件版本号
     */
    private String hardwareVersion;
    /**
     * 固件版本号
     */
    private String firmwareVersion;
    /**
     * 协议版本号
     */
    private String protocolVersion;
    /**
     * 通讯序号
     */
    private String serialNumber;
    /**
     * 上报类型
     */
    private String reportType;
    /**
     * 电池电压
     */
    private String batteryVoltage;
    /**
     * 信号强度
     */
    private String signalIntensity;
    /**
     * 唤醒开关状态
     */
    private String switchState;
    /**
     * 设备盖状态
     */
    private String coverState;
    /**
     * 数据生成时间
     */
    private String stateGenerateTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getThingType() {
        return thingType;
    }

    public void setThingType(String thingType) {
        this.thingType = thingType;
    }

    public String getProductLevel() {
        return productLevel;
    }

    public void setProductLevel(String productLevel) {
        this.productLevel = productLevel;
    }

    public String getHardwareVersion() {
        return hardwareVersion;
    }

    public void setHardwareVersion(String hardwareVersion) {
        this.hardwareVersion = hardwareVersion;
    }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(String protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public String getBatteryVoltage() {
        return batteryVoltage;
    }

    public void setBatteryVoltage(String batteryVoltage) {
        this.batteryVoltage = batteryVoltage;
    }

    public String getSignalIntensity() {
        return signalIntensity;
    }

    public void setSignalIntensity(String signalIntensity) {
        this.signalIntensity = signalIntensity;
    }

    public String getSwitchState() {
        return switchState;
    }

    public void setSwitchState(String switchState) {
        this.switchState = switchState;
    }

    public String getCoverState() {
        return coverState;
    }

    public void setCoverState(String coverState) {
        this.coverState = coverState;
    }

    public String getStateGenerateTime() {
        return stateGenerateTime;
    }

    public void setStateGenerateTime(String stateGenerateTime) {
        this.stateGenerateTime = stateGenerateTime;
    }
}
