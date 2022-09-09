package com.kcb.pay.modules.controller;

import com.kcb.pay.common.*;
import com.kcb.pay.modules.dto.*;
import com.kcb.pay.modules.entity.PayRecordEntity;
import com.kcb.pay.modules.entity.PersonEntity;
import com.kcb.pay.modules.service.CloudflashPayService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;

@Api(value = "",tags = "科创办充值")
@RequestMapping("/phone")
@RestController
@Slf4j
public class CloudflashPayController {

    @Autowired
    CloudflashPayService cloudflashPayService;

    @Value("${sign.publicKey}")
    String publicKey;

    @Autowired
    SHA256RSAUtils sha256RSAUtils;


    @ApiOperation("绑卡接口")
    @PostMapping("/bindingCard")
    public R bangka(@RequestBody TiedcardDTO dto){
        //验签
        try {
            boolean  b = RSAUtils.verifyByPublicKey(dto.getRequest().getBytes(), publicKey, dto.getSignature());
            log.info("经验证数据和签名匹配：{} ",b);
            if(!b){
                R r = R.error("1", "无效的签名");
                Map<String, Object> getsgin = sha256RSAUtils.getsgin(r);
                return R.ok(getsgin);
            }
            //如果签名为true 解密request数据 进行校验
            CardDTO destostring = (CardDTO) sha256RSAUtils.destostring(dto.getRequest(), CardDTO.class);
            if(StringUtils.isEmpty(destostring.getName()) || StringUtils.isEmpty(destostring.getAccount())){
                R r = R.error("1", "姓名或者手机号为空");
                Map<String, Object> getsgin = sha256RSAUtils.getsgin(r);
                return R.ok(getsgin);
            }else {
                PersonEntity personEntity = cloudflashPayService.selectPerson(destostring.getName(), destostring.getAccount());
                if(ObjectUtils.isEmpty(personEntity)){
                    R r = R.error("1", "没有此用户");
                    Map<String, Object> getsgin = sha256RSAUtils.getsgin(r);
                    return R.ok(getsgin);
                }
            }
            R r = R.ok("0", "成功");
            Map<String, Object> getsgin = sha256RSAUtils.getsgin(r);
            return R.ok(getsgin);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error();
        }
    }


    @ApiOperation("余额查询")
    @PostMapping("/queryBalance")
    public R queryBalance(@RequestBody TiedcardDTO dto){
        try {
            boolean  b = RSAUtils.verifyByPublicKey(dto.getRequest().getBytes(), publicKey, dto.getSignature());
            log.info("经验证数据和签名匹配：{} ",b);
            if(!b){
                R r = R.error("1", "无效的签名");
                Map<String, Object> getsgin = sha256RSAUtils.getsgin(r);
                return R.ok(getsgin);
            }
            BalanceDTO destostring = (BalanceDTO) sha256RSAUtils.destostring(dto.getRequest(), BalanceDTO.class);
            if(StringUtils.isEmpty(destostring.getAccount())){
                R r = R.error("1", "手机号不可空");
                Map<String, Object> getsgin = sha256RSAUtils.getsgin(r);
                return R.ok(getsgin);
            }
            PersonEntity person = cloudflashPayService.selectPersonMoney(destostring.getAccount());
            if(ObjectUtils.isEmpty(person)){
                R r = R.error("1", "没有此用户");
                Map<String, Object> getsgin = sha256RSAUtils.getsgin(r);
                return R.ok(getsgin);
            }
                R r = R.ok()
                        .put("account", person.getPhone())
                        .put("cardBalance",person.getSubsidies().add(person.getCash()).multiply(new BigDecimal(100)))
                        .put("studentId",null)
                        .put("schoolNo",null);
            Map<String, Object> getsgin = sha256RSAUtils.getsgin(r);
            return R.ok(getsgin);
        }catch (Exception e){
            e.printStackTrace();
            return R.error();
        }
    }



