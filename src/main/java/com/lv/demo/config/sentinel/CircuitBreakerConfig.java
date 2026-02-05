package com.lv.demo.config.sentinel;

import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lv
 */
@Configuration
public class CircuitBreakerConfig {

    @PostConstruct
    public void initCircuitBreakerRules() {
        List<DegradeRule> rules = new ArrayList<>();
        // 异常比例熔断规则
        DegradeRule checkNameRule = new DegradeRule();
        // 资源名
        checkNameRule.setResource("checkNameExist");
        // 异常比例
        checkNameRule.setGrade(RuleConstant.DEGRADE_GRADE_EXCEPTION_RATIO);
        // 异常比例阈值50%
        checkNameRule.setCount(0.5);
        // 熔断时长10秒
        checkNameRule.setTimeWindow(10);
        // 最小请求数
        checkNameRule.setMinRequestAmount(5);
        // 统计时长10秒
        checkNameRule.setStatIntervalMs(10000);
        rules.add(checkNameRule);

        // 异常数熔断规则
        DegradeRule checkPhoneRule = new DegradeRule();
        checkPhoneRule.setResource("checkPhoneExist");
        // 异常数
        checkPhoneRule.setGrade(RuleConstant.DEGRADE_GRADE_EXCEPTION_COUNT);
        // 异常数阈值10
        checkPhoneRule.setCount(2);
        // 熔断时长60秒
        checkPhoneRule.setTimeWindow(30);
        // 最小请求数
        checkPhoneRule.setMinRequestAmount(5);
        // 统计时长5秒
        checkPhoneRule.setStatIntervalMs(5000);
        rules.add(checkPhoneRule);
        DegradeRuleManager.loadRules(rules);
    }

}
