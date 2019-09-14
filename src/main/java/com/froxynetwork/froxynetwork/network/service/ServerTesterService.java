package com.froxynetwork.froxynetwork.network.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.froxynetwork.froxynetwork.network.dao.ServerTesterDao;
import com.froxynetwork.froxynetwork.network.output.Callback;
import com.froxynetwork.froxynetwork.network.output.RestException;
import com.froxynetwork.froxynetwork.network.output.data.ServerTesterDataOutput;
import com.froxynetwork.froxynetwork.network.output.data.ServerTesterDataOutput.ServerTester;

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
public class ServerTesterService {

	private final Logger LOG = LoggerFactory.getLogger(getClass());

	private ServerTesterDao serverTesterDao;

	public ServerTesterService(Retrofit retrofit) {
		serverTesterDao = retrofit.create(ServerTesterDao.class);
	}

	public void asyncCheckServer(String id, String token, Callback<ServerTester> callback) {
		if (LOG.isDebugEnabled())
			LOG.debug("asyncCheckServer: Checking server {}", id);
		serverTesterDao.checkServer(id, token).enqueue(ServiceHelper.callback(callback, ServerTesterDataOutput.class));
	}

	public ServerTester syncCheckServer(String id, String token) throws RestException, Exception {
		if (LOG.isDebugEnabled())
			LOG.debug("syncCheckServer: Checking server {}", id);
		Response<ServerTesterDataOutput> response = serverTesterDao.checkServer(id, token).execute();
		ServerTesterDataOutput body = ServiceHelper.response(response, ServerTesterDataOutput.class);
		if (body.isError())
			throw new RestException(body);
		else
			return body.getData();
	}
}
