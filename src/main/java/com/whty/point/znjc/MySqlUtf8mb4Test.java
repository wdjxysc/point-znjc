package com.whty.point.znjc;

import com.whty.point.utils.JdbcUtils;

/**
 * @ClassName MySqlUtf8mb4Test
 * @Description TODO
 * @Author Administrator
 * @Date 2020/11/4 10:42
 **/
public class MySqlUtf8mb4Test {
    public static void main(String[] args){
        //预发布
//        JdbcUtils.initMysql("jdbc:mysql://10.0.200.15:23813/ct_exam0424?useSSL=false&useUnicode=true&characterEncoding=utf-8&autoReconnect=true&rewriteBatchedStatements=true&allowMultiQueries=true&serverTimezone=Asia/Shanghai",
//                "ct_exam0424","ct_exam0424","ct_exam0424");
        //正式读
//        JdbcUtils.initMysql("jdbc:mysql://10.0.200.15:15286/ct_exam?useSSL=false&useUnicode=true&characterEncoding=utf-8&autoReconnect=true&rewriteBatchedStatements=true&allowMultiQueries=true&serverTimezone=Asia/Shanghai",
//                "ct_exam","Umnf@7GbvcP","ct_exam");

        //正式写
        JdbcUtils.initMysql("jdbc:mysql://10.0.200.39:1227/ct_exam?useSSL=false&useUnicode=true&characterEncoding=utf-8&autoReconnect=true&rewriteBatchedStatements=true&allowMultiQueries=true&serverTimezone=Asia/Shanghai",
                "ct_exam","Umnf@7GbvcP","ct_exam");
        update();
    }

    private static void update(){
//        JdbcUtils.execute("update test set str = '\uD835\uDF0B'");
//        JdbcUtils.update("update ps_answer_card set paper_snapshot = '\uD835\uDF0B' where id = 3");
        JdbcUtils.insert("insert into ps_answer_card(id, answer_card_id, exam_id, design_answer_card_id, paper_snapshot) values (2, '1', '1', '1', '\uD835\uDF0B\uD840\uDCCA')");
        String str = "\uD840\uDCCA";
    }
}


