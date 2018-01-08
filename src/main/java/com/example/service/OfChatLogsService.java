package com.example.service;

import com.example.entity.OfChatLogs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Component;

/**
 * @author lxx
 * @version V1.0.0
 * @date 2018-1-8
 */

@Component
public interface OfChatLogsService  extends JpaRepository<OfChatLogs, Long>, JpaSpecificationExecutor<OfChatLogs> {

}
