package com.lv.demo.config.sentinel;

import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRuleManager;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lv
 */
@Configuration
public class ParamFlowConfig {

    @PostConstruct
    public void initParamFlowRules() {
        List<ParamFlowRule> rules = new ArrayList<>();
        // 用户ID限流规则
        ParamFlowRule getUserByIdRule = new ParamFlowRule();
        // 资源名
        getUserByIdRule.setResource("getUserById");
        // 参数索引，第一个参数
        getUserByIdRule.setParamIdx(0);
        // QPS限流
        getUserByIdRule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        // 限流阈值10QPS
        getUserByIdRule.setCount(5);
        // 统计时长1秒
        getUserByIdRule.setDurationInSec(60);
        rules.add(getUserByIdRule);
        ParamFlowRuleManager.loadRules(rules);
    }
}
