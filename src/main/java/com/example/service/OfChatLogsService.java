package com.example.service;

import com.example.entity.OfChatHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Component;

/**
 * @author lxx
 * @version V1.0.0
 * @date 2018-1-8
 */

@Component
public interface OfChatLogsService  extends JpaRepository<OfChatHistory, Long>, JpaSpecificationExecutor<OfChatHistory> {

}
