package com.froxynetwork.froxynetwork.network.websocket.auth;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.froxynetwork.froxynetwork.network.websocket.IWebSocket;

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
@RunWith(MockitoJUnitRunner.class)
public class WebSocketNoAuthenticationTest {

	@Test
	public void testIsAuthenticated() {
		WebSocketAuthentication auth = new WebSocketNoAuthentication();
		// Should always be true
		assertTrue(auth.isAuthenticated(null));
	}

	@Test
	public void testRegisterAuthenticationListener() {
		IWebSocket ws = Mockito.mock(IWebSocket.class);
		WebSocketAuthentication auth = new WebSocketNoAuthentication();
		auth.registerAuthenticationListener(ws);
		// Test if this method has been invoked
		verify(ws).onAuthentication();
	}
}
