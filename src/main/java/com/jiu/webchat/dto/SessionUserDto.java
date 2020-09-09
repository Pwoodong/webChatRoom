package com.jiu.webchat.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class SessionUserDto implements Serializable {
	private String id;
	private String username;
	private String status;
}