    @ApiOperation("充值")
    @PostMapping("/pay")
    public R pay(@RequestBody TiedcardDTO dto){
        try {
            boolean  b = RSAUtils.verifyByPublicKey(dto.getRequest().getBytes(), publicKey, dto.getSignature());
            log.info("经验证数据和签名匹配：{} ",b);
            if(!b){
                R r = R.error("1", "无效的签名");
                Map<String, Object> getsgin = sha256RSAUtils.getsgin(r);
                return R.ok(getsgin);
            }
            PayDTO destostring = (PayDTO) sha256RSAUtils.destostring(dto.getRequest(), PayDTO.class);
            BigDecimal decimal = new BigDecimal(destostring.getApplyAmount());
            if(StringUtils.isEmpty(destostring.getAccount())){
                R r = R.error("1", "手机号不可空");
                Map<String, Object> getsgin = sha256RSAUtils.getsgin(r);
                return R.ok(getsgin);
            }
            PersonEntity person = cloudflashPayService.selectPersonMoney(destostring.getAccount());
            if(ObjectUtils.isEmpty(person)){
                R r = R.error("1", "没有此用户");
                Map<String, Object> getsgin = sha256RSAUtils.getsgin(r);
                return R.ok(getsgin);
            }
            if(person.getExpense().equals("F类") || person.getStatus()==1){
                //消费类型F类不支持充值 人员状态为离职不支持充值
                R r = R.error("1", "该用户不允许充值");
                Map<String, Object> getsgin = sha256RSAUtils.getsgin(r);
                return R.ok(getsgin);
            }
            if(person.getExpense().equals("D类")){
                BigDecimal divide = decimal.divide(new BigDecimal(100)).multiply(new BigDecimal(0.6)).setScale(2, BigDecimal.ROUND_HALF_UP); //,BigDecimal.ROUND_CEILING .setScale(2, BigDecimal.ROUND_HALF_UP)
                person.setCash(person.getCash().add(divide));
                person.setApplyAmount(divide);//折扣完金额
            }else {
                person.setCash(person.getCash().add(decimal.divide(new BigDecimal(100))));
                person.setApplyAmount(decimal.divide(new BigDecimal(100)));//充值金额
            }
            person.setApplyId(destostring.getApplyId());
            String outid = cloudflashPayService.insertPayRecord(person);
            R r = R.ok()
                    .put("account", person.getPhone())
                    .put("applyId",destostring.getApplyId())
                    .put("outId",outid)
                    .put("cardBalance",person.getCash())
                    .put("studentId",null)
                    .put("schoolNo",null);
            Map<String, Object> getsgin = sha256RSAUtils.getsgin(r);
            return R.ok(getsgin);
        }catch (Exception e){
            e.printStackTrace();
            return R.error();
        }
    }


    @ApiOperation("充值查询")
    @PostMapping("/queryTopup")
    public R queryTopup(@RequestBody TiedcardDTO dto){
        try {
            boolean  b = RSAUtils.verifyByPublicKey(dto.getRequest().getBytes(), publicKey, dto.getSignature());
            log.info("经验证数据和签名匹配：{} ",b);
            if(!b){
                R r = R.error("1", "无效的签名");
                Map<String, Object> getsgin = sha256RSAUtils.getsgin(r);
                return R.ok(getsgin);
            }
            QueryTopupDTO queryTopupDTO = (QueryTopupDTO) sha256RSAUtils.destostring(dto.getRequest(), QueryTopupDTO.class);
            if(StringUtils.isEmpty(queryTopupDTO.getAccount()) || StringUtils.isEmpty(queryTopupDTO.getOutId())){
                R r = R.error("1", "手机号或流水号不可为空");
                Map<String, Object> getsgin = sha256RSAUtils.getsgin(r);
                return R.ok(getsgin);
            }
            PayRecordEntity entity = cloudflashPayService.queryTupup(queryTopupDTO);
            if(ObjectUtils.isEmpty(entity)){
                R r = R.error("1", "没有此记录");
                Map<String, Object> getsgin = sha256RSAUtils.getsgin(r);
                return R.ok(getsgin);
            }
            R r = R.ok()
                    .put("account", entity.getAccount())
                    .put("rechargeAmount",entity.getApplyAmount().multiply(new BigDecimal(100)))
                    .put("outId",entity.getApplyId())
                    .put("studentId",null)
                    .put("schoolNo",null);
            Map<String, Object> getsgin = sha256RSAUtils.getsgin(r);
            return R.ok(getsgin);
        }catch (Exception e){
            e.printStackTrace();
           return R.error();
        }
    }


    

