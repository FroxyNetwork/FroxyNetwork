package com.froxynetwork.froxynetwork.network.websocket.modules;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.froxynetwork.froxynetwork.network.websocket.WebSocketClientImpl;

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
public class WebSocketAutoReconnectModuleTest {

	@Test
	public void testAutoReconnectServer() {
		WebSocketAutoReconnectModule mod = new WebSocketAutoReconnectModule(1000); // One second
		WebSocketClientImpl ws = Mockito.mock(WebSocketClientImpl.class);
		// Not a client, it should not create a Thread
		when(ws.isClient()).thenReturn(false);
		mod.init(ws);
		// Load it
		mod.load();
		assertFalse(mod.hasThread());

		// Don't forget to stop
		mod.unload();
	}

	@Test
	public void testAutoReconnectClientThread() {
		WebSocketAutoReconnectModule mod = new WebSocketAutoReconnectModule(1000); // One second
		WebSocketClientImpl ws = Mockito.mock(WebSocketClientImpl.class);
		// It's a client, it should create a Thread
		when(ws.isClient()).thenReturn(true);
		mod.init(ws);
		// Load it
		mod.load();
		// Should be true (auto-reconnect Thread)
		assertTrue(mod.hasThread());

		// Don't forget to stop
		mod.unload();
	}

	@Test
	public void testAutoReconnectClientOneTime() {
		WebSocketAutoReconnectModule mod = new WebSocketAutoReconnectModule(100); // 100 ms
		WebSocketClientImpl ws = Mockito.mock(WebSocketClientImpl.class);
		// It's a client, it should create a Thread
		when(ws.isClient()).thenReturn(true);
		mod.init(ws);
		// Load it
		mod.load();
		// Test if reconnect has been called after 100 ms
		verify(ws, timeout(125)).reconnect();

		// Don't forget to stop
		mod.unload();
	}

	@Test
	public void testAutoReconnectClientFiveTimes() {
		WebSocketAutoReconnectModule mod = new WebSocketAutoReconnectModule(100); // 100 ms
		WebSocketClientImpl ws = Mockito.mock(WebSocketClientImpl.class);
		// It's a client, it should create a Thread
		when(ws.isClient()).thenReturn(true);
		mod.init(ws);
		// Load it
		mod.load();
		// Test if reconnect has been called 5 times (every 100 ms)
		verify(ws, timeout(125 * 5).atLeast(5)).reconnect();

		// Don't forget to stop
		mod.unload();
	}
}
