package com.froxynetwork.froxynetwork.network.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.froxynetwork.froxynetwork.network.dao.ServerConfigDao;
import com.froxynetwork.froxynetwork.network.output.Callback;
import com.froxynetwork.froxynetwork.network.output.RestException;
import com.froxynetwork.froxynetwork.network.output.data.server.config.MainServerConfigDataOutput;
import com.froxynetwork.froxynetwork.network.output.data.server.config.MainServerConfigDataOutput.MainServerConfig;
import com.froxynetwork.froxynetwork.network.output.data.server.config.ServerConfigDataOutput;
import com.froxynetwork.froxynetwork.network.output.data.server.config.ServerConfigDataOutput.ServerConfig;

import retrofit2.Response;
import retrofit2.Retrofit;

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
public class ServerConfigService {

	private final Logger LOG = LoggerFactory.getLogger(getClass());

	private ServerConfigDao serverConfigDao;

	public ServerConfigService(Retrofit retrofit) {
		serverConfigDao = retrofit.create(ServerConfigDao.class);
	}

	public void asyncGetServerConfig(Callback<MainServerConfig> callback) {
		if (LOG.isDebugEnabled())
			LOG.debug("asyncGetServerConfig: Retrieving server configuration");
		serverConfigDao.getServerConfig().enqueue(ServiceHelper.callback(callback, MainServerConfigDataOutput.class));
	}

	public MainServerConfig syncGetServerConfig() throws RestException, Exception {
		if (LOG.isDebugEnabled())
			LOG.debug("syncGetServerConfig: Retrieving server configuration");
		Response<MainServerConfigDataOutput> response = serverConfigDao.getServerConfig().execute();
		MainServerConfigDataOutput body = ServiceHelper.response(response, MainServerConfigDataOutput.class);
		if (body.isError())
			throw new RestException(body);
		else
			return body.getData();
	}

	public void asyncGetServerConfig(String type, Callback<ServerConfig> callback) {
		if (LOG.isDebugEnabled())
			LOG.debug("asyncGetServerConfig: Retrieving server configuration type {}", type);
		serverConfigDao.getServerConfig(type).enqueue(ServiceHelper.callback(callback, ServerConfigDataOutput.class));
	}

	public ServerConfig syncGetServerConfig(String type) throws RestException, Exception {
		if (LOG.isDebugEnabled())
			LOG.debug("syncGetServerConfig: Retrieving server configuration type {}", type);
		Response<ServerConfigDataOutput> response = serverConfigDao.getServerConfig(type).execute();
		ServerConfigDataOutput body = ServiceHelper.response(response, ServerConfigDataOutput.class);
		if (body.isError())
			throw new RestException(body);
		else
			return body.getData();
	}
}
