package com.jackromer.gateway.thingProtocols.processSZSecretWellProtocol;

import com.alibaba.fastjson.JSONObject;

/**
 * @Description:密码设备数据解析类
 * @author: jackromer
 * @version: 1.0, 2018年07月17日
 */
public class SecretWellThingDataStreamParser {
    /**
     * 生成网关设备上报设备影子数据流(影子更新)
     * @Description:
     * @return
     */
    public static JSONObject getThingReportUpdateJsonData(SecretWellSubThingData szSecretWell, String gatewayId) {

        JSONObject jsonReport = new JSONObject();

        //产品型号
        jsonReport.put("pl", szSecretWell.getProductLevel());

        //硬件版本号
        jsonReport.put("hv", szSecretWell.getHardwareVersion());

        //固件版本号
        jsonReport.put("fv", szSecretWell.getFirmwareVersion());

        //协议版本号
        jsonReport.put("pv", szSecretWell.getProtocolVersion());

        //通讯序号
        jsonReport.put("sn", szSecretWell.getSerialNumber());

        //上报类型
        jsonReport.put("rt", szSecretWell.getReportType());

        //电池电压
        jsonReport.put("bv", szSecretWell.getBatteryVoltage());

        //信号强度
        jsonReport.put("sig", szSecretWell.getSignalIntensity());

        //唤醒开关状态
        jsonReport.put("ws", szSecretWell.getSwitchState());

        //设备盖状态
        jsonReport.put("cs", szSecretWell.getCoverState());

        //数据生成时间
        jsonReport.put("sgt", szSecretWell.getStateGenerateTime());

        //-------------------------------------------
        JSONObject jsonState = new JSONObject();

        jsonState.put("reported", jsonReport);

        //-------------------------------------------
        JSONObject jsonBody = new JSONObject();

        jsonBody.put("state", jsonState);

        //jsonBody.put("version", 1); 不需要version

        //-------------------------------------------

        JSONObject thingReportDataJson = new JSONObject();

        thingReportDataJson.put("requestId", gatewayId);

        thingReportDataJson.put("subThingId", szSecretWell.getId());

        thingReportDataJson.put("method", "put");

        thingReportDataJson.put("body", jsonBody);

        return thingReportDataJson;

    }
}
