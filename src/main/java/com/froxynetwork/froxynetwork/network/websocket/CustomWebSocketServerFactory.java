package com.froxynetwork.froxynetwork.network.websocket;

import java.io.IOException;
import java.nio.channels.ByteChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.List;

import org.java_websocket.WebSocketAdapter;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.WebSocketServerFactory;
import org.java_websocket.drafts.Draft;

import com.froxynetwork.froxynetwork.network.websocket.auth.WebSocketAuthentication;

/**
 * MIT License
 *
 * Copyright (c) 2020 FroxyNetwork
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
 * The Custom WebSocket Server Factory<br />
 * If you want to implement your own WebSocket Factory, you SHOULD use
 * {@link WebSocketServerImpl} or a class that extends this one
 */
public class CustomWebSocketServerFactory implements WebSocketServerFactory {

	private WebSocketAuthentication authentication;

	public CustomWebSocketServerFactory(WebSocketAuthentication authentication) {
		this.authentication = authentication;
	}

	@Override
	public WebSocketImpl createWebSocket(WebSocketAdapter a, Draft d) {
		return new WebSocketServerImpl(a, d, authentication);
	}

	@Override
	public WebSocketImpl createWebSocket(WebSocketAdapter a, List<Draft> drafts) {
		return new WebSocketServerImpl(a, drafts, authentication);
	}

	@Override
	public ByteChannel wrapChannel(SocketChannel channel, SelectionKey key) throws IOException {
		return channel;
	}

	@Override
	public void close() {
	}
}
