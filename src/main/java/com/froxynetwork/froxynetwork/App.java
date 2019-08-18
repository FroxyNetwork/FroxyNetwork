package com.froxynetwork.froxynetwork;

import com.froxynetwork.froxynetwork.network.NetworkManager;
import com.froxynetwork.froxynetwork.network.output.Callback;
import com.froxynetwork.froxynetwork.network.output.RestException;
import com.froxynetwork.froxynetwork.network.output.data.server.config.ServerConfigDataOutput;
import com.froxynetwork.froxynetwork.network.output.data.server.config.ServerConfigDataOutput.ServersConfig;
import com.froxynetwork.froxynetwork.network.service.ServiceManager;

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
public class App {

	public App() throws Exception {
		// TODO URL in config file
		String url = "https://localhost/";
		String clientId = "WEBSOCKET_5538f57946961ad1c06064b89112d74b";
		String clientSecret = "SECRET_1b49eda57b597a055973dd6f87ac3983";
		NetworkManager nm = new NetworkManager(url, clientId, clientSecret);
		ServiceManager sm = nm.getNetwork();

		// Retrieve server configuration
		sm.getServerConfigService().asyncGetServerConfig(new Callback<ServerConfigDataOutput.ServersConfig>() {

			@Override
			public void onResponse(ServersConfig response) {
				System.out.println(response);
				try {
					ServersConfig sc = sm.getServerConfigService().syncGetServerConfig("koth");
					System.out.println(sc);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				try {
					ServersConfig sc = sm.getServerConfigService().syncGetServerConfig("koth_4players");
					System.out.println(sc);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				// Shutdown at the end
				nm.shutdown();
			}

			@Override
			public void onFailure(RestException ex) {
				// Shutdown at the end
				nm.shutdown();
			}

			@Override
			public void onFatalFailure(Throwable t) {
				// Shutdown at the end
				nm.shutdown();
			}
		});
		// Add server

		// sm.getServerService().asyncAddServer("koth_1", "KOTH", 20001, new
		// Callback<Server>() {
		// @Override
		// public void onResponse(Server server) {
		// System.out.println(server);
		// server.setStatus(ServerStatus.STARTED.name());
		// try {
		// System.out.println("Editing server");
		// Server editedServer = sm.getServerService().syncEditServer(server);
		// System.out.println("Deleting server");
		// sm.getServerService().syncDeleteServer(editedServer.getId());
		// sm.getServerService().asyncGetServer(editedServer.getId(), new
		// Callback<Server>() {
		//
		// @Override
		// public void onResponse(Server response) {
		// System.out.println(response);
		// sm.getServerService().asyncGetServer("999", new Callback<Server>() {
		//
		// @Override
		// public void onResponse(Server response2) {
		// System.out.println(response2);
		// // Shutdown at the end
		// nm.shutdown();
		// };
		//
		// @Override
		// public void onFailure(RestException ex) {
		// ex.printStackTrace();
		// // Shutdown at the end
		// nm.shutdown();
		// }
		//
		// @Override
		// public void onFatalFailure(Throwable t) {
		// t.printStackTrace();
		// // Shutdown at the end
		// nm.shutdown();
		// }
		// });
		// };
		//
		// @Override
		// public void onFailure(RestException ex) {
		// ex.printStackTrace();
		// }
		//
		// @Override
		// public void onFatalFailure(Throwable t) {
		// t.printStackTrace();
		// }
		// });
		// } catch (RestException ex) {
		// ex.printStackTrace();
		// } catch (Exception ex) {
		// ex.printStackTrace();
		// }
		// };
		//
		// @Override
		// public void onFailure(RestException ex) {
		// ex.printStackTrace();
		// }
		//
		// @Override
		// public void onFatalFailure(Throwable t) {
		// t.printStackTrace();
		// }
		// });
	}

	public static void main(String[] args) throws Exception {
		new App();
	}
}
