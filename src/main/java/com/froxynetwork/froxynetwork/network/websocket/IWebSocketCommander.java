package com.froxynetwork.froxynetwork.network.websocket;

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
/**
 * Used to register new commands to execute once a message is received
 */
public interface IWebSocketCommander {

	/**
	 * @return The name of the command
	 */
	public String name();

	/**
	 * @return The description of the command
	 */
	public String description();

	/**
	 * @return {@link From#ALL} if you want to accept messages from WebSocket and
	 *         Server or {@link From#WEBSOCKET} if you want only to accept messages
	 *         from WebSocket
	 */
	public From from();

	/**
	 * Action to execute once the message is received
	 * 
	 * @param from    The original name of the server which sent the message or MAIN
	 *                if from WebSocket
	 * @param message The message
	 */

	public void onReceive(String from, String message);

	/**
	 * Filter commands from the WebSocket
	 */
	public enum From {
		/**
		 * Accept message from WebSocket and Server
		 */
		ALL,
		/**
		 * Accept message only from WebSocket
		 */
		WEBSOCKET
	}
}
