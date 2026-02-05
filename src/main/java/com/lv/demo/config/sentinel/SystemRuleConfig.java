package com.lv.demo.config.sentinel;

import com.alibaba.csp.sentinel.slots.system.SystemRule;
import com.alibaba.csp.sentinel.slots.system.SystemRuleManager;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 18891
 */
@Configuration
public class SystemRuleConfig {

    @PostConstruct
    public void initSystemRules() {
        List<SystemRule> rules = new ArrayList<>();
        // CPU使用率规则
        SystemRule cpuRule = new SystemRule();
        // 系统负载阈值
        cpuRule.setHighestSystemLoad(2.0);
        // CPU使用率阈值80%
        cpuRule.setHighestCpuUsage(0.8);
        // 平均响应时间阈值1秒
        cpuRule.setAvgRt(1000);
        // 最大线程数阈值
        cpuRule.setMaxThread(20);
        // QPS阈值
        cpuRule.setQps(1000);
        rules.add(cpuRule);
        SystemRuleManager.loadRules(rules);
    }
}
