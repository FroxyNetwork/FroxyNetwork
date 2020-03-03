package com.froxynetwork.froxynetwork.network.service;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.froxynetwork.froxynetwork.network.dao.PlayerDao;
import com.froxynetwork.froxynetwork.network.output.Callback;
import com.froxynetwork.froxynetwork.network.output.RestException;
import com.froxynetwork.froxynetwork.network.output.data.PlayerDataOutput;
import com.froxynetwork.froxynetwork.network.output.data.PlayerDataOutput.Player;

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
public class PlayerService {

	private final Logger LOG = LoggerFactory.getLogger(getClass());

	private PlayerDao playerDao;

	public PlayerService(Retrofit retrofit) {
		playerDao = retrofit.create(PlayerDao.class);
	}

	public void asyncGetPlayer(String uuid, Callback<Player> callback) {
		if (LOG.isDebugEnabled())
			LOG.debug("asyncGetPlayer: Retrieving player {}", uuid);
		playerDao.getPlayer(uuid).enqueue(ServiceHelper.callback(callback, PlayerDataOutput.class));
	}

	public Player syncGetPlayer(String uuid) throws RestException, Exception {
		if (LOG.isDebugEnabled())
			LOG.debug("syncGetPlayer: Retrieving player {}", uuid);
		Response<PlayerDataOutput> response = playerDao.getPlayer(uuid).execute();
		PlayerDataOutput body = ServiceHelper.response(response, PlayerDataOutput.class);
		if (body.isError())
			throw new RestException(body);
		else
			return body.getData();
	}

	public void asyncAddPlayer(UUID uuid, String pseudo, String ip, Callback<Player> callback) {
		if (LOG.isDebugEnabled())
			LOG.debug("asyncAddPlayer: Adding new player, uuid = {}, pseudo = {}, ip = {}", uuid, pseudo, ip);
		playerDao.createPlayer(new Player(uuid.toString(), pseudo, null, 0, 0, 0, null, null, ip, null, null))
				.enqueue(ServiceHelper.callback(callback, PlayerDataOutput.class));
	}

	public Player syncAddPlayer(UUID uuid, String pseudo, String ip) throws RestException, Exception {
		if (LOG.isDebugEnabled())
			LOG.debug("asyncAddPlayer: Adding new player, uuid = {}, pseudo = {}, ip = {}", uuid, pseudo, ip);
		Response<PlayerDataOutput> response = playerDao
				.createPlayer(new Player(uuid.toString(), pseudo, null, 0, 0, 0, null, null, ip, null, null)).execute();
		PlayerDataOutput body = ServiceHelper.response(response, PlayerDataOutput.class);
		if (body.isError())
			throw new RestException(body);
		else
			return body.getData();
	}

	public void asyncEditPlayer(Player player, Callback<Player> callback) {
		if (LOG.isDebugEnabled())
			LOG.debug("asyncEditPlayer: Editing Player {}", player.getUuid());
		playerDao.updatePlayer(player.getUuid(), player)
				.enqueue(ServiceHelper.callback(callback, PlayerDataOutput.class));
	}

	public Player syncEditPlayer(Player player) throws RestException, Exception {
		if (LOG.isDebugEnabled())
			LOG.debug("syncEditPlayer: Editing Player {}", player.getUuid());
		Response<PlayerDataOutput> response = playerDao.updatePlayer(player.getUuid(), player).execute();
		PlayerDataOutput body = ServiceHelper.response(response, PlayerDataOutput.class);
		if (body.isError())
			throw new RestException(body);
		else
			return body.getData();
	}
}
