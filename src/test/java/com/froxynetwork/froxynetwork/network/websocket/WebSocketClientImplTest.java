package com.froxynetwork.froxynetwork.network.websocket;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.function.Consumer;

import org.java_websocket.handshake.ServerHandshake;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

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
@RunWith(MockitoJUnitRunner.class)
public class WebSocketClientImplTest {

	private WebSocketClientImpl createClientMock() throws URISyntaxException {
		WebSocketAuthentication auth = mock(WebSocketAuthentication.class);
		return new WebSocketClientImpl(new URI("localhost"), auth);
	}

	@Test
	public void testAuthentication() throws URISyntaxException {
		WebSocketAuthentication auth = mock(WebSocketAuthentication.class);
		WebSocketClientImpl client = new WebSocketClientImpl(new URI("localhost"), auth);
		// It should initialize and register the authentication
		verify(auth).init(client);
		verify(auth).registerAuthenticationListener(client);
	}

	@Test
	public void testIsClient() throws URISyntaxException {
		WebSocketClientImpl client = createClientMock();
		assertTrue(client.isClient());
	}

	@Test
	public void testRegisterWebSocketConnection() throws URISyntaxException {
		WebSocketClientImpl client = createClientMock();
		Consumer<Boolean> cons = mock(Consumer.class);
		client.registerWebSocketConnection(cons);
		ServerHandshake handshakedata = mock(ServerHandshake.class);

		// Check if the method is called
		client.onOpen(handshakedata);
		verify(cons).accept(true);

		// Check again, should be false (Because it's not the first connection)
		client.onOpen(handshakedata);
		verify(cons).accept(false);
	}

	@Test
	public void testRegisterWebSocketAuthentication() throws URISyntaxException {
		WebSocketClientImpl client = createClientMock();
		Runnable run = mock(Runnable.class);
		client.registerWebSocketAuthentication(run);

		// Check if the method is called
		client.onAuthentication();
		verify(run).run();
	}

	@Test
	public void testRegisterWebSocketDisconnection() throws URISyntaxException {
		WebSocketClientImpl client = createClientMock();
		Consumer<Boolean> cons = mock(Consumer.class);
		client.registerWebSocketDisconnection(cons);

		// Check if the method is called
		client.onClose(100, "Reason", true);
		verify(cons).accept(true);

		// Check again, should be false
		client.onClose(100, "Reason", false);
		verify(cons).accept(false);
	}

	@Test
	public void testRegisterCommand() throws URISyntaxException {
		WebSocketClientImpl client = createClientMock();
		IWebSocketCommander commander = mock(IWebSocketCommander.class);
		when(commander.name()).thenReturn("register");
		// Register this commander
		client.registerCommand(commander);
		// Call onCommand
		client.onCommand("register2", "");
		// Should NOT be called
		verify(commander, never()).onReceive("register");
		verify(commander, never()).onReceive("register2");
		// Call onCommand
		client.onCommand("register", "test 1 2 3");
		// Nope
		verify(commander, never()).onReceive("");
		// Yep
		verify(commander).onReceive("test 1 2 3");
	}

	@Test
	public void testOnMessage() throws URISyntaxException {
		WebSocketClientImpl client = createClientMock();
		IWebSocketCommander commander = mock(IWebSocketCommander.class);
		when(commander.name()).thenReturn("register");
		// Register this commander
		client.registerCommand(commander);
		// Call onMessage
		client.onMessage("register2 test 1 2 3");
		// Should NOT be called
		verify(commander, never()).onReceive("register");
		verify(commander, never()).onReceive("register2");
		// Call onMessage
		client.onMessage("register test 1 2 3");
		// Nope
		verify(commander, never()).onReceive("");
		;
		// Yep
		verify(commander).onReceive("test 1 2 3");
	}

	@Test
	public void testSave() throws URISyntaxException {
		WebSocketClientImpl client = createClientMock();
		client.save("test", 1);
		assertEquals(1, client.get("test"));
	}
}
