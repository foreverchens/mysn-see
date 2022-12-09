package com.ychen.see.models.event;

import com.ychen.see.models.event.domain.ChangeEventInfo;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author yyy
 */
@Slf4j
@Component
public class EventDataDomain {


	public void changeEvent(String dataType) {}

	public List<ChangeEventInfo> listEventInfo(String symbol) {
		return null;
	}

}
