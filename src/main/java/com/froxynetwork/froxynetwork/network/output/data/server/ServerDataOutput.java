package com.froxynetwork.froxynetwork.network.output.data.server;

import java.util.Date;

import com.froxynetwork.froxynetwork.network.output.data.GeneralDataOutput;
import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
public class ServerDataOutput extends GeneralDataOutput<ServerDataOutput.Server> {

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Server {
		private String id;
		private String name;
		private String type;
		private int port;
		private String status;
		private Date creationTime;
		private Date endTime;
		private ServerAuth auth;

		public ServerStatus getStatus() {
			switch (status) {
			case "STARTING":
				return ServerStatus.STARTING;
			case "WAITING":
				return ServerStatus.WAITING;
			case "STARTED":
				return ServerStatus.STARTED;
			case "ENDING":
				return ServerStatus.ENDING;
			case "ENDED":
			default:
				return ServerStatus.ENDED;
			}
		}
	}

	public enum ServerStatus {
		STARTING, WAITING, STARTED, ENDING, ENDED;

		/**
		 * Check if b is after or equals to a
		 * 
		 * @param a
		 *            The first status
		 * @param b
		 *            The second status
		 * 
		 * @return true if b is after or equals to a
		 */
		public static boolean isAfter(ServerStatus a, ServerStatus b) {
			return b.ordinal() >= a.ordinal();
		}
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ServerAuth {
		@SerializedName("client_id")
		private String clientId;
		@SerializedName("client_secret")
		private String clientSecret;

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder("ServerDataOutput.ServerAuth(clientId=");
			sb.append(clientId);
			sb.append(", clientSecret=");
			sb.append("<hidden>");
			sb.append(")");
			return sb.toString();
		}
	}
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ServerDocker {
		private int server;
		private String id;
	}
}