    public static void main(String[] args) throws Exception {
        //加密
//        String s = DesUtils.encode3Des("aaavvasssaasddscas", "{\"account\": \"13290370793\",\"certHash\": \"332145666699852111\",\"name\": \"鲍来鑫\",\"passwd\": \"123456\",\"phoneNohash\": \"11231225122\",\"schoolNo\": \"001\",\"signature\": \"1231\",\"studentId\": \"\"}");
//        String s = DesUtils.encode3Des("aaavvasssaasddscas", "{\"phoneNohash\":\"dab0bf7bc705bb2b0d64c539744f5f9d509510e77f3c4150e7d7134ab7d0a9b1\",\"name\": \"刘照星\",\"schoolNo\":1158, \"account\" :13298312259}");

//
        String s =DesUtils.encode3Des("aaavvasssaasddscas","{\"schoolNo\":\"\",\"studentId\":\"\",\"account\":\"12222122221\",\"applyId\":\"001\",\"applyAmount\":10}");
//        String s =DesUtils.encode3Des("aaavvasssaasddscas","{\"schoolNo\":\"\",\"account\":\"12222122221\",\"outId\":\"001\"}");
        System.out.println("3Des加密结果："+s);

        //原来的公钥
        String  publicKey ="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAolLJA4rPUBtMXN/h1OXRxrkO/IMPz24FDPTWV/ImEXchJ+MH5LAZXwrIeQ8dIUbHVp662iDihncrTmhV563aRCr5e2DUW1jFivqlXW9+6+kY/Qq5rGHXXeyL7/Wh+5oBljXJPosr/cY2NOHnBDPdWMFdBiPUT4hRliwNa7fNc+Er7Q0eylbuiM29o6tI0ZD1eqm+AymjtniNMWZ8YkIuLC/+PIqCqpGUvTb3FfpqD12XvE1Mxz/gG2B8sYlF3zhGODpArBRAZpCwXPZ2tWG8lqBZye+bSzYGrBPz9RXe8IdtB/pUC0R6qEbObm0xFUdfHlTJEsNwhtvgvR4Wv5uWTwIDAQAB";
        //新的公钥
//        String  publicKey ="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA8r5qiaSnJuqzBEI6pJ6NMqO2XKgIN1z8iUkWLO4T0/gCW5CKyOWXitEvOMsIb7xHo97dXhJHdQjPKhow0yANJGIPFR+KWuqrS/EuqH11F6Aji//beIuoRiurvKUDq6s1nGhw2nV9L5fy1p8FVokkk6y911WORMnUkaQrdEnWoW42yztu2vPgQdp2LDRsPFJTDXMXVH2AUVWgNwX4hIngilzP1RcRDVfEEryMGSBFFUTQfsUX3L8nmPkdAZsTaPcnIDIKOOrz5cwSc/6N+siDhyYgw1qF0NimC7CjQEJn0HlWXtKWN9Q0tiHMxE0biNMny486mhCOz7i+OYo8IkYnLwIDAQAB";

        String  privateKey ="MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCiUskDis9QG0xc3+HU5dHGuQ78gw/PbgUM9NZX8iYRdyEn4wfksBlfCsh5Dx0hRsdWnrraIOKGdytOaFXnrdpEKvl7YNRbWMWK+qVdb37r6Rj9CrmsYddd7Ivv9aH7mgGWNck+iyv9xjY04ecEM91YwV0GI9RPiFGWLA1rt81z4SvtDR7KVu6Izb2jq0jRkPV6qb4DKaO2eI0xZnxiQi4sL/48ioKqkZS9NvcV+moPXZe8TUzHP+AbYHyxiUXfOEY4OkCsFEBmkLBc9na1YbyWoFnJ75tLNgasE/P1Fd7wh20H+lQLRHqoRs5ubTEVR18eVMkSw3CG2+C9Hha/m5ZPAgMBAAECggEASfpIumCM0wld6AXwukJzNIoqllJ05ihSDPGYrkQhrVwJtE/3zPWk2nfjnaiRshS7zHJS3g6WrompJvbOD+Rf0Sl0whIDNg5jGU/aBZdG1OVLCifVLwb4TEn+DMJ5rF0n0/l3WhW7WtD262r5CiVI/6ZAawfxPr310OcKxsjhzalbrjRXqU7QZhhS0GNGDCE/oS+OLVDi2wiTG+TySB1/9uBqObhAW3Arm8HXNbT40Kud4kDMhisD6NzwCXXvlL38/OahqOwnIVgilE6BsPDIcySEAJeBNPWoCGHdMWNFcHTvJkI39vDAzYAbIBFEEiNRiqNRjtewwHQFiMPbxUDPAQKBgQDQIOMOIw6ImyW8mnCR+/VMy9PHayW0XvAXnj/ZFGBLulsaygAbDIhU3Vse7pQOOm/zhhO0XP3gr3RBIenx6YWV72wrrOegK/CWVI+tBVn/q0ocDv0bDBmjWSSlWGlLbnVagdf+avSgrj2DHGMZXGG+S+KeoRyF3jCJaiEpBnpfrwKBgQDHqMhoX14epMvSvw4FYnQY1wYajOpSu4K3KAvyN6JAnr2ZnYMEDwhDcMzEpmvSk/2HBRtmx8n8O/Oy6wHRR5+uKxV5WbQMR6jhDHjWptrqp6+X0dGs8vm32e39o6E8+iXSy+zMPVPL3djPnyQlv+Vs3WAP47RtlsQuG5trd9A7YQKBgCYVXna/w3ELsqQIPoD38bLTuTTMTmHq3VP1CQnXyKcSuoj0XYCVkny05G+MiFryJnoRFpnySiJK+KLSadRyPpdo8Aot4VRVtfhMEN5AFG/MEAuCUp4YYLibHzGCNAIxfBfLmpJbT9f3Chprn1aMqPYBPL56DJYSSh9a5CZ1oP7RAoGASww8WE8Ql8AmBQ9fe9KoBPY+CxjQs0xcM/BPnTogON3yMp2sM4ha+M6biYdsm/iYoXnf9OBAIhl+3s/LHRkpAlSbe8OmE7+T0RPZEAYj33QsYoqxn6qtBthZxq8M2ljEgtm4JoUezFCVQGV3NuFMoYEMrKxQk6jKEPK7lsewc8ECgYAXp8l5FtKSH9cuAPhTItQ8swdqsDsr9gEVwLUDiD9+ApZitHW8JhPLfdhdlWMELKomWVGo8vIW9RvJnop59Km9/JcIH/A8eAhkGkfL7Bc0QVyXXOIHG1GIo3JILxsym41K55pAJz1ASuYOaAI7Yk7TwJSVbeBh0QfAX1/YD3dWvA==";

        //解密
        String a = DesUtils.decode3Des("aaavvasssaasddscas", s);
        System.out.println("3Des解密结果："+a);

        //给密文签名
        String sign = RSAUtils.signByPrivateKey(s.getBytes(), privateKey);
        System.out.println("签名结果："+sign);

        //验签
        boolean verify1 = RSAUtils.verifyByPublicKey(s.getBytes(),publicKey,sign);
        log.info("经验证数据和签名匹配：{} ", verify1);
    }



}