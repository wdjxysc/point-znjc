package com.whty.point.znjc;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.whty.point.utils.JdbcUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

/**
 * @ClassName PointAddAfter
 * @Description TODO
 * @Author Administrator
 * @Date 2020/6/2 15:25
 **/
public class PointAddAfter {

    private static Log logger = LogFactory.getLog(PointAddAfter.class);

    //积分编码
    private static String POINT_CODE_EXAM = "JF000207";//导学检测
    private static String POINT_CODE_MARK = "JF000208";//练习批阅

//    private static String GATEWAY_FUJIAN = "http://opengateway.fjedu.cn/point-gateway//addpointafter";
//    private static String GATEWAY_FUJIAN = "http://point.t.huijiaoyun.com/point-gateway/addpointafter";
//    private static String GATEWAY_FUJIAN = "http://point-gateway.fjedu.cn/point-gateway/addpointafter";
    private static String GATEWAY_FUJIAN = "http://open.jleduyun.cn:30001/point-gateway/addpointafter?accessToken=ujgb7tyr33gtdsxlzr9vtipzkbs89zr1";//吉林

    //http://point-gateway.fjedu.cn 福建平台 积分gateway

    public static SimpleDateFormat sdf1 =   new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    public static void main(String[] args){
        JdbcUtils.initMysql("jdbc:mysql://10.0.200.15:15286/ct_exam?useSSL=false&useUnicode=true&characterEncoding=utf-8&autoReconnect=true&rewriteBatchedStatements=true&allowMultiQueries=true&serverTimezone=Asia/Shanghai",
                "ct_exam","Umnf@7GbvcP","ct_exam");
        addAfterPoint();
    }

    public static void addAfterPoint(){
        RestTemplate restTemplate = new RestTemplate();
        List<Map<String, Object>> result = JdbcUtils.list("select * from ps_exam where exam_id = ?", new String[]{"EXAM76e22fa20e0f4dd795303323bb8e826d"});


        if(CollectionUtils.isEmpty(result)){
            logger.info("数据连接失败");
        }

        //学生答题
        String pointCode = POINT_CODE_EXAM;
        String sql = "select * from (\n" +
                "\tSELECT e.exam_id as examId, e.creator_id as personId, DATE_FORMAT(e.create_time, '%Y-%m-%d %H:%i:%s') as createTime,  COUNT(1) as answerNum\n" +
                "\tFROM `ps_exam` e\n" +
                "\tinner join ps_examinee_sheet es on e.exam_id = es.exam_id\n" +
                "\tWHERE e.platform_code = 220000\n" +
                "\tand e.create_time > '2020-07-01'\n" +
                "\tand e.create_time < '2020-08-18'\n" +
                "\tand es.answer_time < DATE_ADD(e.create_time, INTERVAL 30 day)\n" +
                "\tGROUP BY e.exam_id\n" +
                "\tORDER BY e.create_time ASC\n" +
                ") t WHERE t.answerNum > 9";


        //老师批阅
//        String pointCode = POINT_CODE_MARK;
//        String sql = "select * FROM(\n" +
//                "\tSELECT *, COUNT(1) as markSheetNum  FROM\n" +
//                "\t(\n" +
//                "\t\tSELECT e.exam_id as examId, sm.submit_marker_id, m.person_id as personId, DATE_FORMAT(e.create_time, '%Y-%m-%d %H:%i:%s') as createTime\n" +
//                "\t\tfrom ps_exam e\n" +
//                "\t\tinner join ps_scoring_mark sm on e.exam_id = sm.exam_id\n" +
//                "\t\tleft join ps_marker m on sm.submit_marker_id = m.marker_id\n" +
//                "\t\tWHERE e.platform_code = 220000\n" +
//                "\t\tand e.create_time > '2020-07-01'\n" +
//                "\t\tand e.create_time < '2020-08-18'\n" +
//                "\t\tand sm.submit_time < DATE_ADD(e.create_time, INTERVAL 30 day)\n" +
//                "\t\tand sm.submit_marker_id is not null\n" +
//                "\t\tand m.person_id is not null\n" +
//                "\t\tGROUP BY sm.sheet_id\n" +
//                "\t\tORDER BY e.create_time ASC\n" +
//                "\t) t\n" +
//                "\tGROUP BY submit_marker_id\n" +
//                "\tORDER BY createTime ASC\n" +
//                ") y\n" +
//                "WHERE markSheetNum > 9";

        result = JdbcUtils.list(sql, null);
        logger.info("查询结果 size:" + result.size());

        for (int i =0; i<result.size(); i++) {
            Map map = result.get(i);
            JSONObject param = new JSONObject();
            param.put("pointtime", map.get("createTime"));
            param.put("typecode", pointCode);
//            param.put("typecode", POINT_CODE_MARK);
            param.put("comeFrom", "0");
            param.put("usercode", map.get("personId").toString());
            param.put("relatedId", "10");

            logger.info("url:" + GATEWAY_FUJIAN + " ,param:" + param.toJSONString() + ",info:" + JSON.toJSONString(map));
            JSONObject jsonObject = post(restTemplate, GATEWAY_FUJIAN, param);
            logger.info("result:" + jsonObject.toJSONString());
            logger.info("total:" + result.size() + ",now:" + (i+1));
        }
    }


    public static JSONObject post(RestTemplate restTemplate , String url, JSONObject param){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<String> entity = new HttpEntity<String>(param.toJSONString(), headers);
        String result = restTemplate.exchange(url, HttpMethod.POST, entity, String.class).getBody();
        return JSON.parseObject(restTemplate.exchange(url, HttpMethod.POST, entity, String.class).getBody());
    }
}
