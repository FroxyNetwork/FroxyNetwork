package com.froxynetwork.froxynetwork.network.output.data;

import com.froxynetwork.froxynetwork.network.output.data.server.ServerDataOutput;

import lombok.Getter;

/**
 * MIT License
 *
 * Copyright (c) 2019 FroxyNetwork
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * 
 * @author 0ddlyoko
 */
@Getter
public enum Error {
	METHOD_NOT_IMPLEMENTED(0, "This method is not implemented"),
	GLOBAL_ERROR(1, "Error #{errorCode} : {error}"),
	GLOBAL_UNKNOWN(2, "Unknown error"),
	GLOBAL_NO_PERMISSION(3, "You don't have the permission to perform this operation"),
	GLOBAL_DATA_INVALID(4, "Invalid data value !"),
	GLOBAL_CONTROLLER_NOT_FOUND(5, "No controller found for your request !"),
	GLOBAL_UNKNOWN_ERROR(6, "An error has occured while performing the operation"),
	GLOBAL_TOKEN_INVALUD(7, "Invalid Token, Please save your informations and refresh your page !"),
	ROUTE_NOT_FOUND(8, "This route doesn't exist"),
	
	PLAYER_DATA_LENGTH(100, "The length of the search must be between 1 and 20, or equals to 36."),
	PLAYER_NOT_FOUND(101, "Player not found"),
	PLAYER_UUID_LENGTH(102, "UUID length must be 36 !"),
	PLAYER_UUID_FORMAT(103, "Bad UUID format !"),
	PLAYER_PSEUDO_LENGTH(104, "Pseudo length must be between 1 and 20 !"),
	PLAYER_IP_LENGTH(105, "Ip length must be between 7 and 15 !"),
	PLAYER_IP_FORMAT(106, "Bad ip format !"),
	PLAYER_UUID_EXISTS(107, "UUID already exist !"),
	PLAYER_PSEUDO_EXISTS(108, "Pseudo already exist !"),
	PLAYER_DISPLAYNAME_LENGTH(109, "DisplayName length must be between 1 and 20 !"),
	PLAYER_COINS_POSITIVE(110, "Coins must be positive !"),
	PLAYER_LEVEL_POSITIVE(111, "Level must be positive !"),
	PLAYER_EXP_POSITIVE(112, "Exp must be positive !"),
	PLAYER_TIME_FORMAT(113, "Bad time format !"),
	PLAYER_LASTLOGIN_GREATER(114, "LastLogin must be greater than saved LastLogin !"),

	SERVER_ID_INVALID(200, "Invalid id"),
	SERVER_NOT_FOUND(201, "Server not found"),
	SERVER_NAME_INVALID(202, "Name must be a string !"),
	SERVER_NAME_LENGTH(203, "Name length must be between 1 and 16 !"),
	SERVER_PORT_INVALID(204, "Port must be a correct number and between 1 and 65535 !"),
	SERVER_SAVING(205, "Error while saving client_id and client_secret"),
	SERVER_STATUS_INVALID(206, "Status must be '" + ServerDataOutput.ServerStatus.WAITING + "', '" + ServerDataOutput.ServerStatus.STARTED + "' or '" + ServerDataOutput.ServerStatus.ENDING + "' !"),
	SERVER_STATUS_BEFORE(207, "Invalid status, current is {currentStatus} !"),
	SERVER_ACTUALLY_ENDED(208, "This server is already ended !"),
	SERVER_TYPE_INVALID(209, "Type must be a string !"),
	SERVER_TYPE_LENGTH(210, "Type length must be between 1 and 16 !"),
	SERVER_TESTER_INVALID(211, "Invalid id / token / client_id"),
	
	INTERNAL_SERVER_JSON(1000, "Internal Server Error: servers.json is not a valid json file !"),
    INTERNAL_SERVER_CONFIG(1001, "Internal Server Error: config.ini is not a valid ini file !"),
    INTERNAL_SERVER_CONFIG_MONGODB(1002, "Internal Server Error: config.ini is not a valid ini file !"),
    INTERNAL_SERVER_DATABASE(1003, "Internal Server Error: Cannot contact database !"),
    SERVER_TYPE_NOT_FOUND(1004, "Server type not found !"),
    SERVER_TYPE_FILE_NOT_FOUND(1005, "Server file not found !");
    
	
	private int id;
	private String message;
	
	private Error(int id, String message) {
		this.id = id;
		this.message = message;
	}
	
	public static Error getFromId(int id) {
		for (Error error : values())
			if (error.id == id)
				return error;
		return null;
	}
}
