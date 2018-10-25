package com.jackromer.gateway.thingProtocols.processSZSecretWellProtocol.tcpMessageProcessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.jackromer.gateway.enums.ThingType;
import com.jackromer.gateway.poolDataProcessor.observerClass.TcpObserver;
import com.jackromer.gateway.thingProtocols.processSZSecretWellProtocol.SecretWellSubThingData;
import com.jackromer.gateway.thingProtocols.processSZSecretWellProtocol.SecretWellThingDataStreamParser;
import com.jackromer.gateway.utils.Base64Util;
import com.jackromer.gateway.utils.XorUtil;


/**
 * @Description: 密码设备协议解析
 * @author: jackromer
 * @version: 1.0, 2018年07月16日
 */
public class SecretWellTcpMessageProcessor implements TcpObserver{

    public static Logger logger = LoggerFactory.getLogger(SecretWellTcpMessageProcessor.class);
  
    volatile private static SecretWellTcpMessageProcessor instance = null;
    
    public SecretWellTcpMessageProcessor(){}
    
    public static SecretWellTcpMessageProcessor getInstance() {

        if(instance != null){//懒汉式

        }else{
            //创建实例之前可能会有一些准备性的耗时工作
            synchronized (SecretWellTcpMessageProcessor.class) {
                if(instance == null){//二次检查
                    instance = new SecretWellTcpMessageProcessor();
                }
            }
        }

        return instance;
    }

    
    
    /**
	 * 处理上报消息
	* @Description:多个线程调用该方法,数据保存在不同的线程中,不需要做互斥处理
	* @param data
	* @Example*460029961128085*Ul1dUlxRF1xNawMCAgIFBwUPCAIECgMEaA==#
	* @return reslutArray, 长度3, 【subThingId, 需要上报的设备影子数据流JSON, 需要回复设备的数据reportReply[ps:无需回复时返回null]】
	 */
    
    @Override
    public String[] processTcpMessage(String gatewayId,String data) {
    	
    	logger.info("密码设备TCP-PROCESSOR收到消息！");
    	
        String [] reslutArray =  new String[3];

        //校验上报数据是否正确应该是以 '*' 开头 + 15位唯一标识 + '*' + 数据区数据 + '#' 结尾
        String header = data.substring(0, 17);
        String datas = data.substring(17, data.length()-1);
        //应为 #
        String end = data.substring(data.length() -1, data.length());

        try {
            if (!"*".equals(header.substring(0, 1)) || !"*".equals(header.substring(16, 17)) || !"#".equals(end)) {
                logger.info("数据格式校验失败,请确认上报数据格式是否正确>>>" + data);
                return reslutArray;
            }
            String subSthingId = header.substring(1, 16);
            SecretWellSubThingData szSecretWell = new SecretWellSubThingData();
            szSecretWell.setThingType(ThingType.SZ_SECRET_WELL.getThingType());
            szSecretWell.setId(subSthingId);

            //数据区解密
            //base64解密
            byte[] base64Reverse = Base64Util.getInstance().decodeData(datas.getBytes());

            //异或解密
            byte[] xorReverse = XorUtil.getInstance().xorDecode(base64Reverse);

            reslutArray = resolveData(xorReverse, szSecretWell, gatewayId);

            return reslutArray;
        } catch (NullPointerException e) {
            logger.info("数据格式校验失败,请确认上报数据格式是否正确>>>" + data);
            return reslutArray;
        }
    }

    /**
     * 密码设备协议解析
     * @param xorReverse 解密后的数据
     * @return
     */
    public String[] resolveData(byte[] xorReverse, SecretWellSubThingData szSecretWell, String gatewayId) {
        //示例数据：cdsfrj0204,10,20,21,0023,state[t,5.6,14,iv,c,20160225183004,f]

        String[] reslutArray = new String[3];

        String dataField = new String(xorReverse);

        final String[] dataFieldArray = dataField.split(",");

        //设备产品型号
        String productLevel = dataFieldArray[0];
        szSecretWell.setProductLevel(productLevel);

        //硬件版本号
        String hardwareVersion = dataFieldArray[1];
        szSecretWell.setHardwareVersion(hardwareVersion);

        //固件版本号
        String firmwareVersion = dataFieldArray[2];
        szSecretWell.setFirmwareVersion(firmwareVersion);

        //协议版本号
        String protocolVersion = dataFieldArray[3];
        szSecretWell.setProtocolVersion(protocolVersion);

        //通讯序列号
        String serialNumber = dataFieldArray[4];
        szSecretWell.setSerialNumber(serialNumber);

        //上报类型
        String reportType = dataFieldArray[5].substring(6, 7);
        szSecretWell.setReportType(reportType);

        //电池电压
        String batteryVoltage = dataFieldArray[6];
        szSecretWell.setBatteryVoltage(batteryVoltage);

        //信号强度
        String signalIntensity = dataFieldArray[7];
        szSecretWell.setSignalIntensity(signalIntensity);

        //唤醒开关状态
        String switchState = dataFieldArray[8];
        szSecretWell.setSwitchState(switchState);

        //设备盖状态
        String coverState = dataFieldArray[9];
        szSecretWell.setCoverState(coverState);

        //数据生成时间
        String stateGenerateTime = dataFieldArray[10];
        szSecretWell.setStateGenerateTime(stateGenerateTime);

        JSON json = SecretWellThingDataStreamParser.getThingReportUpdateJsonData(szSecretWell, gatewayId);

        reslutArray[0] = szSecretWell.getId();

        reslutArray[1] = json.toJSONString();

        reslutArray[2] = "";// 回复数据

        return reslutArray;
    }
}
