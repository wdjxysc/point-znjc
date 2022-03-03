package com.whty.point.znjc;

import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.alibaba.fastjson.JSON;
import com.whty.point.utils.JdbcUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName ExamineeSubmitSta
 * @Description TODO
 * @Author Administrator
 * @Date 2022/1/4 14:56
 **/
public class TeacherPublishSta {
    public static void main(String[] args) {
        //正式读
        JdbcUtils.initMysql("jdbc:mysql://10.0.200.15:15286/ct_exam?useSSL=false&useUnicode=true&characterEncoding=utf-8&autoReconnect=true&rewriteBatchedStatements=true&allowMultiQueries=true&serverTimezone=Asia/Shanghai",
                "ct_exam","Umnf@7GbvcP","ct_exam");
        studentSubmitSta();
    }

    private static void studentSubmitSta(){
        Map<String, Map<String, String>> studentSubmitSta = new HashMap<>();

        for(int i = 1; i<13; i++){
            String timeSql = "and create_time > '2021-"+i+"-01' and create_time < '2021-"+(i+1)+"-01'";
            if(i==12){
                timeSql = "and create_time > '2021-12-01' and create_time < '2022-01-01'";
            }

            String sqlExamNumber = "select " +
                    "creator_name as name, " +
                    "creator_id as person_id, " +
                    "count(1) as num  from ps_exam \n" +
                    "where platform_code = '420100' \n" +
                    timeSql +
                    "and is_del = 0\n" +
                    "and creator_name != '罗艳平' " +
                    "group by creator_id";

            final List<Map<String, Object>> list = JdbcUtils.list(sqlExamNumber, null);
            supplyData(i, list, studentSubmitSta);


//            String sqlPersonId = "select ex.exam_id, ee.person_id, ee.name, count(1) as num\n" +
//                    "from ps_examinee_sheet es\n" +
//                    "    inner join ps_examinee ee on es.exam_id = ee.exam_id " +
//                    "and es.crypt_code = ee.person_id\n" +
//                    "    left join ps_exam ex on ex.exam_id = es.exam_id\n" +
//                    "where ex.platform_code = '420100' " +
//                    timeSql+
//                    " and es.is_del = 0\n" +
//                    "  and ex.creator_org_id not in ('bc633fea764147c999a9280a7b85b215', '971ca231eb8141318db87109b16f34b2')\n" +
//                    "  and ex.exam_id not in ( 'EXAM5227e683dfc64dd3890a16d571bc7515', 'EXAM5e65521757db493e8d99baaf722cf872','EXAM579fc50932d44c66922ae98c77dbdb03','EXAMae3446cb0be844fd8329e8e92bfb1ae3','EXAM173abd9303744fe3ab41407ee927f31f')\n" +
//                    "GROUP BY ee.person_id";
//
//            final List<Map<String, Object>> sqlPersonIdList = JdbcUtils.list(sqlPersonId, null);
//            supplyData(i, sqlPersonIdList, studentSubmitSta);
        }
        System.out.println(JSON.toJSONString(studentSubmitSta));
        System.out.println(studentSubmitSta.size());

        List<Map<String, Object>> excelData = new ArrayList<>();
        for(Map.Entry<String, Map<String, String>> entry : studentSubmitSta.entrySet()){
            Map<String, String> value = entry.getValue();
            Map<String, Object> item = new HashMap<>();
            item.put("name", entry.getValue().get("name"));
            item.put("personId", entry.getKey());
            for(int i = 1; i < 13; i++){
                if(value.get(i + "") != null){
                    item.put(i+"", Integer.parseInt(value.get(i+"")));
                }else{
                    item.put(i+"", 0);
                }
            }
            excelData.add(item);
        }

        ExcelWriter excelWriter = ExcelUtil.getWriter("E:/excel/test2.xlsx");
        excelWriter.addHeaderAlias("name", "老师姓名");
        excelWriter.addHeaderAlias("personId", "老师id");
        excelWriter.addHeaderAlias("1", "老师发布智能检测次数（1月）");
        excelWriter.addHeaderAlias("2", "老师发布智能检测次数（2月）");
        excelWriter.addHeaderAlias("3", "老师发布智能检测次数（3月）");
        excelWriter.addHeaderAlias("4", "老师发布智能检测次数（4月）");
        excelWriter.addHeaderAlias("5", "老师发布智能检测次数（5月）");
        excelWriter.addHeaderAlias("6", "老师发布智能检测次数（6月）");
        excelWriter.addHeaderAlias("7", "老师发布智能检测次数（7月）");
        excelWriter.addHeaderAlias("8", "老师发布智能检测次数（8月）");
        excelWriter.addHeaderAlias("9", "老师发布智能检测次数（9月）");
        excelWriter.addHeaderAlias("10", "老师发布智能检测次数（10月）");
        excelWriter.addHeaderAlias("11", "老师发布智能检测次数（11月）");
        excelWriter.addHeaderAlias("12", "老师发布智能检测次数（12月）");


//        Map<String, String> map = new HashMap<>();
//        map.put("name", "1");
//        excelData.clear();
//        excelData.add(map);
        excelWriter.write(excelData);
        excelWriter.close();
    }

    private static void supplyData(int month, List<Map<String, Object>> sqlPersonIdList, Map<String, Map<String, String>> studentSubmitSta){
        for (Map<String, Object> objectMap: sqlPersonIdList){
            if(null != studentSubmitSta.get(objectMap.get("creator_id").toString())){
                Map<String, String> personMonthNumMap = studentSubmitSta.get(objectMap.get("creator_id").toString());
                personMonthNumMap.put("name", objectMap.get("creator_name").toString());
                if(personMonthNumMap.get("" + month) != null){
                    personMonthNumMap.put("" + month, "" + (Integer.parseInt(personMonthNumMap.get("" + month)) + Integer.parseInt(objectMap.get("num").toString())));
                }else{
                    personMonthNumMap.put("" + month, objectMap.get("num").toString());
                }
            }else{
                Map<String, String> personMonthNumMap = new HashMap<>();
                personMonthNumMap.put("" + month, objectMap.get("num").toString());
                personMonthNumMap.put("name", objectMap.get("creator_name").toString());
                studentSubmitSta.put(objectMap.get("creator_id").toString(), personMonthNumMap);
            }
        }
    }
}
